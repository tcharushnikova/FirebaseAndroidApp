package com.app.firebaseapp.holder

import android.annotation.SuppressLint
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.firebaseapp.model.Message
import com.app.firebaseapp.R

class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var messageText: TextView? = itemView.findViewById(R.id.messageTextView)
    private var messageDate: TextView? = itemView.findViewById(R.id.messageDateView)

    @SuppressLint("SimpleDateFormat")
    fun bind(message: Message) {
        messageText!!.text = message.text
        val fmt: DateFormat = SimpleDateFormat("HH:mm")
        messageDate!!.text = fmt.format(message.date)
    }
}