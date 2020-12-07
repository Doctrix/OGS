package fr.oxygames.app.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import fr.oxygames.app.R
import fr.oxygames.app.adapter.BlogListAdapter
import fr.oxygames.app.adapter.SectionsPagerAdapter
import fr.oxygames.app.databinding.ActivityBlogBinding
import fr.oxygames.app.databinding.FragmentBlogListBinding
import fr.oxygames.app.model.Blog
import fr.oxygames.app.model.Users
import org.jetbrains.anko.longToast

class BlogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBlogBinding
    private lateinit var bindingBlogList: FragmentBlogListBinding
    lateinit var blog :Blog
    lateinit var user: Users
    lateinit var auth: FirebaseAuth
    lateinit var firebaseUser: FirebaseUser
    lateinit var refUsers: DatabaseReference
    lateinit var databaseReference: DatabaseReference
    lateinit var database: FirebaseDatabase
    var listItems: MutableList<Blog> = mutableListOf()
    var adapter: BlogListAdapter = BlogListAdapter(this, listItems)

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlogBinding.inflate(layoutInflater)
        bindingBlogList = FragmentBlogListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        firebaseUser = auth.currentUser!!
        refUsers = database.reference.child("Users").child(firebaseUser.uid)
        databaseReference = database.reference.child("Blog")

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPage: ViewPager = binding.viewPager
        val tab: TabLayout = binding.tabs
        viewPage.adapter = sectionsPagerAdapter
        tab.setupWithViewPager(viewPage)

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
        refUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    user = snapshot.getValue(Users::class.java)!!
                    binding.title.text = user.getUsername()
                    fillListItems()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                longToast("error")
            }
        })

        val blogListRow = bindingBlogList.blogList
        blogListRow.setHasFixedSize(true)
        blogListRow.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        blogListRow.adapter = adapter

        blogListRow.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE){

                }
            }
        })

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Support", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private fun fillListItems() {
        val postId = auth.currentUser
        val postReference = databaseReference.child(postId?.uid!!)
        listItems.clear()
        val model: Blog = Blog()
        model.setTitle("title")
        model.setDesc("desc")
        model.setImage("image")
        listItems.add(model)
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
