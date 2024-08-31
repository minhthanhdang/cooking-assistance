package com.example.cookingassistance

import androidx.room.Embedded
import androidx.room.Relation

data class RecipeWithSteps(
    @Embedded var recipe: Recipe,
    @Relation(
        parentColumn = "recipeID",
        entityColumn = "recipeID"
    )
    val steps: List<Step>
)
