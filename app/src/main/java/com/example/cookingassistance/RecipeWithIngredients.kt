package com.example.cookingassistance

import androidx.room.Embedded
import androidx.room.Relation

data class RecipeWithIngredients(
    @Embedded var recipe: Recipe,
    @Relation(
        parentColumn = "recipeID",
        entityColumn = "recipeID"
    )
    val ingredients: List<Ingredient>
)
