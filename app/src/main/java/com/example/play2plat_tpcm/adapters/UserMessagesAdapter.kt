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
import com.example.play2plat_tpcm.api.Message
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserMessagesAdapter(
    private val messages: List<Message>,
    private val onProfilePictureClickListener: OnProfilePictureClickListener,
    private val onReplyClickListener: OnReplyClickListener,
    private val onOptionsClickListener: onMoreOptionsClickListener, // Corrigir o nome da interface
    private val isPreview: Boolean
) : RecyclerView.Adapter<GamePostsAdapter.GamePostViewHolder>() {

    interface OnProfilePictureClickListener {
        fun onProfilePictureClick(userId: Int)
    }

    interface OnReplyClickListener {
        fun onReplyClick(messageId: Int, username: String?)
    }

    interface onMoreOptionsClickListener {
        fun onOptionsClick(messageId: Int)
    }

    class UserMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profilePicture: CircleImageView = itemView.findViewById(R.id.profile_picture)
        val username: TextView = itemView.findViewById(R.id.username)
        val location: TextView = itemView.findViewById(R.id.location)
        val textPost: TextView = itemView.findViewById(R.id.text_post)
        val imagePost: ImageView = itemView.findViewById(R.id.image_post)
        val responseList: RecyclerView = itemView.findViewById(R.id.response_list)
        val replyIcon: ImageView = itemView.findViewById(R.id.reply_icon)
        val moreOptions: ImageView = itemView.findViewById(R.id.more_options)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserMessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return UserMessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserMessageViewHolder, position: Int) {
        val message = messages[position]
        val context = holder.itemView.context
        if (message.user.username == null || post.user.isDeleted == true ){
            holder.username.text = context.getString(R.string.deleted_user)
        }
        else{
            holder.username.text = post.user.username
        }

        holder.textPost.text = message.message
        if(post.user.avatar != null && post.user.avatar != "" && post.user.username != null){
            Picasso.get().load(post.user.avatar).into(holder.profilePicture)
        } else {
            Picasso.get().load(R.drawable.icon_noimageuser).into(holder.profilePicture)
        }

        holder.imagePost.setOnClickListener {
            val fragment = FullScreenImageFragment.newInstance(post.image)
            val fragmentManager = (holder.itemView.context as AppCompatActivity).supportFragmentManager
            fragment.show(fragmentManager, "FullScreenImageFragment")
        }

        // Carregar a imagem do post se disponível
        if (message.image != null) {
            holder.imagePost.visibility = View.VISIBLE
            Picasso.get().load(message.image).into(holder.imagePost)
        } else {
            holder.imagePost.visibility = View.GONE
        }


        if (!isPreview) {
            // Carregar as respostas do post
            holder.responseList.layoutManager = LinearLayoutManager(holder.itemView.context)
            getPostsAnswers(post.id, holder.responseList)
        }

        holder.profilePicture.setOnClickListener {
            if(post.user.username != null){
                onProfilePictureClickListener.onProfilePictureClick(post.user.id)
            }
        }

        if (isPreview) {
            holder.replyIcon.visibility = View.GONE
            holder.moreOptions.visibility = View.GONE
        } else {
            holder.replyIcon.visibility = View.VISIBLE
            holder.replyIcon.setOnClickListener {
                post.user.username?.let { username ->
                    onReplyClickListener.onReplyClick(post.id, username)
                } ?: run {
                    // Lidar com o caso onde username é nulo
                    Log.e("GamePostsAdapter", "Username is null for post id: ${post.id}")
                    onReplyClickListener.onReplyClick(post.id, context.getString(R.string.deleted_user))
                }
            }

            holder.moreOptions.setOnClickListener {
                onOptionsClickListener.onOptionsClick(post.id)
            }

            val sharedPreferences = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getInt("user_id", 0)

            // Verificar se o userId do post corresponde ao userId do SharedPreferences
            if (post.user.id == userId) {
                holder.moreOptions.visibility = View.VISIBLE
            } else {
                holder.moreOptions.visibility = View.GONE
            }
        }

        if (post.isAnswer == null) {
            // Se for nulo, definir o background para button_bd_3
            holder.itemView.setBackgroundResource(R.drawable.button_bd_3)
        } else {
            // Se não for nulo, definir o background para button_bd_7
            holder.itemView.setBackgroundResource(R.drawable.button_bd_7)
        }
    }

    override fun getItemCount(): Int = posts.size

    private fun getPostsAnswers(postId: Int, recyclerView: RecyclerView) {
        ApiManager.apiService.getAnswers(postId).enqueue(object : Callback<List<GameCommentsResponse>> {
            override fun onResponse(
                call: Call<List<GameCommentsResponse>>,
                response: Response<List<GameCommentsResponse>>
            ) {
                if (response.isSuccessful) {
                    val answers = response.body()
                    if (answers != null) {
                        recyclerView.adapter = UserMessagesAdapter(answers, onProfilePictureClickListener, onReplyClickListener, onOptionsClickListener, isPreview)
                    }
                } else {
                    Log.e("GamePostsAdapter", "Erro na resposta: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<List<GameCommentsResponse>>, t: Throwable) {
                Log.e("GamePostsAdapter", "Falha na chamada da API: ${t.message}")
            }
        })
    }

    fun getPostAtPosition(position: Int): GameCommentsResponse = posts[position]
}


