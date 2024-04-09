package com.emessage.kotlinmessenger

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.activity_profile_photo.*
import kotlinx.android.synthetic.main.activity_profile_photo.image_button
import kotlinx.android.synthetic.main.activity_profile_photo.profile_image
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

var u : Uri? = null

class ProfilePhotoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_photo)

        image_button.setOnClickListener {
            var intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 5)
        }

        profile_image.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 5)
        }

        change.setOnClickListener {
            if (u == null) {
                Toast.makeText(this, "Please select an Image", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            Alerter.Companion.create(this)
                .setTitle("Changing Profile Photo...")
                .enableProgress(true)
                .setDuration(3000)
                .setDismissable(false)
                .disableOutsideTouch()
                .setBackgroundColorInt(Color.BLUE)
                .show()

            var uuid = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/${uuid}")
            var pho = "https://firebasestorage.googleapis.com/v0/b/kotlin-messenger-d5c6d.appspot.com/o/images%2F${uuid}?alt=media&token=9fc6c017-62cd-49e5-acb5-e9d60c70cce9"

            ref.putFile(u!!)
                .addOnSuccessListener {
                    var r = FirebaseDatabase.getInstance()
                        .getReference("/users/${FirebaseAuth.getInstance().uid}/photoUrl")
                    r.setValue(pho)

                    Alerter.Companion.create(this)
                        .setTitle("Profile Photo Changed Successfully !!")
                        .setDuration(4000)
                        .setIcon(R.drawable.ic_baseline_accessibility_new_24)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(Color.BLUE)
                        .show()
                }
                .addOnFailureListener {
                    Alerter.Companion.create(this)
                        .setTitle("Couldn't Change Photo Account !!")
                        .setDuration(4000)
                        .setText(it.message.toString())
                        .setIcon(R.drawable.ic_baseline_error_outline_24)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(Color.RED)
                        .show()
                }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 5 && resultCode == Activity.RESULT_OK && data != null) {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, data.data)
            u = data.data
            profile_image.setImageBitmap(bitmap)
            profile_image.borderWidth = 5
            image_button.visibility = View.INVISIBLE
        }

    }
}

