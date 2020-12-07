package fr.oxygames.app.viewModel

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import fr.oxygames.app.R
import fr.oxygames.app.model.Blog

class PageViewModel : ViewModel() {

    private var firebaseRepository: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Blog")

    private val _index = MutableLiveData<Int>()
    val text: LiveData<String> = Transformations.map(_index) {
        "Page: $it"
    }

    fun setIndex(index: Int) {
        _index.value = index
    }

    private val postBlogList: MutableLiveData<List<Blog>> by lazy {
        MutableLiveData<List<Blog>>().also {
            loadBlogData()
        }
    }

    fun getBlogList(): LiveData<List<Blog>> {
        return postBlogList
    }

    private fun loadBlogData() {

        val inflater: LayoutInflater? = null
        val container: ViewGroup? = null
        val root = inflater!!.inflate(R.layout.fragment_blog_list, container, false)
        val row = inflater.inflate(R.layout.blog_row, container, false)

        val titlePage: TextView = root.findViewById(R.id.copyright)
        val list: RecyclerView = root.findViewById(R.id.blog_list)
        val imagePost: ImageView = row.findViewById(R.id.post_image)
        val titlePost: TextView = row.findViewById(R.id.post_title)
        val descPost: TextView = row.findViewById(R.id.post_desc)

        firebaseRepository.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (dataSnapshot in p0.children) {
                    val blog: Blog? = dataSnapshot.getValue(Blog::class.java)

                    titlePage.text = "123456"
                    titlePost.text = blog!!.getTitle()
                    descPost.text = blog.getDesc()
                    Picasso.get().load(blog.getImage()).placeholder(R.drawable.ic_profile)
                        .into(imagePost)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}
