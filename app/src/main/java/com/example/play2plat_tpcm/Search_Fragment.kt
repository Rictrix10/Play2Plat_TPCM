package com.example.play2plat_tpcm

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.Game
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Search_Fragment : Fragment(), FreeGamesAdapter.OnGamePictureClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var freeGamesAdapter: FreeGamesAdapter
    private var freeGamesList: MutableList<Game> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search_, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_free_games)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        freeGamesAdapter = FreeGamesAdapter(freeGamesList, this)
        recyclerView.adapter = freeGamesAdapter

        loadFreeGames()

        return view
    }

    private fun loadFreeGames() {
        ApiManager.apiService.getAllGames().enqueue(object : Callback<List<Game>> {
            override fun onResponse(call: Call<List<Game>>, response: Response<List<Game>>) {
                if (response.isSuccessful) {
                    val games = response.body()
                    if (games != null) {
                        freeGamesList.clear()
                        freeGamesList.addAll(games.filter { it.isFree })
                        freeGamesAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<List<Game>>, t: Throwable) {

            }
        })
    }

    private fun redirectToViewGame(gameId: Int) {
        val platforms = arrayListOf<String>()

        val viewGameFragment = View_Game_Fragment.newInstance(gameId, platforms)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.layout, viewGameFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onGamePictureClick(gameId: Int) {

        redirectToViewGame(gameId)
    }
}


