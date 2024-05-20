package com.example.play2plat_tpcm.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.example.play2plat_tpcm.R
import com.example.play2plat_tpcm.api.Platforms

class PlatformsAdapter(context: Context, platforms: List<Platforms>, private val platformTitle: TextView) :
    ArrayAdapter<Platforms>(context, 0, platforms) {

    private val selectedPlatformPositions = mutableSetOf<Int>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(
                R.layout.platform_list_item,
                parent,
                false
            )
        }

        val currentPlatform = getItem(position)

        val platformNameTextView = listItemView?.findViewById<TextView>(R.id.platform_name)
        platformNameTextView?.text = currentPlatform?.name

        val platformCheckbox = listItemView?.findViewById<CheckBox>(R.id.platform_checkbox)
        platformCheckbox?.setOnCheckedChangeListener(null) // Remove listener before setting state
        platformCheckbox?.isChecked = selectedPlatformPositions.contains(position)
        platformCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedPlatformPositions.add(position)
            } else {
                selectedPlatformPositions.remove(position)
            }
            notifyDataSetChanged() // Notify the adapter to update the views
            updatePlatformTitle()
        }

        return listItemView!!
    }

    private fun updatePlatformTitle() {
        val selectedPlatforms = selectedPlatformPositions.mapNotNull { position ->
            getItem(position)?.name
        }
        if (selectedPlatforms.isEmpty()) {
            platformTitle.text = "Platforms"
        } else {
            platformTitle.text = selectedPlatforms.joinToString(", ")
        }
    }
}
