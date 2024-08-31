package com.example.cookingassistance

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Step(
    var desc: String,
    var stepIndex: Int,
    var recipeID: Int,
    @PrimaryKey(autoGenerate = true)
    val stepID: Int = 0
)
