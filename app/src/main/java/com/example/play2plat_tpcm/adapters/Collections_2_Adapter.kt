package com.example.play2plat_tpcm.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.example.play2plat_tpcm.R

class Collections_2_Adapter(
    context: Context,
    private val values: Array<String>,
    private val collectionTitle: TextView
) : ArrayAdapter<String>(context, 0, values) {

    private var selectedPosition: Int = -1
    private val sharedPreferences = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)

    init {
        val lastCollectionId = sharedPreferences.getInt("last_collection_id", 1)
        val translatedLastCollection = getCollectionNameById(lastCollectionId)
        selectedPosition = values.indexOf(translatedLastCollection)
        collectionTitle.text = translatedLastCollection
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.collections_list_item, parent, false)
        val linearLayout = view.findViewById<LinearLayout>(R.id.collection_item_layout)
        val textView = view.findViewById<TextView>(R.id.collection_name)

        textView.text = values[position]

        val isSelected = position == selectedPosition
        val backgroundColor = if (isSelected) {
            context.getColor(R.color.opaque_WHITE)
        } else {
            context.getColor(android.R.color.transparent)
        }
        linearLayout.setBackgroundColor(backgroundColor)

        view.setOnClickListener {
            if (selectedPosition != position) {
                selectedPosition = position
                collectionTitle.text = values[position]

                // Save the last collection ID
                val collectionId = values.indexOf(values[position])
                sharedPreferences.edit().putInt("last_collection_id", collectionId).apply()

                notifyDataSetChanged()
            }
        }

        return view
    }

    private fun getCollectionNameById(collectionId: Int): String {
        val res: Resources = context.resources
        val collections = res.getStringArray(R.array.collections_names)
        return collections.getOrNull(collectionId) ?: ""
    }

    fun getSelectedPosition(): Int {
        return selectedPosition
    }
}
