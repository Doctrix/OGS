package fr.oxygames.app.activity

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import fr.oxygames.app.R
import fr.oxygames.app.adapter.PostsAdapter
import fr.oxygames.app.databinding.ActivityPostsBinding
import fr.oxygames.app.listener.IFirebaseLoadDone
import fr.oxygames.app.model.PostModel
import fr.oxygames.app.model.UserModel
import fr.oxygames.app.transformer.DepthPageTransformer

/**
 * Created by Doctrix on 06/12/2020.
 */
class PostsActivity : AppCompatActivity(), IFirebaseLoadDone, ValueEventListener {

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

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

    override fun onCancelled(error: DatabaseError) {
        iFirebaseLoadDone.onPostLoadFailed(error.message)
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        val posts:MutableList<PostModel> = ArrayList()
        for (postSnapShot in snapshot.children) {
            val post = postSnapShot.getValue(PostModel::class.java)
            posts.add(post!!)
        }
        iFirebaseLoadDone.onPostLoadSuccess(posts)
    }

    private lateinit var binding:ActivityPostsBinding

    override fun onPostLoadSuccess(postsList: List<PostModel>) {
        adapter = PostsAdapter(this, postsList)
        binding.vpPosts.adapter = adapter
    }

    override fun onPostLoadFailed(message: String) {
        Toast.makeText(this,""+message,Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        posts.addValueEventListener(this)
    }

    override fun onDestroy() {
        posts.removeEventListener(this)
        super.onDestroy()
    }

    private lateinit var auth:FirebaseAuth
    private lateinit var firebaseUser:FirebaseUser
    private lateinit var refUsers:DatabaseReference
    private lateinit var adapter:PostsAdapter
    private lateinit var posts:DatabaseReference
    private lateinit var iFirebaseLoadDone:IFirebaseLoadDone

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val databaseInstance = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser!!

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

        //Init DB Posts - "Blog"
        posts = databaseInstance.reference.child("Blog")

        //Init event
        iFirebaseLoadDone = this

        loadPosts()

        binding.vpPosts.setPageTransformer(true,DepthPageTransformer())
    }

    private fun loadPosts() {
       posts.addValueEventListener(this)
    }
}
