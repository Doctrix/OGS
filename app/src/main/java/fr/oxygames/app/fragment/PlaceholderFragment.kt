package fr.oxygames.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import fr.oxygames.app.R
import fr.oxygames.app.model.Blog
import fr.oxygames.app.viewModel.PageViewModel

/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment : Fragment() {

    private lateinit var pageViewModel: PageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_blog_list, container, false)
        val row = inflater.inflate(R.layout.blog_row, container, false)

        val titlePage: TextView = root.findViewById(R.id.copyright)
        val blogList: RecyclerView = root.findViewById(R.id.blog_list)
        val imagePost: ImageView = row.findViewById(R.id.post_image)
        val titlePost: TextView = row.findViewById(R.id.post_title)
        val descPost: TextView = row.findViewById(R.id.post_desc)
        val databaseInstance = FirebaseDatabase.getInstance()
        val referenceBlog = databaseInstance.reference.child("Blog")

        pageViewModel.text.observe(viewLifecycleOwner, Observer<String> {
            referenceBlog.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    for (dataSnapshot in p0.children) {
                        val blog: Blog? = dataSnapshot.getValue(Blog::class.java)

                        titlePage.text = it
                        titlePost.text = blog!!.getTitle()
                        descPost.text = blog.getDesc()
                        Picasso.get()
                            .load(blog.getImage())
                            .placeholder(R.drawable.ic_profile)
                            .into(imagePost)
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        })
        return root
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}
