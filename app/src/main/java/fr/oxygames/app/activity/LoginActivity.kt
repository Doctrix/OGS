package fr.oxygames.app.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import fr.oxygames.app.databinding.ActivityLoginBinding
import org.jetbrains.anko.longToast

class LoginActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // toolbar
        val toolbar: Toolbar = binding.toolbarLogin
        binding.toolbarLogin.title = "Login"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent (this@LoginActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth = FirebaseAuth.getInstance()

        binding.buttonLogin.setOnClickListener {
            loginUser()
        }

        binding.textRegister.setOnClickListener {
            longToast("Register loading ...")
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loginUser() {
        val email: String = binding.emailLogin.text.toString()
        val password: String = binding.passwordLogin.text.toString()

        if (email == "" )
        {
            longToast("Please write Email")
        }
        else if (password == "")
        {
            longToast("Please write Password")
        }
        else
        {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    longToast("Loading ...")
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                else
                {
                    longToast("Error Message")
                }
            }
        }
    }
}
