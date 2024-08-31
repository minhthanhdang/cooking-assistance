package com.example.cookingassistance

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface StepDAO {
    @Upsert
    fun upsertStep(step: Step)

    @Delete
    fun deleteStep(step: Step)

    @Query("SELECT * FROM step WHERE recipeID = :recipeID ORDER BY stepIndex")
    fun getStepFromRecipeID(recipeID: Int): List<Step>

    @Query("SELECT * FROM step WHERE stepID = :stepID")
    fun getStepIfIDExist(stepID: Int): Step?
}