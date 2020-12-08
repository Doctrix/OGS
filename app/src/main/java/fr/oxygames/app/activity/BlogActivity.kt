package fr.oxygames.app.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
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

    // private lateinit var
    private lateinit var binding: ActivityBlogBinding
    private lateinit var bindingBlogList: FragmentBlogListBinding
    private lateinit var blog :Blog

    // lateinit var
    lateinit var user: Users
    lateinit var auth: FirebaseAuth
    lateinit var firebaseUser: FirebaseUser
    lateinit var refUsers: DatabaseReference
    lateinit var databaseReference: DatabaseReference
    lateinit var database: FirebaseDatabase

    // val
    val ITEM_COUNT = 21

    // var
    var total_item = 0
    var last_visible_item = 0

    var isLoading = false
    var isMaxData = false

    var last_node: String? = ""
    var last_key: String? = ""

    var listItems: MutableList<Blog> = mutableListOf()
    var adapter: BlogListAdapter = BlogListAdapter(this, listItems)

    // menu
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
            R.id.action_refresh -> {
                isMaxData = false
                last_node = adapter.lastItemId
                adapter.removeLastItem()
                adapter.notifyDataSetChanged()
                getLastKey()
                getPost()
            }
        }
        return false
    }

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // reference
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        firebaseUser = auth.currentUser!!
        refUsers = database.reference.child("Users").child(firebaseUser.uid)
        databaseReference = database.reference.child("Blog")
        binding = ActivityBlogBinding.inflate(layoutInflater)
        bindingBlogList = FragmentBlogListBinding.inflate(layoutInflater)

        // val
        val blogListRow: RecyclerView = bindingBlogList.blogList
        val viewPage: ViewPager = binding.viewPager
        val tab: TabLayout = binding.tabs
        val toolbar: Toolbar = binding.toolbarBlog
        val layoutManager = LinearLayoutManager(this , LinearLayout.VERTICAL, false)
        val dividerItemDecoration = DividerItemDecoration(blogListRow.context, layoutManager.orientation)
        val adapter = BlogListAdapter(this, listItems)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)

        // set
        setContentView(binding.root)
        setSupportActionBar(toolbar)

        getLastKey()
        getPost()

        // title page
        toolbar.title = "Blog"
        blogListRow.layoutManager = layoutManager
        viewPage.adapter = sectionsPagerAdapter
        blogListRow.adapter = adapter

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        blogListRow.setHasFixedSize(true)

        blogListRow.addItemDecoration(dividerItemDecoration)
        tab.setupWithViewPager(viewPage)

        // add
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
        blogListRow.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                total_item = layoutManager.itemCount
                last_visible_item = layoutManager.findLastVisibleItemPosition()

                if(!isLoading && total_item <= last_visible_item+ITEM_COUNT)
                {
                    getPost()
                    isLoading = true
                }

            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE){

                }
            }
        })

        // onClickListener
        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Support", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@BlogActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun getPost() {
        if (!isMaxData)
        {
            val query:Query
            if(TextUtils.isEmpty(last_node))
                query = FirebaseDatabase.getInstance().reference
                    .child("Blog")
                    .orderByKey()
                    .limitToFirst(ITEM_COUNT)
            else
                query = FirebaseDatabase.getInstance().reference
                    .child("Blog")
                    .orderByKey()
                    .startAt(last_node)
                    .limitToFirst(ITEM_COUNT)
            query.addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.hasChildren())
                    {
                        val blogList = ArrayList<Blog>()
                        for (snapshot: DataSnapshot in p0.children)
                            blogList.add(snapshot.getValue(Blog::class.java)!!)
                        last_node = blogList[blogList.size - 1].getUID()

                        if (!last_node.equals(last_key))
                            blogList.removeAt(blogList.size - 1)
                        else
                            last_node = "end"
                        adapter.addAll(blogList)
                        isLoading = false
                    }
                    else
                    {
                        isLoading = false
                        isMaxData = true
                    }
                }
            })
        }
    }

    private fun getLastKey() {
        val get_last_key:Query = FirebaseDatabase.getInstance().getReference()
            .child("Blog")
            .orderByKey()
            .limitToLast(1)
        get_last_key.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (blogSnapShot : DataSnapshot in snapshot.children)
                    last_key = blogSnapShot.key
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
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
}
