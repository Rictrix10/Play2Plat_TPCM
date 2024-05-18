package com.example.play2plat_tpcm.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.example.play2plat_tpcm.R

class PegyAdapter(context: Context, private val values: Array<String>, private val pegiTitle: TextView) :
    ArrayAdapter<String>(context, 0, values) {

    private var selectedPosition: Int = -1

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.pegy_list_item, parent, false)
        val textView = view.findViewById<TextView>(R.id.pegy_name)
        val checkBox = view.findViewById<CheckBox>(R.id.pegy_checkbox)

        textView.text = values[position]
        checkBox.isChecked = position == selectedPosition

        // Definir um ouvinte de clique na checkbox
        checkBox.setOnClickListener {
            if (checkBox.isChecked) {
                // Quando a checkbox é marcada, atualize o título do Pegi
                selectedPosition = position
                pegiTitle.text = "Pegi: ${values[position]}"
                notifyDataSetChanged()
            } else {
                // Quando a checkbox é desmarcada, redefina o título do Pegi Info
                selectedPosition = -1
                pegiTitle.text = "Pegi Information"
                notifyDataSetChanged()
            }
        }

        return view
    }

    fun getSelectedPosition(): Int {
        return selectedPosition
    }
}

