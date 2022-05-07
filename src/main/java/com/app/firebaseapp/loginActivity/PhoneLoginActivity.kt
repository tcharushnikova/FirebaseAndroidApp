package com.app.firebaseapp.loginActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.app.firebaseapp.MainActivity
import java.util.concurrent.TimeUnit
import com.app.firebaseapp.R

class PhoneLoginActivity : AppCompatActivity() {
    private lateinit var phoneText: TextView
    private lateinit var phone: EditText
    private lateinit var phoneButton: Button

    private lateinit var codeText: TextView
    private lateinit var code: EditText
    private lateinit var verifyButton: Button

    private lateinit var backButton: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var verificationID: String
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_login)
        supportActionBar?.hide()
        initVars()

        auth = Firebase.auth

        backButton.setOnClickListener {
            onClickBack()
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                progressBar.visibility = View.VISIBLE
                code.setText(credential.smsCode)
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(baseContext, R.string.verification_failed, Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onCodeSent(
                s: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                phoneText.visibility = View.GONE
                phone.visibility = View.GONE
                phoneButton.visibility = View.GONE

                codeText.visibility = View.VISIBLE
                code.visibility = View.VISIBLE
                verifyButton.visibility = View.VISIBLE

                verificationID = s
                Toast.makeText(baseContext, R.string.code_sent, Toast.LENGTH_SHORT)
                    .show()
            }
        }

        phoneButton.setOnClickListener {
            if (phone.text.toString() == "" || phone.text.length != 12)
                Toast.makeText(baseContext, R.string.enter_phone_to_verify, Toast.LENGTH_SHORT)
                    .show()
            else
                sendVerificationCode()
        }

        verifyButton.setOnClickListener {
            if (code.text.toString() == "")
                Toast.makeText(baseContext, R.string.enter_code_to_verify, Toast.LENGTH_SHORT)
                    .show()
            else {
                progressBar.visibility = View.VISIBLE
                verifyCode(code.text.toString())
            }
        }
    }

    private fun initVars() {
        phoneText = findViewById(R.id.enterPhoneText)
        phone = findViewById(R.id.phoneField)
        phoneButton = findViewById(R.id.sendCodeButton)
        codeText = findViewById(R.id.enterCodeText)
        code = findViewById(R.id.codeField)
        verifyButton = findViewById(R.id.verifyButton)
        backButton = findViewById(R.id.back)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun onClickBack() {
        startActivity(Intent(this, EmailLoginActivity::class.java))
    }

    private fun sendVerificationCode() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone.text.toString())
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyCode(smsCode: String) {
        val credential = PhoneAuthProvider.getCredential(verificationID, smsCode)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(baseContext, R.string.login_successful, Toast.LENGTH_SHORT).show()
                checkAuthState()
            } else
                Toast.makeText(baseContext, R.string.login_failed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkAuthState() {
        if (auth.currentUser != null)
            startActivity(Intent(this, MainActivity::class.java))
    }
}