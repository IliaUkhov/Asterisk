package com.company.model

import com.company.model.interfaces.StressSignedWord

open class Word(
        _spelling: String,
        vararg _stressedLettersIndices: Int): StressSignedWord {

    override val spelling: String = _spelling.toLowerCase()
    override val stressedLettersIndices = _stressedLettersIndices
    val syllableCount = {
        spelling.count { vowels.contains(it) }
    }

    override fun asSyllableSequence(): String {
        var syllableSequence = ""
        var i = 0

        for (char in spelling) {
            if (i + 1 in stressedLettersIndices) {
                syllableSequence += if (stressedLettersIndices.size == 1) '!' else '?'
            } else if (char in vowels) {
                syllableSequence += "."
            }
            i += 1
        }
        return syllableSequence
    }

    override fun toString(): String {
        var wordWithStresses = spelling
        for ((pos, i) in stressedLettersIndices.withIndex()) {
            wordWithStresses = wordWithStresses.substring(0, pos - 1 + i) +
                    '\u0301'+
                    wordWithStresses.substring(pos - 1 + i, wordWithStresses.length)
        }
        return wordWithStresses
    }

    companion object {
        val vowels = charArrayOf('а', 'о', 'у',
                'и', 'е', 'ё', 'ы', 'э', 'ю', 'я')

        @JvmStatic fun String.syllableCount(): Int {
            return this.count { vowels.contains(it) }
        }

        @JvmStatic fun String.hasVowels(): Boolean {
            return this.count { vowels.contains(it) } > 0
        }

        @JvmStatic fun asSyllableSequenceWithKnownStress(word: String): String {
            var syllableSequence = ""
            val symbolToRepresentStress = if (word.count { it == 'ё' } > 1) '?' else '!'

            for (char in word) {
                when (char) {
                    'ё' -> syllableSequence += symbolToRepresentStress
                    in vowels -> syllableSequence += '.'
                }
            }
            return syllableSequence
        }
    }
}