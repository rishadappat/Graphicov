package com.appat.graphicov.utilities.extensions

import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet


val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Float.dp: Float
    get() = (this / Resources.getSystem().displayMetrics.density)
val Float.px: Float
    get() = (this * Resources.getSystem().displayMetrics.density)

fun View.setHeight(height: Int, animated: Boolean)
{
    if(animated) {
        TransitionManager.beginDelayedTransition(
            this.parent as ViewGroup, TransitionSet()
                .addTransition(ChangeBounds())
        )
    }
    val params: ViewGroup.LayoutParams = this.layoutParams
    params.height = height
    this.layoutParams = params
}

fun View.setWidth(width: Int, animated: Boolean)
{
    if(animated) {
        TransitionManager.beginDelayedTransition(
            this.parent as ViewGroup, TransitionSet()
                .addTransition(ChangeBounds())
        )
    }
    val params: ViewGroup.LayoutParams = this.layoutParams
    params.width = width
    this.layoutParams = params
}

fun View.resize(width: Int, height: Int, animated: Boolean)
{
    if(animated) {
        TransitionManager.beginDelayedTransition(
            this.parent as ViewGroup, TransitionSet()
                .addTransition(ChangeBounds())
        )
    }
    val params: ViewGroup.LayoutParams = this.layoutParams
    params.width = width
    params.height = height
    this.layoutParams = params
}

fun Context.isDarkThemeOn(): Boolean {
    return resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
}