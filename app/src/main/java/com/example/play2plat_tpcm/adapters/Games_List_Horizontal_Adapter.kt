package com.example.play2plat_tpcm.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.play2plat_tpcm.api.Game
import com.example.play2plat_tpcm.databinding.ItemGameHorizontalBinding
import com.squareup.picasso.Picasso

class Games_List_Horizontal_Adapter(
    private var games: List<Game>,
    private val listener: OnGameClickListener
) : RecyclerView.Adapter<Games_List_Horizontal_Adapter.GameCoverViewHolder>() {

    inner class GameCoverViewHolder(private val binding: ItemGameHorizontalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(game: Game, showArrow: Boolean) {
            Picasso.get().load(game.coverImage).into(binding.gameCoverImage2)
            binding.gameCoverImage2.setOnClickListener {
                game.id?.let { id ->
                    listener.onGameClick(id)
                }
            }
            if (showArrow) {
                binding.iconArrowRight.visibility = View.VISIBLE
            } else {
                binding.iconArrowRight.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameCoverViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemGameHorizontalBinding.inflate(inflater, parent, false)
        return GameCoverViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GameCoverViewHolder, position: Int) {
        val showArrow = position == 6 && games.size > 7
        holder.bind(games[position], showArrow)
    }

    override fun getItemCount() = if (games.size > 7) 7 else games.size

    fun updateGames(newGames: List<Game>) {
        games = newGames
        notifyDataSetChanged()
    }

    interface OnGameClickListener {
        fun onGameClick(gameId: Int)
    }
}
