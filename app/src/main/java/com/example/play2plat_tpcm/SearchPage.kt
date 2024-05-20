package com.example.play2plat_tpcm

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView

class SearchPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchView: SearchView = findViewById(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Lógica quando a busca é submetida
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Lógica quando o texto da busca muda
                return false
            }
        })

        // Personalização do campo de texto do SearchView
        val searchEditText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setHintTextColor(resources.getColor(android.R.color.darker_gray))
        searchEditText.setTextColor(resources.getColor(android.R.color.black))
    }
}