package com.example.cookingassistance

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Ingredient(
    var name: String,
    var amount: String,
    var recipeID: Int,
    @PrimaryKey(autoGenerate = true)
    val ingredientID: Int = 0
)
