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
import android.widget.CompoundButton

class CompanyAdapter(context: Context, companies: List<Company>, private val companyTitle: TextView) :
    ArrayAdapter<Company>(context, 0, companies) {

    private var selectedCompanyPosition: Int = -1

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
        companyCheckbox?.setOnCheckedChangeListener(null) // Remove listener before setting state
        companyCheckbox?.isChecked = position == selectedCompanyPosition
        companyCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedCompanyPosition = position
                notifyDataSetChanged() // Notify the adapter of the change to update the views
                companyTitle.text = currentCompany?.name
            } else {
                selectedCompanyPosition = -1
                notifyDataSetChanged() // Notify the adapter of the change to update the views
                companyTitle.text = "Company"
            }
        }

        if (selectedCompanyPosition == -1) {
            companyTitle.text = "Company"
        }

        return listItemView!!
    }
}


