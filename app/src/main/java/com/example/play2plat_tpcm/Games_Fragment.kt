package com.example.play2plat_tpcm

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.Collections
import com.example.play2plat_tpcm.api.GameCommentsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Games_Fragment : Fragment(), GamesAdapter.OnGamePictureClickListener {


    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_games_, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        //gameImageView = view.findViewById(R.id.gameImageView)
        getStateCollection()

        return view
    }


    private fun getStateCollection() {
        val state = "Paused"
        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", 0)
        ApiManager.apiService.getStateCollection(userId, state).enqueue(object :
            Callback<List<Collections>> {
            override fun onResponse(
                call: Call<List<Collections>>,
                response: Response<List<Collections>>
            ) {
                if (response.isSuccessful) {
                    val games = response.body()
                    Log.d("GamePostsFragment", "Resposta da API: $games")
                    if (games != null) {
                        recyclerView.adapter = GamesAdapter(games, this@Games_Fragment)
                    }
                } else {
                    Log.e("GamePostsFragment", "Erro na resposta: ${response.errorBody()}")
                    // Tratar erro
                }
            }

            override fun onFailure(call: Call<List<Collections>>, t: Throwable) {
                Log.e("Games_Fragment", "Falha na chamada da API: ${t.message}")
                // Tratar falha
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
