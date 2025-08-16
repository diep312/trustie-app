package com.example.trustie.data.local.wave2vec

import android.content.Context
import android.util.Log
import ai.onnxruntime.*
import com.example.trustie.data.local.lm.ArpaLanguageModel
import java.io.File
import java.nio.FloatBuffer

// Result type for streaming: both text and token sequence
data class TranscriptionResult(
    val text: String,
    val tokens: List<Int>,
    val logitsArray:  Array<FloatArray>
)

class OnnxWav2Vec2Manager(
    private val context: Context,
    private val vocab: List<String>,
    private val modelAssetPath: String,
    val languageModel: ArpaLanguageModel? = null
) {
    private var env: OrtEnvironment? = null
    private var session: OrtSession? = null
    var blankTokenId: Int = -1 // 109

    fun loadModel() {
        env = OrtEnvironment.getEnvironment()
        val opts = OrtSession.SessionOptions().apply {
            setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT)
        }

        val modelFile = assetToFile(modelAssetPath)
        session = env?.createSession(modelFile.absolutePath, opts)
        Log.d("ONNX", "Model loaded from $modelAssetPath")

        blankTokenId = vocab.indexOf("<pad>").takeIf { it >= 0 } ?: vocab.indexOf("<blank>").takeIf { it >= 0 } ?: 0
    }

    private fun assetToFile(assetName: String): File {
        val file = File(context.filesDir, assetName.substringAfterLast("/"))
        context.assets.open(assetName).use { input ->
            file.outputStream().use { output -> input.copyTo(output) }
        }
        return file
    }

    /**
     * Streaming-friendly: returns both decoded text and raw argmax token IDs (for overlap trimming)
     */
    fun transcribeWithTokens(shorts: ShortArray): TranscriptionResult {
        if (session == null) throw IllegalStateException("ONNX model not loaded")

        // PCM16 → float32 [-1, 1]
        val floats = FloatArray(shorts.size) { i -> shorts[i] / 32768.0f }
        val maxAmp = floats.maxOf { kotlin.math.abs(it) }
        Log.d("ONNX", "Audio length: ${floats.size} samples, max amplitude: $maxAmp")

        // Mean/variance normalization (important for Wav2Vec2)
        val mean = floats.average().toFloat()
        var varSum = 0f
        for (i in floats.indices) {
            val v = floats[i] - mean
            floats[i] = v
            varSum += v * v
        }
        val std = kotlin.math.sqrt(varSum / floats.size).coerceAtLeast(1e-7f)
        for (i in floats.indices) floats[i] /= std

        val shape = longArrayOf(1, floats.size.toLong())
        val inputName = session!!.inputNames.first()
        Log.d("ONNX", "Using input name: $inputName")



        OnnxTensor.createTensor(env, FloatBuffer.wrap(floats), shape).use { tensor ->
            val result = session!!.run(mapOf(inputName to tensor))

            @Suppress("UNCHECKED_CAST")
            val logits = result[0].value as Array<Array<FloatArray>>
            val T = logits[0].size
            val V = logits[0][0].size

            if (V != vocab.size) {
                Log.e("ONNX", "Vocab mismatch: model=$V, app=${vocab.size}.")
                return TranscriptionResult("", emptyList(), emptyArray())
            }

            val tokens = IntArray(T) { t ->
                logits[0][t].indices.maxByOrNull { i -> logits[0][t][i] } ?: blankTokenId
            }

            // Use LM-based beam search if available
            val text = if (languageModel != null) {
                ctcBeamSearchWithLM(
                    logits = Array(T) { t -> logits[0][t] },
                    beamWidth = 25,
                    topK = 10,
                    blankId = blankTokenId,
                    lmWeight = 0.5
                )
            } else {
                ctcBeamSearch(
                    logits = Array(T) { t -> logits[0][t] },
                    beamWidth = 25,
                    topK = 10,
                    blankId = blankTokenId
                )
            }

            return TranscriptionResult(
                text = text,
                tokens = tokens.toList(),
                logitsArray = Array(T) { t -> logits[0][t] }
            )
        }
    }

    /**
     * Greedy CTC decode: skip blanks, merge repeats, map vocab IDs to text
     */
    fun ctcDecode(tokens: IntArray): String {
        val sb = StringBuilder()
        var prevToken = -1
        for (id in tokens) {
            if (id != blankTokenId && id != prevToken) {
                var token = vocab.getOrNull(id) ?: ""
                if (token == "|") token = " "   // space
                if (token == "<unk>") continue // drop unknowns
                sb.append(token)
            }
            prevToken = id
        }
        return sb.toString().trim()
    }


    // In OnnxWav2Vec2Manager.kt

    /**
     * Numerically stable log-softmax for one frame of logits.
     */
    private fun logSoftmaxRow(row: FloatArray): FloatArray {
        var max = Float.NEGATIVE_INFINITY
        for (v in row) if (v > max) max = v
        var sum = 0.0
        for (v in row) sum += kotlin.math.exp((v - max).toDouble())
        val lse = max + kotlin.math.log10(sum)
        return FloatArray(row.size) { i -> (row[i] - lse).toFloat() }
    }

    /**
     * logsumexp(a, b) in log-domain
     */
    private fun logSumExp(a: Double, b: Double): Double {
        if (a == Double.NEGATIVE_INFINITY) return b
        if (b == Double.NEGATIVE_INFINITY) return a
        val m = maxOf(a, b)
        return m + kotlin.math.ln(kotlin.math.exp(a - m) + kotlin.math.exp(b - m))
    }

    /**
     * Character for token id (handles space and <unk>).
     */
    private fun idToChar(id: Int): String {
        if (id < 0 || id >= vocab.size) return ""
        val t = vocab[id]
        return when (t) {
            "|" -> " "
            "<unk>" -> "" // drop unknowns
            else -> t
        }
    }

    /**
     * Prefix beam search for CTC (no LM).
     * - logits: [T, V] frame-major logits (NOT softmaxed)
     * - beamWidth: typical 10..50
     * - topK: per-frame pruning of tokens (e.g., 10..20) for speed
     */
    fun ctcBeamSearch(
        logits: Array<FloatArray>,
        beamWidth: Int = 25,
        topK: Int = 10,
        blankId: Int = this.blankTokenId
    ): String {
        // Each prefix stores two log-probs: ending with blank (p_b) and non-blank (p_nb)
        data class Beam(val p_b: Double, val p_nb: Double)
        var beams: MutableMap<String, Beam> = mutableMapOf("" to Beam(0.0, Double.NEGATIVE_INFINITY)) // log(1)=0

        for (t in logits.indices) {
            val logp = logSoftmaxRow(logits[t]) // [V]
            // Get topK token indices for pruning
            val indices = logp.indices.sortedByDescending { logp[it] }.take(topK)

            val next: MutableMap<String, Beam> = mutableMapOf()

            // Helper to accumulate into next map
            fun add(prefix: String, add_b: Double, add_nb: Double) {
                val cur = next[prefix]
                if (cur == null) {
                    next[prefix] = Beam(add_b, add_nb)
                } else {
                    next[prefix] = Beam(
                        logSumExp(cur.p_b, add_b),
                        logSumExp(cur.p_nb, add_nb)
                    )
                }
            }

            for ((prefix, beam) in beams) {
                val p_b = beam.p_b
                val p_nb = beam.p_nb

                // Extend with blank
                val lpBlank = logp[blankId].toDouble()
                add(prefix, add_b = logSumExp(p_b, p_nb) + lpBlank, add_nb = Double.NEGATIVE_INFINITY)

                // Extend with tokens
                for (c in indices) {
                    if (c == blankId) continue
                    val ch = idToChar(c)
                    if (ch.isEmpty()) continue // skip <unk> etc.
                    val lp = logp[c].toDouble()

                    if (prefix.isNotEmpty() && idToChar(prefix.last().code) == ch) {
                        // If same char repeated: can only come from blank state
                        // But our prefix is a string of actual chars, so we check last char
                        // For simplicity, we compare the actual char strings.
                    }

                    val lastChar = if (prefix.isEmpty()) "" else prefix.substring(prefix.length - 1)
                    if (ch == lastChar && lastChar.isNotEmpty()) {
                        // Repeating char: transition from non-blank keeps same prefix
                        // p_nb' (same prefix) += p_nb + lp
                        add(prefix, add_b = Double.NEGATIVE_INFINITY, add_nb = p_nb + lp)
                        // Transition from blank adds the same char (new char only when previous was blank)
                        val newPref = prefix + "" // no change here for the repeat case
                        // Nothing extra to add; already accounted above.
                    } else {
                        // New char appended: can come from blank or non-blank
                        val newPref = prefix + ch
                        // p_nb(new) += (p_b + lp) OR (p_nb + lp)
                        add(newPref, add_b = Double.NEGATIVE_INFINITY, add_nb = logSumExp(p_b + lp, p_nb + lp))
                    }
                }
            }

            // Prune to top beamWidth by total prob
            beams = next.entries
                .sortedByDescending { logSumExp(it.value.p_b, it.value.p_nb) }
                .take(beamWidth)
                .associate { it.key to it.value }
                .toMutableMap()
        }

        val best = beams.maxByOrNull { logSumExp(it.value.p_b, it.value.p_nb) }?.key ?: ""
        // Whitespace cleanup
        return best.replace(Regex("\\s+"), " ").trim()
    }


    fun ctcBeamSearchWithLM(
        logits: Array<FloatArray>,
        beamWidth: Int = 25,
        topK: Int = 10,
        blankId: Int = this.blankTokenId,
        lmWeight: Double = 0.5,
        wordInsertionPenalty: Double = 0.0
    ): String {
        data class Beam(
            val prefix: String,
            val words: List<String>,
            val currentWord: String,
            val p_b: Double,
            val p_nb: Double,
            val lm_score: Double
        )

        var beams: MutableMap<String, Beam> = mutableMapOf(
            "" to Beam("", emptyList(), "", 0.0, Double.NEGATIVE_INFINITY, 0.0)
        )

        for (t in logits.indices) {
            val logp = logSoftmaxRow(logits[t])
            val indices = logp.indices.sortedByDescending { logp[it] }.take(topK)

            val next: MutableMap<String, Beam> = mutableMapOf()

            fun addBeam(beam: Beam) {
                val key = "${beam.prefix}||${beam.currentWord}"
                val existing = next[key]
                if (existing == null) {
                    next[key] = beam
                } else {
                    // Merge probabilities
                    next[key] = beam.copy(
                        p_b = logSumExp(existing.p_b, beam.p_b),
                        p_nb = logSumExp(existing.p_nb, beam.p_nb)
                    )
                }
            }

            for ((_, beam) in beams) {
                // Extend with blank
                val lpBlank = logp[blankId].toDouble()
                addBeam(beam.copy(
                    p_b = logSumExp(beam.p_b, beam.p_nb) + lpBlank,
                    p_nb = Double.NEGATIVE_INFINITY
                ))

                // Extend with tokens
                for (c in indices) {
                    if (c == blankId) continue
                    val ch = idToChar(c)
                    if (ch.isEmpty()) continue

                    val lp = logp[c].toDouble()

                    when {
                        ch == " " && beam.currentWord.isNotEmpty() -> {
                            // Complete word
                            val newWords = beam.words + beam.currentWord
                            val lmScore = if (languageModel != null) {
                                beam.lm_score + languageModel.scoreWithBackoffLn(
                                    beam.words,
                                    beam.currentWord
                                ) * lmWeight + wordInsertionPenalty
                            } else {
                                beam.lm_score
                            }

                            addBeam(Beam(
                                prefix = beam.prefix + beam.currentWord + " ",
                                words = newWords,
                                currentWord = "",
                                p_b = Double.NEGATIVE_INFINITY,
                                p_nb = logSumExp(beam.p_b + lp, beam.p_nb + lp),
                                lm_score = lmScore
                            ))
                        }
                        ch != " " -> {
                            // Continue building word
                            val newCurrentWord = beam.currentWord + ch
                            addBeam(beam.copy(
                                currentWord = newCurrentWord,
                                p_b = Double.NEGATIVE_INFINITY,
                                p_nb = logSumExp(beam.p_b + lp, beam.p_nb + lp)
                            ))
                        }
                    }
                }
            }

            // Prune beams
            beams = next.entries
                .sortedByDescending {
                    val totalAcoustic = logSumExp(it.value.p_b, it.value.p_nb)
                    totalAcoustic + it.value.lm_score
                }
                .take(beamWidth)
                .associate { it.key to it.value }
                .toMutableMap()
        }

        // Final scoring with incomplete words
        val finalBeams = beams.map { (_, beam) ->
            var finalBeam = beam
            if (beam.currentWord.isNotEmpty() && languageModel != null) {
                val lmScore = beam.lm_score + languageModel.scoreWithBackoffLn(
                    beam.words,
                    beam.currentWord
                ) * lmWeight
                finalBeam = beam.copy(lm_score = lmScore)
            }
            finalBeam
        }

        val best = finalBeams.maxByOrNull {
            logSumExp(it.p_b, it.p_nb) + it.lm_score
        }

        val result = if (best != null) {
            best.prefix + best.currentWord
        } else {
            ""
        }

        return result.replace(Regex("\\s+"), " ").trim()
    }
}