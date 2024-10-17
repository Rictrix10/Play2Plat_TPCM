package com.example.play2plat_tpcm

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.play2plat_tpcm.api.GameCommentsResponse
import com.example.play2plat_tpcm.ui.theme.FullScreenImageFragment
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import android.util.Log
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.UserMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsersAdapter(
    private val users: List<UserMessage>,
    private val onProfilePictureClickListener: OnProfilePictureClickListener,
) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    interface OnProfilePictureClickListener {
        fun onProfilePictureClick(userId: Int)
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profilePicture: CircleImageView = itemView.findViewById(R.id.profile_picture)
        val username: TextView = itemView.findViewById(R.id.username)
        val location: TextView = itemView.findViewById(R.id.location)
        val textPost: TextView = itemView.findViewById(R.id.text_post)
        val imagePost: ImageView = itemView.findViewById(R.id.image_post)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_users_searched, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        val context = holder.itemView.context

        //if (user.username == null || user.isDeleted == true ){
        if (user.username == null){
            holder.username.text = context.getString(R.string.deleted_user)
        }
        else{
            holder.username.text = user.username
        }

        if(user.avatar != null && user.avatar != "" && user.username != null){
            Picasso.get().load(user.avatar).into(holder.profilePicture)
        } else {
            Picasso.get().load(R.drawable.icon_noimageuser).into(holder.profilePicture)
        }


        holder.profilePicture.setOnClickListener {
            if(user.username != null){
                onProfilePictureClickListener.onProfilePictureClick(user.id)
            }
        }

    }

    override fun getItemCount(): Int = users.size

}


