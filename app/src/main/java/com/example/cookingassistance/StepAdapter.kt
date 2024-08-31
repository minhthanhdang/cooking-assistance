package com.example.cookingassistance

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StepAdapter (private val stepData: MutableList<String>) : RecyclerView.Adapter<StepAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.add_step, parent, false) as View
        return ViewHolder(view)
    }

    override fun getItemCount() = stepData.size

    override fun onBindViewHolder(holder: StepAdapter.ViewHolder, position: Int) {
        val item = stepData[position]
        holder.bind(item)
    }

    inner class ViewHolder(private val v: View): RecyclerView.ViewHolder(v) {
        val stepEdit: EditText = v.findViewById(R.id.step_edit)
        val stepOrder: TextView = v.findViewById(R.id.step_order)

        init {
            stepEdit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    stepData[adapterPosition] = s.toString()
                }
                override fun afterTextChanged(s: Editable?) {
                }
            })
        }

        fun bind(item: String) {
            stepEdit.setText(item)
            stepOrder.text = (adapterPosition + 1).toString() + ")"
        }
    }

    public fun getStep(): MutableList<String> {
        return stepData
    }

}