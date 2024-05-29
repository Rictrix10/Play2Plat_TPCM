package com.example.play2plat_tpcm.adapters

import android.content.Context
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
    private val onOptionSelected: (String) -> Unit // Callback para notificar a seleção
) : ArrayAdapter<String>(context, 0, values) {

    private var selectedPosition: Int = -1

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
                collectionTitle.text = "Collections" // Limpa o título da coleção na tela principal
            } else {
                // Atualiza a posição selecionada
                selectedPosition = position
                // Atualiza o título da coleção na tela principal
                collectionTitle.text = "${values[position]}"
                // Chama o callback para notificar a seleção
                onOptionSelected(values[position])
            }
            // Notifica o adaptador sobre a alteração para atualizar a interface do usuário
            notifyDataSetChanged()
        }

        return view
    }

    fun getSelectedPosition(): Int {
        return selectedPosition
    }
}



