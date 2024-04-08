package com.apextech.bexatobotuz.viewModel

import com.apextech.bexatobotuz.data.local.entity.HistoryEntity

interface TranslateViewModel {
    suspend fun fetchCyrills()
    suspend fun fetchLatins()
    fun translate(text: String, isLatin: Boolean? = null)
    fun translateHistory(historyEntity: HistoryEntity)
    fun replaceTranslator()
    fun addFavourite()
    fun translateWord(word: String): String

}