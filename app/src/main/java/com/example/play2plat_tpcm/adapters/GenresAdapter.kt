package com.example.play2plat_tpcm.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.example.play2plat_tpcm.R
import com.example.play2plat_tpcm.api.Genre

class GenresAdapter(context: Context, genres: List<Genre>, private val genreTitle: TextView) :
    ArrayAdapter<Genre>(context, 0, genres) {

    private val selectedGenrePositions = mutableSetOf<Int>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(
                R.layout.genre_list_item,
                parent,
                false
            )
        }

        val currentGenre = getItem(position)

        val genreNameTextView = listItemView?.findViewById<TextView>(R.id.genre_name)
        genreNameTextView?.text = currentGenre?.name

        val genreCheckbox = listItemView?.findViewById<CheckBox>(R.id.genre_checkbox)
        genreCheckbox?.setOnCheckedChangeListener(null) // Remove listener before setting state
        genreCheckbox?.isChecked = selectedGenrePositions.contains(position)
        genreCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedGenrePositions.add(position)
            } else {
                selectedGenrePositions.remove(position)
            }
            notifyDataSetChanged() // Notify the adapter to update the views
            updateGenreTitle()
        }

        return listItemView!!
    }

    private fun updateGenreTitle() {
        val selectedGenres = selectedGenrePositions.mapNotNull { position ->
            getItem(position)?.name
        }
        if (selectedGenres.isEmpty()) {
            genreTitle.text = "Genres"
        } else {
            genreTitle.text = selectedGenres.joinToString(", ")
        }
    }

    fun clearSelection() {
        selectedGenrePositions.clear()
        notifyDataSetChanged()
    }

}
