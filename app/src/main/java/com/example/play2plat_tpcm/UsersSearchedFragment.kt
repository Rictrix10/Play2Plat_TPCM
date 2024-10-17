package com.example.play2plat_tpcm

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.UserMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsersSearchedFragment : Fragment(), UsersAdapter.OnProfilePictureClickListener {

    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
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
        val view = inflater.inflate(R.layout.fragment_users_searched, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        // Initialize searchView
        searchView = view.findViewById(R.id.search_view)
        imageBackView = view.findViewById(R.id.back_icon)
        imageFilterView = view.findViewById(R.id.black_square)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.length >= 3) {
                        // Perform the search
                        getUsersSearched(it)
                    } else {
                        // Mostrar uma mensagem de erro informando que pelo menos três caracteres são necessários
                        // Por exemplo:
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.insert_three_characters),
                            Toast.LENGTH_SHORT
                        ).show()

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

    private fun getUsersSearched(name: String) {
        ApiManager.apiService.searchUsers(name).enqueue(object :
            Callback<List<UserMessage>> {
            override fun onResponse(
                call: Call<List<UserMessage>>,
                response: Response<List<UserMessage>>
            ) {
                if (response.isSuccessful) {
                    val users = response.body()
                    if (users != null && users.isNotEmpty()) {

                        recyclerView.adapter = UsersAdapter(users, this@UsersSearchedFragment)

                    } else {

                        recyclerView.adapter = UsersAdapter(emptyList(), this@UsersSearchedFragment)
                    }
                } else {
                    recyclerView.adapter = UsersAdapter(emptyList(), this@UsersSearchedFragment)

                }
            }

            override fun onFailure(call: Call<List<UserMessage>>, t: Throwable) {
                Log.e("UsersFragment", "Falha na chamada da API: ${t.message}")
            }
        })
    }


    private fun showSearchedGames(filterType: String, parameter: String) {
        val fragment = Games_List_Grid_Fragment.newInstance(filterType, parameter, null)
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }


    override fun onProfilePictureClick(userId: Int) {
        val viewGameFragment = Profile_Fragment.newInstance(userId)
        navigationViewModel.addToStack(viewGameFragment)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.layout, viewGameFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showKeyboard() {
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }


    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        return networkInfo != null && networkInfo.isConnected
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
