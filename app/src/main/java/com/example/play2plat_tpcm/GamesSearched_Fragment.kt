package com.example.play2plat_tpcm

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GamesSearched_Fragment : Fragment(), GamesAdapter.OnGamePictureClickListener {

    private lateinit var searchView: SearchView
    private var searchQuery: String? = null
    private lateinit var fragmentContainer: FrameLayout
    private lateinit var imageBackView: ImageView
    private lateinit var imageFilterView: ImageView
    private val navigationViewModel: FragmentNavigationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_games_searched_, container, false)
        fragmentContainer = view.findViewById(R.id.fragment_container)

        // Initialize searchView
        searchView = view.findViewById(R.id.search_view)
        imageBackView = view.findViewById(R.id.back_icon)
        imageFilterView = view.findViewById(R.id.black_square)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.length >= 3) {
                        // Perform the search
                        showSearchedGames("Search", it)
                    } else {
                        // Mostrar uma mensagem de erro informando que pelo menos três caracteres são necessários
                        // Por exemplo:
                        Toast.makeText(requireContext(), "Por favor, insira pelo menos três caracteres.", Toast.LENGTH_SHORT).show()
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // You can handle text change here if needed
                return false
            }
        })

        imageBackView.setOnClickListener {

            if (isNetworkAvailable()) {
                val fragmentManager = requireActivity().supportFragmentManager

                val currentFragment = fragmentManager.primaryNavigationFragment
                if (currentFragment != null) {
                    navigationViewModel.removeFromStack(currentFragment)
                }

                requireActivity().onBackPressed()
            }
            else{
                redirectToNoConnectionFragment()
            }
        }

        imageFilterView.setOnClickListener {
            redirectToFilters()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchView.isIconified = false
        searchView.isFocusable = true
        searchView.isIconifiedByDefault = false
        searchView.requestFocusFromTouch()

        // Abrir o teclado
        //val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        //imm?.showSoftInput(searchView.findFocus(), InputMethodManager.SHOW_IMPLICIT)

        /*
        // Utiliza ViewTreeObserver para garantir que a SearchView receba foco
        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Remove o listener para evitar chamadas repetidas
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                // Focar na SearchView e mostrar o teclado
            }
        })

         */
    }


    private fun showSearchedGames(filterType: String, parameter: String) {
        val fragment = Games_List_Grid_Fragment.newInstance(filterType, parameter, null)
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun redirectToViewGame(gameId: Int) {
        val platforms = arrayListOf<String>()

        val viewGameFragment = View_Game_Fragment.newInstance(gameId, platforms)
        navigationViewModel.addToStack(viewGameFragment)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.layout, viewGameFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onGamePictureClick(gameId: Int) {
        if (isNetworkAvailable()) {
            redirectToViewGame(gameId)
        }
        else{
            redirectToNoConnectionFragment()
        }
    }

    private fun showKeyboard() {
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    private fun redirectToFilters() {
        if (isNetworkAvailable()){
            val filtersFragment = Filters_Fragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.layout, filtersFragment)
                .addToBackStack(null)
                .commit()
        }
        else{
            redirectToNoConnectionFragment()
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun redirectToNoConnectionFragment() {
        val noConnectionFragment= NoConnectionFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.layout, noConnectionFragment)
            .addToBackStack(null)
            .commit()
    }


    private fun Int.dpToPx(): Int {
        val scale = resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            GamesSearched_Fragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
