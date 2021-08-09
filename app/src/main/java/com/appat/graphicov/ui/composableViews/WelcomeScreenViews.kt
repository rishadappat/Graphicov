package com.appat.graphicov.ui.composableViews

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.appat.graphicov.R
import com.appat.graphicov.roomdb.entities.CountryDataEntity
import com.appat.graphicov.ui.genericComposableViews.GenericViews
import com.google.accompanist.glide.rememberGlidePainter

class WelcomeScreenViews {

    @Composable
    fun Layout(country: CountryDataEntity?, changeClicked: () -> Unit, selectCountryClicked: () -> Unit, proceedClicked: () -> Unit)
    {
        Column {
            TitleContainer()
            SplashImage()
            SelectCountryContainer(country, changeClicked, selectCountryClicked)
            ProceedButton(proceedClicked)
        }
    }

    @Composable
    fun TitleContainer() {
        Column {
            Text(
                text = stringResource(R.string.thank_you_for_downloading),
                color = colorResource(R.color.white),
                fontSize = 18.sp
            )
            Text(
                text = stringResource(R.string.app_name),
                color = colorResource(R.color.white),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    @Composable
    fun SplashImage() {
        Column(verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)) {
            val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/splash_anim.json"))
            LottieAnimation(composition, speed = 0.8.toFloat())
        }
    }

    @Composable
    fun SelectCountryTitle()
    {
        Text(
            text = stringResource(R.string.thank_you_for_downloading),
            color = colorResource(R.color.white),
            fontSize = 16.sp
        )
    }

    @Composable
    fun SelectCountryContainer(country: CountryDataEntity?, changeClicked: () -> Unit, selectCountryClicked: () -> Unit)
    {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth() ){
            ProgressIndicator()
            SelectCountryButton(selectCountryClicked)
            CountryDetailsView(country, changeClicked)
        }
    }

    @Composable
    fun ProgressIndicator()
    {
        CircularProgressIndicator(
            color = colorResource(R.color.colorAccent)
        )
    }

    @Composable
    fun SelectCountryButton(onClick: () -> Unit)
    {
        GenericViews().GraphicovButton(R.string.select_country, onClick)
    }

    @Composable
    fun CountryDetailsView(country: CountryDataEntity?, changeClicked: () -> Unit)
    {
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .width(400.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .wrapContentWidth(Alignment.Start)) {
                Image(
//                painter = rememberGlidePainter(country?.flag),
                    painterResource(id = R.drawable.search),
                    contentDescription = "",
                    modifier = Modifier
                        .size(40.dp, 40.dp)
                        .padding(end = 10.dp)
                    )
                Text(
                    text = country?.country ?: "",
                    color = colorResource(R.color.white),
                    fontSize = 16.sp
                )
            }
            Column(verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .wrapContentWidth(Alignment.End)
                    .height(40.dp)
                    .weight(1f)
                    .padding(start = 10.dp)) {
                GenericViews().GraphicovSmallButton(R.string.change, changeClicked)
            }
        }
    }

    @Composable
    fun ProceedButton(proceedClicked: () -> Unit)
    {
        Column(verticalArrangement = Arrangement.Bottom) {
            GenericViews().GraphicovFillButton(R.string.proceed, proceedClicked)
        }
    }
}