package com.emessage.kotlinmessenger

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.renderscript.Sampler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.getSystemService
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.tapadoo.alerter.Alerter
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_account_information.*
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_profile_photo.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.image_button
import kotlinx.android.synthetic.main.activity_register.profile_image
import kotlinx.android.synthetic.main.from.view.*
import kotlinx.android.synthetic.main.fromphoto.view.*
import kotlinx.android.synthetic.main.to.view.*
import kotlinx.android.synthetic.main.to.view.toci
import kotlinx.android.synthetic.main.tophoto.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.System.load
import java.net.URL
import java.util.*

const val TOPIC = "/topics/myTopic2"

class ChatActivity : AppCompatActivity() {

    var p = -1

    val adapter = GroupAdapter<ViewHolder>()

    var my_photo : String = "https://data.whicdn.com/images/333421091/original.png?t=1564409077"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        supportActionBar?.title = intent.getStringExtra(WriteAMessageActivity.USER_KEY)

        sendbutton.setOnClickListener {
            if (messagebox.text.toString().isEmpty() || messagebox.text.toString() <= "                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            ") {
                Toast.makeText(this , "Message is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            else {
                sendTheMessage()
            }
        }

        pic.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }




        var uid = FirebaseAuth.getInstance().uid
        var ref = FirebaseDatabase.getInstance().getReference("/users")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                var userinfo = p0.getValue(User::class.java)

                if (userinfo!!.uid == uid) {
                    my_photo = userinfo.photoUrl
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





        listenForMessages()

        recyclert.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, data.data)

            var uuid = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/${uuid}")
            var pho = "https://firebasestorage.googleapis.com/v0/b/kotlin-messenger-d5c6d.appspot.com/o/images%2F${uuid}?alt=media&token=9fc6c017-62cd-49e5-acb5-e9d60c70cce9"

            var alert = Alerter
            alert.create(this)
                .setBackgroundColorInt(Color.GRAY)
                .setIcon(R.drawable.ic_baseline_textsms_24)
                .setTitle("Do You want to send this Image ??")
                .addButton("Yes", R.style.AlertButton, View.OnClickListener {
                    ref.putFile(data.data!!)
                        .addOnSuccessListener {
                            sendThePhoto(pho)
                        }
                    alert.hide()

                    Alerter.create(this)
                        .disableOutsideTouch()
                        .enableProgress(true)
                        .setBackgroundColorInt(Color.BLUE)
                        .setTitle("Sending Image...")
                        .setDuration(2000)
                        .setDismissable(false)
                        .show()

                })
                .addButton("No", R.style.AlertButton, View.OnClickListener {
                    alert.hide()
                    Toast.makeText(this, "Image Deselected", Toast.LENGTH_LONG).show()
                })
                .enableIconPulse(true)
                .setDismissable(false)
                .disableOutsideTouch()
                .show()
        }

    }

    private fun listenForMessages(){
        val ref = FirebaseDatabase.getInstance().getReference("/messages")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                var mp: MediaPlayer? = null
                var amp: MediaPlayer? = null
                val chatMessage = p0.getValue(ChatMessage::class.java)


                    if (chatMessage!!.photo == "empty" ) {
                        if (chatMessage!!.from_id == FirebaseAuth.getInstance().uid && chatMessage.to_id == intent.getStringExtra(
                                WriteAMessageActivity.USER_KEY_I
                            ) && chatMessage.to_id != chatMessage.from_id
                        ) {
                            adapter.add(ChatTo(chatMessage.text, my_photo))
                            messagebox.text = null
                            p++
                            recyclert.scrollToPosition(p)
                            if (System.currentTimeMillis() / 1000 - chatMessage.timeStamp <= 4) {
                                mp = MediaPlayer.create(this@ChatActivity, R.raw.send_chime)
                                mp?.start()
                            }
                        } else if (chatMessage.from_id == intent.getStringExtra(WriteAMessageActivity.USER_KEY_I) && chatMessage.to_id == FirebaseAuth.getInstance().uid && chatMessage.to_id != chatMessage.from_id) {
                            adapter.add(
                                ChatFrom(
                                    chatMessage.text,
                                    intent.getStringExtra(WriteAMessageActivity.USER_KEY_E)
                                )
                            )
                            p++
                            recyclert.scrollToPosition(p)
                            if (System.currentTimeMillis() / 1000 - chatMessage.timeStamp <= 4) {
                                mp = MediaPlayer.create(this@ChatActivity, R.raw.receive_chime)
                                mp?.start()
                            }
                        }
                    }

                    else if (chatMessage.photo != "empty") {
                        if (chatMessage.from_id == FirebaseAuth.getInstance().uid && chatMessage.to_id == intent.getStringExtra(WriteAMessageActivity.USER_KEY_I) && chatMessage.to_id != chatMessage.from_id) {
                            adapter.add(PhotoTo(chatMessage.photo, my_photo))
                            messagebox.text = null
                            p++
                            recyclert.scrollToPosition(p)
                            if (System.currentTimeMillis() / 1000 - chatMessage.timeStamp <= 4) {
                                mp = MediaPlayer.create(this@ChatActivity, R.raw.send_chime)
                                mp?.start()
                            }
                        } else if (chatMessage.from_id == intent.getStringExtra(WriteAMessageActivity.USER_KEY_I) && chatMessage.to_id == FirebaseAuth.getInstance().uid && chatMessage.to_id != chatMessage.from_id) {
                            adapter.add(PhotoFrom(chatMessage.photo, intent.getStringExtra(WriteAMessageActivity.USER_KEY_E))
                            )
                            p++
                            recyclert.scrollToPosition(p)
                            if (System.currentTimeMillis() / 1000 - chatMessage.timeStamp <= 4) {
                                mp = MediaPlayer.create(this@ChatActivity, R.raw.receive_chime)
                                mp?.start()
                            }
                        }
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
    }

    private fun sendTheMessage(){
        var fromid = FirebaseAuth.getInstance().uid
        var toid = intent.getStringExtra(WriteAMessageActivity.USER_KEY_I)
        var text = messagebox.text.toString().trim()
        if (fromid == toid){
            Toast.makeText(this@ChatActivity, "You can't send a message to yourself.", Toast.LENGTH_LONG).show()
            return
        }
        var reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        var message = ChatMessage(text, "empty", reference.key!!, fromid!!, toid, System.currentTimeMillis()/1000)
        reference.setValue(message)

        val latestref = FirebaseDatabase.getInstance().getReference("Latest-Messages/$fromid/$toid")
        latestref.setValue(message)
        val latestrefto = FirebaseDatabase.getInstance().getReference("Latest-Messages/$toid/$fromid")
        latestrefto.setValue(message)


        var ref = FirebaseDatabase.getInstance().getReference("/u")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.getValue(Token::class.java)!!.usern == toid) {
                    val recipientToken = snapshot.getValue(Token::class.java)!!.usert

                    var name : String? = null

                    FirebaseDatabase.getInstance().getReference("/users").addChildEventListener(object : ChildEventListener {
                        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                            if (snapshot.getValue(User::class.java)!!.uid == FirebaseAuth.getInstance().uid) {
                                name = snapshot.getValue(User::class.java)!!.username

                                FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)


                                val title = name
                                val message = text
                                if(title!!.isNotEmpty() && message.isNotEmpty() && recipientToken.isNotEmpty()) {
                                    PushNotification(
                                        NotificationData(title, message),
                                        recipientToken
                                    ).also {
                                        sendNotification(it)
                                    }
                                }
                            }
                        }

                        override fun onChildChanged(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
                            TODO("Not yet implemented")
                        }

                        override fun onChildRemoved(snapshot: DataSnapshot) {
                            TODO("Not yet implemented")
                        }

                        override fun onChildMoved(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
                            TODO("Not yet implemented")
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }







    private fun sendThePhoto(phot : String){
        var fromid = FirebaseAuth.getInstance().uid
        var toid = intent.getStringExtra(WriteAMessageActivity.USER_KEY_I)
        if (fromid == toid){
            Toast.makeText(this@ChatActivity, "You can't send a message to yourself.", Toast.LENGTH_LONG).show()
            return
        }
        var reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        var message = ChatMessage("📷 Photo", phot, reference.key!!, fromid!!, toid, System.currentTimeMillis()/1000)
        reference.setValue(message)

        val latestref = FirebaseDatabase.getInstance().getReference("Latest-Messages/$fromid/$toid")
        latestref.setValue(message)
        val latestrefto = FirebaseDatabase.getInstance().getReference("Latest-Messages/$toid/$fromid")
        latestrefto.setValue(message)


        var ref = FirebaseDatabase.getInstance().getReference("/u")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.getValue(Token::class.java)!!.usern == toid) {
                    val recipientToken = snapshot.getValue(Token::class.java)!!.usert

                    var name : String? = null

                    FirebaseDatabase.getInstance().getReference("/users").addChildEventListener(object : ChildEventListener {
                        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                            if (snapshot.getValue(User::class.java)!!.uid == FirebaseAuth.getInstance().uid) {
                                name = snapshot.getValue(User::class.java)!!.username

                                FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)


                                val title = name
                                val message = "📷 Photo"
                                if(title!!.isNotEmpty() && recipientToken.isNotEmpty()) {
                                    PushNotification(
                                        NotificationData(title, message),
                                        recipientToken
                                    ).also {
                                        sendNotification(it)
                                    }
                                }
                            }
                        }

                        override fun onChildChanged(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
                            TODO("Not yet implemented")
                        }

                        override fun onChildRemoved(snapshot: DataSnapshot) {
                            TODO("Not yet implemented")
                        }

                        override fun onChildMoved(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
                            TODO("Not yet implemented")
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }









    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {

            } else {
                Toast.makeText(this@ChatActivity, response.message().toString(), Toast.LENGTH_SHORT).show()
            }
        } catch(e: Exception) {
            Toast.makeText(this@ChatActivity, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}

class ChatFrom(val text : String, var url : String): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textViewfrom.text = text
        var target = viewHolder.itemView.fromci
        Picasso.get().load(url).placeholder(R.drawable.g).into(target)
    }

    override fun getLayout(): Int {
        return  R.layout.from
    }
}

class ChatTo(var text : String, var url : String): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textViewto.text = text
        var target = viewHolder.itemView.toci
        Picasso.get().load(url).placeholder(R.drawable.g).into(target)
    }

    override fun getLayout(): Int {
        return R.layout.to
    }
}

class PhotoFrom(val photo : String, var url : String): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        var target = viewHolder.itemView.fromci2
        Picasso.get().load(url).placeholder(R.drawable.g).into(target)
        Picasso.get().load(photo).placeholder(R.drawable.ic_baseline_photo_size_select_actual_24).into(viewHolder.itemView.imageView22)
    }

    override fun getLayout(): Int {
        return  R.layout.fromphoto
    }
}

class PhotoTo(val photo : String, var url : String): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        var target = viewHolder.itemView.toci2
        Picasso.get().load(url).placeholder(R.drawable.g).into(target)
        Picasso.get().load(photo).placeholder(R.drawable.ic_baseline_photo_size_select_actual_24).into(viewHolder.itemView.imageView5)
    }

    override fun getLayout(): Int {
        return  R.layout.tophoto
    }
}