package com.appat.graphicov.utilities.viewcomponents

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import com.appat.graphicov.R
import com.appat.graphicov.databinding.MarkerViewBinding
import com.appat.graphicov.utilities.Utility
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF


class CustomMarkerView(context: Context?) : MarkerView(
    context,
    R.layout.marker_view
) {

    var binding: MarkerViewBinding? = null

    init {
        binding = MarkerViewBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        Log.d("XValue", e?.x.toString())
        binding?.tvContent?.text = Utility.formatLong(e?.y?.toLong() ?: 0)
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }
}