package com.example.play2plat_tpcm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewGamePagerAdapter(
    fragmentActivity: FragmentActivity,
    private val gameId: Int,
    private val description: String,
    private val genres: List<String>,
    private val platforms: List<String>,
    private val gameName: String,
    private var primaryColor: Int,
    private var secondaryColor: Int
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 2 // Número de tabs
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AboutFragment.newInstance(gameId, description, genres, platforms)
            1 -> InteractFragment.newInstance(gameId, gameName, primaryColor, secondaryColor)
            else -> throw IllegalStateException("Unexpected position: $position")
        }
    }
}


