package com.appat.graphicov.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.appat.graphicov.databinding.GlobalDataItemBinding
import com.appat.graphicov.roomdb.entities.CountryDataEntity
import com.appat.graphicov.ui.ListDataActivity
import com.appat.graphicov.utilities.CountryDiffUtil
import com.appat.graphicov.utilities.Utility
import com.bumptech.glide.Glide

class ListDataAdapter(private val activity: ListDataActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class ItemType(val item: Int)
    {
        ITEM(0)
    }

    private val countries = ArrayList<CountryDataEntity>()


    inner class ListDataViewHolder(itemBinding: GlobalDataItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        val flagImage: AppCompatImageView = itemBinding.flagImage
        val countryName: TextView = itemBinding.countryName
        val effected: TextView = itemBinding.effectedTextView
        val recovered: TextView = itemBinding.recoveredTextView
        val death: TextView = itemBinding.deathTextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemBinding = GlobalDataItemBinding.inflate(LayoutInflater.from(parent.context))
        return ListDataViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return countries.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ListDataViewHolder) {
            val currentItem = countries[position]
            holder.countryName.text = currentItem.country
            holder.effected.text = Utility.formatLong(currentItem.cases ?: 0)
            holder.death.text = Utility.formatLong(currentItem.deaths ?: 0)
            holder.recovered.text = Utility.formatLong(currentItem.recovered ?: 0)
            Glide.with(activity)
                .load(currentItem.flag)
                .into(holder.flagImage)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return ItemType.ITEM.item
    }

    fun setData(data: List<CountryDataEntity>) {
        if(data.isNotEmpty()) {
            val diffCallback = CountryDiffUtil(countries, data)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            countries.clear()
            countries.addAll(data)
            diffResult.dispatchUpdatesTo(this)
        }
    }
}