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

class SequenceAdapter(
    context: Context,
    sequences: List<Sequence>,
    private val sequenceTitle: TextView,
    private val canSelectMultiple: Boolean = false
) : ArrayAdapter<Sequence>(context, 0, sequences) {

    private val selectedSequences: MutableList<Sequence> = mutableListOf()

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

        sequenceCheckbox?.isChecked = selectedSequences.contains(currentSequence)

        sequenceCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!canSelectMultiple) {
                    selectedSequences.clear()
                }
                selectedSequences.add(currentSequence!!)
            } else {
                selectedSequences.remove(currentSequence)
            }
            updateSequenceTitle()
        }

        updateSequenceTitle()

        return listItemView!!
    }

    private fun updateSequenceTitle() {
        sequenceTitle.text = if (selectedSequences.isNotEmpty()) {
            selectedSequences.joinToString(", ") { it.name }
        } else {
            "Sequences"
        }
    }
}
