package com.apextech.bexatobotuz.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.apextech.bexatobotuz.data.remote.response.WordResponse

@Entity(tableName = "favourites")
data class FavouriteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val latin: String,
    val cyrill: String,
)
