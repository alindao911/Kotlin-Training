package com.example.kotlintraining.Utils

import com.example.kotlintraining.Models.Push
import com.example.kotlintraining.Models.Token
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class FcmPush {
    var JSON = MediaType.parse("application/json; charset=utf-8")
    var url= "https://fcm.googleapis.com/fcm/send"
    var serverKey = "AIzaSyDOqSDszOkyeyhRlHRwtzGOvyh872nD7Dw"
    var gson : Gson? = null
    var okHttpClient : OkHttpClient? = null

    companion object {
        var instance = FcmPush()
    }

    init {
        gson = Gson()
        okHttpClient = OkHttpClient()
    }

    fun sendMessage(destinationUid : String, title: String, message : String) {
        var ref = FirebaseDatabase.getInstance().getReference("pushtokens/${destinationUid}")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    var token = p0.getValue(Token::class.java)
                    var push = Push()
                    push.to = token?.pushToken
                    push.notification.title = title
                    push.notification.body = message

                    var body = RequestBody.create(JSON, gson!!.toJson(push))
                    var request = Request.Builder()
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "key=${serverKey}")
                        .url(url)
                        .post(body)
                        .build()

                    okHttpClient?.newCall(request)?.enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {

                        }

                        override fun onResponse(call: Call, response: Response) {
                            println(response.body()?.string())
                        }

                    })
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}