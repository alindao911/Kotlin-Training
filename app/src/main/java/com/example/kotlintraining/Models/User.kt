package com.example.kotlintraining.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid: String, val username: String, val profileImageUrl: String, val bio: String, var deviceToken: String) : Parcelable {
    constructor() : this("", "", "", "", "")
}