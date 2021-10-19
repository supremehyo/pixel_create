package com.jzhao.pixelartconverter

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GridBitHolder (view: View) : RecyclerView.ViewHolder(view) {
    val bit_number_tv: TextView = itemView.findViewById(R.id.bit_number_tv)
}