package fr.oxygames.app.activity

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import fr.oxygames.app.R
import fr.oxygames.app.adapter.PostsAdapter
import fr.oxygames.app.databinding.ActivityPostsBinding
import fr.oxygames.app.databinding.ActivityPostsDetailsBinding
import fr.oxygames.app.model.PostModel
import fr.oxygames.app.model.UserModel
import org.jetbrains.anko.longToast

/**
 * Created by Doctrix on 06/12/2020.
 */

//private const val TAG = "PostsActivity"

class PostsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostsBinding
    private lateinit var bindingPost: ActivityPostsDetailsBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var refUsers: DatabaseReference
    lateinit var posts: MutableList<PostModel>

    lateinit var adapter: PostsAdapter
    lateinit var rvPosts: ViewPager
    lateinit var refPosts: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val databaseInstance = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser!!

        //posts = mutableListOf()

        // toolbar
        val toolbar: Toolbar = binding.toolbarPosts
        toolbar.title = "Blog"
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@PostsActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // ref user toolbar
        refUsers = databaseInstance.reference.child("Users").child(firebaseUser.uid)
        refUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserModel::class.java)!!
                    binding.tvTitlePosts.text = user.getUsername()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        refPosts = databaseInstance.reference.child("Blog").child(firebaseUser.uid)

        rvPosts = binding.vpPosts
        //rvPosts.setHasFixedSize(true)
        //rvPosts.layoutManager = LinearLayoutManager(this)

        adapter = PostsAdapter(this, posts)
        rvPosts.adapter = adapter
        //loadPosts()
    }

    private fun loadPosts() {
        refPosts.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val posts = snapshot.getValue(PostModel::class.java)!!
                    bindingPost.tvTitlePostDetails.text = posts.setTitle("title").toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                longToast("error")
            }
        })
    }

        // menu toolbar
        override fun onCreateOptionsMenu(menu: Menu): Boolean {
            menuInflater.inflate(R.menu.menu_admin, menu)
            return true
        }
        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.action_add -> {
                    val intent = Intent(this@PostsActivity, AddPostsActivity::class.java)
                    startActivity(intent)
                    finish()
                    return true
                }
                /*R.id.action_refresh -> {

                    return true
                }*/
            }
            return false
        }
        override fun onBackPressed() {
            super.onBackPressed()
            finishAffinity()
        }
    }
