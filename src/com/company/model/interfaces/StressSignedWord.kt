package com.company.model.interfaces

interface StressSignedWord {
    val spelling: String
    val stressedLettersIndices: IntArray
    fun asSyllableSequence(): String
}