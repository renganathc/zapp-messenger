package com.emessage.kotlinmessenger
import android.app.ProgressDialog
import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_write_a_message.*
import kotlinx.android.synthetic.main.rvf.view.*

class WriteAMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_a_message)

        supportActionBar?.title = "Select User"

        getUsers()
    }

        companion object{
            val USER_KEY = "USER_KEY"
            val USER_KEY_I = "USER_KEY_I"
            val USER_KEY_E = "USER_KEY_E"
            //val USER_KEY_G = "USER_KEY_G"
        }

    private fun getUsers(){
        val adapter = GroupAdapter<ViewHolder>()
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    Log.d("TaGg", it.toString())
                    val user = it.getValue(User::class.java)
                    adapter.add(UserItem(user!!))
                    textView2.visibility = View.INVISIBLE
                    recycler.visibility = View.VISIBLE
                }

                adapter.setOnItemClickListener { item, view ->

                    var useritem = item as UserItem
                    //val uid = FirebaseAuth.getInstance().uid
                    //var reference = FirebaseDatabase.getInstance().getReference("/users/$uid") as UserItem

                    if (useritem.user.uid == FirebaseAuth.getInstance().uid)
                    {
                        Toast.makeText(this@WriteAMessageActivity, "You can't send a message to yourself", Toast.LENGTH_LONG).show()
                        return@setOnItemClickListener
                    }

                    var intent = Intent(view.context, ChatActivity::class.java)
                    intent.putExtra(USER_KEY, useritem.user.username)
                    intent.putExtra(USER_KEY_I, useritem.user.uid)
                    intent.putExtra(USER_KEY_E, useritem.user.photoUrl)
                    //intent.putExtra(USER_KEY_G, reference.user.photoUrl)
                    startActivity(intent)
                }

                recycler.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }
}

class UserItem(val user : User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        var uid = FirebaseAuth.getInstance().uid
        if (uid != user.uid) {
            viewHolder.itemView.name.text = user.username
            Picasso.get().load(user.photoUrl).placeholder(R.drawable.g).into(viewHolder.itemView.circleImageView)
            viewHolder.itemView.stat.text = user.status
        }
        else {
            viewHolder.itemView.name.text = "You"
            Picasso.get().load(user.photoUrl).placeholder(R.drawable.g).into(viewHolder.itemView.circleImageView)
            viewHolder.itemView.stat.text = user.status
        }
    }

    override fun getLayout(): Int {
        return R.layout.rvf
    }
}
