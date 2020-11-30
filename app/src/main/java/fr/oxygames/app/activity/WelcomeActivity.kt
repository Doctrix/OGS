package fr.oxygames.app.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import fr.oxygames.app.R
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.android.synthetic.main.content_welcome.*
import org.jetbrains.anko.longToast

class WelcomeActivity : AppCompatActivity() {
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        button_page_login.setOnClickListener {
            val intent = Intent (this@WelcomeActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            longToast("loading page login ...")
        }

        button_page_register.setOnClickListener {
            val intent = Intent (this@WelcomeActivity, RegisterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            longToast("loading page register ...")
        }

        button_page_help.setOnClickListener {
            val uri = Uri.parse("https://oxygames.fr")
            val intent = Intent(Intent.ACTION_VIEW, uri)
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
