package fr.oxygames.app.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import fr.oxygames.app.R
import fr.oxygames.app.activity.ui.main.SectionsPagerAdapter
import fr.oxygames.app.databinding.ActivityBlogBinding
import fr.oxygames.app.model.Users
import org.jetbrains.anko.longToast

class BlogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBlogBinding
    var firebaseUser: FirebaseUser? = null
    var refUsers: DatabaseReference? = null
    lateinit var user: Users

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)

        // toolbar
        val toolbar: Toolbar = binding.toolbarBlog
        toolbar.title = "Blog"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@BlogActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // data user
        refUsers!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    user = snapshot.getValue(Users::class.java)!!
                    binding.title.text = user.getUsername()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                longToast("error")
            }
        })

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Support", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    // <-- menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_admin, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> {
                val intent = Intent(this@BlogActivity, PostActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
        }
        return false
    }
    // menu -->
}
