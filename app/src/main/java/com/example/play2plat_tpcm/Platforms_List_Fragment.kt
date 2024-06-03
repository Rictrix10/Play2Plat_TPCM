package com.example.play2plat_tpcm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.GamePlatform
import com.example.play2plat_tpcm.api.UserPlatform
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Platforms_List_Fragment : Fragment() {
    private var platforms: List<String>? = null
    private var canEditPlatforms: Boolean = false
    private var isUserPlatforms: Boolean = false
    private var userId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        platforms = arguments?.getStringArrayList("platforms") ?: ArrayList()
        canEditPlatforms = arguments?.getBoolean("canEditPlatforms") ?: false
        isUserPlatforms = arguments?.getBoolean("isUserPlatforms") ?: false
        userId = arguments?.getInt("id")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout XML para este fragmento
        return inflater.inflate(R.layout.fragment_platforms_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Encontra os layouts das plataformas
        val pcLayout = view.findViewById<LinearLayout>(R.id.pc_layout)
        val xboxLayout = view.findViewById<LinearLayout>(R.id.xbox_layout)
        val playstationLayout = view.findViewById<LinearLayout>(R.id.playstation_layout)
        val switchLayout = view.findViewById<LinearLayout>(R.id.switch_layout)
        val androidLayout = view.findViewById<LinearLayout>(R.id.android_layout)
        val iosLayout = view.findViewById<LinearLayout>(R.id.ios_layout)

        // Lista de todos os layouts e suas respectivas plataformas
        val platformLayouts = mapOf(
            "PC" to pcLayout,
            "XBox" to xboxLayout,
            "PlayStation" to playstationLayout,
            "Switch" to switchLayout,
            "Android" to androidLayout,
            "Mac/IOS" to iosLayout
        )

        if (!canEditPlatforms) {
            // Mostrar apenas os layouts das plataformas presentes na lista platforms
            for (platform in platforms!!) {
                platformLayouts[platform]?.visibility = View.VISIBLE
            }

            // Remover completamente os layouts das plataformas que não estão presentes na lista platforms
            for ((platform, layout) in platformLayouts) {
                if (platform !in platforms!!) {
                    val parent = layout.parent as ViewGroup?
                    parent?.removeView(layout)
                }
            }

        } else {
            // Mostra todos os layouts de plataformas
            for ((platform, layout) in platformLayouts) {
                layout.visibility = View.VISIBLE
                if (!platforms!!.contains(platform)) {
                    layout.alpha = 0.5f // Torna opaco
                }
                // Configura o clique para alternar entre opaco e não opaco
                layout.setOnClickListener {
                    if (userId != null) {
                        if (layout.alpha == 1.0f) {
                            layout.alpha = 0.5f
                            if (isUserPlatforms) {
                                // Remove platform from user
                                deletePlatformFromUser(userId!!.toInt(), platformToId(platform))
                            }else{
                                deletePlatformFromGame(userId!!.toInt(), platformToId(platform))
                            }
                        } else {
                            layout.alpha = 1.0f
                            if (isUserPlatforms) {
                                // Add platform to user
                                addPlatformToUser(userId!!.toInt(), platformToId(platform))
                            }
                            else{
                                addPlatformToGame(userId!!.toInt(), platformToId(platform))
                            }
                        }
                    } else {
                        Toast.makeText(context, "User ID is null", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun addPlatformToUser(userId: Int, platformId: Int) {
        val userPlatform = UserPlatform(userId, platformId)
        ApiManager.apiService.addPlatformsToUser(userPlatform).enqueue(object : Callback<UserPlatform> {
            override fun onResponse(call: Call<UserPlatform>, response: Response<UserPlatform>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Platform added", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to add platform", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserPlatform>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addPlatformToGame(userId: Int, platformId: Int) {
        val userPlatform = GamePlatform(userId, platformId)
        ApiManager.apiService.addPlatformsToGame(userPlatform).enqueue(object : Callback<GamePlatform> {
            override fun onResponse(call: Call<GamePlatform>, response: Response<GamePlatform>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Platform added", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to add platform", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GamePlatform>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deletePlatformFromUser(userId: Int, platformId: Int) {
        ApiManager.apiService.deletePlatformFromUser(userId, platformId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Platform removed", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to remove platform", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deletePlatformFromGame(userId: Int, platformId: Int) {
        ApiManager.apiService.deletePlatformFromGame(userId, platformId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Platform removed", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to remove platform", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun platformToId(platform: String): Int {
        return when (platform) {
            "PC" -> 2
            "Xbox" -> 3
            "PlayStation" -> 4
            "Switch" -> 5
            "Android" -> 6
            "Mac/IOS" -> 1
            else -> -1
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(
            platforms: List<String>,
            canEditPlatforms: Boolean,
            isUserPlatforms: Boolean,
            id: Int
        ) = Platforms_List_Fragment().apply {
            arguments = Bundle().apply {
                putStringArrayList("platforms", ArrayList(platforms))
                putBoolean("canEditPlatforms", canEditPlatforms)
                putBoolean("isUserPlatforms", isUserPlatforms)
                putInt("id", id)
            }
        }
    }
}
