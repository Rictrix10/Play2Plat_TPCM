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
    private val canSelectMultiple: Boolean = false
) : ArrayAdapter<Company>(context, 0, companies) {

    private val selectedCompanies: MutableList<Company> = mutableListOf()

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

        companyCheckbox?.isChecked = selectedCompanies.contains(currentCompany)

        companyCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!canSelectMultiple) {
                    selectedCompanies.clear()
                }
                selectedCompanies.add(currentCompany!!)
            } else {
                selectedCompanies.remove(currentCompany)
            }
            updateCompanyTitle()
        }

        updateCompanyTitle()

        return listItemView!!
    }

    private fun updateCompanyTitle() {
        companyTitle.text = if (selectedCompanies.isNotEmpty()) {
            selectedCompanies.joinToString(", ") { it.name }
        } else {
            "Company"
        }
    }
}
