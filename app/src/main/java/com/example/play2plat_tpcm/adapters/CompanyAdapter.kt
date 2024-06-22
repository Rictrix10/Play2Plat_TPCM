package com.example.play2plat_tpcm.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.example.play2plat_tpcm.R
import com.example.play2plat_tpcm.api.Company

class CompanyAdapter(
    context: Context,
    companies: List<Company>,
    private val companyTitle: TextView,
    private val canSelectMultiple: Boolean = false,
    private val selectedCompanyName: String?
) : ArrayAdapter<Company>(context, 0, companies) {

    private val selectedCompanies: MutableList<Company> = mutableListOf()

    init {
        // Encontrar a empresa selecionada inicialmente
        companies.find { it.name == selectedCompanyName }?.let {
            selectedCompanies.add(it)
        }
        updateCompanyTitle()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(
                R.layout.company_list_item,
                parent,
                false
            )
        }

        val currentCompany = getItem(position)

        val companyNameTextView = listItemView?.findViewById<TextView>(R.id.company_name)
        companyNameTextView?.text = currentCompany?.name

        val companyCheckbox = listItemView?.findViewById<CheckBox>(R.id.company_checkbox)

        // Remove any existing listener before setting the checked state
        companyCheckbox?.setOnCheckedChangeListener(null)

        // Set the checkbox checked state
        companyCheckbox?.isChecked = selectedCompanies.contains(currentCompany)

        // Add the new listener
        companyCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!canSelectMultiple) {
                    clearAllSelections()
                }
                selectedCompanies.add(currentCompany!!)
            } else {
                selectedCompanies.remove(currentCompany)
            }
            updateCompanyTitle()
            notifyDataSetChanged() // Notify the adapter to refresh the list view
        }

        return listItemView!!
    }

    private fun clearAllSelections() {
        selectedCompanies.clear()
    }

    private fun updateCompanyTitle() {
        companyTitle.text = if (selectedCompanies.isNotEmpty()) {
            selectedCompanies.joinToString(", ") { it.name }
        } else {
            // Use `context.getString` para pegar a string traduzida
            context.getString(R.string.companies)
        }
    }

    fun getSelectedCompanies(): List<Company> {
        return selectedCompanies
    }

    fun clearSelection() {
        selectedCompanies.clear()
        notifyDataSetChanged()
    }
}


