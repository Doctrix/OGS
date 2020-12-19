package fr.oxygames.app.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import fr.oxygames.app.R
import fr.oxygames.app.databinding.ActivityMainBinding
import fr.oxygames.app.model.UserModel
import org.jetbrains.anko.longToast
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var user: UserModel
    var firebaseUser: FirebaseUser? = null
    var refUsers: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        // toolbar
        val toolbar: Toolbar = binding.toolbarMain
        binding.toolbarMain.title = "My account"
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // data user
        refUsers!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    user = snapshot.getValue(UserModel::class.java)!!
                    binding.usernameMain.text = user.getUsername()
                    binding.facebookMain.text = user.getFacebook()
                    binding.instMain.text = user.getInstagram()
                    binding.websiteMain.text = user.getWebsite()
                    binding.statusProfilMain.text = user.getStatus()
                    Picasso.get().load(user.getCover()).placeholder(R.drawable.ic_cover)
                        .into(binding.coverMain)
                    Picasso.get().load(user.getAvatar()).placeholder(R.drawable.ic_profile)
                        .into(binding.imageProfilMain)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                longToast("error")
            }
        })

        // open page profile
        binding.usernameMain.setOnClickListener {
            val intent = Intent(this@MainActivity, HomeActivity::class.java)
            startActivity(intent)
            longToast("Loading ...")
        }

        // open website
        binding.websiteMain.setOnClickListener {
            val uri = Uri.parse(user.getWebsite())
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
            longToast("Website : $uri")
        }

        // open page instagram
        binding.instMain.setOnClickListener {
            val uri = Uri.parse(user.getInstagram())
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
            longToast("Page Instagram")
        }

        // open page facebook
        binding.facebookMain.setOnClickListener {
            val uri = Uri.parse(user.getFacebook())
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
            longToast("Page Facebook")
        }

    }

    private fun updateStatus(status: String) {
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        ref.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()
        updateStatus("Online")
    }

    override fun onPause() {
        super.onPause()
        updateStatus("Offline")
    }

    // <-- menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // button home
            R.id.action_home -> {
                val intent = Intent(this@MainActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }

            // button tutorial
            R.id.action_blog -> {
                val intent = Intent(this@MainActivity, PostsActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }

            // button store
            R.id.action_store -> {
                val intent = Intent(this@MainActivity, StoreActivity::class.java)
                startActivity(intent)
                finish()
                longToast("Store")
                return true
            }

            // button server
            R.id.action_server -> {
                val intent = Intent(this@MainActivity, LoginServerActivity::class.java)
                startActivity(intent)
                finish()
                longToast("Login to server")
                return true
            }

            // button support
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
                val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
                longToast("logout ...")
                return true
            }
        }
        return false
    }
    // menu -->
}
