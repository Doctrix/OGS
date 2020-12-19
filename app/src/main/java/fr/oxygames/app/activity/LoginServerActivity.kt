package fr.oxygames.app.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import fr.oxygames.app.databinding.ActivityLoginServerBinding
import fr.oxygames.app.model.UserModel
import org.jetbrains.anko.longToast

class LoginServerActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginServerBinding
    lateinit var user: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityLoginServerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // toolbar - back menu
        val toolbar: Toolbar = binding.toolbarLogin
        binding.toolbarLogin.title = "Login Server"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@LoginServerActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        /*val viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        viewModel.getResultLogin().observe(this, Observer {
            longToast("$user.getUsername()")
            val intent = Intent(this@LoginServerActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        })*/

        /*binding.presenter = object : DataBase? {
            override fun connection() {
                val email: String = binding.emailLogin.text.toString()
                val password: String = binding.passwordLogin.text.toString()
                viewModel.loginCall(email, password)
            }
        }*/

        val linkRegister = binding.registerLink
        linkRegister.setOnClickListener {
            longToast("Register loading ...")
            val intent = Intent(this@LoginServerActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
