package com.example.kotlintraining

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kotlintraining.Fragments.HomeFragment
import com.example.kotlintraining.Fragments.MyAccountFragment
import com.example.kotlintraining.Models.User
import com.example.kotlintraining.Utils.FcmPush
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId

class MainActivity : AppCompatActivity() {
    companion object {
        var currentUser: User? = null
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.menu_people -> {
                moveToFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.menu_my_account -> {
                moveToFragment(MyAccountFragment())
                return@OnNavigationItemSelectedListener true
            }
        }

        return@OnNavigationItemSelectedListener false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView : BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navView.setOnNavigationItemSelectedListener (onNavigationItemSelectedListener)

        verifyUserIsLoggedIn()
        moveToFragment(HomeFragment())
        fetchCurrentUser()
        registerPushToken()
    }

    private fun registerPushToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener {
                task ->
                val token = task.result?.token
                val uid = FirebaseAuth.getInstance().uid
                val map = mutableMapOf<String, Any>()
                map["pushToken"] = token!!

                FirebaseDatabase.getInstance().getReference("/pushtokens/$uid").setValue(map)
            }
    }

    private fun verifyUserIsLoggedIn() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/${uid}")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)

            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun moveToFragment (fragment : Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }
}
