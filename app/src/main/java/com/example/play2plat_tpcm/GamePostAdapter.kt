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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GamePostsAdapter(
    private val posts: List<GameCommentsResponse>,
    private val onProfilePictureClickListener: OnProfilePictureClickListener,
    private val onReplyClickListener: OnReplyClickListener,
    private val onOptionsClickListener: onMoreOptionsClickListener, // Corrigir o nome da interface
    private val isPreview: Boolean
) : RecyclerView.Adapter<GamePostsAdapter.GamePostViewHolder>() {

    interface OnProfilePictureClickListener {
        fun onProfilePictureClick(userId: Int)
    }

    interface OnReplyClickListener {
        fun onReplyClick(postId: Int, username: String)
    }

    interface onMoreOptionsClickListener {
        fun onOptionsClick(postId: Int)
    }

    class GamePostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profilePicture: CircleImageView = itemView.findViewById(R.id.profile_picture)
        val username: TextView = itemView.findViewById(R.id.username)
        val location: TextView = itemView.findViewById(R.id.location)
        val textPost: TextView = itemView.findViewById(R.id.text_post)
        val imagePost: ImageView = itemView.findViewById(R.id.image_post)
        val responseList: RecyclerView = itemView.findViewById(R.id.response_list)
        val replyIcon: ImageView = itemView.findViewById(R.id.reply_icon)
        val moreOptions: ImageView = itemView.findViewById(R.id.more_options)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamePostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_game_post, parent, false)
        return GamePostViewHolder(view)
    }

    override fun onBindViewHolder(holder: GamePostViewHolder, position: Int) {
        val post = posts[position]
        val context = holder.itemView.context
        if (post.user.username == null || post.user.isDeleted == true ){
            holder.username.text = "Deleted User"
        }
        else{
            holder.username.text = post.user.username
        }
        holder.location.text = post.location
        holder.textPost.text = post.comments
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
        if (post.image != null) {
            holder.imagePost.visibility = View.VISIBLE
            Picasso.get().load(post.image).into(holder.imagePost)
        } else {
            holder.imagePost.visibility = View.GONE
        }

        if (post.location != "null, null") {
            holder.location.text = post.location
            holder.location.visibility = View.VISIBLE
        } else {
            holder.location.visibility = View.GONE
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
                onReplyClickListener.onReplyClick(post.id, post.user.username!!)
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
                        recyclerView.adapter = GamePostsAdapter(answers, onProfilePictureClickListener, onReplyClickListener, onOptionsClickListener, isPreview)
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


