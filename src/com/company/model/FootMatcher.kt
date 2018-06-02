package com.company.model

import com.company.model.Word.Companion.syllableCount
import com.company.model.Word.Companion.hasVowels
import com.company.model.Word.Companion.asSyllableSequenceWithKnownStress
import com.company.model.interfaces.DictionaryOperator
import com.company.model.interfaces.PoemFootMatcher

class FootMatcher: PoemFootMatcher {

    companion object {
        private val footDict = hashMapOf(
                "Trochee" to "!.",
                "Iamb" to ".!",
                "Dactyl" to "!..",
                "Amphibrachium" to ".!.",
                "Anapaest" to "..!",
                "Brachycolon" to "!")
        private val splitDelimiters = charArrayOf(' ', '.', ',', '!', '?', ':', ';', '-')
    }

    override fun analyzePoem(dict: DictionaryOperator, poem: String, strictness: Double): String {
        if (!poem.hasVowels()) {
            return ""
        }
        val rows = poem.split(Regex("\\r\\n|[\\r\\n]")).filter { it.isNotEmpty() }
        val matchValues = hashMapOf<String, Double>()
        for ((footName, footSequence) in footDict) {
            var matchesSequence = ""
            for (row in rows) {
                val rowSyllableSequence = toSyllableSequence(dict, row)
                if (rowSyllableSequence.contains("<")) {
                    return "Unknown word: " + rowSyllableSequence
                            .replaceAfter(">", "")
                            .replaceBefore("<", "")
                            .replace("<", "")
                            .replace(">", "")
                }
                matchesSequence += matchSequence(rowSyllableSequence, footSequence)
                //println("$footName  $matchesSequence")
            }
            matchValues[footName] = matchesSequence.count { it == '+' }.toDouble() /
                    matchesSequence.count().toDouble()
        }
        return if (matchValues.values.max()!! < strictness) "Unknown"
        else matchValues.maxBy { it.value }!!.key
    }

    private fun matchSequence(syllableSequence: String, footSequence: String): String {
        //println(syllableSequence)
        var matchesSequence = ""
        var i = 0
        while (i < syllableSequence.length) {
            var match = true
            loop@for ((a, footSyllable) in footSequence.withIndex()) {
                if (i + footSequence.length >= syllableSequence.length) {
                    match = false; break@loop
                }
                when (syllableSequence[i + a]) {
                    '!' -> if (footSyllable != '!') { match = false; break@loop }
                    '.' -> if (footSyllable != '.') { match = false; break@loop }
                }
            }
            if (match) {
                i += footSequence.length
                matchesSequence += "+"
            } else {
                matchesSequence += "-"
                i += 1
            }
        }
        return matchesSequence
    }

    private fun toSyllableSequence(dict: DictionaryOperator, poem: String): String {
        val words = poem
                .toLowerCase()
                .replace(Regex("\\r\\n|[\\r\\n]"), ".")
                .split(*splitDelimiters).filter { it.hasVowels() && it.isNotEmpty() }
        var poemSyllableSequence = ""

        words.forEach {
            val s = wordToSyllableSequence(it, dict)
            poemSyllableSequence += s
        }
        return poemSyllableSequence
    }

    private fun wordToSyllableSequence(word: String, dict: DictionaryOperator): String {
         return if (word.syllableCount() == 1) "?"
                else if (word.contains('ё')) asSyllableSequenceWithKnownStress(word)
                else dict.lookFor(word)?.asSyllableSequence() ?: "<$word>"
    }
}