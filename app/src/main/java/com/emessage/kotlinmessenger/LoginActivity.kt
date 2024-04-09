/* package com.emessage.kotlinmessenger

import android.app.ProgressDialog
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.auth.account.WorkAccount.getClient
import com.google.android.gms.auth.api.signin.GoogleSignIn.getClient
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.account.WorkAccount.getClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.activity_account_information.*
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 120
    }

    private lateinit var mAuth : FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        login_button.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                // ...

                Alerter.Companion.create(this)
                    .setTitle("Couldn't Sign You In")
                    .setText("${task.exception}")
                    .setDuration(4000)
                    .enableSwipeToDismiss()
                    .setBackgroundColorInt(Color.RED)
                    .setIcon(R.drawable.ic_baseline_error_outline_24)
                    .enableIconPulse(true)
                    .show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    Toast.makeText(this, "Signed In successfully", Toast.LENGTH_SHORT).show()

                    var uid = FirebaseAuth.getInstance().uid
                    var ref = FirebaseDatabase.getInstance().getReference("/users")
                    var new = true

                    ref.addChildEventListener(object : ChildEventListener {
                        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                            var userinfo = p0.getValue(User::class.java)

                            if (userinfo!!.uid == uid) {
                                new = false
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

                    if (new == true) {
                        var ef = FirebaseDatabase.getInstance().getReference("/users/$uid")
                        ef.setValue(
                            User(
                                uid!!,
                                FirebaseAuth.getInstance().currentUser!!.displayName!!,
                                FirebaseAuth.getInstance().currentUser!!.photoUrl!!.toString(),
                                FirebaseAuth.getInstance().currentUser!!.email!!
                            )
                        )
                    }

                    Alerter.Companion.create(this)
                        .disableOutsideTouch()
                        .enableProgress(true)
                        .setTitle("Signing In...")
                        .setDuration(3000)
                        .setDismissable(false)
                        .setBackgroundColorInt(Color.RED)
                        .setText("Please wait while we sign you in")
                        .show()
                    Handler().postDelayed({
                        val intent = Intent(this, NewMessagesActivity::class.java)
                        startActivity(intent)
                    },3000)
                } else {
                    Alerter.Companion.create(this)
                        .setTitle("Couldn't Sign You In")
                        .setText("${task.exception}")
                        .setDuration(4000)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(Color.RED)
                        .setIcon(R.drawable.ic_baseline_error_outline_24)
                        .enableIconPulse(true)
                        .show()
                }
            }
    }
} */

package com.emessage.kotlinmessenger

import android.app.ProgressDialog
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        register.setOnClickListener {
            var intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        login_button.setOnClickListener {

            //var progressbar = ProgressDialog(this)
            //progressbar.setCancelable(false)
            //progressbar.setMessage("Signing In to your account...")
            //progressbar.show()

            //Handler().postDelayed({progressbar.dismiss()},2100)

            Alerter.Companion.create(this)
                .disableOutsideTouch()
                .enableProgress(true)
                .setTitle("Signing In...")
                .setDuration(3000)
                .setDismissable(false)
                .setBackgroundColorInt(Color.RED)
                .setText("Please wait while we sign you in")
                .show()

            var email = email_id.text.toString().trim()
            var password = passwrd.text.toString().trim()

            if (email.isEmpty()){
                email_id.error = "Enter an Email Address"
                return@setOnClickListener
            }
            else if (password.isEmpty()){
                passwrd.error = "Enter a Password"
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Toast.makeText(this, "Signed In successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, NewMessagesActivity::class.java)
                    intent.setFlags(FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    if(it.message == "The email address is badly formatted.") {
                        email_id.error = "Email is not in proper format"
                    }else if(it.message == "The password is invalid or the user does not have a password.") {
                        Alerter.Companion.create(this)
                            .setTitle("Couldn't Sign You In")
                            .setText("Incorrect Password")
                            .setDuration(4000)
                            .enableSwipeToDismiss()
                            .setBackgroundColorInt(Color.RED)
                            .setIcon(R.drawable.ic_baseline_error_outline_24)
                            .enableIconPulse(true)
                            .show()
                    } else if (it.message == "There is no user record corresponding to this identifier. The user may have been deleted."){
                        Alerter.Companion.create(this)
                            .setTitle("Couldn't Sign You In")
                            .setText("There is no user using this account. Please create an account by registering with us.")
                            .setDuration(4000)
                            .enableSwipeToDismiss()
                            .setBackgroundColorInt(Color.RED)
                            .setIcon(R.drawable.ic_baseline_error_outline_24)
                            .enableIconPulse(true)
                            .show()
                    } else {
                            Alerter.Companion.create(this)
                                .setTitle("Couldn't Sign You In")
                                .setText("${it.message}")
                                .setDuration(4000)
                                .enableSwipeToDismiss()
                                .setBackgroundColorInt(Color.RED)
                                .setIcon(R.drawable.ic_baseline_error_outline_24)
                                .enableIconPulse(true)
                                .show()
                    }
                }
        }
    }
}
