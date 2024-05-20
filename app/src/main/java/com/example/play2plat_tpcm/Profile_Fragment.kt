import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.play2plat_tpcm.EditProfile
import com.squareup.picasso.Picasso
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.User
import com.example.play2plat_tpcm.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import de.hdodenhof.circleimageview.CircleImageView

class Profile_Fragment : Fragment() {

    private lateinit var usernameTextView: TextView
    private lateinit var profileImageView: ImageView
    private lateinit var editIconImageView: ImageView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usernameTextView = view.findViewById(R.id.username)
        profileImageView = view.findViewById(R.id.profile_picture)
        editIconImageView = view.findViewById(R.id.edit_icon)
        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", 0)

        editIconImageView.setOnClickListener {
            redirectToEditProfile()
        }

        ApiManager.apiService.getUserById(userId).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        // Atualize as views com os dados do utilizador
                        usernameTextView.text = user.username
                        loadImage(user.avatar)
                    } else {
                        Log.e("Profile_Fragment", "Resposta da API não retornou dados do usuário.")
                    }
                } else {
                    // Manipular erro de resposta da API
                    Log.e("Profile_Fragment", "Erro na resposta da API: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                // Lidar com falha na solicitação
                Log.e("Profile_Fragment", "Erro na solicitação: ${t.message}")
            }
        })
    }

    private fun loadImage(avatarUrl: String?) {
        if (!avatarUrl.isNullOrEmpty()) {
            Picasso.get().load(avatarUrl).into(profileImageView)
        } else {
            // Se o avatar do usuário for nulo ou vazio, carregue a imagem padrão
            profileImageView.setImageResource(R.drawable.noimageuser)
        }
    }

    private fun redirectToEditProfile() {
        val editProfileFragment = EditProfile()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.layout, editProfileFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_, container, false)
    }
}
