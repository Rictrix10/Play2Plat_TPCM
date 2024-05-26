package com.example.play2plat_tpcm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit

class Games_Fragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_games_, container, false)

        // Find the button and set a click listener
        val buttonViewGame: Button = view.findViewById(R.id.button_view_game)
        buttonViewGame.setOnClickListener {
            // Replace the current fragment with ViewGameFragment
            parentFragmentManager.commit {
                replace(R.id.layout, View_Game_Fragment())
                addToBackStack(null) // This allows you to navigate back
            }
        }

        return view
    }
}
