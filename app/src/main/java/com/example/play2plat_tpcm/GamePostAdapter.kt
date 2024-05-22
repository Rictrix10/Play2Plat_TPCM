package com.example.play2plat_tpcm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.play2plat_tpcm.api.GamePost
import de.hdodenhof.circleimageview.CircleImageView

class GamePostsAdapter(private val posts: List<GamePost>) :
    RecyclerView.Adapter<GamePostsAdapter.GamePostViewHolder>() {

    class GamePostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profilePicture: CircleImageView = itemView.findViewById(R.id.profile_picture)
        val username: TextView = itemView.findViewById(R.id.username)
        val textPost: TextView = itemView.findViewById(R.id.text_post)
        val imagePost: ImageView = itemView.findViewById(R.id.image_post)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamePostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_game_post, parent, false)
        return GamePostViewHolder(view)
    }

    override fun onBindViewHolder(holder: GamePostViewHolder, position: Int) {
        val post = posts[position]
        // Aqui você deve definir os dados do post nos views, por exemplo:
        holder.username.text = post.username
        holder.textPost.text = post.text
        holder.profilePicture.setImageResource(post.profilePicture) // Supondo que seja um recurso drawable
        holder.imagePost.setImageResource(post.imagePost) // Supondo que seja um recurso drawable
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}
