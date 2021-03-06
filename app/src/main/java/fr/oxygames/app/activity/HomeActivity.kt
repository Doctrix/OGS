package fr.oxygames.app.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import fr.oxygames.app.R
import fr.oxygames.app.databinding.ActivityHomeBinding
import fr.oxygames.app.fragment.ChatsFragment
import fr.oxygames.app.fragment.SearchFragment
import fr.oxygames.app.fragment.SettingsFragment
import fr.oxygames.app.model.ChatModel
import fr.oxygames.app.model.UserModel
import org.jetbrains.anko.longToast

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    lateinit var chatModel: ChatModel
    lateinit var user: UserModel
    lateinit var database: FirebaseDatabase
    lateinit var refUsers: DatabaseReference

    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // toolbar
        val toolbar: Toolbar = binding.toolbarHome
        binding.toolbarHome.title = ""
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@HomeActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val tabLayout: TabLayout = binding.tabLayoutHome
        val viewPager: ViewPager = binding.viewPagerHome

        database = FirebaseDatabase.getInstance()

        firebaseUser = FirebaseAuth.getInstance().currentUser

        refUsers = database.reference.child("Users").child(firebaseUser!!.uid)
        // username and profile picture
        refUsers!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    user = p0.getValue(UserModel::class.java)!!
                    binding.usernameHome.text = user.getUsername()
                    Picasso.get().load(user.getAvatar()).placeholder(R.drawable.ic_avatar)
                        .into(binding.profileImageHome)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                longToast("error")
            }
        })

        val refChat = database.reference.child("Chats")
        refChat!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
                var countUnreadMessages = 0

                for (dataSnapShot in p0.children) {
                    val chatModel = dataSnapShot.getValue(ChatModel::class.java)
                    if (chatModel!!.getReceiver() == firebaseUser!!.uid && !chatModel.isIsSeen()) {
                        countUnreadMessages += 1
                    }
                }
                if (countUnreadMessages == 0) {
                    viewPagerAdapter.addFragment(ChatsFragment(), "Chats")
                } else {
                    viewPagerAdapter.addFragment(ChatsFragment(), "($countUnreadMessages) Chats")
                }

                viewPagerAdapter.addFragment(SearchFragment(), "Search")
                viewPagerAdapter.addFragment(SettingsFragment(), "Settings")
                viewPager.adapter = viewPagerAdapter
                tabLayout.setupWithViewPager(viewPager)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    // <-- menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // button profile
            R.id.action_profile -> {
                val intent = Intent(this@HomeActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }

            // button logout
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this@HomeActivity, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
        }
        return false
    }
    // menu -->

    internal class ViewPagerAdapter(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager) {
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
}
