package com.app.firebaseapp.model

import java.util.*

class Message {
    val text: String
    val date: Date
    val isSend: Boolean

    constructor(text: String, isSend: Boolean) {
        this.text = text
        this.date = Date()
        this.isSend = isSend
    }

    constructor(text: String, date: Date, isSend: Boolean) {
        this.text = text
        this.date = date
        this.isSend = isSend
    }
}