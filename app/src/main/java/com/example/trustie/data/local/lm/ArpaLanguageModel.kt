package com.example.trustie.data.local.lm

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.ln

class ArpaLanguageModel(
    private val context: Context,
    private val arpaPath: String = "models/vi_lm_tiny.arpa"
) {
    private val ngrams = mutableMapOf<Int, MutableMap<String, Float>>() // order -> (ngram -> logprob)
    private val backoffs = mutableMapOf<Int, MutableMap<String, Float>>() // order -> (context -> backoff)
    private var maxOrder = 0

    init {
        loadArpa()
    }

    private fun loadArpa() {
        try {
            context.assets.open(arpaPath).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    var currentOrder = 0
                    var inData = false

                    reader.forEachLine { line ->
                        val trimmed = line.trim()

                        when {
                            trimmed.startsWith("\\data\\") -> inData = true
                            trimmed.startsWith("\\end\\") -> return@forEachLine
                            trimmed.matches(Regex("\\\\\\d+-grams:")) -> {
                                currentOrder = trimmed.substring(1, trimmed.indexOf('-')).toInt()
                                maxOrder = maxOf(maxOrder, currentOrder)
                                ngrams[currentOrder] = mutableMapOf()
                                backoffs[currentOrder] = mutableMapOf()
                            }
                            trimmed.isNotEmpty() && currentOrder > 0 && !trimmed.startsWith("ngram") -> {
                                // Parse n-gram line: logprob<tab>words<tab>[backoff]
                                val parts = trimmed.split("\t")
                                if (parts.size >= 2) {
                                    val logprob = parts[0].toFloatOrNull() ?: return@forEachLine
                                    val words = parts[1]

                                    ngrams[currentOrder]!![words] = logprob

                                    // If there's a backoff weight
                                    if (parts.size >= 3 && currentOrder < maxOrder) {
                                        val backoff = parts[2].toFloatOrNull()
                                        if (backoff != null) {
                                            backoffs[currentOrder]!![words] = backoff
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Log.d("ArpaLM", "Loaded ARPA model with max order $maxOrder")
            ngrams.forEach { (order, map) ->
                Log.d("ArpaLM", "Order $order: ${map.size} n-grams")
            }
        } catch (e: Exception) {
            Log.e("ArpaLM", "Error loading ARPA model", e)
        }
    }

    /**
     * Score a word given context with backoff.
     * Returns log10 probability.
     */
    fun scoreWithBackoff(context: List<String>, word: String): Double {
        // Try from highest order down to unigram
        for (order in minOf(context.size + 1, maxOrder) downTo 1) {
            val ngramContext = if (order > 1) {
                context.takeLast(order - 1)
            } else {
                emptyList()
            }

            val ngramKey = (ngramContext + word).joinToString(" ")
            val logprob = ngrams[order]?.get(ngramKey)

            if (logprob != null) {
                return logprob.toDouble()
            }

            // Try backoff if available
            if (order > 1) {
                val contextKey = ngramContext.joinToString(" ")
                val backoffWeight = backoffs[order - 1]?.get(contextKey) ?: 0f
                // Continue to lower order with backoff weight
                val lowerScore = scoreWithBackoff(ngramContext.dropLast(1), word)
                return backoffWeight + lowerScore
            }
        }

        // Unknown word penalty
        return -10.0
    }

    /**
     * Convert log10 to natural log for consistency with acoustic scores
     */
    fun scoreWithBackoffLn(context: List<String>, word: String): Double {
        return scoreWithBackoff(context, word) * ln(10.0)
    }
}