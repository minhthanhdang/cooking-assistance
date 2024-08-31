package com.example.cookingassistance

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert

@Dao
interface RecipeDAO {
    @Upsert
    fun upsertRecipe(recipe: Recipe)

    @Delete
    fun deleteRecipe(recipe: Recipe)

    @Query("SELECT * FROM recipe WHERE recipeID = :recipeID")
    fun getRecipeAndIngredientsWithRecipeID(recipeID: Int): List<RecipeWithIngredients>

    @Query("SELECT recipeID FROM recipe ORDER BY recipeID desc limit 1")
    fun getLatestRecipeID(): Int

    @Query("SELECT name FROM recipe")
    fun getAllRecipeName(): List<String>

    @Query("SELECT * FROM ingredient WHERE recipeID = :recipeID")
    fun getAllIngredientsByID(recipeID: Int): List<Ingredient>

    @Query("SELECT recipeID FROM recipe WHERE name = :name")
    fun getRecipeIDByName(name: String): Int

    @Query("SELECT * FROM recipe")
    fun getAllRecipe(): List<Recipe>

    @Query("SELECT name FROM recipe WHERE recipeID = :recipeID")
    fun getRecipeNameFromID(recipeID: Int): String

    @Query("SELECT * FROM recipe WHERE recipeID = :recipeID")
    fun getRecipeFromID(recipeID: Int): Recipe
}