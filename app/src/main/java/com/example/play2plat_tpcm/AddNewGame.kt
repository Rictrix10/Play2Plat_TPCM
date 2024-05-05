package com.example.play2plat_tpcm

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class AddNewGame : AppCompatActivity() {

    private lateinit var selectedImageUri: Uri

    private val pickVisualMediaLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri // Salva a URI da imagem selecionada
            Log.d("AddNewGame", "Selected image URI: $selectedImageUri")
            imageView.setImageURI(selectedImageUri)
            saveImageToFolder(selectedImageUri)
        } else {
            Log.d("AddNewGame", "No image URI received")
        }
    }


    private lateinit var gameTitleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var companySpinner: Spinner
    private lateinit var sequenceSpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_new_game)

        imageView = findViewById(R.id.image_view) // Assuming your ImageView has this id

        val pickImageButton = findViewById<Button>(R.id.pick_image)

        pickImageButton.setOnClickListener {
            Log.d("AddNewGame", "Pick image button clicked")
            selectVisualMedia()
        }

        gameTitleEditText = findViewById(R.id.game_title)
        descriptionEditText = findViewById(R.id.description)
        companySpinner = findViewById(R.id.company)
        sequenceSpinner = findViewById(R.id.sequence)
        saveButton = findViewById(R.id.save)


        loadCompanies()
        loadSequences()

        saveButton.setOnClickListener {
            val gameTitle = gameTitleEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val selectedCompany = companySpinner.selectedItem as Company // Obter a empresa selecionada

            val newGame = Game(
                name = gameTitle,
                description = description,
                isFree = false,
                releaseDate = "2024-04-24T00:00:00Z",
                pegiInfo = 18,
                coverImage = selectedImageUri.toString(),
                sequenceId = 1,
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

    private fun saveImageToFolder(imageUri: Uri) {
        val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
        val outputStream: OutputStream
        try {
            val folder = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "images-games")
            if (!folder.exists()) {
                folder.mkdirs() // Cria o diretório se não existir
            }
            val imageFile = File(folder, "image.jpg")
            outputStream = FileOutputStream(imageFile)
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream?.read(buffer).also { bytesRead = it!! } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            inputStream?.close()
            outputStream.close()
            Log.d("AddNewGame", "Imagem salva em: ${imageFile.absolutePath}")
        } catch (e: Exception) {
            Log.e("AddNewGame", "Erro ao salvar imagem: ${e.message}")
        }
    }


    private fun selectVisualMedia() {
        pickVisualMediaLauncher.launch("image/*") // Inicia a seleção de imagem
    }
}
