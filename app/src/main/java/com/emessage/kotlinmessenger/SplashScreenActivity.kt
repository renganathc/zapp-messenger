package com.emessage.kotlinmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.messaging.FirebaseMessaging

class SplashScreenActivity : AppCompatActivity() {

    lateinit var handler : Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        handler = Handler()

        handler.postDelayed({
            var intent = Intent(this, NewMessagesActivity::class.java)
            startActivity(intent)
            finish()
        },1000)
    }
}
