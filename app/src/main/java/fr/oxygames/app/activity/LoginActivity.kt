package fr.oxygames.app.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import fr.oxygames.app.R
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.longToast

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val toolbar: Toolbar = findViewById(R.id.toolbar_login)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Login"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent (this@LoginActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth = FirebaseAuth.getInstance()

        button_login.setOnClickListener {
            loginUser()
        }

        text_register.setOnClickListener {
            longToast("Input provided")
            val intent = Intent(this, RegisterActivity::class.java) //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun loginUser() {
        val email: String = email_login.text.toString()
        val password: String = password_login.text.toString()

        when {
            email == "" -> {
            longToast("Please write Email")
            }
            password == "" -> {
                longToast("Please write Password")
            }
            else -> {
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                    if (task.isSuccessful)
                    {
                        val intent = Intent(this,MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                        longToast("Loading ...")
                    }
                    else
                    {
                        longToast("Error Message")
                    }
                }
            }
        }
    }
}
