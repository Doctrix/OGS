package fr.oxygames.app.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import fr.oxygames.app.databinding.ActivityWelcomeBinding
import fr.oxygames.app.databinding.ContentWelcomeBinding
import org.jetbrains.anko.longToast

class WelcomeActivity : AppCompatActivity() {
    private lateinit var bindingContent: ContentWelcomeBinding
    private lateinit var bindingActivity: ActivityWelcomeBinding

    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bindingContent  = ContentWelcomeBinding.inflate(layoutInflater)
        val bindingActivity = ActivityWelcomeBinding.inflate(layoutInflater)
        val viewContent = bindingContent.root
        val viewActivity = bindingActivity.root
        setContentView(viewContent)
        setContentView(viewActivity)

        bindingActivity.toolbarWelcome.title = "WELCOME"
        bindingContent.titleWelcome.text = "hello"
        bindingActivity.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        bindingContent.buttonPageLogin.setOnClickListener{
            bindingContent.apply {
                val intent = Intent(this@WelcomeActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        bindingContent.buttonPageRegister.setOnClickListener {
            intent = Intent(this@WelcomeActivity, RegisterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            longToast("loading page register ...")
        }

        bindingContent.buttonPageHelp.setOnClickListener() {
            val uri = Uri.parse("https://oxygames.fr")
            val intent= Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
            longToast("Website : $uri")
        }
    }

//     override fun onStart() {
//         super.onStart()
//         firebaseUser = FirebaseAuth.getInstance().currentUser
//         if (firebaseUser != null) {
//             val intent = Intent(this@WelcomeActivity, MainActivity::class.java)
//             startActivity(intent)
//             finish()
//         }
//     }
}
