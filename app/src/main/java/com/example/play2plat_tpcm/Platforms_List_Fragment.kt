package com.example.play2plat_tpcm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment

class Platforms_List_Fragment : Fragment() {
    private var platforms: List<String>? = null
    private var canEditPlatforms: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        platforms = arguments?.getStringArrayList("platforms") ?: ArrayList()
        canEditPlatforms = arguments?.getBoolean("canEditPlatforms") ?: false
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
            "Xbox" to xboxLayout,
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
                    layout.alpha = if (layout.alpha == 1.0f) 0.5f else 1.0f
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(platforms: List<String>, canEditPlatforms: Boolean) =
            Platforms_List_Fragment().apply {
                arguments = Bundle().apply {
                    putStringArrayList("platforms", ArrayList(platforms))
                    putBoolean("canEditPlatforms", canEditPlatforms)
                }
            }
    }
}
