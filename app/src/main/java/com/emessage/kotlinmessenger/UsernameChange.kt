package com.emessage.kotlinmessenger

import android.app.ProgressDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.os.HandlerCompat.postDelayed
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.activity_account_information.*
import kotlinx.android.synthetic.main.activity_username_change.*

class UsernameChange : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_username_change)

        supportActionBar?.title = "Username Settings"

            var username: String? = null
            var email: String? = null
            var photourl: String? = null
            var status: String? = null


            var uid = FirebaseAuth.getInstance().uid
            var ref = FirebaseDatabase.getInstance().getReference("/users")

            ref.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    var userinfo = p0.getValue(User::class.java)

                    if (userinfo!!.uid == uid) {
                        username = userinfo.username
                        email = userinfo.email
                        photourl = userinfo.photoUrl
                        status = userinfo.status

                        currentuname.text = userinfo.username
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

        button.setOnClickListener{

            if (newuname.text.toString().isEmpty()) {
                newuname.error = "Enter a Username"
                return@setOnClickListener
            } else if (newuname.text.toString().length > 25) {
                newuname.error = "Username should not be more than 25 characters long"
                return@setOnClickListener
            }

            var new_username = newuname.text.toString().trim()
            var dr = FirebaseDatabase.getInstance().getReference("/users/$uid")

            dr.setValue(User(uid!!, new_username, photourl!!, email!!, status!!))

                    Alerter.Companion.create(this)
                    .setTitle("Please Wait...")
                    .setText("Changing Username...")
                    .setDuration(2000)
                    .setBackgroundColorInt(Color.GRAY)
                    .enableProgress(true)
                    .disableOutsideTouch()
                    .setDismissable(false)
                    .show()

                    Handler().postDelayed({
                        Alerter.Companion.create(this)
                            .setTitle("Username Changed Successfully")
                            .setDuration(4000)
                            .enableSwipeToDismiss()
                            .setBackgroundColorInt(Color.RED)
                            .setIcon(R.drawable.ic_baseline_accessibility_new_24)
                            .enableIconPulse(true)
                            .show()
                        Toast.makeText(this, "Username Changed Successfully", Toast.LENGTH_SHORT).show()
                        currentuname.text = new_username }, 2000)
        }
    }
}
