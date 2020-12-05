package fr.oxygames.app.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import fr.oxygames.app.databinding.ActivityLoginBinding
import fr.oxygames.app.model.Users
import fr.oxygames.app.presenter.Presenter
import fr.oxygames.app.viewModel.LoginViewModel
import org.jetbrains.anko.longToast

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var user: Users

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // toolbar - back menu
        val toolbar: Toolbar = binding.toolbarLogin
        binding.toolbarLogin.title = "Login"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@LoginActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        val viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        viewModel.getResultLogin().observe(this, Observer {
            longToast("$user.getUsername()")
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        })

        binding.presenter = object : Presenter {
            override fun login() {
                val email: String = binding.emailLogin.text.toString()
                val password: String = binding.passwordLogin.text.toString()
                viewModel.loginCall(email, password)
            }
        }

        val linkRegister = binding.registerLink
        linkRegister.setOnClickListener {
            longToast("Register loading ...")
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
