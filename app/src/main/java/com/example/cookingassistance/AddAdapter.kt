package com.example.cookingassistance

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AddAdapter (private val ingredientData: MutableList<String>, private val amountData: MutableList<String>) : RecyclerView.Adapter<AddAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.add_row, parent, false) as View
        return ViewHolder(view)
    }

    override fun getItemCount() = ingredientData.size

    override fun onBindViewHolder(holder: AddAdapter.ViewHolder, position: Int) {
        val item = listOf(ingredientData[position], amountData[position])
        holder.bind(item)
    }

    inner class ViewHolder(private val v: View): RecyclerView.ViewHolder(v) {
        val ingredientName: EditText = v.findViewById(R.id.ingredient_name)
        val amount: EditText = v.findViewById(R.id.amount)
        val index: TextView = v.findViewById(R.id.index)

        init {
            ingredientName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    ingredientData[adapterPosition] = s.toString()
                }
                override fun afterTextChanged(s: Editable?) {
                }
            })

            amount.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    amountData[adapterPosition] = s.toString()
                }
                override fun afterTextChanged(s: Editable?) {
                }
            })

        }

        fun bind(item: List<String>) {
            ingredientName.setText(item[0])
            amount.setText(item[1])
            index.text = (adapterPosition + 1).toString() + ")"
        }
    }

    public fun getIngredientData(): MutableList<String> {
        return ingredientData
    }

    public fun getAmountData(): MutableList<String> {
        return amountData
    }

}