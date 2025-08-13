package com.example.trustie.data.local.wave2vec

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import org.tensorflow.lite.flex.FlexDelegate
import kotlinx.coroutines.sync.withLock
import kotlin.math.max

class TFLiteModelManager(
    private val context: Context,
    private val modelPath: String,
    private val vocab: List<String>,  // from tokenizer.json
    private val blankToken: String = "" // usually "<pad>" or ""
) {
    private lateinit var interpreter: Interpreter
    private var inputShape: IntArray = intArrayOf()

    suspend fun loadModel() = withContext(Dispatchers.IO) {
        val delegate = FlexDelegate()
        val options = Interpreter.Options().apply {
            addDelegate(delegate)
            setNumThreads(4)
            setUseNNAPI(false)
            setUseXNNPACK(false)
        }
        interpreter = Interpreter(loadModelFile(modelPath), options)
        inputShape = interpreter.getInputTensor(0).shape()
        Log.i("TFLite", "inputs=${interpreter.inputTensorCount}, outputs=${interpreter.outputTensorCount}")
        for (i in 0 until interpreter.inputTensorCount) {
            val t = interpreter.getInputTensor(i)
            Log.i("TFLite", "in[$i] dtype=${t.dataType()} shape=${t.shape().contentToString()}")
        }
        for (i in 0 until interpreter.outputTensorCount) {
            val t = interpreter.getOutputTensor(i)
            Log.i("TFLite", "out[$i] dtype=${t.dataType()} shape=${t.shape().contentToString()}")
        }
    }

    private fun loadModelFile(modelPath: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelPath)
        FileInputStream(fileDescriptor.fileDescriptor).use { input ->
            val fileChannel = input.channel
            return fileChannel.map(
                FileChannel.MapMode.READ_ONLY,
                fileDescriptor.startOffset,
                fileDescriptor.declaredLength
            )
        }
    }

    /**
     * Convert PCM16 to Float32 [-1, 1]
     */
    private fun pcm16ToFloat32(pcmData: ShortArray): FloatArray {
        val f = FloatArray(pcmData.size) { i -> pcmData[i] / 32768f }
        val (mean, std) = normalize(f)
        for (i in f.indices) f[i] = (f[i] - mean) / std
        return f
    }

    private fun normalize(meanStd: FloatArray): Pair<Float, Float> {
        var sum = 0.0
        for (v in meanStd) sum += v
        val mean = (sum / meanStd.size).toFloat()
        var varSum = 0.0
        for (v in meanStd) { val d = v - mean; varSum += d * d }
        val std = kotlin.math.sqrt((varSum / meanStd.size).toFloat())
        return mean to if (std < 1e-7f) 1e-7f else std
    }

    private val mutex = Mutex()
    private val MIN_WINDOW_SAMPLES = 16_000

    suspend fun transcribe(pcmData: ShortArray): String = withContext(Dispatchers.Default) {
        require(::interpreter.isInitialized) { "Model not loaded. Call loadModel() first." }

        // 0) Ensure window is long enough for Wav2Vec
        if (pcmData.size < MIN_WINDOW_SAMPLES) {
            // Return empty and let the caller accumulate more samples
            return@withContext ""
        }

        // 1) Convert and normalize outside the lock
        val floatInput = pcm16ToFloat32(pcmData)
        val n = floatInput.size

        // 2) Do all interpreter calls inside a single lock
        val logits0: Array<FloatArray> = mutex.withLock {
            // Resize dynamic inputs to [1, n]
            interpreter.resizeInput(0, intArrayOf(1, n))
            if (interpreter.inputTensorCount > 1) {
                // Second input is typically attention/padding mask for Select TF Ops exports
                interpreter.resizeInput(1, intArrayOf(1, n))
            }
            interpreter.allocateTensors()

            // Build inputs as rank-2 arrays [1, n]
            val in0 = arrayOf(floatInput)
            val inputs: Array<Any> =
                if (interpreter.inputTensorCount > 1) {
                    val mask = arrayOf(FloatArray(n) { 1f }) // 1 = valid samples
                    arrayOf<Any>(in0, mask)
                } else {
                    arrayOf<Any>(in0)
                }

            // Allocate outputs AFTER allocateTensors() using the current shape
            val outShape = interpreter.getOutputTensor(0).shape() // e.g., [1, T', vocab]
            val logits = Array(outShape[0]) { Array(outShape[1]) { FloatArray(outShape[2]) } }

            val outputMap: MutableMap<Int, Any> = mutableMapOf()
            outputMap[0] = logits
            interpreter.runForMultipleInputsOutputs(inputs, outputMap)

            // Return the first batch [T', vocab]
            logits[0]
        }

        // 3) Decode CTC → string
        val tokenIds = ctcGreedyDecode(logits0) // make sure this uses blankId for "<pad>"
        tokensToText(tokenIds)                  // map "|" → space and collapse whitespace
    }

    /**
     * Greedy CTC decode: argmax over vocab, remove duplicates & blanks
     */

    private val blankId: Int by lazy {
        val id = vocab.indexOf("<pad>")
        require(id != -1) { "Blank '<pad>' not found in vocab" }
        id
    }

    private fun ctcGreedyDecode(logits: Array<FloatArray>): List<Int> {
        val tokenIds = mutableListOf<Int>()
        var lastId = -1
        for (frame in logits) {
            var maxIdx = 0
            var maxScore = Float.NEGATIVE_INFINITY
            for (i in frame.indices) if (frame[i] > maxScore) { maxScore = frame[i]; maxIdx = i }
            if (maxIdx != lastId && maxIdx != blankId) tokenIds.add(maxIdx)
            lastId = maxIdx
        }
        return tokenIds
    }


    private fun tokensToText(tokenIds: List<Int>): String {

        val sb = StringBuilder()
        for (id in tokenIds) {
            val tok = vocab.getOrNull(id) ?: continue
            when (tok) {
                "<pad>", "<unk>" -> {} // drop
                "|" -> sb.append(' ')
                else -> sb.append(tok)
            }
        }
        return sb.toString().replace(Regex("""\s+"""), " ").trim()
    }

    fun close() {
        if (::interpreter.isInitialized) {
            interpreter.close()
        }
    }
}
