package com.example.play2plat_tpcm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewGamesAdapter(
    fragmentActivity: FragmentActivity,
    private val gameId: Int,
    private val description: String,
    private val genres: List<String>,
    private val platforms: List<String>,
    private val gameName: String,
    private val sequence: String?,
    private val company: String,
    private var primaryColor: Int,
    private var secondaryColor: Int,
    private var averageStars: Float
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 2 // Número de tabs
    }

    override fun createFragment(position: Int): Fragment {
        val safeSequence = sequence ?: "No"
        return when (position) {
            0 -> AboutFragment.newInstance(gameId, description, genres, platforms, safeSequence, company)
            1 -> InteractFragment.newInstance(gameId, gameName, primaryColor, secondaryColor, averageStars)
            else -> throw IllegalStateException("Unexpected position: $position")
        }
    }
}


