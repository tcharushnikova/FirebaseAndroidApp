package com.app.firebaseapp.loginActivity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.app.firebaseapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var createText: TextView
    private lateinit var successText: TextView
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var regButton: Button
    private lateinit var backButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.hide()
        initVars()

        auth = Firebase.auth

        backButton.setOnClickListener {
            onClickBack()
        }

        regButton.setOnClickListener {
            onClickSignUp()
        }
    }

    private fun initVars() {
        createText = findViewById(R.id.createText)
        successText = findViewById(R.id.successText)
        email = findViewById(R.id.emailField)
        password = findViewById(R.id.passwordField)
        regButton = findViewById(R.id.signUpButton)
        backButton = findViewById(R.id.back)

        val arguments = intent.extras
        email.setText(arguments?.getString("email") ?: "")
        password.setText(arguments?.getString("password") ?: "")
    }

    private fun onClickBack() {
        val intent = Intent(this, EmailLoginActivity::class.java)
        intent.putExtra("email", email.text.toString())
        intent.putExtra("password", password.text.toString())
        startActivity(intent)
    }

    private fun onClickSignUp() {
        if (email.text.toString() != "" && password.text.toString() != "")
            auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(baseContext, R.string.registration_successful, Toast.LENGTH_SHORT)
                            .show()
                        emailVerification()
                    } else
                        Toast.makeText(baseContext, R.string.registration_failed, Toast.LENGTH_SHORT)
                            .show()
                }
        else
            Toast.makeText(baseContext, R.string.enter_email_and_password, Toast.LENGTH_SHORT)
                .show()
    }

    private fun emailVerification() {
        auth.useAppLanguage()
        auth.currentUser!!.sendEmailVerification().addOnCompleteListener {
            if (it.isSuccessful) {
                createText.visibility = View.GONE
                email.visibility = View.GONE
                password.visibility = View.GONE
                regButton.visibility = View.GONE

                successText.visibility = View.VISIBLE
            } else
                Toast.makeText(baseContext, R.string.failed_verify_email, Toast.LENGTH_SHORT).show()
        }
    }
}