package com.example.play2plat_tpcm.adapters

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.play2plat_tpcm.R
import com.example.play2plat_tpcm.api.Game
import com.example.play2plat_tpcm.databinding.ItemGameHorizontalBinding
import com.squareup.picasso.Picasso

import android.util.Log
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView

import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.UserGame
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Games_List_Horizontal_Adapter(
    private var games: List<Game>,
    private val listener: OnGameClickListener,
    private val filterType: String?,
    private val otherUser: Boolean
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
        val otherUser = otherUser
        if (position < games.size) {
            holder.bind(games[position])
            holder.binding.moreGamesView!!.visibility = View.GONE // Hide 'more_games_view'
        } else if (position == games.size) {

            if(games.size != 0 && (currentFilterType == "Playing" || currentFilterType == "Favorite")){
                holder.binding.moreGamesView!!.visibility = View.GONE
            }
            else{
                holder.binding.moreGamesView!!.visibility = View.VISIBLE

                if (currentFilterType == "Playing" && otherUser == false) {
                    val showText = context.getString(R.string.no_playing_games_user)
                    holder.binding.moreGamesText!!.text = showText
                }
                else if(currentFilterType == "Playing" && otherUser == true) {
                    val showText = context.getString(R.string.no_playing_games_other)
                    holder.binding.moreGamesText!!.text = showText
                }
                else if(currentFilterType == "Favorite" && otherUser == false) {
                    val showText = context.getString(R.string.no_favorite_games_user)
                    holder.binding.moreGamesText!!.text = showText
                }
                else if(currentFilterType == "Favorite" && otherUser == true) {
                    val showText = context.getString(R.string.no_favorite_games_other)
                    holder.binding.moreGamesText!!.text = showText
                }
                else if(isNetworkAvailable() == false){

                }
                else{
                    val showText = context.getString(R.string.more_games_future)
                    holder.binding.moreGamesText!!.text = showText
                }

            }

            //holder.binding.moreGamesText!!.text = currentFilterType
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

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
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
