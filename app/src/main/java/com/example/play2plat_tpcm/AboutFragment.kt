package com.example.play2plat_tpcm

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.play2plat_tpcm.Platforms_List_Fragment
import com.example.play2plat_tpcm.R

class AboutFragment : Fragment() {

    //private lateinit var gameId: TextView
    private lateinit var description: TextView
    private lateinit var genres: TextView
    private lateinit var platforms: TextView
    private var currentUserType: Int = 0
    private lateinit var fragmentSequence: FrameLayout
    private lateinit var fragmentCompany: FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_about, container, false)

        description = view.findViewById(R.id.api_description)
        genres = view.findViewById(R.id.genres)
        platforms = view.findViewById(R.id.platforms)
        fragmentSequence = view.findViewById(R.id.fragment_sequence)
        fragmentCompany = view.findViewById(R.id.fragment_company)

        val gameIdArg = arguments?.getInt("gameId", 0) ?: 0
        val descriptionArg = arguments?.getString("description", "") ?: ""
        val platformsArg = arguments?.getStringArrayList("platforms") ?: ArrayList()
        val platformsList = platformsArg.map { it ?: "" } // Converting Serializable to List<String>
        val genresArg = arguments?.getStringArrayList("genres")?.joinToString(" • ") ?: ""


        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        currentUserType = sharedPreferences.getInt("user_type_id", 0)


        description.text=descriptionArg
        genres.text =genresArg

        val canEditPlatforms = currentUserType == 1
        if(platforms != null){
            val platformsFragment = Platforms_List_Fragment.newInstance(platformsList, canEditPlatforms, false, gameIdArg)
            childFragmentManager.beginTransaction().replace(R.id.platforms_fragment, platformsFragment).commit()
        }

        val fragmentSequence = Games_List_Horizontal_Fragment.newInstance("SameSequence", "", gameIdArg)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_sequence, fragmentSequence)
            .commit()

        val fragmentCompany = Games_List_Horizontal_Fragment.newInstance("SameCompany", "", gameIdArg)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_company, fragmentCompany)
            .commit()

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(gameId: Int, description: String, genres: List<String>, platforms: List<String>) =
            AboutFragment().apply {
                arguments = Bundle().apply {
                    putInt("gameId", gameId)
                    putString("description", description)
                    putStringArrayList("genres", ArrayList(genres))
                    putStringArrayList("platforms", ArrayList(platforms))
                }
            }
    }
}
