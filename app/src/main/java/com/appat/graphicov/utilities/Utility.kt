package com.appat.graphicov.utilities

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.telephony.TelephonyManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.appat.graphicov.R
import com.appat.graphicov.roomdb.CovidGraphDatabase
import com.appat.graphicov.utilities.Constants.ddMMyyyyTime
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.play.core.review.ReviewManagerFactory
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object Utility {

    fun getContext(): Context {
        return GraphiCovApplication.app!!.applicationContext
    }

    fun getString(resId: Int):String {
        return GraphiCovApplication.app?.getString(resId) ?: ""
    }

    fun formatLong(value: Long): String
    {
        return String.format("%,d", value)
    }

    fun getColor(color: Int): Int {
        return ContextCompat.getColor(getContext(), color)
    }

    fun getDrawable(drawable: Int): Drawable? {
        return ContextCompat.getDrawable(getContext(), drawable)
    }

    fun getDatabase(): CovidGraphDatabase
    {
        return CovidGraphDatabase.getInstance(getContext())
    }

    fun getDateTime(timeStamp: Long?): String {
        try {
            if(timeStamp != null)
            {
                val sdf = SimpleDateFormat(ddMMyyyyTime, getLocaleForDate())
                val netDate = Date(timeStamp)
                return sdf.format(netDate)
            }
        } catch (ex: Exception) {
            return ""
        }
        return ""
    }

    private fun getLocaleForDate(): Locale
    {
        return Locale("en")
    }

    fun fromDateString(value: String?, format: String): Date? {
        val df: DateFormat = SimpleDateFormat(format, getLocaleForDate())
        return if (value != null) {
            try {
                return df.parse(value)
            } catch (e: ParseException) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }

    fun dateToString(date: Date?, format: String): String {
        if(date == null)
        {
            return ""
        }
        val df: DateFormat = SimpleDateFormat(format, getLocaleForDate())
        return df.format(date) ?: ""
    }

    fun formatDate(date: String?, inputFormat: String, outputFormat: String): String
    {
        if(date == "" || date == null)
        {
            return ""
        }
        val inputDateFormat = SimpleDateFormat(inputFormat, getLocaleForDate())
        val outputDateFormat = SimpleDateFormat(outputFormat, getLocaleForDate())
        return try {
            val inputDate = inputDateFormat.parse(date)
            outputDateFormat.format(inputDate!!)
        } catch (e: ParseException) {
            e.printStackTrace()
            ""
        }
    }

    fun getDrawableFromUrl(context: Context, url: String, success: (Drawable?) -> Unit)
    {
        Glide.with(context).load(url).into(object : CustomTarget<Drawable?>() {
            override fun onResourceReady(
                resource: Drawable,
                transition: Transition<in Drawable?>?
            ) {
                success(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {

            }
        })
    }

    fun getCurrentCountryISO(context: Context): String
    {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.networkCountryIso.uppercase(Locale.getDefault())
    }

    fun openInAppReview(context: AppCompatActivity)
    {
        val manager = ReviewManagerFactory.create(context)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { requestResponse ->
            if (requestResponse.isSuccessful) {
                // We got the ReviewInfo object
                val reviewInfo = requestResponse.result
                val flow = manager.launchReviewFlow(context, reviewInfo)
                flow.addOnCompleteListener {

                }
            }
        }
    }

    fun openUrlInCustomTab(uriString: String, context: AppCompatActivity)
    {
        Log.d("openUrlInCustomTab", uriString)
        val uri = Uri.parse(uriString)
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent :CustomTabsIntent  = builder.build()
        customTabsIntent.launchUrl(context, uri)
    }

    fun openShare(context: AppCompatActivity)
    {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type="text/plain"
        val packageName = getPlayStoreUrl(context)
        shareIntent.putExtra(Intent.EXTRA_TEXT, packageName)
        context.startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
    }

    fun openPlayStoreUrl(context: AppCompatActivity)
    {
        openUrlInCustomTab(getPlayStoreUrl(context), context)
    }

    private fun getPlayStoreUrl(context: AppCompatActivity): String
    {
        return  String.format(getString(R.string.play_store_url), context.packageName)
    }
}