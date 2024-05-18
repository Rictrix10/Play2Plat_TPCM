package com.example.play2plat_tpcm.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.example.play2plat_tpcm.R
import com.example.play2plat_tpcm.api.Sequence

class SequenceAdapter(context: Context, sequences: List<Sequence>, private val sequenceTitle: TextView) :
    ArrayAdapter<Sequence>(context, 0, sequences) {

    private var selectedSequencePosition: Int = -1

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(
                R.layout.sequence_list_item,
                parent,
                false
            )
        }

        val currentSequence = getItem(position)

        val sequenceNameTextView = listItemView?.findViewById<TextView>(R.id.sequence_name)
        sequenceNameTextView?.text = currentSequence?.name

        val sequenceCheckbox = listItemView?.findViewById<CheckBox>(R.id.sequence_checkbox)
        sequenceCheckbox?.setOnCheckedChangeListener(null) // Remove listener before setting state
        sequenceCheckbox?.isChecked = position == selectedSequencePosition
        sequenceCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedSequencePosition = position
                notifyDataSetChanged() // Notify the adapter of the change to update the views
                sequenceTitle.text = currentSequence?.name
            } else {
                selectedSequencePosition = -1
                notifyDataSetChanged() // Notify the adapter of the change to update the views
                sequenceTitle.text = "Sequences"
            }
        }

        if (selectedSequencePosition == -1) {
            sequenceTitle.text = "Sequences"
        }

        return listItemView!!
    }
}


