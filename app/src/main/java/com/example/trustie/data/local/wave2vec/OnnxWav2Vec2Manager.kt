package com.example.trustie.data.local.wave2vec

import android.content.Context
import android.util.Log
import ai.onnxruntime.*
import java.io.File
import java.nio.FloatBuffer

// Result type for streaming: both text and token sequence
data class TranscriptionResult(
    val text: String,
    val tokens: List<Int>
)

class OnnxWav2Vec2Manager(
    private val context: Context,
    private val vocab: List<String>,
    private val modelAssetPath: String
) {
    private var env: OrtEnvironment? = null
    private var session: OrtSession? = null
    private var blankTokenId: Int = 109 // Known from vocab.json (<pad>)

    fun loadModel() {
        env = OrtEnvironment.getEnvironment()
        val opts = OrtSession.SessionOptions().apply {
            setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT)
        }

        val modelFile = assetToFile(modelAssetPath)
        session = env?.createSession(modelFile.absolutePath, opts)
        Log.d("ONNX", "Model loaded from $modelAssetPath")
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

        val shape = longArrayOf(1, floats.size.toLong())
        val inputName = session!!.inputNames.first()
        Log.d("ONNX", "Using input name: $inputName")

        OnnxTensor.createTensor(env, FloatBuffer.wrap(floats), shape).use { tensor ->
            val result = session!!.run(mapOf(inputName to tensor))

            @Suppress("UNCHECKED_CAST")
            val logits = result[0].value as Array<Array<FloatArray>>
            Log.d("ONNX", "Logits shape: [${logits.size}, ${logits[0].size}, ${logits[0][0].size}]")

            // Greedy argmax to get token IDs per frame
            val tokens = IntArray(logits[0].size) { t ->
                logits[0][t].indices.maxByOrNull { i -> logits[0][t][i] } ?: 0
            }

            Log.d("ONNX", "First 20 token IDs: ${tokens.take(20)}")
            val decoded = ctcDecode(tokens)

            return TranscriptionResult(
                text = decoded,
                tokens = tokens.toList()
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
}