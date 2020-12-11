package fr.oxygames.app.activity

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import fr.oxygames.app.R
import fr.oxygames.app.adapter.BlogAdapter
import fr.oxygames.app.databinding.ActivityBlogBinding
import fr.oxygames.app.model.BlogModel
import fr.oxygames.app.model.UsersModel
import org.jetbrains.anko.longToast

class BlogActivity : AppCompatActivity() {

    // private lateinit var
    lateinit var auth: FirebaseAuth
    lateinit var recyclerView: RecyclerView
    lateinit var blogAdapter: BlogAdapter
    lateinit var listItems: MutableList<BlogModel>/* = mutableListOf()*/

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var database: FirebaseDatabase
    private lateinit var refUsers: DatabaseReference
    private lateinit var databaseReference: DatabaseReference
    private lateinit var user: UsersModel

    private lateinit var binding: ActivityBlogBinding

//    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // reference
        auth = FirebaseAuth.getInstance()
        recyclerView = binding.blogList

        database = FirebaseDatabase.getInstance()
        firebaseUser = auth.currentUser!!
        refUsers = database.reference.child("Users").child(firebaseUser.uid)
        databaseReference = database.reference.child("Blog")

        val toolbar: Toolbar = binding.toolbarBlog
        setSupportActionBar(toolbar)



        toolbar.title = "Blog"
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@BlogActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val layoutManager = LinearLayoutManager(this /*, LinearLayout.VERTICAL, true*/)
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true

        recyclerView.layoutManager = layoutManager

        listItems = ArrayList()

        refUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    user = snapshot.getValue(UsersModel::class.java)!!
                    binding.title.text = user.getUsername()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                longToast("error")
            }
        })

        /*recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
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
                when {
                    !recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE -> {

                    }
                }
            }
        })*/

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Support", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        loadPosts()
    }

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

                return true
            }
        }
        return false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    private fun loadPosts() {
        val postReference = databaseReference
        postReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listItems.clear()
                for (ds: DataSnapshot in snapshot.children){
                    val blogModel: BlogModel? = ds.getValue(BlogModel::class.java)
                    listItems.add(blogModel!!)
                    blogAdapter = BlogAdapter(this@BlogActivity, listItems)
                    recyclerView.adapter = blogAdapter

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}
