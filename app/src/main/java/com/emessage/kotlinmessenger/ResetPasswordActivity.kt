package com.emessage.kotlinmessenger

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        ema.text = FirebaseAuth.getInstance().currentUser?.email

        reset.setOnClickListener {

            FirebaseAuth.getInstance().sendPasswordResetEmail(FirebaseAuth.getInstance().currentUser?.email!!)

                    Alerter.Companion.create(this)
                    .setTitle("Please Wait...")
                    .setText("Sending Password Reset Email...")
                    .setDuration(2000)
                    .setBackgroundColorInt(Color.GRAY)
                    .enableProgress(true)
                    .disableOutsideTouch()
                    .setDismissable(false)
                    .show()

                    Handler().postDelayed({Alerter.Companion.create(this)
                        .setTitle("Password Reset Email sent Sucessfully")
                        .setDuration(4000)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(Color.RED)
                        .setIcon(R.drawable.ic_baseline_accessibility_new_24)
                        .enableIconPulse(true)
                        .show()},2000)
            }
        }
    }