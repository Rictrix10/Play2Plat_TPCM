package com.example.play2plat_tpcm.adapters

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.example.play2plat_tpcm.R

class CollectionsAdapter(
    context: Context,
    private val values: Array<String>,
    private val collectionTitle: TextView,
    private val onOptionSelected: (String?) -> Unit // Callback para notificar a seleção
) : ArrayAdapter<String>(context, 0, values) {

    private var selectedPosition: Int = -1
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("CollectionsPrefs", Context.MODE_PRIVATE)

    init {
        // Restaura a posição selecionada de SharedPreferences
        selectedPosition = sharedPreferences.getInt("selectedPosition", -1)
        if (selectedPosition != -1) {
            collectionTitle.text = values[selectedPosition]
            onOptionSelected(values[selectedPosition])
        } else {
            // Verifica se algum item corresponde ao título atual
            for (i in values.indices) {
                if (isTitleMatching(i)) {
                    selectedPosition = i
                    onOptionSelected(values[i])
                    break
                }
            }
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.collections_list_item, parent, false)
        val linearLayout = view.findViewById<LinearLayout>(R.id.collection_item_layout)
        val textView = view.findViewById<TextView>(R.id.collection_name)

        textView.text = values[position]

        // Verifica se a posição atual é a posição selecionada ou se o título corresponde
        val isSelected = position == selectedPosition || isTitleMatching(position)

        // Atualiza a cor de fundo do LinearLayout com base na seleção ou correspondência de título
        val backgroundColor = if (isSelected) {
            context.getColor(R.color.opaque_WHITE) // Define a cor de fundo para branco opaco se estiver selecionado ou corresponder ao título
        } else {
            // Se não estiver selecionado ou corresponder ao título, define a cor de fundo como transparente
            context.getColor(android.R.color.transparent)
        }
        linearLayout.setBackgroundColor(backgroundColor)

        // Definir um ouvinte de clique no item da lista inteira
        view.setOnClickListener {
            if (selectedPosition == position) {
                // Se a posição já estiver selecionada, desmarque-a
                selectedPosition = -1
                collectionTitle.text = "Collections" // Limpa o título da coleção na tela principal
                onOptionSelected(null) // Notificar que a opção foi desmarcada
            } else {
                // Atualiza a posição selecionada
                selectedPosition = position
                // Atualiza o título da coleção na tela principal
                collectionTitle.text = values[position]
                // Chama o callback para notificar a seleção
                onOptionSelected(values[position])
            }
            // Salva a posição selecionada em SharedPreferences
            with(sharedPreferences.edit()) {
                putInt("selectedPosition", selectedPosition)
                apply()
            }
            // Notifica o adaptador sobre a alteração para atualizar a interface do usuário
            notifyDataSetChanged()
        }

        return view
    }

    fun getSelectedPosition(): Int {
        return selectedPosition
    }

    private fun isTitleMatching(position: Int): Boolean {
        return values[position] == collectionTitle.text.toString()
    }
}
