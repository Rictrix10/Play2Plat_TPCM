package com.example.play2plat_tpcm

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.Game
import com.example.play2plat_tpcm.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddNewGame : AppCompatActivity() {

    private lateinit var selectedImageUri: Uri // URI da imagem selecionada

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
    private lateinit var pegiInfoSpinner: Spinner
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
        //companySpinner = findViewById(R.id.company)
        pegiInfoSpinner = findViewById(R.id.pegi_info)
        saveButton = findViewById(R.id.save)

        saveButton.setOnClickListener {
            val gameTitle = gameTitleEditText.text.toString()
            val description = descriptionEditText.text.toString()
            //val companyId = companySpinner.selectedItemId // Obtém o ID da empresa selecionada
            //val pegiInfo = pegiInfoSpinner.selectedItem.toString().toInt() // Obtém a classificação PEGI selecionada

            val newGame = Game(
                name = gameTitle,
                description = description,
                isFree = false,
                releaseDate = "2024-04-24T00:00:00Z",
                pegiInfo = 18,
                coverImage = selectedImageUri.toString(), 
                //coverImage = "image.png",
                sequenceId = 1,
                companyId = 1,
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

    private fun selectVisualMedia() {
        pickVisualMediaLauncher.launch("image/*") // Inicia a seleção de imagem
    }
}
