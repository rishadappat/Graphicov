package com.appat.graphicov.utilities

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.appat.graphicov.ui.composableViews.WelcomeScreenViews

class PreviewView {
    @Preview
    @Composable
    fun ComposablePreview() {
        WelcomeScreenViews().Layout(
            country = null,
            changeClicked = {  },
            selectCountryClicked = {  }) {

        }
    }
}