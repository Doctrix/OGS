package fr.oxygames.app.activity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import fr.oxygames.app.R
import fr.oxygames.app.model.Users
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_visit_user_profile.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.longToast

class MainActivity : AppCompatActivity() {

    var refUsers: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    var user: Users? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar_main)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "My account"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // button back
        toolbar.setNavigationOnClickListener {
            finish()
        }

        refUsers!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    user = snapshot.getValue(Users::class.java)

                    username_main.text = user!!.getUsername()
                    facebook_main.text = user!!.getFacebook()
                    inst_main.text = user!!.getInstagram()
                    website_main.text = user!!.getWebsite()
                    status_profil_main.text = user!!.getStatus()
                    
                    Picasso.get().load(user!!.getCover()).placeholder(R.drawable.ic_cover).into(cover_main)
                    Picasso.get().load(user!!.getAvatar()).placeholder(R.drawable.ic_profile).into(image_profil_main)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                longToast("error")
            }
        })

        // open website
        website_main.setOnClickListener {
            val uri = Uri.parse(user!!.getWebsite())
            longToast("Website : $uri")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        // open page instagram
        inst_main.setOnClickListener {
            longToast("Page Instagram")
            val uri = Uri.parse(user!!.getInstagram())
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        // open page facebook
        facebook_main.setOnClickListener {
            longToast("Page Facebook")
            val uri = Uri.parse(user!!.getFacebook())
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        // button HOME
        button_home_main.setOnClickListener {
            val intent = Intent (this@MainActivity, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        // button LOGOUT
        button_logout_main.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            longToast("deconnexion")
            val intent = Intent (this@MainActivity, WelcomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun updateStatus(status : String) {
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        ref!!.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()

        updateStatus("Online")
        val textViewColor = status_profil_main.findViewById(R.id.status_profil_main) as TextView
        textViewColor.setTextColor(Color.parseColor("green"))
    }

    override fun onPause() {
        super.onPause()

        updateStatus("offline")
        val textViewColor = status_profil_main.findViewById(R.id.status_profil_main) as TextView
        textViewColor.setTextColor(Color.parseColor("white"))
    }
}
