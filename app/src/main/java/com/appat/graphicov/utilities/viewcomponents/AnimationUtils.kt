package com.appat.graphicov.utilities.viewcomponents

import android.app.Activity
import android.view.View
import android.view.Window
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair

fun makeSceneTransitionAnimation(activity: Activity, vararg pairs: Pair<View, String>): ActivityOptionsCompat {
    val updatedPairs = pairs.toMutableList()
    val navBar = activity.findViewById<View>(android.R.id.navigationBarBackground)
    if (navBar != null) {
        updatedPairs.add(Pair(navBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME))
    }
    return ActivityOptionsCompat.makeSceneTransitionAnimation(activity, *updatedPairs.toTypedArray())
}