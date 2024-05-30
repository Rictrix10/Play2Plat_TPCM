package com.example.play2plat_tpcm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.play2plat_tpcm.api.Collections
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class GamesAdapter(
    private val games: List<Collections>,
    private val onGamePictureClickListener: OnGamePictureClickListener
) : RecyclerView.Adapter<GamesAdapter.GamesViewHolder>() {

    interface OnGamePictureClickListener {
        fun onGamePictureClick(gameId: Int)
    }

    class GamesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageGame: ImageView = itemView.findViewById(R.id.gameImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_game_collection, parent, false)
        return GamesViewHolder(view)
    }

    override fun onBindViewHolder(holder: GamesViewHolder, position: Int) {
        val game = games[position]
        Picasso.get().load(game.coverImage).into(holder.imageGame)

        holder.imageGame.setOnClickListener {
            onGamePictureClickListener.onGamePictureClick(game.id)
        }
    }

    override fun getItemCount(): Int {
        return games.size
    }
}
