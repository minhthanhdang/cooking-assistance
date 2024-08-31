package com.example.cookingassistance

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDAO {
    @Upsert
    fun upsertIngredient(ingredient: Ingredient)

    @Delete
    fun deleteIngredient(ingredient: Ingredient)

    @Query("SELECT * FROM ingredient WHERE recipeID = :recipeID")
    fun getIngredientFromRecipe(recipeID: Int): List<Ingredient>

    @Query("SELECT * FROM ingredient WHERE ingredientID = :ingredientID")
    fun getIngredientIfIDExist(ingredientID: Int): Ingredient?
}