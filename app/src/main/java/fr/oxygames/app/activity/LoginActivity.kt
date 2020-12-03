package fr.oxygames.app.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import fr.oxygames.app.databinding.ActivityLoginBinding
import fr.oxygames.app.viewModel.UserViewModel
import org.jetbrains.anko.longToast

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        binding.loginModel = null
        binding.lifecycleOwner = this

        // toolbar
        val toolbar: Toolbar = binding.toolbarLogin
        binding.toolbarLogin.title = "Login"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@LoginActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        val btnLogin = binding.buttonLogin
        val linkRegister = binding.registerLink

        FirebaseAuth.getInstance()

        btnLogin.setOnClickListener {
            loginUser()
        }
        linkRegister.setOnClickListener {
            longToast("Register loading ...")
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun loginUser() {
        val email = binding.userViewModel!!.user.value!!.getEmail()
        val password = binding.userViewModel!!.user.value!!.getPassword()
        when {
            email == "" -> {
                longToast("Please write Email")
            }
            password == "" -> {
                longToast("Please write Password")
            }
            else -> {
                mAuth!!.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            longToast("okokok")
                            /*val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()*/
                        } else {
                            longToast("nononono")
                            /*val message = task.exception!!.toString()
                            longToast("Error $message")
                            FirebaseAuth.getInstance().signOut()*/
                        }
                    }
            }
        }
    }


    // si le user est deja connecter
    /*override fun onStart() {
        super.onStart()

        if (FirebaseAuth.getInstance().currentUser != null)
        {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }*/
}
