package fr.oxygames.app.activity

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import fr.oxygames.app.R
import fr.oxygames.app.adapter.PostsAdapter
import fr.oxygames.app.databinding.ActivityPostsBinding
import fr.oxygames.app.model.PostModel
import fr.oxygames.app.model.UserModel

/**
 * Created by Doctrix on 06/12/2020.
 */

private const val TAG = "PostsActivity"
class PostsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var refUsers: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var posts: MutableList<PostModel>
    private lateinit var user: UserModel
    private lateinit var adapter: PostsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        posts = mutableListOf()

        adapter = PostsAdapter(this, posts)

        val rvPosts = binding.blogList
        rvPosts.adapter = adapter
        rvPosts.layoutManager = LinearLayoutManager(this)

        // toolbar
        val toolbar: Toolbar = binding.toolbarBlog
        toolbar.title = "Blog"
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@PostsActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // ref user toolbar
        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser!!
        database = FirebaseDatabase.getInstance()
        refUsers = database.reference.child("Users").child(firebaseUser.uid)
        refUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserModel::class.java)!!
                    binding.title.text = user.getUsername()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        // button footer
        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Support", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val postReference = database
            .reference.child("Blog")
        postReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val postList = snapshot.getValue(PostModel::class.java)
                    posts
                }
            }

            override fun onCancelled(error: DatabaseError) {

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
