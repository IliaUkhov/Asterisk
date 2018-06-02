package com.company.model.interfaces

interface PoemFootMatcher {
    fun analyzePoem(dict: DictionaryOperator, poem: String, strictness: Double = 0.5): String
}