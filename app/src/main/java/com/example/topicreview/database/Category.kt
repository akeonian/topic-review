package com.example.topicreview.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    @ColumnInfo(name = "parent_id") val parentId: Int = -1
)