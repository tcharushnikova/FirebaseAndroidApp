package com.app.firebaseapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.firebaseapp.adapter.MessageListAdapter
import com.app.firebaseapp.loginActivity.EmailLoginActivity
import com.app.firebaseapp.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var sendButton: Button
    private lateinit var questionText: EditText
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var chatMessageList: RecyclerView
    private var messageListAdapter: MessageListAdapter = MessageListAdapter()

    private var sPref: SharedPreferences? = null
    private val APP_PREFERENCES = "mysettings"
    private val APP_PREFERENCES_THEME = "THEME"
    private var isLight = true

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth
        if (auth.currentUser == null) {
            startActivity(Intent(this, EmailLoginActivity::class.java))
            finish()
        }
        else {
            initVars()
            initSpeech()

            db = Firebase.database
            dbRef = db.getReference(auth.currentUser!!.uid)

            setAndGetSharedPreferences()

            sendButton.setOnClickListener {
                onSend()
            }

            onChangeListener(dbRef)
        }
    }

    private fun initVars() {
        sendButton = findViewById(R.id.sendButton)
        questionText = findViewById(R.id.questionField)

        chatMessageList = findViewById(R.id.chatMessageList)
        chatMessageList.layoutManager = LinearLayoutManager(this)
        chatMessageList.adapter = messageListAdapter
    }

    private fun initSpeech() {
        Locale.setDefault(Locale("ru"))
        textToSpeech = TextToSpeech(applicationContext) {
            if (it != TextToSpeech.ERROR)
                textToSpeech.language = Locale.getDefault()
        }
    }

    private fun setAndGetSharedPreferences() {
        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        isLight = sPref!!.getBoolean(APP_PREFERENCES_THEME, true)
        if (!isLight)
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
    }

    private fun onSend() {
        val text: String = questionText.text.toString()
        if (text == "")
            return
        dbRef.child(dbRef.push().key!!).setValue(Message(text, isSend = true))

        val answer = AI().getAnswer(text)
        dbRef.child(dbRef.push().key!!).setValue(Message(answer, isSend = false))
        textToSpeech.speak(answer, TextToSpeech.QUEUE_FLUSH, null, null)

        questionText.setText("")
        dismissKeyboard()
    }

    private fun onChangeListener(dbRef: DatabaseReference) {
        dbRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<Message>()
                val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                for (s in snapshot.children) {
                    val textMessage = s.child("text").value as String
                    val isSendMessage = s.child("send").value as Boolean

                    val minutes = s.child("date").child("minutes").value
                    val hours = s.child("date").child("hours").value
                    val dateMessage = dateFormat.parse("$hours:$minutes")

                    val message = Message(textMessage, dateMessage!!, isSendMessage)
                    list.add(message)
                }
                messageListAdapter.messageList = list
                messageListAdapter.notifyDataSetChanged()
                chatMessageList.scrollToPosition(messageListAdapter.messageList.size - 1)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext, R.string.db_error, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun dismissKeyboard() {
        val view: View? = this.currentFocus
        if (view != null) {
            val imm =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(
                view.windowToken,
                0
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.day_settings -> {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
                isLight = true
            }
            R.id.night_settings -> {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
                isLight = false
            }
            R.id.clear -> {
                dbRef.removeValue()
                messageListAdapter.notifyDataSetChanged()
            }
            R.id.sign_out -> {
                auth.signOut()
                startActivity(Intent(this, EmailLoginActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        val editor: SharedPreferences.Editor = sPref!!.edit()
        editor.putBoolean(APP_PREFERENCES_THEME, isLight)
        editor.apply()

        super.onStop()
    }
}