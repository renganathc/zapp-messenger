package com.emessage.kotlinmessenger

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.os.HandlerCompat.postDelayed
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.emessage.kotlinmessenger.WriteAMessageActivity.Companion.USER_KEY
import com.emessage.kotlinmessenger.WriteAMessageActivity.Companion.USER_KEY_E
import com.emessage.kotlinmessenger.WriteAMessageActivity.Companion.USER_KEY_I
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Picasso
import com.tapadoo.alerter.Alerter
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_account_information.*
import kotlinx.android.synthetic.main.activity_new_messages.*
import kotlinx.android.synthetic.main.jdc.view.*

var chatPartnerId: String? = null

class NewMessagesActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()
    var turn = false

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        turn = true
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        turn = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_messages)

        adapter.setOnItemClickListener { item, view ->
            // Toast.makeText(this, "Click the button below to send a message. This is view only...", Toast.LENGTH_LONG).show()


            val da = item as LatestMessageRow
            var uid : String? = null
            if (da.chatMessage.to_id != FirebaseAuth.getInstance().uid) {
                uid = da.chatMessage.to_id
            } else {
                uid = da.chatMessage.from_id
            }

            var ref = FirebaseDatabase.getInstance().getReference("/users")

            ref.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    var userinfo = p0.getValue(User::class.java)

                    if (userinfo!!.uid == uid) {
                        val intent = Intent(this@NewMessagesActivity, ChatActivity::class.java)

                        val row = item

                        intent.putExtra(WriteAMessageActivity.USER_KEY_I, uid)
                        intent.putExtra(WriteAMessageActivity.USER_KEY, userinfo.username)
                        intent.putExtra(WriteAMessageActivity.USER_KEY_E, userinfo.photoUrl)
                        startActivity(intent)
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

        recycler_view_for_new_messages.adapter = adapter

        FirebaseMessaging.getInstance().isAutoInitEnabled = true

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }

                var uid = FirebaseAuth.getInstance().uid
                var ref = FirebaseDatabase.getInstance().getReference("/users")
                var nme = "ERROR"

                ref.addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                        var userinfo = p0.getValue(User::class.java)

                        if (userinfo!!.uid == uid) {
                            nme = userinfo.username
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

                // Get new Instance ID token
                val token = task.result?.token
                var dat = FirebaseDatabase.getInstance().getReference("/u/${uid}")
                dat.setValue(Token("${uid!!}","${token!!}"))
            })

        listenForMessages()

        verifyWhetherUserIsLoggedIin()

        floatingActionButton.setOnClickListener {
            var intent = Intent(this, WriteAMessageActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun verifyWhetherUserIsLoggedIin() {
        if (FirebaseAuth.getInstance().uid == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        } else {
            var uid = FirebaseAuth.getInstance().uid
            var ref = FirebaseDatabase.getInstance().getReference("/users")

            ref.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    var userinfo = p0.getValue(User::class.java)

                    if (turn == false) {
                        if (userinfo!!.uid == uid) {
                            Alerter.Companion.create(this@NewMessagesActivity)
                                .setTitle("Welcome Back ${userinfo.username} !!")
                                .setDuration(4000)
                                .enableSwipeToDismiss()
                                .setBackgroundColorInt(Color.GREEN)
                                .setIcon(R.drawable.ic_baseline_accessibility_new_24)
                                .enableIconPulse(true)
                                .show()

                            var mp: MediaPlayer =
                                MediaPlayer.create(this@NewMessagesActivity, R.raw.hello)
                            mp.start()
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
    }

    class LatestMessageRow(val chatMessage: ChatMessage) : Item<ViewHolder>() {

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.messge.text = chatMessage.text

            if (chatMessage.from_id == FirebaseAuth.getInstance().uid) {
                chatPartnerId = chatMessage.to_id
            } else {
                chatPartnerId = chatMessage.from_id
            }

            val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val user = p0.getValue(User::class.java)
                    viewHolder.itemView.user.text = user!!.username
                    Picasso.get().load(user.photoUrl).placeholder(R.drawable.g).into(viewHolder.itemView.circleImageView2)
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        }

        override fun getLayout(): Int {
            return R.layout.jdc
        }
    }

    /*val listenForMessages = HashMap<String, ChatMessage>()

    private fun refreshMe() {
        adapter.clear()
        listenForMessages.values.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }*/

    private fun listenForMessages(){
        val from_id = FirebaseAuth.getInstance().uid
        var ref = FirebaseDatabase.getInstance().getReference("Latest-Messages/$from_id")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                /*listenForMessages[p0.key] == chatMessage
                refreshMe()*/
                adapter.add(LatestMessageRow(chatMessage!!))
                if (chatMessage!!.text != null){textView5.visibility = View.INVISIBLE}
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.new_message){
            val intent = Intent(this, WriteAMessageActivity::class.java)
            startActivity(intent)
        }

        else if (item.itemId == R.id.acnt){
            val intent = Intent(this, AccountInformationActivity::class.java)
            startActivity(intent)
        }

        else if (item.itemId == R.id.abtus){
            val intent = Intent(this, AboutUsActivity::class.java)
            startActivity(intent)
        }

        else if(item.itemId == R.id.signOut){

            var done = false

            FirebaseDatabase.getInstance().getReference("/u/${FirebaseAuth.getInstance().uid}/usert").setValue("signed-out")
                .addOnSuccessListener {
                    var a = Alerter
                    a.create(this)
                        .setBackgroundColorInt(Color.GRAY)
                        .setIcon(R.drawable.ic_baseline_follow_the_signs_24)
                        .setTitle("Sign Out")
                        .setText("Are you sure you want to sign out ??")
                        .addButton("Yes", R.style.AlertButton, View.OnClickListener {
                            FirebaseAuth.getInstance().signOut()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            Toast.makeText(this, "Signed Out Successfully", Toast.LENGTH_SHORT).show()
                        })
                        .addButton("No", R.style.AlertButton, View.OnClickListener {
                            FirebaseMessaging.getInstance().isAutoInitEnabled = true

                            FirebaseInstanceId.getInstance().instanceId
                                .addOnCompleteListener(OnCompleteListener { task ->
                                    if (!task.isSuccessful) {
                                        return@OnCompleteListener
                                    }

                                    var uid = FirebaseAuth.getInstance().uid
                                    var ref = FirebaseDatabase.getInstance().getReference("/users")
                                    var nme = "ERROR"

                                    ref.addChildEventListener(object : ChildEventListener {
                                        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                                            var userinfo = p0.getValue(User::class.java)

                                            if (userinfo!!.uid == uid) {
                                                nme = userinfo.username
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

                                    // Get new Instance ID token
                                    val token = task.result?.token
                                    var dat = FirebaseDatabase.getInstance().getReference("/u/${uid}")
                                    dat.setValue(Token("${uid!!}","${token!!}"))
                                })
                            a.hide()
                        })
                        .enableIconPulse(true)
                        .disableOutsideTouch()
                        .show()
                    done = true
                }

            Handler().postDelayed({

                if (done == false) {
                    var al = Alerter
                    al.create(this)
                        .setBackgroundColorInt(Color.GRAY)
                        .setIcon(R.drawable.ic_baseline_follow_the_signs_24)
                        .setTitle("You're not Connected")
                        .setText("Since, you don't have a proper network connection Signing out will still make you receive notifications. Are you sure you want to sign out ??")
                        .addButton("Yes, Sign Out", R.style.AlertButton, View.OnClickListener {
                            FirebaseAuth.getInstance().signOut()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            Toast.makeText(this, "Signed Out Successfully", Toast.LENGTH_SHORT).show()
                        })
                        .addButton("No, Thanks", R.style.AlertButton, View.OnClickListener {
                            FirebaseMessaging.getInstance().isAutoInitEnabled = true

                            FirebaseInstanceId.getInstance().instanceId
                                .addOnCompleteListener(OnCompleteListener { task ->
                                    if (!task.isSuccessful) {
                                        return@OnCompleteListener
                                    }

                                    var uid = FirebaseAuth.getInstance().uid
                                    var ref = FirebaseDatabase.getInstance().getReference("/users")
                                    var nme = "ERROR"

                                    ref.addChildEventListener(object : ChildEventListener {
                                        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                                            var userinfo = p0.getValue(User::class.java)

                                            if (userinfo!!.uid == uid) {
                                                nme = userinfo.username
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

                                    // Get new Instance ID token
                                    val token = task.result?.token
                                    var dat = FirebaseDatabase.getInstance().getReference("/u/${uid}")
                                    dat.setValue(Token("${uid!!}","${token!!}"))
                                })
                            al.hide()
                        })
                        .enableIconPulse(true)
                        .disableOutsideTouch()
                        .setDismissable(false)
                        .show()
                }

            }, 1300)

        }

        else if(item.itemId == R.id.change_of_username){
            val intent = Intent(this, UsernameChange::class.java)
            startActivity(intent)
        }

        else if(item.itemId == R.id.resep){
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }

        else if(item.itemId == R.id.change_of_status){
            val intent = Intent(this, StatusActivity::class.java)
            startActivity(intent)
        }

        else if(item.itemId == R.id.change_of_photo){
            val intent = Intent(this, ProfilePhotoActivity::class.java)
            startActivity(intent)
        }

        else if(item.itemId == R.id.web){
            val intent = Intent(this, WebActivity::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
