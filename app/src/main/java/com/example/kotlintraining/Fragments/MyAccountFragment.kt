package com.example.kotlintraining.Fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.kotlintraining.MainActivity.Companion.currentUser

import com.example.kotlintraining.R
import com.example.kotlintraining.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_my_account.view.*

/**
 * A simple [Fragment] subclass.
 */
class MyAccountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_account, container, false)

        intializeUser(view)
        view.button_logout_my_account.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(context, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        return view
    }

    private fun intializeUser(view : View) {
        Picasso.get().load(currentUser!!.profileImageUrl).into(view.imageview_profimage_my_account)
        view.textview_username_myaccount.text = currentUser!!.username
        view.textview_email_myaccount.text = FirebaseAuth.getInstance().currentUser?.email.toString()
        view.textview_bio_myaccount.text = currentUser?.bio
    }


}
