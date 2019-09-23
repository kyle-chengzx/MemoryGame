package com.example.memorygame

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList
import android.graphics.Bitmap



class ItemRecyclerAdapter(private val context: Context, private val imageList : ArrayList<DataManager.Image>) : RecyclerView.Adapter<ItemRecyclerAdapter.ViewHolder>() {

    val layoutInflater = LayoutInflater.from(context)
    var allCards = ArrayList<View>()

    private var onCardSelectedLister : OnCardSelectedLister? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.item_card,parent,false)
        allCards.add(itemView)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return DataManager.gameCards.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tag = imageList[position].id
        val imagebmp = imageList[position].bmp
        holder.imageDisplay.tag = tag
        holder.imageBmp = imagebmp
        holder.imageDisplay.setImageBitmap(imagebmp)
    }

    fun setOnSelectedListener(listener: OnCardSelectedLister) {
        onCardSelectedLister = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageDisplay = itemView.findViewById<ImageView>(R.id.imageView)
        var imageBmp : Bitmap? = null

        init {
            itemView.setOnClickListener() {
                onCardSelectedLister?.onCardSelect(itemView)
            }
        }

    }

    interface OnCardSelectedLister {
        fun onCardSelect(itemView : View)
    }


}