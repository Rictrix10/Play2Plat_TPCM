package com.example.play2plat_tpcm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.play2plat_tpcm.api.GameCommentsResponse
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class GamePostsAdapter(
    private val posts: List<GameCommentsResponse>,
    private val onProfilePictureClickListener: OnProfilePictureClickListener
) : RecyclerView.Adapter<GamePostsAdapter.GamePostViewHolder>() {

    interface OnProfilePictureClickListener {
        fun onProfilePictureClick(userId: Int)
    }

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
        holder.username.text = post.user.username
        holder.textPost.text = post.comments
        Picasso.get().load(post.user.avatar).into(holder.profilePicture)
        Picasso.get().load(post.image).into(holder.imagePost)

        holder.profilePicture.setOnClickListener {
            onProfilePictureClickListener.onProfilePictureClick(post.user.id)
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}
