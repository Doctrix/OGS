package fr.oxygames.app.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
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

    var listItems: MutableList<Blog> = mutableListOf()
    var adapter: BlogListAdapter = BlogListAdapter(this,listItems)

    private var database: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    var refUsers: DatabaseReference? = null
    lateinit var user: Users

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlogBinding.inflate(layoutInflater)
        bindingBlogList = FragmentBlogListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        //database = FirebaseDatabase.getInstance().reference.child("Blog")

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPage: ViewPager = binding.viewPager
        viewPage.adapter = sectionsPagerAdapter
        val tab: TabLayout = binding.tabs
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

        val blogListRow = bindingBlogList.blogList
        blogListRow.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL,false)
        blogListRow.adapter = adapter

        fillListItems()

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Support", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private fun fillListItems() {
        listItems.clear()
        for (x in 1..3) {
            val model: Blog = Blog()
            model.setTitle("Title $x")
            model.setDesc("desc $x")
            listItems.add(model)
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
