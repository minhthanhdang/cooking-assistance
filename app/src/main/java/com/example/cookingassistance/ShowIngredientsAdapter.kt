package com.example.cookingassistance

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ShowIngredientsAdapter (private val ingredientData: MutableList<Ingredient>) : RecyclerView.Adapter<ShowIngredientsAdapter.ViewHolder>(){
    var deletedIngredient: MutableList<Ingredient> = mutableListOf()
    var editing = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowIngredientsAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.edit_ingredient, parent, false) as View
        return ViewHolder(view)
    }

    override fun getItemCount() = ingredientData.size

    override fun onBindViewHolder(holder: ShowIngredientsAdapter.ViewHolder, position: Int) {
        val item = ingredientData[position]
        holder.bind(item)
    }

    inner class ViewHolder(private val v: View): RecyclerView.ViewHolder(v) {
        val ingredientName: EditText = v.findViewById(R.id.ingredient_name)
        val amount: EditText = v.findViewById(R.id.amount)
        val deleteIngredient = v.findViewById<Button>(R.id.delete_ingredient)
        val index = v.findViewById<TextView>(R.id.index)

        init {
            ingredientName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    ingredientData[adapterPosition].name = s.toString()
                }
                override fun afterTextChanged(s: Editable?) {
                }
            })

            amount.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    ingredientData[adapterPosition].amount = s.toString()
                }
                override fun afterTextChanged(s: Editable?) {
                }
            })

            deleteIngredient.setOnClickListener {
                deletedIngredient.add(ingredientData[adapterPosition])
                ingredientData.removeAt(adapterPosition)
                notifyDataSetChanged()
            }

            ingredientName.isEnabled = false
            amount.isEnabled = false
        }

        fun bind(item: Ingredient) {
            ingredientName.setText(item.name)
            amount.setText(item.amount)
            index.text = (adapterPosition + 1).toString() + ")"
            if (editing) {
                ingredientName.isEnabled = true
                amount.isEnabled = true
                deleteIngredient.visibility = VISIBLE
            } else {
                ingredientName.isEnabled = false
                amount.isEnabled = false
                deleteIngredient.visibility = GONE
            }
        }
    }

    public fun getIngredientData(): MutableList<Ingredient> {
        return ingredientData
    }

    public fun getDeletedIngredients(): MutableList<Ingredient> {
        return deletedIngredient
    }

    public fun startEditing() {
        editing = true
        notifyDataSetChanged()
    }

    public fun stopEditing() {
        editing = false
        notifyDataSetChanged()
    }

}