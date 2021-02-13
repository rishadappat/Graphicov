package com.appat.graphicov.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.appat.graphicov.databinding.CountryPickerItemBinding
import com.appat.graphicov.ui.CountrySelectionActivity
import com.appat.graphicov.roomdb.entities.CountryDataEntity
import com.appat.graphicov.utilities.CountryDiffUtil
import com.appat.graphicov.utilities.extensions.px
import com.appat.graphicov.utilities.extensions.resize
import com.bumptech.glide.Glide

class CountryPickerAdapter(private val activity: CountrySelectionActivity
) : RecyclerView.Adapter<CountryPickerAdapter.CountriesViewHolder>() {

    private val countries = ArrayList<CountryDataEntity>()

    var selectedCountryCode = ""

    inner class CountriesViewHolder(itemBinding: CountryPickerItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        val flagImage: AppCompatImageView = itemBinding.flagImage
        val countryName: TextView = itemBinding.countryName
        val tick: AppCompatImageView = itemBinding.tick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountriesViewHolder {
        val itemBinding = CountryPickerItemBinding.inflate(LayoutInflater.from(parent.context))
        return CountriesViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return countries.size
    }

    override fun onBindViewHolder(holder: CountriesViewHolder, position: Int) {
        val currentItem = countries[position]
        holder.countryName.text = currentItem.country
        Glide.with(activity)
            .load(currentItem.flag)
            .into(holder.flagImage)

        if(selectedCountryCode == currentItem.iso2)
        {
            holder.tick.resize(20.px, 20.px, false)
        }
        else {
            holder.tick.resize(0.px, 0.px, false)
        }
        holder.itemView.setOnClickListener {
            notifyItemChanged(getIndex(selectedCountryCode))
            selectedCountryCode = currentItem.iso2 ?: ""
            activity.showFab()
            holder.tick.resize(20.px, 20.px, true)
            notifyItemChanged(getIndex(selectedCountryCode))
        }
    }

    fun setData(data: List<CountryDataEntity>) {
        if(data.size != countries.size) {
            val diffCallback = CountryDiffUtil(countries, data)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            countries.clear()
            countries.addAll(data)
            diffResult.dispatchUpdatesTo(this)
            Log.d("Loaded Data", data.size.toString())
        }
    }

    private fun getIndex(countryISO: String): Int {
        return countries.indexOfFirst{ it.iso2 == countryISO }
    }
}