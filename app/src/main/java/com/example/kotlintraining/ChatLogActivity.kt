package com.example.kotlintraining

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kotlintraining.Models.ChatMessage
import com.example.kotlintraining.Models.User
import com.example.kotlintraining.Utils.FcmPush
import com.example.kotlintraining.Views.ChatFromItem
import com.example.kotlintraining.Views.ChatToItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLogActivity : AppCompatActivity() {

    private val adapter = GroupAdapter<GroupieViewHolder>()
    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter
        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        chatlog_toolbar.title = toUser?.username
        setSupportActionBar(chatlog_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        send_button_chatlog.setOnClickListener {
            performSendMessage()
        }
        listenForMessages()
    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/${fromId}/${toId}")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                if (chatMessage != null) {
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = MainActivity.currentUser
                        adapter.add(ChatToItem(chatMessage.text, currentUser!!))
                    } else {
                        adapter.add(ChatFromItem(chatMessage.text, toUser!!))

                    }
                }

                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

    private fun performSendMessage() {
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user?.uid
        val text = edittext_chat_log.text.toString()
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/${fromId}/${toId}").push()
        val toRef = FirebaseDatabase.getInstance().getReference("/user-messages/${toId}/${fromId}").push()
        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/${fromId}/${toId}")
        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/${toId}/${fromId}")
        val chatMessage = ChatMessage(ref.key!!, fromId!!, toId!!, System.currentTimeMillis()/1000  ,text)
        ref.setValue(chatMessage)
            .addOnSuccessListener {
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
                FcmPush.instance.sendMessage(toId, user.username, text)
            }
        toRef.setValue(chatMessage)
        latestMessageRef.setValue(chatMessage)
        latestMessageToRef.setValue(chatMessage)
    }
}
