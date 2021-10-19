package com.jzhao.pixelartconverter.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jzhao.pixelartconverter.R
import kotlinx.android.synthetic.main.bit_list_item.view.*


class GridBitAdapter (private var context: Context?,var bit: ArrayList<String> ,var w : Int ,var h : Int) : BaseAdapter() {



    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View
        val holder : ViewHolder



        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.bit_list_item, null)
            holder = ViewHolder()
            holder.bit = view.findViewById(R.id.bit_tv)



            view.tag = holder
            /* convertView가 null, 즉 최초로 화면을 실행할 때에
            ViewHolder에 각각의 TextView와 ImageView를 findVidwById로 설정.
            마지막에 태그를 holder로 설정한다. */

        } else {
            holder = convertView.tag as ViewHolder
            view = convertView

            /* 이미 만들어진 View가 있으므로, tag를 통해 불러와서 대체한다. */
        }

        val bb = bit[position]
        holder.bit!!.text = bb
        holder.bit!!.width = w
        holder.bit!!.height = h
        holder.bit!!.setOnClickListener {
            it.setBackgroundColor(Color.RED)
        }
        /* holder와 실제 데이터를 연결한다. null일 수 있으므로 변수에 '?'을 붙여 safe call 한다. */

        return view
        /*
        val temp : String = bit[position]
        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val bit_view : View = inflater.inflate(R.layout.bit_list_item, null)
        bit_view.bit_tv.text = temp
        bit_view.bit_tv.setOnClickListener {
            it.setBackgroundColor(Color.RED)
        }
        return bit_view*/
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return bit.size
    }

    private class ViewHolder {
        var bit : TextView? = null

    }

}