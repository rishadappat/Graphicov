package com.appat.graphicov.utilities

import android.content.Context
import com.appat.graphicov.R
import com.appat.graphicov.utilities.extensions.isDarkThemeOn
import com.kieronquinn.monetcompat.core.MonetCompat

object MonetColors {
    fun getBackgroundColor(context: Context, monet: MonetCompat): Int {
        return monet.getBackgroundColor(context, context.isDarkThemeOn())
    }

    fun getBackgroundColorSecondary(context: Context, monet: MonetCompat): Int {
        return monet.getBackgroundColorSecondary(context, context.isDarkThemeOn()) ?: R.color.colorPrimary
    }

    fun getAccentColor(context: Context, monet: MonetCompat): Int {
        return monet.getAccentColor(context, context.isDarkThemeOn())
    }

    fun getPrimaryColor(context: Context, monet: MonetCompat): Int {
        return monet.getPrimaryColor(context, context.isDarkThemeOn())
    }
}