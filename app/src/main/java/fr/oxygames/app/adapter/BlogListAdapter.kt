package fr.oxygames.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import fr.oxygames.app.R
import fr.oxygames.app.model.Blog

/**
 * Created by Doctrix on 06/12/2020.
 */
class BlogListAdapter(var context: Context, var items: MutableList<Blog>) : RecyclerView.Adapter<BlogListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(context).inflate(R.layout.blog_row, parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        var imagePost = itemView.findViewById<ImageView>(R.id.post_image)
        var titlePost = itemView.findViewById<ImageView>(R.id.post_title)
        var descPost = itemView.findViewById<ImageView>(R.id.post_desc)
    }
}