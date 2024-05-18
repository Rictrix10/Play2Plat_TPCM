package com.example.play2plat_tpcm

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.Company
import com.example.play2plat_tpcm.api.Game
import com.example.play2plat_tpcm.api.Sequence
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import com.google.gson.Gson
import com.google.gson.JsonParser


class AddNewGame : AppCompatActivity() {

    private var selectedImageUri: Uri? = null


    private val pickVisualMediaLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri // Salva a URI da imagem selecionada
            Log.d("AddNewGame", "Selected image URI: $selectedImageUri")
            imageView.setImageURI(selectedImageUri)

        } else {
            Log.d("AddNewGame", "No image URI received")
        }
    }



    private lateinit var gameTitleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var companySpinner: Spinner
    private lateinit var sequenceSpinner: Spinner
    private lateinit var pegiInfoSpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var imageView: ImageView
    private lateinit var isFreeCheckBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.fragment_add_new_game)

        imageView = findViewById(R.id.image_view) // Assuming your ImageView has this id

        val pickImageButton = findViewById<Button>(R.id.pick_image)
        pickImageButton.setOnClickListener {
            Log.d("AddNewGame", "Pick image button clicked")
            selectVisualMedia()
        }

        gameTitleEditText = findViewById(R.id.game_title)
        descriptionEditText = findViewById(R.id.description)

        saveButton = findViewById(R.id.save)
        isFreeCheckBox = findViewById(R.id.is_free_checkbox)


        loadCompanies()
        loadSequences()
        loadPegiInfo()



        saveButton.setOnClickListener {
            val gameTitle = gameTitleEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val selectedCompany = companySpinner.selectedItem as Company
            val selectedSequence = sequenceSpinner.selectedItem as Sequence
            val selectedPegiInfo = pegiInfoSpinner.selectedItem.toString().toInt()
            val isFree = isFreeCheckBox.isChecked

            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageUri)
            val file = bitmapToFile(this, bitmap)

            val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
            val imagePart = MultipartBody.Part.createFormData("file", "image.jpg", requestFile)

            val call = ApiManager.apiService.uploadImage(imagePart)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val imageUrl = response.body()?.string()
                        imageUrl?.let {
                            val pattern = Regex("\"url\":\"(\\S+)\"") // Cria um padrão regex para extrair a URL
                            val matchResult = pattern.find(it)

                            matchResult?.let { result ->
                                val coverImageUrl = result.groupValues[1] // Obtém o valor correspondente ao grupo capturado

                                val newGame = Game(
                                    id = null,
                                    name = gameTitle,
                                    description = description,
                                    isFree = isFree,
                                    releaseDate = "2024-04-24T00:00:00Z",
                                    pegiInfo = selectedPegiInfo,
                                    coverImage = coverImageUrl, // Atribui a URL da imagem
                                    sequenceId = selectedSequence.id,
                                    companyId = selectedCompany.id,
                                )

                                Log.d("AddNewGame", "Novo jogo: $newGame")

                                // Fazer a chamada para a API para salvar o jogo
                                ApiManager.apiService.createGame(newGame).enqueue(object : Callback<Game> {
                                    override fun onResponse(call: Call<Game>, response: Response<Game>) {
                                        if (response.isSuccessful) {
                                            val createdGame = response.body()
                                            Log.d("AddNewGame", "Jogo criado com sucesso: $createdGame")
                                        } else {
                                            Log.e("AddNewGame", "Erro ao criar jogo: ${response.message()}")
                                        }
                                    }

                                    override fun onFailure(call: Call<Game>, t: Throwable) {
                                        Log.e("AddNewGame", "Falha na requisição: ${t.message}")
                                    }
                                })
                            }
                        }
                    } else {
                        // Erro no upload
                        Log.e("AddNewGame", "Erro no upload: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // Erro na requisição
                }
            })

        }
    }

    private fun loadCompanies() {
        ApiManager.apiService.getCompanies().enqueue(object : Callback<List<Company>> {
            override fun onResponse(call: Call<List<Company>>, response: Response<List<Company>>) {
                if (response.isSuccessful) {
                    val companies = response.body()
                    if (companies != null) {
                        val adapter = CompanySpinnerAdapter(this@AddNewGame, android.R.layout.simple_spinner_item, companies)
                        companySpinner.adapter = adapter
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

    private fun loadSequences() {
        ApiManager.apiService.getSequences().enqueue(object : Callback<List<Sequence>> {
            override fun onResponse(call: Call<List<Sequence>>, response: Response<List<Sequence>>) {
                if (response.isSuccessful) {
                    val sequences = response.body()
                    if (sequences != null) {
                        val adapter = SequenceSpinnerAdapter(this@AddNewGame, android.R.layout.simple_spinner_item, sequences)
                        sequenceSpinner.adapter = adapter
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

    private fun loadPegiInfo() {
        val pegiInfoValues = resources.getStringArray(R.array.pegi_info_values)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, pegiInfoValues)
        pegiInfoSpinner.adapter = adapter
    }



    private fun selectVisualMedia() {
        pickVisualMediaLauncher.launch("image/*") // Inicia a seleção de imagem
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



}
