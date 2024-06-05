package com.example.play2plat_tpcm

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.play2plat_tpcm.api.GameCommentsResponse
import com.example.play2plat_tpcm.ui.theme.FullScreenImageFragment
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

        // Adiciona uma variável para controlar o estado da imagem (expandida ou não)
        var isImageExpanded: Boolean = false
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

        holder.imagePost.setOnClickListener {
            val fragment = FullScreenImageFragment.newInstance(post.image)
            val fragmentManager = (holder.itemView.context as AppCompatActivity).supportFragmentManager
            fragment.show(fragmentManager, "FullScreenImageFragment")
        }

        if (post.image.isNullOrEmpty()) {
            holder.imagePost.visibility = View.GONE
        } else {
            holder.imagePost.visibility = View.VISIBLE
            Picasso.get().load(post.image).into(holder.imagePost)
        }

        holder.profilePicture.setOnClickListener {
            onProfilePictureClickListener.onProfilePictureClick(post.user.id)
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    fun getPostAtPosition(position: Int): GameCommentsResponse {
        return posts[position]
    }
}
