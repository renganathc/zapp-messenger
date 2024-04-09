package com.emessage.kotlinmessenger

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.activity_status.*
import kotlinx.android.synthetic.main.activity_status.button
import kotlinx.android.synthetic.main.activity_username_change.*

class StatusActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)

        supportActionBar?.title = "Update Status"





        var uid = FirebaseAuth.getInstance().uid
        var dref = FirebaseDatabase.getInstance().getReference("/users")

        dref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                var userinfo = p0.getValue(User::class.java)

                if (userinfo!!.uid == uid) {

                    currents.text = userinfo.status
                }
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })








        button.setOnClickListener {
            var status = news.text.toString().trim()

            if (status.length > 70) {
                news.error = "Status cannot be longer than 70 characters"
                return@setOnClickListener
            }

            else if (status.isEmpty() || status <= "                                                                                                                                                                                     ") {
                news.error = "Status cannot be Empty"
                return@setOnClickListener
            }

            var done = false

            FirebaseDatabase.getInstance().getReference("/users/${FirebaseAuth.getInstance().uid}/status").setValue(status)
                .addOnSuccessListener {
                    Alerter.Companion.create(this)
                        .setTitle("Updated Status !!")
                        .setDuration(3000)
                        .setIcon(R.drawable.ic_baseline_accessibility_new_24)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(Color.BLUE)
                        .show()

                    done = true
                }

            Handler().postDelayed({
                Alerter.Companion.create(this)
                    .setTitle("Couldn't Update Status !!")
                    .setText("Please check your network connection...")
                    .setDuration(3000)
                    .setIcon(R.drawable.ic_baseline_error_outline_24)
                    .enableSwipeToDismiss()
                    .setBackgroundColorInt(Color.RED)
                    .show()
            }, 1300)
        }
    }
}