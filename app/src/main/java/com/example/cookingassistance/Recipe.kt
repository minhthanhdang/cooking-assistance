package com.example.cookingassistance

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Recipe(
    var name: String,

    @PrimaryKey(autoGenerate = true)
    val recipeID: Int = 0
)
