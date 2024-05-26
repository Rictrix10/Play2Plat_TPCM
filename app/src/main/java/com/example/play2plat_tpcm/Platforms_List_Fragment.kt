package com.example.play2plat_tpcm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment


class Platforms_List_Fragment : Fragment() {
    private var platforms: List<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        platforms = arguments?.getStringArrayList("platforms") ?: ArrayList()
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

        // Encontra o layout pai onde os layouts das plataformas serão adicionados
        val platformContainer = view.findViewById<ViewGroup>(R.id.platform_container)

        // Exibe os layouts das plataformas disponíveis para o jogo
        for (platform in platforms!!) {
            when (platform) {
                "PC" -> view.findViewById<LinearLayout>(R.id.pc_layout).visibility = View.VISIBLE
                "Xbox" -> view.findViewById<LinearLayout>(R.id.xbox_layout).visibility = View.VISIBLE
                "PlayStation" -> view.findViewById<LinearLayout>(R.id.playstation_layout).visibility = View.VISIBLE
                "Switch" -> view.findViewById<LinearLayout>(R.id.switch_layout).visibility = View.VISIBLE
                "Android", "Mobile" -> view.findViewById<LinearLayout>(R.id.android_layout).visibility = View.VISIBLE
                "Mac/IOS" -> view.findViewById<LinearLayout>(R.id.ios_layout).visibility = View.VISIBLE
                // Adicione casos para outras plataformas conforme necessário
            }
        }
    }



    companion object {
        @JvmStatic
        fun newInstance(platforms: List<String>) =
            Platforms_List_Fragment().apply {
                arguments = Bundle().apply {
                    putStringArrayList("platforms", ArrayList(platforms))
                }
            }
    }

}




