package com.example.cookingassistance

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ShowStepsAdapter (private val stepData: MutableList<Step>) : RecyclerView.Adapter<ShowStepsAdapter.ViewHolder>(){
    var deletedSteps = mutableListOf<Step>()
    var editing = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowStepsAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.show_step, parent, false) as View
        return ViewHolder(view)
    }

    override fun getItemCount() = stepData.size

    override fun onBindViewHolder(holder: ShowStepsAdapter.ViewHolder, position: Int) {
        val item = stepData[position]
        holder.bind(item)
    }

    inner class ViewHolder(private val v: View): RecyclerView.ViewHolder(v) {
        val stepContent: EditText = v.findViewById(R.id.step_edit)
        val stepOrder: TextView = v.findViewById(R.id.step_order)
        val deleteStep = v.findViewById<Button>(R.id.delete_step)

        init {
            stepContent.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    stepData[adapterPosition].desc = s.toString()
                }
                override fun afterTextChanged(s: Editable?) {
                }
            })

            deleteStep.setOnClickListener {
                deletedSteps.add(stepData[adapterPosition])
                stepData.removeAt(adapterPosition)
                notifyDataSetChanged()
            }

            stepContent.isEnabled = false

        }

        fun bind(item: Step) {
            stepContent.setText(item.desc)
            stepOrder.text = "${(adapterPosition + 1)})"

            if (editing) {
                stepContent.isEnabled = true
                deleteStep.visibility = View.VISIBLE
            } else {
                stepContent.isEnabled = false
                deleteStep.visibility = View.GONE
            }
        }
    }

    public fun getStepData(): MutableList<Step> {
        return stepData
    }

    public fun getDeletedStep(): MutableList<Step> {
        return deletedSteps
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