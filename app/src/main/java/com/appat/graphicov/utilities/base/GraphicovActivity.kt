package com.appat.graphicov.utilities.base

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.appat.graphicov.utilities.MonetColors
import com.appat.graphicov.utilities.extensions.isDarkThemeOn
import com.kieronquinn.monetcompat.app.MonetCompatActivity

abstract class GraphicovActivity<B : ViewBinding>(val bindingFactory: (LayoutInflater) -> B): MonetCompatActivity() {
    lateinit var binding: B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenCreated {
            monet.awaitMonetReady()
            window.setBackgroundDrawable(ColorDrawable(MonetColors.getBackgroundColor(this@GraphicovActivity, monet)))
            binding = bindingFactory(layoutInflater)
            setContentView(binding.root)
            binding.root.background = ColorDrawable(MonetColors.getBackgroundColor(this@GraphicovActivity, monet))
        }
    }
}