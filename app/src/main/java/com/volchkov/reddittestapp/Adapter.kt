package com.volchkov.reddittestapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class Adapter internal constructor(
    context: Context?
) :
    RecyclerView.Adapter<Adapter.ViewHolder>() {
    private var mClickListener: ItemClickListener? = null
    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    // Расширение recyclerview_row из xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.recyclerview_row, parent, false)
        return ViewHolder(view)
    }

    // Связь данных с Views
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {

    }

    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        override fun onClick(view: View?) {
            mClickListener?.onItemClick(view, adapterPosition)
        }
    }

    // Перехват нажатий
    fun setClickListener(itemClickListener: MainActivity) {
        this.mClickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

}

