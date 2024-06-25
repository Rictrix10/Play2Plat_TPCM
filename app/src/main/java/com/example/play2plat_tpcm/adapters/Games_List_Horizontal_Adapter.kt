package com.example.play2plat_tpcm.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.play2plat_tpcm.R
import com.example.play2plat_tpcm.api.Game
import com.example.play2plat_tpcm.databinding.ItemGameHorizontalBinding
import com.squareup.picasso.Picasso

class Games_List_Horizontal_Adapter(
    private var games: List<Game>,
    private val listener: OnGameClickListener,
    private val filterType: String?
) : RecyclerView.Adapter<Games_List_Horizontal_Adapter.GameCoverViewHolder>() {

    private lateinit var context: Context
    private var isFirstItem = true

    inner class GameCoverViewHolder(val binding: ItemGameHorizontalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(game: Game) {
            Picasso.get().load(game.coverImage).into(binding.gameCoverImage2)
            binding.gameCoverImage2.setOnClickListener {
                game.id?.let { id ->
                    listener.onGameClick(id)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameCoverViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val binding = ItemGameHorizontalBinding.inflate(inflater, parent, false)
        return GameCoverViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GameCoverViewHolder, position: Int) {
        val currentFilterType = filterType
        if (position < games.size) {
            holder.bind(games[position])
            holder.binding.moreGamesView!!.visibility = View.GONE // Hide 'more_games_view'
        } else if (position == games.size) {
            holder.binding.moreGamesView!!.visibility = View.VISIBLE
            holder.binding.moreGamesText!!.text = currentFilterType
        }

        // Apply left margin of 8dp only to the first item
        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        if (isFirstItem) {
            layoutParams.leftMargin = context.resources.getDimensionPixelSize(R.dimen.margin_horizontal_first_item)
            isFirstItem = false
        } else {
            layoutParams.leftMargin = 0
        }
        holder.itemView.layoutParams = layoutParams
    }

    //override fun getItemCount() = if (games.size > 12) 12 else games.size
    override fun getItemCount() = if (games.size > 12) 12 else games.size + if (games.size <= 3) 1 else 0
    fun updateGames(newGames: List<Game>) {
        games = newGames
        notifyDataSetChanged()
    }

    interface OnGameClickListener {
        fun onGameClick(gameId: Int)

    }
}
