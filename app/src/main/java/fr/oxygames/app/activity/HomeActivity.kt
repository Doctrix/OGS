package fr.oxygames.app.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseUserMetadata
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import fr.oxygames.app.R
import fr.oxygames.app.fragment.ChatsFragment
import fr.oxygames.app.fragment.SearchFragment
import fr.oxygames.app.fragment.SettingsFragment
import fr.oxygames.app.model.Chat
import fr.oxygames.app.model.Users
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.longToast

class HomeActivity : AppCompatActivity() {

    private var refUsers: DatabaseReference? = null
    private var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        val toolbar: Toolbar = findViewById(R.id.toolbar_home)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Home"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // back button
        toolbar.setNavigationOnClickListener {
            val intent = Intent (this@HomeActivity, MainActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        val viewPager: ViewPager = findViewById(R.id.view_pager)

        val ref = FirebaseDatabase.getInstance().reference.child("Chats")

        ref!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot)
            {
                val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

                var countUnreadMessages = 0

                for (dataSnapShot in p0.children)
                {
                    val chat = dataSnapShot.getValue(Chat::class.java)
                    if (chat!!.getReceiver().equals(firebaseUser!!.uid) && !chat.getIsSeen()){
                        countUnreadMessages += 1
                    }
                }
                if (countUnreadMessages == 0){
                    viewPagerAdapter.addFragment(ChatsFragment(), "Chats")
                }
                else
                {
                    viewPagerAdapter.addFragment(ChatsFragment(), "($countUnreadMessages) Chats")
                }

                /*viewPagerAdapter.addFragment(SearchFragment(), "Search")*/
                viewPagerAdapter.addFragment(SettingsFragment(), "Settings")
                viewPager.adapter = viewPagerAdapter
                tabLayout.setupWithViewPager(viewPager)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        // username and profile picture
        refUsers!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists())
                {
                    val user: Users? = p0.getValue(Users::class.java)

                    user_name.text = user!!.getUsername()
                    Picasso.get().load(user.getAvatar()).placeholder(R.drawable.ic_avatar).into(profile_image_home)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                longToast("error")
            }
        })
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
                longToast("Loading ...")
                val intent = Intent(this@HomeActivity, HomeActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                return true
            }

            // button profile
            R.id.action_profile -> {
                longToast("Loading ...")
                val intent = Intent(this@HomeActivity, MainActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                return true
            }

            // button logout
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                longToast("deco ...")
                val intent = Intent (this@HomeActivity, WelcomeActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                return true
            }
        }
        return false
    }
    // menu -->

    internal class ViewPagerAdapter(fragmentManager: FragmentManager) :
            FragmentPagerAdapter(fragmentManager)
    {
        private val fragments: ArrayList<Fragment>
        private val titles: ArrayList<String>

        init {
            fragments = ArrayList<Fragment>()
            titles = ArrayList<String>()
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(i: Int): CharSequence {
            return titles[i]
        }
    }

    private fun updateStatus(status : String)
    {
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        ref!!.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()

        updateStatus("Online")
    }

    override fun onPause() {
        super.onPause()

        updateStatus("offline")
    }
}
