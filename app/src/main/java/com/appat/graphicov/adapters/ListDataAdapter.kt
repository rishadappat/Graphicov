package com.appat.graphicov.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.appat.graphicov.databinding.GlobalDataHeaderBinding
import com.appat.graphicov.databinding.GlobalDataItemBinding
import com.appat.graphicov.databinding.GlobalDataItemHeaderBinding
import com.appat.graphicov.models.responses.GlobalDataResponse
import com.appat.graphicov.roomdb.entities.CountryDataEntity
import com.appat.graphicov.ui.ListDataActivity
import com.appat.graphicov.utilities.CountryDiffUtil
import com.appat.graphicov.utilities.Utility
import com.bumptech.glide.Glide

class ListDataAdapter(private val activity: ListDataActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class ItemType(val item: Int)
    {
        HEADER(0),
        ITEMHEADER(1),
        ITEM(2)
    }

    private val countries = ArrayList<CountryDataEntity>()
    private var todayGlobalDataResponse: GlobalDataResponse? = null


    inner class ListDataViewHolder(itemBinding: GlobalDataItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        val flagImage: AppCompatImageView = itemBinding.flagImage
        val countryName: TextView = itemBinding.countryName
        val effected: TextView = itemBinding.effectedTextView
        val recovered: TextView = itemBinding.recoveredTextView
        val death: TextView = itemBinding.deathTextView
    }

    inner class ListDataItemHeaderViewHolder(itemHeaderBinding: GlobalDataItemHeaderBinding) : RecyclerView.ViewHolder(itemHeaderBinding.root)

    inner class ListDataHeaderViewHolder(itemHeaderBinding: GlobalDataHeaderBinding) : RecyclerView.ViewHolder(itemHeaderBinding.root) {
        val effectedTextView = itemHeaderBinding.effectedTextView
        val recoveredTextView = itemHeaderBinding.recoveredTextView
        val deathTextView = itemHeaderBinding.deathTextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == ItemType.HEADER.item)
        {
            val itemBinding = GlobalDataHeaderBinding.inflate(LayoutInflater.from(parent.context))
            return ListDataHeaderViewHolder(itemBinding)
        }
        if(viewType == ItemType.ITEMHEADER.item)
        {
            val itemBinding = GlobalDataItemHeaderBinding.inflate(LayoutInflater.from(parent.context))
            return ListDataItemHeaderViewHolder(itemBinding)
        }
        val itemBinding = GlobalDataItemBinding.inflate(LayoutInflater.from(parent.context))
        return ListDataViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return countries.size + 2
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ListDataViewHolder) {
            val currentItem = countries[position - 2]
            holder.countryName.text = currentItem.country
            holder.effected.text = Utility.formatLong(currentItem.cases ?: 0)
            holder.death.text = Utility.formatLong(currentItem.deaths ?: 0)
            holder.recovered.text = Utility.formatLong(currentItem.recovered ?: 0)
            Glide.with(activity)
                .load(currentItem.flag)
                .into(holder.flagImage)
        }
        else if(holder is ListDataHeaderViewHolder)
        {
            holder.effectedTextView.animateText(Utility.formatLong(todayGlobalDataResponse?.cases ?: 0))
            holder.recoveredTextView.animateText(Utility.formatLong(todayGlobalDataResponse?.recovered ?: 0))
            holder.deathTextView.animateText(Utility.formatLong(todayGlobalDataResponse?.deaths ?: 0))
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(position == 0)
        {
            return ItemType.HEADER.item
        }
        if(position == 1)
        {
            return ItemType.ITEMHEADER.item
        }
        return ItemType.ITEM.item
    }

    fun setData(data: List<CountryDataEntity>) {
        if(data.isNotEmpty() && data.size != countries.size) {
            val diffCallback = CountryDiffUtil(countries, data)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            countries.clear()
            countries.addAll(data)
            diffResult.dispatchUpdatesTo(this)
            Log.d("Loaded Data", data.size.toString())
        }
    }

    fun setGlobalData(globalData: GlobalDataResponse)
    {
        todayGlobalDataResponse = globalData
        notifyItemChanged(0)
    }
}