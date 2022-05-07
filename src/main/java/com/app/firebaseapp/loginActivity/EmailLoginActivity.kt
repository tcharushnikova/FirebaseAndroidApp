package com.app.firebaseapp.loginActivity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.app.firebaseapp.MainActivity
import com.app.firebaseapp.R

class EmailLoginActivity : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var regButton: Button
    private lateinit var googleButton: Button
    private lateinit var phoneButton: Button
    private lateinit var guestButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private val clientId = "891038140640-30nihm1vdl7vdou4un243i5m4rb9nktp.apps.googleusercontent.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_login)
        supportActionBar?.hide()
        initVars()

        auth = Firebase.auth

        regButton.setOnClickListener {
            onClickSignUp()
        }

        loginButton.setOnClickListener {
            onClickSignIn()
        }

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null)
                    fbAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(baseContext, R.string.api_exception, Toast.LENGTH_SHORT).show()
            }
        }

        googleButton.setOnClickListener {
            onClickGoogleSignIn()
        }

        phoneButton.setOnClickListener {
            startActivity(Intent(this, PhoneLoginActivity::class.java))
        }

        guestButton.setOnClickListener {
            onClickGuestSignIn()
        }
    }

    private fun initVars() {
        email = findViewById(R.id.emailField)
        password = findViewById(R.id.passwordField)
        loginButton = findViewById(R.id.signUpButton)
        regButton = findViewById(R.id.regButton)
        googleButton = findViewById(R.id.loginButton2)
        phoneButton = findViewById(R.id.loginButton3)
        guestButton = findViewById(R.id.loginButton4)

        val arguments = intent.extras
        email.setText(arguments?.getString("email") ?: "")
        password.setText(arguments?.getString("password") ?: "")
    }

    private fun onClickSignUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        intent.putExtra("email", email.text.toString())
        intent.putExtra("password", password.text.toString())
        startActivity(intent)
    }

    private fun onClickSignIn() {
        if (email.text.toString() != "" && password.text.toString() != "")
            auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        if (auth.currentUser!!.isEmailVerified) {
                            Toast.makeText(baseContext, R.string.login_successful, Toast.LENGTH_SHORT)
                                .show()
                            checkAuthState()
                        } else
                            Toast.makeText(baseContext, R.string.verify_email, Toast.LENGTH_SHORT)
                                .show()
                    } else
                        Toast.makeText(baseContext, R.string.login_failed, Toast.LENGTH_SHORT)
                            .show()
                }
        else
            Toast.makeText(baseContext, R.string.enter_email_and_password, Toast.LENGTH_SHORT)
                .show()
    }

    private fun getClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(this, gso)
    }

    private fun onClickGoogleSignIn() {
        val signInClient = getClient()
        launcher.launch(signInClient.signInIntent)
    }

    private fun fbAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(baseContext, R.string.login_successful, Toast.LENGTH_SHORT).show()
                checkAuthState()
            } else
                Toast.makeText(baseContext, R.string.login_failed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun onClickGuestSignIn() {
        auth.signInAnonymously().addOnCompleteListener {
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