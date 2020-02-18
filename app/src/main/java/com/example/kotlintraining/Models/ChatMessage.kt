package com.example.kotlintraining.Models

class ChatMessage(val id: String, val fromId: String, val toId: String, val timeStamp: Long, val text: String) {
    constructor() : this("", "", "", -1, "")
}