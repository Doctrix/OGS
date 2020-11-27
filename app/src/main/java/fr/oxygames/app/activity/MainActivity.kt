package fr.oxygames.app.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import fr.oxygames.app.R
import fr.oxygames.app.model.Users
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.longToast

class MainActivity : AppCompatActivity() {

    var refUsers: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "My account"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            val intent = Intent (this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        refUsers!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    val user: Users? = snapshot.getValue(Users::class.java)
                    user!!.setStatus("Online")
                    username_TextView.text = user.getUsername()
                    fb_TextView.text = user.getFacebook()
                    insta_TextView.text = user.getInstagram()
                    website_TextView.text = user.getWebsite()
                    status_profil.text = user.getStatus()
                    Picasso.get().load(user.getCover()).placeholder(R.drawable.ic_cover).into(cover)
                    Picasso.get().load(user.getAvatar()).placeholder(R.drawable.ic_profile).into(image_profil)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                longToast("error ...")
            }
        })

        button_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            longToast("deco ...")

            val intent = Intent (this, WelcomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        button_home.setOnClickListener {

            val intent = Intent (this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        website_TextView.setOnClickListener {
            longToast("website")
        }
        insta_TextView.setOnClickListener {
            longToast("instagram")
        }
        fb_TextView.setOnClickListener {
            longToast("facebook")
        }
    }
}
