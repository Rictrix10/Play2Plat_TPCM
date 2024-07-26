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

class MessagesAdapter(
    private val usersMessages: List<UserMessage>,
    private val onProfilePictureClickListener: OnProfilePictureClickListener,
) : RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder>() {

    interface OnProfilePictureClickListener {
        fun onProfilePictureClick(userId: Int)
    }


    class MessagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profilePicture: CircleImageView = itemView.findViewById(R.id.profile_picture)
        val username: TextView = itemView.findViewById(R.id.username)
        val location: TextView = itemView.findViewById(R.id.location)
        val textPost: TextView = itemView.findViewById(R.id.text_post)
        val imagePost: ImageView = itemView.findViewById(R.id.image_post)
        val responseList: RecyclerView = itemView.findViewById(R.id.response_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_users_messages, parent, false)
        return MessagesViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        val usersMessage = usersMessages[position]
        val context = holder.itemView.context
        //if (usersMessage.username == null || usersMessage.isDeleted == true ){   USAR DEPOIS ESTA CONDIÇÃO
        if (usersMessage.username == null){
            holder.username.text = context.getString(R.string.deleted_user)
        }
        else{
            holder.username.text = usersMessage.username
        }


        if(usersMessage.avatar != null && usersMessage.avatar != "" && usersMessage.username != null){
            Picasso.get().load(usersMessage.avatar).into(holder.profilePicture)
        } else {
            Picasso.get().load(R.drawable.icon_noimageuser).into(holder.profilePicture)
        }

        /*
        if (post.location != "null, null") {
            holder.location.text = post.location
            holder.location.visibility = View.VISIBLE
        } else {
            holder.location.visibility = View.GONE
        }

         */


        holder.profilePicture.setOnClickListener {
            if(usersMessage.username != null){
                onProfilePictureClickListener.onProfilePictureClick(usersMessage.id)
            }
        }

    }

    override fun getItemCount(): Int = usersMessages.size

}


