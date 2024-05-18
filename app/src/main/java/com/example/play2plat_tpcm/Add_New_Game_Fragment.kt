package com.example.play2plat_tpcm

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.Company
import com.example.play2plat_tpcm.api.Game
import com.example.play2plat_tpcm.api.Sequence
import com.example.play2plat_tpcm.api.Genre
import com.example.play2plat_tpcm.api.Platforms
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
import com.example.play2plat_tpcm.adapters.PlatformsAdapter
import com.example.play2plat_tpcm.adapters.PegyAdapter
import com.example.play2plat_tpcm.api.GameGenre
import com.example.play2plat_tpcm.api.GamePlatform
import kotlin.math.min


/**
 * A simple [Fragment] subclass.
 */
class Add_New_Game_Fragment : Fragment() {

    private var selectedImageUri: Uri? = null

    private val pickVisualMediaLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            Log.d("AddNewGame", "Selected image URI: $selectedImageUri")
            imageView.setImageURI(selectedImageUri)
            saveImageToProjectFolder(selectedImageUri!!)
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
    private lateinit var platformAdapter: PlatformsAdapter
    private lateinit var platformAccordion: LinearLayout
    private lateinit var platformTitle: TextView
    private lateinit var platformList: ListView
    private lateinit var platforms: List<Platforms>
    private lateinit var pegiAccordion: LinearLayout
    private lateinit var pegiTitle: TextView
    private lateinit var pegiList: ListView
    private lateinit var pegiInfoValues: Array<String>
    private lateinit var pegiAdapter: PegyAdapter



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_new_game, container, false)

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

        companyAccordion = view.findViewById(R.id.company_accordion)
        companyTitle = view.findViewById(R.id.company_title)
        companyList = view.findViewById(R.id.company_list)

        sequenceAccordion = view.findViewById(R.id.sequence_accordion)
        sequenceTitle = view.findViewById(R.id.sequence_title)
        sequenceList = view.findViewById(R.id.sequence_list)

        genreAccordion = view.findViewById(R.id.genres_accordion)
        genreTitle = view.findViewById(R.id.genres_title)
        genreList = view.findViewById(R.id.genres_list)

        platformAccordion = view.findViewById(R.id.platform_accordion)
        platformTitle = view.findViewById(R.id.platform_title)
        platformList = view.findViewById(R.id.platform_list)

        pegiAccordion = view.findViewById(R.id.pegi_accordion)
        pegiTitle = view.findViewById(R.id.pegi_title)
        pegiList = view.findViewById(R.id.pegi_list)

        var selectedCompanyPosition: Int = -1
        var selectedSequencePosition: Int = -1
        var selectedGenrePosition: Int = -1
        var selectedPlatformPosition: Int = -1

        companyAccordion.setOnClickListener {
            toggleListVisibility(companyList, companyTitle, R.drawable.icon_companies)
        }

        sequenceAccordion.setOnClickListener {
            toggleListVisibility(sequenceList, sequenceTitle, R.drawable.icon_sequence)
        }

        genreAccordion.setOnClickListener {
            toggleListVisibility(genreList, genreTitle, R.drawable.icon_genres)
        }

        platformAccordion.setOnClickListener {
            toggleListVisibility(platformList, platformTitle, R.drawable.icon_platforms)
        }

        pegiAccordion.setOnClickListener {
            toggleListVisibility(pegiList, pegiTitle, R.drawable.icon_age)
        }

        loadCompanies(view.context)
        loadSequences(view.context)
        loadGenres(view.context)
        loadPlatforms(view.context)
        loadPegiInfo(view.context)

        saveButton.setOnClickListener {
            val gameTitle = gameTitleEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val selectedCompanyText = companyTitle.text.toString()
            val selectedSequenceText = sequenceTitle.text.toString()
            val selectedGenreTexts = genreTitle.text.toString().split(", ")
            val selectedPlatformTexts = platformTitle.text.toString().split(", ")
            val selectedPegiInfo = pegiAdapter.getSelectedPosition().takeIf { it != -1 }?.let {
                val pegiInfo = pegiInfoValues[it]
                // Remove o prefixo "Pegi:" do título do Pegi
                val pegiValue = pegiInfo.substringAfter(":").trim()
                pegiValue.toIntOrNull() ?: 0
            } ?: 0
            val isFree = isFreeCheckBox.isChecked

            var selectedCompanyId: Int? = null
            var selectedSequenceId: Int? = null
            val selectedGenreIds = mutableListOf<Int>()
            val selectedPlatformIds = mutableListOf<Int>()

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

            for (platform in platforms) {
                if (selectedPlatformTexts.contains(platform.name)) {
                    selectedPlatformIds.add(platform.id)
                }
            }

            if (selectedCompanyId != null && selectedSequenceId != null && selectedGenreIds.isNotEmpty() && selectedPlatformIds.isNotEmpty()) {
                val newGame = Game(
                    id = null,
                    name = gameTitle,
                    description = description,
                    isFree = isFree,
                    releaseDate = "2024-04-24T00:00:00Z",
                    pegiInfo = selectedPegiInfo,
                    coverImage = selectedImageUri.toString(),
                    sequenceId = selectedSequenceId,
                    companyId = selectedCompanyId,
                )

                Log.d("AddNewGame", "Novo jogo: $newGame")

                ApiManager.apiService.createGame(newGame).enqueue(object : Callback<Game> {
                    override fun onResponse(call: Call<Game>, response: Response<Game>) {
                        if (response.isSuccessful) {
                            val createdGame = response.body()
                            if (createdGame != null) {
                                Log.d("AddNewGame", "Jogo criado com sucesso: $createdGame")
                                for (genreId in selectedGenreIds) {
                                    val gameToGenreAssociation = GameGenre(
                                        gameId = createdGame.id,
                                        genreId = genreId
                                    )

                                    ApiManager.apiService.addGenresToGame(gameToGenreAssociation).enqueue(object : Callback<GameGenre> {
                                        override fun onResponse(call: Call<GameGenre>, response: Response<GameGenre>) {
                                            if (response.isSuccessful) {
                                                Log.d("AddNewGame", "Gênero associado com sucesso: $gameToGenreAssociation")
                                            } else {
                                                Log.e("AddNewGame", "Erro ao associar gênero: ${response.message()}")
                                            }
                                        }

                                        override fun onFailure(call: Call<GameGenre>, t: Throwable) {
                                            Log.e("AddNewGame", "Falha na requisição de associação de gênero: ${t.message}")
                                        }
                                    })
                                }

                                for (platformId in selectedPlatformIds) {
                                    val gameToPlatformAssociation = GamePlatform(
                                        gameId = createdGame.id,
                                        platformId = platformId
                                    )

                                    ApiManager.apiService.addPlatformsToGame(gameToPlatformAssociation).enqueue(object : Callback<GamePlatform> {
                                        override fun onResponse(call: Call<GamePlatform>, response: Response<GamePlatform>) {
                                            if (response.isSuccessful) {
                                                Log.d("AddNewGame", "Plataforma associada com sucesso: $gameToPlatformAssociation")
                                            } else {
                                                Log.e("AddNewGame", "Erro ao associar plataforma: ${response.message()}")
                                            }
                                        }

                                        override fun onFailure(call: Call<GamePlatform>, t: Throwable) {
                                            Log.e("AddNewGame", "Falha na requisição de associação de plataforma: ${t.message}")
                                        }
                                    })
                                }

                            } else {
                                Log.e("AddNewGame", "Erro: Resposta nula ao criar jogo.")
                            }
                        } else {
                            Log.e("AddNewGame", "Erro ao criar jogo: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<Game>, t: Throwable) {
                        Log.e("AddNewGame", "Falha na requisição: ${t.message}")
                    }
                })

            } else {
                Log.e("AddNewGame", "Empresa, sequência, gêneros ou plataformas selecionados não encontrados.")
            }
        }


        return view
    }

    private fun loadCompanies(context: Context) {
        ApiManager.apiService.getCompanies().enqueue(object : Callback<List<Company>> {
            override fun onResponse(call: Call<List<Company>>, response: Response<List<Company>>) {
                if (response.isSuccessful) {
                    companies = response.body() ?: emptyList()
                    if (companies.isNotEmpty()) {
                        companyAdapter = CompanyAdapter(context, companies, companyTitle)
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
                        sequenceAdapter = SequenceAdapter(context, sequences, sequenceTitle)
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
                        genreAdapter = GenresAdapter(context, genres, genreTitle)
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

    private fun loadPlatforms(context: Context) {
        ApiManager.apiService.getPlatforms().enqueue(object : Callback<List<Platforms>> {
            override fun onResponse(call: Call<List<Platforms>>, response: Response<List<Platforms>>) {
                if (response.isSuccessful) {
                    platforms = response.body() ?: emptyList()
                    if (platforms.isNotEmpty()) {
                        platformAdapter = PlatformsAdapter(context, platforms, platformTitle)
                        platformList.adapter = platformAdapter
                        adjustListViewHeight(platformList)
                    }
                } else {
                    Log.e("AddNewGame", "Erro ao carregar gêneros: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Platforms>>, t: Throwable) {
                Log.e("AddNewGame", "Falha na requisição: ${t.message}")
            }
        })
    }

    private fun loadPegiInfo(context: Context) {
        pegiInfoValues = context.resources.getStringArray(R.array.pegi_info_values)
        pegiAdapter = PegyAdapter(context, pegiInfoValues, pegiTitle)
        pegiList.adapter = pegiAdapter
        adjustListViewHeight(pegiList)
    }



    private fun toggleListVisibility(listView: ListView, titleView: TextView, iconResource: Int) {
        if (listView.visibility == View.VISIBLE) {
            listView.visibility = View.GONE
            titleView.setCompoundDrawablesWithIntrinsicBounds(iconResource, 0, R.drawable.icon_up, 0)
        } else {
            listView.visibility = View.VISIBLE
            titleView.setCompoundDrawablesWithIntrinsicBounds(iconResource, 0, R.drawable.icon_down, 0)
        }
    }

    private fun saveImageToProjectFolder(imageUri: Uri) {
        val imageInputStream: InputStream? = context?.contentResolver?.openInputStream(imageUri)

        if (imageInputStream != null) {
            val imageFile = File(requireContext().getExternalFilesDir(null), "saved_image.jpg")

            try {
                val outputStream: OutputStream = FileOutputStream(imageFile)
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (imageInputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }
                outputStream.flush()
                outputStream.close()
                imageInputStream.close()
                Log.d("AddNewGame", "Imagem salva em: ${imageFile.absolutePath}")
            } catch (e: Exception) {
                Log.e("AddNewGame", "Erro ao salvar a imagem: ${e.message}")
            }
        } else {
            Log.e("AddNewGame", "Imagem não encontrada no URI fornecido.")
        }
    }

    private fun selectVisualMedia() {
        pickVisualMediaLauncher.launch("image/*")
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



}
