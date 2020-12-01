package fr.oxygames.app.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import fr.oxygames.app.databinding.ActivityWelcomeBinding
import org.jetbrains.anko.longToast

class WelcomeActivity : AppCompatActivity()
{
    private lateinit var bindingActivity: ActivityWelcomeBinding
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        val binding = ActivityWelcomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.toolbarWelcome.title = "WELCOME"
        binding.titleWelcome.text = "OxyGameS"
        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        binding.buttonPageLogin.setOnClickListener{
            binding.apply {
                val intent = Intent(this@WelcomeActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        binding.buttonPageRegister.setOnClickListener {
            intent = Intent(this@WelcomeActivity, RegisterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            longToast("loading page register ...")
        }

        binding.buttonPageHelp.setOnClickListener() {
            val uri = Uri.parse("https://oxygames.fr")
            val intent= Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
            longToast("Website : $uri")
        }
    }

     override fun onStart() {
         super.onStart()
         firebaseUser = FirebaseAuth.getInstance().currentUser
         if (firebaseUser != null) {
             val intent = Intent(this@WelcomeActivity, MainActivity::class.java)
             startActivity(intent)
             finish()
         }
     }
}
