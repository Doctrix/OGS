package fr.oxygames.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import fr.oxygames.app.R
import fr.oxygames.app.model.Blog

/**
 * Created by Doctrix on 06/12/2020.
 */
class BlogListAdapter (
    private var context: Context,
    private var listBlog: MutableList<Blog>,
) : RecyclerView.Adapter<BlogListAdapter.ViewHolder>() {

    val lastItemId:String?
    get() = listBlog[listBlog.size - 1].getUID()

    fun addAll(newPost:List<Blog>)
    {
        val init: Int = listBlog.size
        listBlog.addAll(newPost)
        notifyItemRangeChanged(init,newPost.size)
    }

    fun removeLastItem()
    {
        listBlog.removeAt(listBlog.size - 1)
    }

    init {
        this.listBlog = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(context).inflate(R.layout.blog_row, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listBlog.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titlePost.text = listBlog[position].getTitle()
        holder.descPost.text = listBlog[position].getDesc()
        Picasso.get().load(listBlog[position].getImage()).placeholder(R.drawable.ic_profile).into(holder.imagePost)
        retrieveLastPost(holder)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imagePost: ImageView = itemView.findViewById(R.id.post_image)
        var titlePost: TextView = itemView.findViewById(R.id.post_title)
        var descPost: TextView = itemView.findViewById(R.id.post_desc)
    }

    private fun retrieveLastPost(holder: ViewHolder) {
        val databaseInstance = FirebaseDatabase.getInstance()
        val referenceBlog = databaseInstance.reference.child("Blog")

        referenceBlog.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (dataSnapshot in p0.children) {


                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}
