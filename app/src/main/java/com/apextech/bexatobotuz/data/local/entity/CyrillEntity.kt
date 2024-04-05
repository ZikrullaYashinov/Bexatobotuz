package com.apextech.bexatobotuz.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.apextech.bexatobotuz.data.remote.response.WordResponse

@Entity(tableName = "cyrills")
data class CyrillEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val wordId: Int,
    val latin: String,
    val cyrill: String,
) {
    fun toWordResponse(): WordResponse {
        return WordResponse(wordId, latin, cyrill)
    }
}
