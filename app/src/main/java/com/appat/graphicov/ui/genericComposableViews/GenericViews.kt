package com.appat.graphicov.ui.genericComposableViews

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.appat.graphicov.R

class GenericViews {
    @Composable
    fun GraphicovButton(@StringRes text:Int, onClick: () -> Unit)
    {
        OutlinedButton(
            modifier= Modifier.height(50.dp),
            onClick = onClick,
            shape = CircleShape,
            border = BorderStroke(1.dp, colorResource(R.color.colorAccent)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = colorResource(R.color.colorAccent), backgroundColor = Color.Transparent)
        )
        {
            Text(text = stringResource(text))
        }
    }

    @Composable
    fun GraphicovFillButton(@StringRes text:Int, onClick: () -> Unit)
    {
        OutlinedButton(
            modifier= Modifier.height(50.dp).fillMaxWidth(),
            onClick = onClick,
            shape = CircleShape,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = colorResource(R.color.black), backgroundColor = colorResource(R.color.colorAccent))
        )
        {
            Text(text = stringResource(text))
        }
    }

    @Composable
    fun GraphicovSmallButton(@StringRes text:Int, onClick: () -> Unit)
    {
        OutlinedButton(
            modifier= Modifier.height(40.dp),
            onClick = onClick,
            shape = CircleShape,
            border = BorderStroke(1.dp, colorResource(R.color.colorAccent)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = colorResource(R.color.colorAccent), backgroundColor = Color.Transparent)
        )
        {
            Text(text = stringResource(text))
        }
    }
}