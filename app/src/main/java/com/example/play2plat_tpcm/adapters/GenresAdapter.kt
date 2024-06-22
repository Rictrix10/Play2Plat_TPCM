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

class GenresAdapter(
    context: Context,
    genres: List<Genre>,
    private val genreTitle: TextView,
    private val selectedGenres: List<String> = emptyList() // Lista opcional de nomes de gêneros selecionados
) : ArrayAdapter<Genre>(context, 0, genres) {

    private val selectedGenrePositions = mutableSetOf<Int>()

    init {
        // Marcar as posições dos gêneros que já estão selecionados
        selectedGenres.forEachIndexed { index, genreName ->
            genres.indexOfFirst { it.name == genreName }.takeIf { it != -1 }?.let {
                selectedGenrePositions.add(it)
            }
        }
        updateGenreTitle()
    }

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
        genreCheckbox?.setOnCheckedChangeListener(null) // Remover o listener antes de definir o estado
        genreCheckbox?.isChecked = selectedGenrePositions.contains(position)
        genreCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedGenrePositions.add(position)
            } else {
                selectedGenrePositions.remove(position)
            }
            notifyDataSetChanged()
            updateGenreTitle()
        }

        return listItemView!!
    }

    private fun updateGenreTitle() {
        val selectedGenresNames = selectedGenrePositions.mapNotNull { position ->
            getItem(position)?.name
        }

        // Use `context.getString` para pegar a string traduzida
        if (selectedGenresNames.isEmpty()) {
            genreTitle.text = context.getString(R.string.genres)  // Pega a tradução do arquivo de strings
        } else {
            genreTitle.text = selectedGenresNames.joinToString(", ")
        }
    }

    fun clearSelection() {
        selectedGenrePositions.clear()
        notifyDataSetChanged()
    }
}



