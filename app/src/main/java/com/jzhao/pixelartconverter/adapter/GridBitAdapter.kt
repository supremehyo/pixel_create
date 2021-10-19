package com.jzhao.pixelartconverter.adapter

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.jzhao.pixelartconverter.ColorImageHolder
import com.jzhao.pixelartconverter.GridBitHolder
import com.jzhao.pixelartconverter.R

class GridBitAdapter (var bit: ArrayList<String>) : RecyclerView.Adapter<GridBitHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridBitHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bit_list_item, parent, false)

        return GridBitHolder(view)
    }

    override fun getItemCount(): Int = bit.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: GridBitHolder, position: Int) {
        var bit_number = bit[position]
        holder.apply {
            bit_number_tv.text = bit_number
        }
    }

}