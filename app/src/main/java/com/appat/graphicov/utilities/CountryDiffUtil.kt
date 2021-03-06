package com.appat.graphicov.utilities

import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import com.appat.graphicov.roomdb.entities.CountryDataEntity
import com.appat.graphicov.utilities.viewcomponents.Change

class CountryDiffUtil (private val oldList: List<CountryDataEntity>, private val newList: List<CountryDataEntity>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].countryId === newList[newItemPosition].countryId
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return oldList[oldPosition].countryId == newList[newPosition].countryId
    }

    @Nullable
    override fun getChangePayload(oldPosition: Int, newPosition: Int): Any? {
        val oldItem = oldList[oldPosition]
        val newItem = newList[newPosition]

        return Change(
            oldItem,
            newItem)
    }
}