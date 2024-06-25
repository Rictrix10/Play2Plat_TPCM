package com.example.play2plat_tpcm

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.Company
import com.example.play2plat_tpcm.api.Game
import com.example.play2plat_tpcm.api.Sequence
import com.example.play2plat_tpcm.api.Genre
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import com.example.play2plat_tpcm.adapters.CompanyAdapter
import com.example.play2plat_tpcm.adapters.SequenceAdapter
import com.example.play2plat_tpcm.adapters.GenresAdapter
import com.example.play2plat_tpcm.adapters.PegyAdapter
import com.example.play2plat_tpcm.api.GameGenre
import com.example.play2plat_tpcm.api.GameInfo
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import kotlin.math.min


/**
 * A simple [Fragment] subclass.
 */
class Edit_Game_Fragment : Fragment() {

    private var gameInfo: GameInfo? = null
    private var selectedImageUri: Uri? = null

    private val pickVisualMediaLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            Log.d("AddNewGame", "Selected image URI: $selectedImageUri")
            imageView.setImageURI(selectedImageUri)
        } else {
            Log.d("AddNewGame", "No image URI received")
        }
    }

    private lateinit var gameTitleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var pegiInfoSpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var imageView: ImageView
    private lateinit var isFreeCheckBox: CheckBox
    private lateinit var companyAdapter: CompanyAdapter
    private lateinit var companyAccordion: LinearLayout
    private lateinit var companyTitle: TextView
    private lateinit var companyList: ListView
    private lateinit var companies: List<Company>
    private lateinit var sequenceAdapter: SequenceAdapter
    private lateinit var sequenceAccordion: LinearLayout
    private lateinit var sequenceTitle: TextView
    private lateinit var sequenceList: ListView
    private lateinit var sequences: List<Sequence>
    private lateinit var genreAdapter: GenresAdapter
    private lateinit var genreAccordion: LinearLayout
    private lateinit var genreTitle: TextView
    private lateinit var genreList: ListView
    private lateinit var genres: List<Genre>
    private lateinit var pegiAccordion: LinearLayout
    private lateinit var pegiTitle: TextView
    private lateinit var pegiList: ListView
    private lateinit var pegiInfoValues: Array<String>
    private lateinit var pegiAdapter: PegyAdapter
    private lateinit var backButton: ImageView
    private val navigationViewModel: FragmentNavigationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            gameInfo = it.getParcelable(ARG_GAME)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_edit_game, container, false)

        imageView = view.findViewById(R.id.image_view)

        val pickImageButton = view.findViewById<Button>(R.id.pick_image)
        pickImageButton.setOnClickListener {
            Log.d("AddNewGame", "Pick image button clicked")
            selectVisualMedia()
        }

        gameTitleEditText = view.findViewById(R.id.game_title)
        descriptionEditText = view.findViewById(R.id.description)
        saveButton = view.findViewById(R.id.save)
        isFreeCheckBox = view.findViewById(R.id.is_free_checkbox)
        backButton = view.findViewById(R.id.back_icon)

        companyAccordion = view.findViewById(R.id.company_accordion)
        companyTitle = view.findViewById(R.id.company_title)
        companyList = view.findViewById(R.id.company_list)

        sequenceAccordion = view.findViewById(R.id.sequence_accordion)
        sequenceTitle = view.findViewById(R.id.sequence_title)
        sequenceList = view.findViewById(R.id.sequence_list)

        genreAccordion = view.findViewById(R.id.genres_accordion)
        genreTitle = view.findViewById(R.id.genres_title)
        genreList = view.findViewById(R.id.genres_list)

        pegiAccordion = view.findViewById(R.id.pegi_accordion)
        pegiTitle = view.findViewById(R.id.pegi_title)
        pegiList = view.findViewById(R.id.pegi_list)

        Log.d("EditGameFragment", "GameInfo received: $gameInfo")
        if(gameInfo != null){
            populateFields()
        }

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }


        var selectedCompanyPosition: Int = -1
        var selectedSequencePosition: Int = -1
        var selectedGenrePosition: Int = -1

        companyAccordion.setOnClickListener {
            toggleListVisibility(companyList, companyTitle, R.drawable.icon_companies)
        }

        sequenceAccordion.setOnClickListener {
            toggleListVisibility(sequenceList, sequenceTitle, R.drawable.icon_sequence)
        }

        genreAccordion.setOnClickListener {
            toggleListVisibility(genreList, genreTitle, R.drawable.icon_genres)
        }

        pegiAccordion.setOnClickListener {
            toggleListVisibility(pegiList, pegiTitle, R.drawable.icon_age)
        }

        loadCompanies(view.context)
        loadSequences(view.context)
        loadGenres(view.context)
        loadPegiInfo(view.context)



        saveButton.setOnClickListener {
            val gameTitle = gameTitleEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val selectedCompanyText = companyTitle.text.toString()
            val selectedSequenceText = sequenceTitle.text.toString()
            val selectedGenreTexts = genreTitle.text.toString().split(", ")
            val selectedPegiInfo = pegiAdapter.getSelectedPosition().takeIf { it != -1 }?.let {
                val pegiInfo = pegiInfoValues[it]
                val pegiValue = pegiInfo.substringAfter(":").trim()
                pegiValue.toIntOrNull() ?: 0
            } ?: 0
            val isFree = isFreeCheckBox.isChecked
            var selectedCompanyId: Int? = null
            var selectedSequenceId: Int? = null
            val selectedGenreIds = mutableListOf<Int>()

            for (company in companies) {
                if (company.name == selectedCompanyText) {
                    selectedCompanyId = company.id
                    break
                }
            }

            for (sequence in sequences) {
                if (sequence.name == selectedSequenceText) {
                    selectedSequenceId = sequence.id
                    break
                }
            }

            for (genre in genres) {
                if (selectedGenreTexts.contains(genre.name)) {
                    selectedGenreIds.add(genre.id)
                }
            }

            if (gameTitle.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.game_name_empty), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (description.length <= 6) {
                Toast.makeText(requireContext(), getString(R.string.description_too_short), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedGenreIds.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.genre_empty), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedCompanyId == null) {
                Toast.makeText(requireContext(), getString(R.string.company_empty), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            if (selectedPegiInfo == 0) {
                Toast.makeText(requireContext(), getString(R.string.pegi_info_empty), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val context = requireContext()

            // Verifica se uma nova imagem foi selecionada
            if (selectedImageUri == null) {
                // Se não houve seleção de nova imagem, utilizar a imagem existente (gameInfo.coverImage)
                editGame(gameInfo!!.coverImage!!, gameTitle, description, isFree, selectedPegiInfo, selectedSequenceId, selectedCompanyId, selectedGenreIds)
            } else {
                // Se houve seleção de nova imagem, fazer upload da nova imagem
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, selectedImageUri)
                val file = bitmapToFile(context, bitmap)

                val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
                val imagePart = MultipartBody.Part.createFormData("file", "image.jpg", requestFile)

                val call = ApiManager.apiService.uploadImage(imagePart)
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val imageUrl = response.body()?.string()
                            imageUrl?.let {
                                val pattern = Regex("\"url\":\"(\\S+)\"")
                                val matchResult = pattern.find(it)

                                matchResult?.let { result ->
                                    val coverImageUrl = result.groupValues[1]

                                    // Editar o jogo utilizando a nova imagem
                                    editGame(coverImageUrl, gameTitle, description, isFree, selectedPegiInfo, selectedSequenceId, selectedCompanyId, selectedGenreIds)
                                }
                            }
                        } else {
                            Log.e("AddNewGame", "Erro ao fazer upload da imagem: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("AddNewGame", "Falha na requisição de upload: ${t.message}")
                    }
                })
            }
        }



        return view
    }

    val gson = GsonBuilder().serializeNulls().create()

    private fun editGame(coverImage: String, gameTitle: String, description: String, isFree: Boolean, selectedPegiInfo: Int, selectedSequenceId: Int?, selectedCompanyId: Int?, selectedGenreIds: List<Int>) {
        Log.d("Edit Game", "sequence: $selectedSequenceId")

        val newGame = Game(
            id = null,
            name = gameTitle,
            description = description,
            isFree = isFree,
            releaseDate = "2024-04-24T00:00:00Z",
            pegiInfo = selectedPegiInfo,
            coverImage = coverImage,
            sequenceId = selectedSequenceId ?: null,
            companyId = selectedCompanyId!!
        )

        Log.d("Edit Game", "Request payload: ${gson.toJson(newGame)}")

        ApiManager.apiService.editGame(newGame, gameInfo!!.id)
            .enqueue(object : Callback<Game> {
                override fun onResponse(call: Call<Game>, response: Response<Game>) {
                    Log.d("Edit Game", "Response: ${response.raw()}")
                    if (response.isSuccessful) {
                        val createdGame = response.body()
                        Log.d("Edit Game", "Response body: $createdGame")
                        createdGame?.let { game ->
                            Log.d("AddNewGame", "Jogo editado com sucesso: $game")

                            ApiManager.apiService.deleteGameGenres(gameInfo!!.id)
                                .enqueue(object : Callback<Void> {
                                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                        if (response.isSuccessful) {
                                            Log.d("AddNewGame", "Gêneros do jogo deletados com sucesso")

                                            var successfulAssociations = 0
                                            val totalAssociations = selectedGenreIds.size

                                            for (genreId in selectedGenreIds) {
                                                val gameToGenreAssociation = GameGenre(gameId = game.id, genreId = genreId)
                                                ApiManager.apiService.addGenresToGame(gameToGenreAssociation)
                                                    .enqueue(object : Callback<GameGenre> {
                                                        override fun onResponse(call: Call<GameGenre>, response: Response<GameGenre>) {
                                                            if (response.isSuccessful) {
                                                                Log.d("AddNewGame", "Gênero associado com sucesso: $gameToGenreAssociation")
                                                                successfulAssociations++
                                                                if (successfulAssociations == totalAssociations) {
                                                                    navigateBack()
                                                                }
                                                            } else {
                                                                Log.e("AddNewGame", "Erro ao associar gênero: ${response.message()}")
                                                            }
                                                        }

                                                        override fun onFailure(call: Call<GameGenre>, t: Throwable) {
                                                            Log.e("AddNewGame", "Falha na requisição de associação de gênero: ${t.message}")
                                                        }
                                                    })
                                            }
                                        } else {
                                            Log.e("AddNewGame", "Erro ao deletar gêneros do jogo: ${response.message()}")
                                        }
                                    }

                                    override fun onFailure(call: Call<Void>, t: Throwable) {
                                        Log.e("AddNewGame", "Falha na requisição de deleção de gêneros do jogo: ${t.message}")
                                    }
                                })
                        } ?: run {
                            Log.e("AddNewGame", "Erro: Corpo da resposta nulo.")
                        }
                    } else {
                        Log.e("AddNewGame", "Erro ao editar jogo: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<Game>, t: Throwable) {
                    Log.e("AddNewGame", "Falha na requisição de edição de jogo: ${t.message}")
                }
            })
    }



    private fun navigateBack() {
        // Verifica se o estado não foi salvo antes de realizar a transação
        if (!requireActivity().supportFragmentManager.isStateSaved) {
            val fragmentManager = requireActivity().supportFragmentManager

            // Remove the current fragment from back stack
            fragmentManager.popBackStack()

            // Remove the current fragment from ViewModel's stack
            val currentFragment = fragmentManager.primaryNavigationFragment
            if (currentFragment != null) {
                navigationViewModel.removeFromStack(currentFragment)
            }

            // Se houver um fragmento anterior na pilha, mostra ele novamente
            if (fragmentManager.backStackEntryCount > 0) {
                val previousFragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.backStackEntryCount - 1).name
                val previousFragment = fragmentManager.findFragmentByTag(previousFragmentTag)

                if (previousFragment != null) {
                    fragmentManager.beginTransaction()
                        .replace(R.id.layout, previousFragment)
                        .commit()
                }
            } else {
                // Se não houver fragmento anterior na pilha, volta para a tela anterior ou faz outra ação necessária
                requireActivity().onBackPressed()
            }
        } else {
            Toast.makeText(requireContext(), getString(R.string.activity_state_saved), Toast.LENGTH_SHORT).show()
        }
    }




    private fun populateFields() {
        if(gameInfo != null){
            Log.d("EditGameFragment", "GameInfo received: $gameInfo")
            gameTitleEditText.setText(gameInfo!!.name)
            descriptionEditText.setText(gameInfo!!.description)
            isFreeCheckBox.isChecked = gameInfo!!.isFree

            // Carregar a imagem do coverImage usando Picasso
            Picasso.get().load(gameInfo!!.coverImage).into(imageView)
            }
    }

    private fun loadCompanies(context: Context) {
        ApiManager.apiService.getCompanies().enqueue(object : Callback<List<Company>> {
            override fun onResponse(call: Call<List<Company>>, response: Response<List<Company>>) {
                if (response.isSuccessful) {
                    companies = response.body() ?: emptyList()
                    if (companies.isNotEmpty()) {
                        companyAdapter = CompanyAdapter(context, companies, companyTitle, false, gameInfo!!.company)
                        companyList.adapter = companyAdapter
                        adjustListViewHeight(companyList)
                    }
                } else {
                    Log.e("AddNewGame", "Erro ao carregar empresas: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Company>>, t: Throwable) {
                Log.e("AddNewGame", "Falha na requisição: ${t.message}")
            }
        })
    }

    private fun loadSequences(context: Context) {
        ApiManager.apiService.getSequences().enqueue(object : Callback<List<Sequence>> {
            override fun onResponse(call: Call<List<Sequence>>, response: Response<List<Sequence>>) {
                if (response.isSuccessful) {
                    sequences = response.body() ?: emptyList()
                    if (sequences.isNotEmpty()) {
                        sequenceAdapter = SequenceAdapter(context, sequences, sequenceTitle, false, gameInfo!!.sequence)
                        sequenceList.adapter = sequenceAdapter
                        adjustListViewHeight(sequenceList)
                    }
                } else {
                    Log.e("AddNewGame", "Erro ao carregar sequências: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Sequence>>, t: Throwable) {
                Log.e("AddNewGame", "Falha na requisição: ${t.message}")
            }
        })
    }

    private fun loadGenres(context: Context) {
        ApiManager.apiService.getGenres().enqueue(object : Callback<List<Genre>> {
            override fun onResponse(call: Call<List<Genre>>, response: Response<List<Genre>>) {
                if (response.isSuccessful) {
                    genres = response.body() ?: emptyList()
                    if (genres.isNotEmpty()) {
                        genreAdapter = GenresAdapter(context, genres, genreTitle, gameInfo!!.genres)
                        genreList.adapter = genreAdapter
                        adjustListViewHeight(genreList)
                    }
                } else {
                    Log.e("AddNewGame", "Erro ao carregar gêneros: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Genre>>, t: Throwable) {
                Log.e("AddNewGame", "Falha na requisição: ${t.message}")
            }
        })
    }

    private fun loadPegiInfo(context: Context) {
        pegiInfoValues = context.resources.getStringArray(R.array.pegi_info_values)
        pegiAdapter = PegyAdapter(context, pegiInfoValues, pegiTitle, gameInfo!!.pegiInfo)
        pegiList.adapter = pegiAdapter
        adjustListViewHeight(pegiList)
    }



    private fun toggleListVisibility(listView: ListView, titleView: TextView, iconResource: Int) {
        val isExpanded = listView.visibility == View.VISIBLE

        val initialHeight = if (isExpanded) listView.height else 0
        val targetHeight = if (isExpanded) 0 else getTargetHeight(listView)

        val valueAnimator = ValueAnimator.ofInt(initialHeight, targetHeight)
        valueAnimator.addUpdateListener { animator ->
            val animatedValue = animator.animatedValue as Int
            val layoutParams = listView.layoutParams
            layoutParams.height = animatedValue
            listView.layoutParams = layoutParams
        }
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                if (!isExpanded) {
                    listView.visibility = View.VISIBLE
                    titleView.setCompoundDrawablesWithIntrinsicBounds(iconResource, 0, R.drawable.icon_down, 0)
                }
            }

            override fun onAnimationEnd(animation: Animator) {
                if (isExpanded) {
                    listView.visibility = View.GONE
                    titleView.setCompoundDrawablesWithIntrinsicBounds(iconResource, 0, R.drawable.icon_up, 0)
                }
                // Reajuste a altura da ListView após a conclusão da animação

            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        valueAnimator.duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
        valueAnimator.start()
    }


    private fun getTargetHeight(view: View): Int {
        val listAdapter = (view as ListView).adapter ?: return 0
        val totalItems = listAdapter.count

        // Calcula a altura dos itens
        var totalHeight = 0
        for (i in 0 until totalItems) {
            val listItem = listAdapter.getView(i, null, view)
            listItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            totalHeight += listItem.measuredHeight
        }

        // Se houver mais de 5 itens, calcula a altura dos primeiros 5 itens
        if (totalItems > 5) {
            totalHeight = 0
            for (i in 0 until 5) {
                val listItem = listAdapter.getView(i, null, view)
                listItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                totalHeight += listItem.measuredHeight
            }
        }

        // Define a altura da ListView
        val params = view.layoutParams
        val targetHeight = if (totalItems > 5) {
            totalHeight + (view.dividerHeight * (5 - 1)) // Altura dos 5 itens + divisores
        } else {
            totalHeight + (view.dividerHeight * (totalItems - 1)) // Altura de todos os itens + divisores
        }

        return targetHeight
    }


    private fun selectVisualMedia() {
        pickVisualMediaLauncher.launch("image/*")
    }

    private fun bitmapToFile(context: Context, bitmap: Bitmap): File {
        val filesDir = context.filesDir
        val imageFile = File(filesDir, "image.jpg")

        val outputStream = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        return imageFile
    }


    private fun adjustListViewHeight(listView: ListView) {
        val listAdapter = listView.adapter ?: return
        val totalItems = listAdapter.count

        // Calcula a altura dos itens
        var totalHeight = 0
        for (i in 0 until totalItems) {
            val listItem = listAdapter.getView(i, null, listView)
            listItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            totalHeight += listItem.measuredHeight
        }

        // Se houver mais de 5 itens, calcula a altura dos primeiros 5 itens
        if (totalItems > 5) {
            totalHeight = 0
            for (i in 0 until 5) {
                val listItem = listAdapter.getView(i, null, listView)
                listItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                totalHeight += listItem.measuredHeight
            }
        }

        // Define a altura da ListView
        val params = listView.layoutParams
        params.height = if (totalItems > 5) {
            totalHeight + (listView.dividerHeight * (5 - 1)) // Altura dos 5 itens + divisores
        } else {
            totalHeight + (listView.dividerHeight * (totalItems - 1)) // Altura de todos os itens + divisores
        }
        listView.layoutParams = params
        listView.requestLayout()

        // Habilita/desabilita o scroll
        listView.isScrollContainer = totalItems > 5
    }

    companion object {
        private const val ARG_GAME = "game"

        @JvmStatic
        fun newInstance(game: GameInfo?) =
            Edit_Game_Fragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_GAME, game)
                }
            }
    }

}
