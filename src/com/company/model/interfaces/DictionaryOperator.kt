package com.company.model.interfaces

interface DictionaryOperator {
    fun write(word: StressSignedWord): Boolean
    fun remove(word: String): Boolean
    fun save()
    fun lookFor(word: String): StressSignedWord?
    fun getAll(omitStresses: Boolean): Set<String>
}