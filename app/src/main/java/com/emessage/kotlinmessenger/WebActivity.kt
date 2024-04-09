package com.emessage.kotlinmessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_web.*

class WebActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        supportActionBar?.hide()

        webview.loadUrl("https://renganath.netlify.app")
        webview.settings.javaScriptEnabled = true
        webview.settings.supportZoom()


    }
}