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
    private val canSelectMultiple: Boolean = false,
    private val selectedSequenceName: String?
) : ArrayAdapter<Sequence>(context, 0, sequences) {

    private val selectedSequences: MutableList<Sequence> = mutableListOf()

    init {
        // Encontrar a sequência selecionada inicialmente
        sequences.find { it.name == selectedSequenceName }?.let {
            selectedSequences.add(it)
        }
        updateSequenceTitle()
    }

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

        // Remove any existing listener before setting the checked state
        sequenceCheckbox?.setOnCheckedChangeListener(null)

        // Set the checkbox checked state
        sequenceCheckbox?.isChecked = selectedSequences.contains(currentSequence)

        // Add the new listener
        sequenceCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!canSelectMultiple) {
                    clearAllSelections()
                }
                selectedSequences.add(currentSequence!!)
            } else {
                selectedSequences.remove(currentSequence)
            }
            updateSequenceTitle()
            notifyDataSetChanged() // Notify the adapter to refresh the list view
        }

        return listItemView!!
    }

    private fun clearAllSelections() {
        selectedSequences.clear()
    }

    private fun updateSequenceTitle() {
        sequenceTitle.text = if (selectedSequences.isNotEmpty()) {
            selectedSequences.joinToString(", ") { it.name }
        } else {
            "Sequences"
        }
    }

    fun getSelectedSequences(): List<Sequence> {
        return selectedSequences
    }

    fun clearSelection() {
        selectedSequences.clear()
        notifyDataSetChanged()
    }
}

