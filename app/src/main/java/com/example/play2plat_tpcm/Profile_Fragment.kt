package com.example.play2plat_tpcm

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.palette.graphics.Palette
import com.squareup.picasso.Picasso
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_USER_ID = "user_id"

class Profile_Fragment : Fragment() {

    private lateinit var usernameTextView: TextView
    private lateinit var profileImageView: ImageView
    private lateinit var editIconImageView: ImageView
    private lateinit var containerLayout: ConstraintLayout
    private lateinit var backIconImageView: ImageView
    private var userId: Int = 0
    private var currentUserId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt(ARG_USER_ID)
        }

        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        currentUserId = sharedPreferences.getInt("user_id", 0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usernameTextView = view.findViewById(R.id.username)
        profileImageView = view.findViewById(R.id.profile_picture)
        editIconImageView = view.findViewById(R.id.edit_icon)
        containerLayout = view.findViewById(R.id.container_layout)
        backIconImageView = view.findViewById(R.id.back_icon)

        // Configurar a visibilidade do ícone de edição
        if (userId == currentUserId) {
            editIconImageView.visibility = View.VISIBLE
            editIconImageView.setOnClickListener {
                redirectToEditProfile()
            }
            backIconImageView.visibility = View.GONE
        } else {
            editIconImageView.visibility = View.GONE
            backIconImageView.visibility = View.VISIBLE
            backIconImageView.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }

        // Configurar ação do ícone de voltar
        backIconImageView.setOnClickListener {
            requireActivity().onBackPressed()
        }


        ApiManager.apiService.getUserById(userId).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        usernameTextView.text = user.username
                        loadImage(user.avatar)

                        val platforms = user.platforms
                        val canEditPlatforms = userId == currentUserId
                        if(platforms != null){
                            val platformsFragment = Platforms_List_Fragment.newInstance(platforms, canEditPlatforms, true, currentUserId)
                            childFragmentManager.beginTransaction().replace(R.id.platforms_fragment, platformsFragment).commit()
                        }

                    } else {
                        Log.e("Profile_Fragment", "API response did not return user data.")
                    }
                } else {
                    Log.e("Profile_Fragment", "API response error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("Profile_Fragment", "Request error: ${t.message}")
            }
        })

        view.findViewById<Button>(R.id.redirect_button)?.setOnClickListener {
            val fragment = newInstance(1) // Passar o ID do usuário desejado
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.layout, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun loadImage(avatarUrl: String?) {
        if (!avatarUrl.isNullOrEmpty()) {
            Picasso.get().load(avatarUrl).into(profileImageView, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    val bitmap = (profileImageView.drawable as BitmapDrawable).bitmap
                    applyGradientFromBitmap(bitmap)
                }

                override fun onError(e: Exception?) {
                    Log.e("Profile_Fragment", "Error loading image: ${e?.message}")
                }
            })
        } else {
            profileImageView.setImageResource(R.drawable.noimageuser)
            val bitmap = (profileImageView.drawable as BitmapDrawable).bitmap
            applyGradientFromBitmap(bitmap)
        }
    }

    private fun applyGradientFromBitmap(bitmap: Bitmap) {
        Palette.from(bitmap).generate { palette ->
            palette?.let {
                var dominantColor = it.dominantSwatch?.rgb ?: Color.GRAY
                var vibrantColor = it.vibrantSwatch?.rgb ?: Color.DKGRAY

                // Verificar se as cores extraídas são muito claras
                val isDominantColorTooLight = ColorUtils.calculateLuminance(dominantColor) > 0.8
                val isVibrantColorTooLight = ColorUtils.calculateLuminance(vibrantColor) > 0.8

                if (isDominantColorTooLight) {
                    dominantColor = Color.GRAY
                }
                if (isVibrantColorTooLight) {
                    vibrantColor = Color.DKGRAY
                }

                // Se as cores vibrantes e dominantes forem iguais, ajustar a cor vibrante
                if (vibrantColor == dominantColor) {
                    vibrantColor = it.lightVibrantSwatch?.rgb
                        ?: it.darkVibrantSwatch?.rgb
                                ?: it.mutedSwatch?.rgb
                                ?: it.lightMutedSwatch?.rgb
                                ?: it.darkMutedSwatch?.rgb
                                ?: Color.DKGRAY
                }

                val colors = intArrayOf(dominantColor, vibrantColor)
                val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)
                containerLayout.background = gradientDrawable
            } ?: run {
                val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(Color.GRAY, Color.DKGRAY))
                containerLayout.background = gradientDrawable
            }
        }
    }


    private fun redirectToEditProfile() {
        val editProfileFragment = Edit_Profile_Fragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.layout, editProfileFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_, container, false)
    }


    companion object {
        @JvmStatic
        fun newInstance(userId: Int) =
            Profile_Fragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_USER_ID, userId)
                }
            }
    }
}
