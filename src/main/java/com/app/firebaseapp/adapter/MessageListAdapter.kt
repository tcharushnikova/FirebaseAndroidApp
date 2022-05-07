package com.app.firebaseapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.firebaseapp.model.Message
import com.app.firebaseapp.R
import com.app.firebaseapp.holder.MessageViewHolder

class MessageListAdapter : RecyclerView.Adapter<MessageViewHolder>() {
    private val ASSISTANT_TYPE = 0
    private val USER_TYPE = 1

    var messageList: ArrayList<Message> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view: View = if (viewType == USER_TYPE)
            LayoutInflater.from(parent.context)
                .inflate(R.layout.user_message, parent, false)
        else
            LayoutInflater.from(parent.context)
                .inflate(R.layout.assistant_message, parent, false)

        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messageList[position])
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(index: Int): Int {
        return if (messageList[index].isSend) USER_TYPE
        else ASSISTANT_TYPE
    }
}