package com.emessage.kotlinmessenger

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.email_id
import java.util.*

class RegisterActivity : AppCompatActivity() {

    // var mail : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        image_button.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        register_button.setOnClickListener {

            val un = username.text.toString().trim()
            val email = email_id.text.toString().trim()
            // mail = email_id.text.toString().trim()
            val password = pw.text.toString().trim()
            val cpa = cpw.text.toString().trim()

            if (email.isEmpty() && un.isEmpty()) {
                email_id.error = "Enter an Email"
                username.error = "Enter a Username"
                return@setOnClickListener
            } else if (un.length > 23) {
                username.error = "Username should not be more than 23 characters long"
                return@setOnClickListener
            } else if (email.isEmpty()) {
                email_id.error = "Enter an Email"
                return@setOnClickListener
            } else if (password.isEmpty()) {
                pw.error = "Enter a password"
                return@setOnClickListener
            } else if(password.length < 6) {
                pw.error = "Password should be at least 6 Characters long"
                Toast.makeText(this, "Password should be at least 6 Characters long", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (password != cpa) {
                cpw.error = "Passwords do not match"
                return@setOnClickListener
            } else if (uriOfImage == null) {
                Toast.makeText(this, "Please choose an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("MainActivity", "Email : $email")
            Log.d("MainActivity", "Password : $password")

            Alerter.Companion.create(this)
                .disableOutsideTouch()
                .enableProgress(true)
                .setTitle("Creating your Account...")
                .setDuration(3500)
                .setDismissable(false)
                .setBackgroundColorInt(Color.GRAY)
                .setText("Please wait while we are creating your account $un ...")
                .show()

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    uploadImageToFirebase()
                    Log.d("MainActivity", "User created with UID: ${it.result!!.user!!.uid}")
                }
                .addOnFailureListener {
                    Log.d("MainActivity", "Account could not be created : ${it.message}")
                    if(it.message == "The email address is badly formatted."){
                        email_id.error = "Email is not in proper format"
                        return@addOnFailureListener
                    }
                    else{
                        Alerter.Companion.create(this)
                            .setTitle("Couldn't Create Account !!")
                            .setText(it.message.toString())
                            .setDuration(4000)
                            .setIcon(R.drawable.ic_baseline_error_outline_24)
                            .enableSwipeToDismiss()
                            .setBackgroundColorInt(Color.RED)
                            .show()
                    }
                }
        }

        profile_image.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        back_to_login.setOnClickListener {
            finish()
        }
    }

    var uriOfImage : Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            uriOfImage = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uriOfImage)
            profile_image.setImageBitmap(bitmap)
            profile_image.borderWidth = 5
            image_button.visibility = View.INVISIBLE
        }
    }

    private fun uploadImageToFirebase(){
        var uuid = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/${uuid}")
        var pho = "https://firebasestorage.googleapis.com/v0/b/kotlin-messenger-d5c6d.appspot.com/o/images%2F${uuid}?alt=media&token=9fc6c017-62cd-49e5-acb5-e9d60c70cce9"

        ref.putFile(uriOfImage!!)
                .addOnSuccessListener {
                    saveFileToDatabase(pho)

                    Alerter.Companion.create(this)
                        .setTitle("Account Created Successfully !!")
                        .setDuration(4000)
                        .setIcon(R.drawable.ic_baseline_accessibility_new_24)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(Color.GRAY)
                        .show()

                }
            .addOnFailureListener{
                Alerter.Companion.create(this)
                    .setTitle("Couldn't Create Account !!")
                    .setDuration(4000)
                    .setText(it.message.toString())
                    .setIcon(R.drawable.ic_baseline_error_outline_24)
                    .enableSwipeToDismiss()
                    .setBackgroundColorInt(Color.RED)
                    .show()
            }
        }

    private fun saveFileToDatabase(photoUrl: String){
        var url = photoUrl
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val email = email_id.text.toString().trim()
        val user = User(uid, username.text.toString(), url, email, "Hey There! I'm using ZApp !")

        ref.setValue(user)

        FirebaseAuth.getInstance().signOut()

        Handler().postDelayed({finish()},3000)
    }

}