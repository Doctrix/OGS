package fr.oxygames.app.activity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import fr.oxygames.app.R
import fr.oxygames.app.databinding.ActivityMainBinding
import fr.oxygames.app.databinding.ContentMainBinding
import fr.oxygames.app.model.Users
import org.jetbrains.anko.longToast

class MainActivity : AppCompatActivity() {
    private lateinit var bindingActivity: ActivityMainBinding

    var refUsers: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    var user: Users? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        val bindingActivity = ActivityMainBinding.inflate(layoutInflater)
        val bindingContent = ContentMainBinding.inflate(layoutInflater)

        setContentView(bindingActivity.root)
        setContentView(bindingContent.root)

        val toolbar: Toolbar = bindingActivity.toolbarMain

        setSupportActionBar(bindingActivity.toolbarMain)
        bindingActivity.toolbarMain.title = "My account"

        // button back
        toolbar.setNavigationOnClickListener {
            finish()
        }

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        // data user
        refUsers!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    user = snapshot.getValue(Users::class.java)
                    bindingContent.usernameMain.text = user!!.getUsername()
                    bindingContent.facebookMain.text = user!!.getFacebook()
                    bindingContent.instMain.text = user!!.getInstagram()
                    bindingContent.websiteMain.text = user!!.getWebsite()
                    bindingActivity.statusProfilMain.text = user!!.getStatus()
                    Picasso.get().load(user!!.getCover()).placeholder(R.drawable.ic_cover).into(bindingActivity.coverMain)
                    Picasso.get().load(user!!.getAvatar()).placeholder(R.drawable.ic_profile).into(bindingActivity.imageProfilMain)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                longToast("error")
            }
        })

        bindingContent.usernameMain.setOnClickListener {
            val intent = Intent(this@MainActivity, HomeActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            longToast("Loading ...")
        }

        // open website
        bindingContent.websiteMain.setOnClickListener {
            val uri = Uri.parse(user!!.getWebsite())
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
            longToast("Website : $uri")
        }

        // open page instagram
        bindingContent.instMain.setOnClickListener {
            val uri = Uri.parse(user!!.getInstagram())
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
            longToast("Page Instagram")
        }

        // open page facebook
        bindingContent.facebookMain.setOnClickListener {
            val uri = Uri.parse(user!!.getFacebook())
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
            longToast("Page Facebook")
        }
    }

    // <-- menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId)
        {
            // button home
            R.id.action_home -> {
                val intent = Intent(this@MainActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
                longToast("Loading ...")
                return true
            }

            // button profile
            R.id.action_help -> {
                val uri = Uri.parse("https://oxygames.fr")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
                longToast("Website : $uri")
                return true
            }

            // button logout
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent (this@MainActivity, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
                longToast("deco ...")
                return true
            }
        }
        return false
    }
    // menu -->

    private fun updateStatus(status : String) {
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        ref!!.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()
        updateStatus("Online")
        val textViewColor = bindingActivity.statusProfilMain.findViewById(R.id.status_profil_main) as TextView
        textViewColor.setTextColor(Color.parseColor("green"))
    }

    override fun onPause() {
        super.onPause()
        updateStatus("offline")
        val textViewColor = bindingActivity.statusProfilMain.findViewById(R.id.status_profil_main) as TextView
        textViewColor.setTextColor(Color.parseColor("white"))
    }
}
