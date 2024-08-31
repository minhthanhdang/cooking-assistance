package com.example.cookingassistance

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Recipe::class, Ingredient::class, Step::class],
    version = 3
)
abstract class RecipeDatabase: RoomDatabase() {
    abstract fun recipeDAO(): RecipeDAO
    abstract fun ingredientDAO(): IngredientDAO
    abstract fun stepDAO(): StepDAO
}