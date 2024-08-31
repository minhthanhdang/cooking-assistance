package com.example.cookingassistance

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class CardViewAdapter (private val context: Context, private val data: List<Recipe>, private val listener: (Recipe) -> Unit) : RecyclerView.Adapter<CardViewAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.layout_cook_item, parent, false) as View
        return ViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: CardViewAdapter.ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    inner class ViewHolder(private val v: View): RecyclerView.ViewHolder(v) {
        private val food: ImageView = v.findViewById(R.id.cardimg)
        private val recipeName = v.findViewById<TextView>(R.id.recipe_name)

        fun bind(item: Recipe) {
            recipeName.text = item.name
            val recipeID = item.recipeID

            food.setImageBitmap(getPhotoFromInternalStorage("Recipe$recipeID.jpg"))


            v.setOnClickListener{ listener(item) }
        }
    }

    private fun getPhotoFromInternalStorage(filename: String): Bitmap? {
        val file = File(context.filesDir, filename)
        val bytes = file.readBytes()
        val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        return bmp
    }
}

