package com.example.play2plat_tpcm.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.example.play2plat_tpcm.R
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.UserGame
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CollectionsAdapter(
    context: Context,
    private val values: Array<String>,
    private val collectionTitle: TextView,
    private val userId: Int,
    private val gameId: Int
) : ArrayAdapter<String>(context, 0, values) {

    private var selectedPosition: Int = -1

    companion object {
        private const val TAG = "CollectionsAdapter"

        // Constantes para os estados de coleção em inglês
        private const val WISH_LIST = "Wish List"
        private const val PLAYING = "Playing"
        private const val PAUSED = "Paused"
        private const val CONCLUDED = "Concluded"

        // Mapa para mapear estados de coleção nos diferentes idiomas para o inglês
        private val collectionTranslationMap = mapOf(
            "Lista de Desejos" to WISH_LIST,
            "A jogar" to PLAYING,
            "Pausado" to PAUSED,
            "Concluído" to CONCLUDED
        )
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.collections_list_item, parent, false)
        val linearLayout = view.findViewById<LinearLayout>(R.id.collection_item_layout)
        val textView = view.findViewById<TextView>(R.id.collection_name)

        textView.text = values[position]

        // Verifica se a posição atual é a posição selecionada
        val isSelected = position == selectedPosition

        // Atualiza a cor de fundo do LinearLayout com base na seleção
        val backgroundColor = if (isSelected) {
            context.getColor(R.color.opaque_WHITE) // Define a cor de fundo para branco opaco se estiver selecionado
        } else {
            // Se não estiver selecionado, define a cor de fundo como transparente
            context.getColor(android.R.color.transparent)
        }
        linearLayout.setBackgroundColor(backgroundColor)

        // Definir um ouvinte de clique no item da lista inteira
        view.setOnClickListener {
            if (selectedPosition == position) {
                // Se a posição já estiver selecionada, desmarque-a
                selectedPosition = -1
                collectionTitle.text = context.getString(R.string.collections)// Limpa o título da coleção na tela principal
                deleteUserGame()
            } else {
                val previousPosition = selectedPosition
                // Atualiza a posição selecionada
                selectedPosition = position
                // Atualiza o título da coleção na tela principal
                collectionTitle.text = values[position]
                if (previousPosition == -1) {
                    addUserGame(translateToEnglish(values[position]))
                } else {
                    updateUserGame(translateToEnglish(values[position]))
                }
            }
            // Notifica o adaptador sobre a alteração para atualizar a interface do usuário
            notifyDataSetChanged()
        }

        return view
    }

    fun getSelectedPosition(): Int {
        return selectedPosition
    }

    fun updateSelectedPosition(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }

    private fun translateToEnglish(state: String): String {
        // Verifica se há uma tradução disponível, caso contrário, retorna o próprio estado
        return collectionTranslationMap[state] ?: state
    }

    private fun addUserGame(state: String) {
        val userGame = UserGame(userId, gameId, state)
        Log.d(TAG, "addUserGame: Adding user game: $userGame")
        ApiManager.apiService.addUserGame(userGame).enqueue(object : Callback<UserGame> {
            override fun onResponse(call: Call<UserGame>, response: Response<UserGame>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "addUserGame: User game added successfully")
                    // Handle success
                } else {
                    Log.e(TAG, "addUserGame: Failed to add user game. Error: ${response.message()}")
                    // Handle error
                }
            }

            override fun onFailure(call: Call<UserGame>, t: Throwable) {
                Log.e(TAG, "addUserGame: Failed to add user game. Exception: ${t.message}")
                // Handle failure
            }
        })
    }

    private fun updateUserGame(state: String) {
        val userGame = UserGame(userId, gameId, state)
        Log.d(TAG, "updateUserGame: Updating user game: $userGame")
        ApiManager.apiService.updateUserGame(userId, gameId, userGame).enqueue(object : Callback<UserGame> {
            override fun onResponse(call: Call<UserGame>, response: Response<UserGame>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "updateUserGame: User game updated successfully")
                    // Handle success
                } else {
                    Log.e(TAG, "updateUserGame: Failed to update user game. Error: ${response.message()}")
                    // Handle error
                }
            }

            override fun onFailure(call: Call<UserGame>, t: Throwable) {
                Log.e(TAG, "updateUserGame: Failed to update user game. Exception: ${t.message}")
                // Handle failure
            }
        })
    }

    private fun deleteUserGame() {
        Log.d(TAG, "deleteUserGame: Deleting user game with userId=$userId, gameId=$gameId")
        ApiManager.apiService.deleteUserGame(userId, gameId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "deleteUserGame: User game deleted successfully")
                    // Handle success
                } else {
                    Log.e(TAG, "deleteUserGame: Failed to delete user game. Error: ${response.message()}")
                    // Handle error
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e(TAG, "deleteUserGame: Failed to delete user game. Exception: ${t.message}")
                // Handle failure
            }
        })
    }
}

