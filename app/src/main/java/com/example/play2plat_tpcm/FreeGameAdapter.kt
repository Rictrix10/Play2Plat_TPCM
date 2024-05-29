package com.example.play2plat_tpcm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.play2plat_tpcm.api.Game
import com.squareup.picasso.Picasso

class FreeGamesAdapter(
    private val freeGames: List<Game>,
    private val onGamePictureClickListener: OnGamePictureClickListener
) : RecyclerView.Adapter<FreeGamesAdapter.FreeGameViewHolder>() {

    interface OnGamePictureClickListener {
        fun onGamePictureClick(gameId: Int)
    }

    class FreeGameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gameCoverImage: ImageView = itemView.findViewById(R.id.game_cover_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FreeGameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_free_game, parent, false)
        return FreeGameViewHolder(view)
    }

    override fun onBindViewHolder(holder: FreeGameViewHolder, position: Int) {
        val game = freeGames[position]
        Picasso.get().load(game.coverImage).into(holder.gameCoverImage)

        holder.gameCoverImage.setOnClickListener {
            game.id?.let { gameId ->
                onGamePictureClickListener.onGamePictureClick(gameId)
            }
        }
    }

    override fun getItemCount(): Int {
        return freeGames.size
    }
}



