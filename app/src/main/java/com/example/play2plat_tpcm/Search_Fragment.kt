package com.example.play2plat_tpcm

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.Collections
import com.example.play2plat_tpcm.api.Game
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Search_Fragment : Fragment(), GamesAdapter.OnGamePictureClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var GamesAdapter: GamesAdapter
    private lateinit var fragmentContainer: FrameLayout
    //private lateinit var searchView: SearchView
    private lateinit var searchButton: Button
    private var GamesList: MutableList<Collections> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search_, container, false)

        GamesAdapter = GamesAdapter(GamesList, this)
        fragmentContainer = view.findViewById(R.id.fragment_container)
        //searchView = view.findViewById(R.id.search_view) // Assuming you have a SearchView with this ID
        searchButton = view.findViewById(R.id.search)

        val textViewHeight = 50.dpToPx()

        // Obtém a altura da tela
        val displayMetrics = resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels

        // Calcula a altura disponível para o FrameLayout
        val availableHeight = screenHeight - textViewHeight

        // Define a altura do FrameLayout
        val layoutParams = fragmentContainer.layoutParams
        layoutParams.height = availableHeight
        fragmentContainer.layoutParams = layoutParams

        val fragment = Games_List_Horizontal_Fragment.newInstance("Genres", "Action")
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        //searchView.isEnabled = false

        // Set up SearchView click listener
        searchButton.setOnClickListener {
            redirectToGamesSearched()
        }

        return view
    }

    private fun seachGamebyName(filterType: String, paramater: String) {
        val fragment = Games_List_Grid_Fragment.newInstance(filterType, paramater)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun redirectToViewGame(gameId: Int) {
        val platforms = arrayListOf<String>()

        val viewGameFragment = View_Game_Fragment.newInstance(gameId, platforms)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.layout, viewGameFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun Int.dpToPx(): Int {
        val scale = resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }

    override fun onGamePictureClick(gameId: Int) {
        redirectToViewGame(gameId)
    }

    private fun redirectToGamesSearched() {
        val gamesSearchedFragment = GamesSearched_Fragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.layout, gamesSearchedFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}


