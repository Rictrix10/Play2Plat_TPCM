package com.example.play2plat_tpcm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.play2plat_tpcm.api.Game

class FreeGamesAdapter(
    private val freeGames: List<Game>
) : RecyclerView.Adapter<FreeGamesAdapter.FreeGameViewHolder>() {

    class FreeGameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gameName: TextView = itemView.findViewById(R.id.free_game_name)  // Correção aqui
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FreeGameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_free_game, parent, false)
        return FreeGameViewHolder(view)
    }

    override fun onBindViewHolder(holder: FreeGameViewHolder, position: Int) {
        val game = freeGames[position]
        holder.gameName.text = game.name
    }

    override fun getItemCount(): Int {
        return freeGames.size
    }
}
