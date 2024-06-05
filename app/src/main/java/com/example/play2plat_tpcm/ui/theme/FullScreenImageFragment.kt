package com.example.play2plat_tpcm.ui.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.example.play2plat_tpcm.R
import com.squareup.picasso.Picasso

class FullScreenImageFragment : DialogFragment() {
    companion object {
        private const val ARG_IMAGE_URL = "imageUrl"

        fun newInstance(imageUrl: String): FullScreenImageFragment {
            val args = Bundle()
            args.putString(ARG_IMAGE_URL, imageUrl)
            val fragment = FullScreenImageFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_full_screen_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Carregar a imagem na ImageView
        val imageUrl = arguments?.getString(ARG_IMAGE_URL)
        imageUrl?.let {
            Picasso.get().load(it).into(view.findViewById<ImageView>(R.id.fullScreenImageView))
        }

        // Configurar clique fora da imagem para fechar o fragmento
        view.setOnClickListener {
            dismiss()
        }
    }

}
