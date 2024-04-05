package com.apextech.bexatobotuz.data.remote.response

import com.apextech.bexatobotuz.data.local.entity.CyrillEntity
import com.apextech.bexatobotuz.data.local.entity.LatinEntity

data class WordResponse(
    val id: Int,
    val letterLatin: String,
    val letterCyrill: String,
) {
    fun toLatinEntity(): LatinEntity {
        return LatinEntity(wordId = this.id, latin = this.letterLatin, cyrill = this.letterCyrill)
    }

    fun toCyrillEntity(): CyrillEntity {
        return CyrillEntity(wordId = this.id, latin = this.letterLatin, cyrill = this.letterCyrill)
    }
}
