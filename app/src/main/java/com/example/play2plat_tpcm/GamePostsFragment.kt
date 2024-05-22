package com.example.play2plat_tpcm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.play2plat_tpcm.api.GamePost

class GamePostsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_game_posts, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = GamePostsAdapter(getGamePosts())

        return view
    }

    private fun getGamePosts(): List<GamePost> {
        // Esta função deve retornar a lista de posts. Aqui estamos adicionando exemplos fictícios.
        return listOf(
            GamePost(R.drawable.noimageuser, "User1", "This is a post text", R.drawable.email),
            GamePost(R.drawable.noimageuser, "User2", "Another post text", R.drawable.facebook),
            GamePost(R.drawable.noimageuser, "User3", "This is a post text 2", R.drawable.email),
            GamePost(R.drawable.noimageuser, "User4", "Another post text 2", R.drawable.facebook),
            GamePost(R.drawable.noimageuser, "User5", "This is a post text 3", R.drawable.email),
            GamePost(R.drawable.noimageuser, "User6", "Another post text 3", R.drawable.facebook)
        )
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GamePostsFragment().apply {
            }
    }
}
