package com.company.model

import com.company.model.interfaces.DictionaryOperator
import com.company.model.interfaces.StressSignedWord
import java.io.*
import java.util.*

class Dictionary: DictionaryOperator {

    private var dictStructure: TreeMap<String, IntArray>

    init {
        val fIn = FileInputStream("dictionary")
        var oIn: ObjectInputStream
        try {
            oIn = ObjectInputStream(fIn)
        } catch (e: Exception) {
            dictStructure = TreeMap()
            save()
            oIn = ObjectInputStream(fIn)
        }
        @Suppress("UNCHECKED_CAST")
        dictStructure = oIn.readObject() as TreeMap<String, IntArray>
        oIn.close()
        fIn.close()
    }

    override fun save() {
        val fOut = FileOutputStream("dictionary")
        val oOut = ObjectOutputStream(fOut)
        oOut.writeObject(dictStructure)
        oOut.close()
        fOut.close()
    }

    override fun write(word: StressSignedWord): Boolean {
        if (dictStructure.containsKey(word.spelling)) {
            return false
        } else {
            dictStructure[word.spelling] = word.stressedLettersIndices
        }
        return true
    }

    override fun remove(word: String): Boolean {
        return dictStructure.remove(word.replace("\u0301", "").split(' ')[0]) != null
    }

    override fun getAll(omitStresses: Boolean): Set<String> {
        val newSet = TreeSet<String>()
        if (omitStresses) {
            dictStructure.forEach { newSet.add("${it.key} ${it.value.printElements()}") }
        } else {
            dictStructure.forEach { newSet.add(Word(it.key, *it.value).toString()) }
        }
        return newSet
    }

    override fun lookFor(word: String): StressSignedWord? {
        val stresses = dictStructure[word.toLowerCase()]

        return when (stresses) {
            is IntArray -> Word(word.toLowerCase(), *stresses)
            else -> null
        }
    }

    companion object {
        fun IntArray.printElements(): String {
            val builder: StringBuilder = StringBuilder();
            this.forEach { builder.append("$it ") }
            builder.trimEnd(' ')
            return builder.toString()
        }
    }
}