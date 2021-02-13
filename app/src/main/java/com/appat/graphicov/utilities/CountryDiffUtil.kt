package com.appat.graphicov.utilities

import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import com.appat.graphicov.roomdb.entities.CountryDataEntity

class CountryDiffUtil (private val oldList: List<CountryDataEntity>, private val newList: List<CountryDataEntity>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].countryId === newList[newItemPosition].countryId
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return oldList[oldPosition].countryName == newList[newPosition].countryName
    }

    @Nullable
    override fun getChangePayload(oldPosition: Int, newPosition: Int): Any? {
        return super.getChangePayload(oldPosition, newPosition)
    }
}