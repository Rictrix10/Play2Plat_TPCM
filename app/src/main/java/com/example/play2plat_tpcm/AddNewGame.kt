package com.example.play2plat_tpcm

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.Game
import com.example.play2plat_tpcm.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date
import java.util.Locale

class AddNewGame : AppCompatActivity() {
    private lateinit var gameTitleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var companySpinner: Spinner
    private lateinit var pegiInfoSpinner: Spinner
    private lateinit var saveButton: Button

    private fun formatDate(dateTimeString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val date: Date = inputFormat.parse(dateTimeString) ?: Date()

        return outputFormat.format(date)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_new_game)

        gameTitleEditText = findViewById(R.id.game_title)
        descriptionEditText = findViewById(R.id.description)
        companySpinner = findViewById(R.id.company)
        pegiInfoSpinner = findViewById(R.id.pegi_info)
        saveButton = findViewById(R.id.save)

        saveButton.setOnClickListener {

            val gameTitle = gameTitleEditText.text.toString()
            val description = descriptionEditText.text.toString()
            /*
            val company = companySpinner.selectedItem.toString()
            val pegiInfo = pegiInfoSpinner.selectedItem.toString().toInt()
             */
            //val releaseDate = formatDate("2024-04-24T12:00:00Z")

            val newGame = Game(
                name = gameTitle,
                description = description,
                isFree = false,
                //releaseDate = releaseDate,
                releaseDate = "2024-04-24T00:00:00Z",
                pegiInfo = 18,
                coverImage = "gta5.png",
                sequenceId = 1,
                companyId = 1
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
}
