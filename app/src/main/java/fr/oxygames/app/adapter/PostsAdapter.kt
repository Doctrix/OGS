package fr.oxygames.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fr.oxygames.app.R
import fr.oxygames.app.model.PostModel

/**
 * Created by Doctrix on 06/12/2020.
 */

class PostsAdapter (val context: Context, val posts: List<PostModel>) :
    RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_posts_details, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(post: PostModel) {

            val imagePost: ImageView = itemView.findViewById(R.id.ivImagePost)
            val titlePost: TextView = itemView.findViewById(R.id.tvTitlePost)
            val descPost: TextView = itemView.findViewById(R.id.tvDescriptionPost)

            val image = post.getImage()
            val title = post.getTitle()
            val description = post.getDesc()

            Picasso.get().load(image).placeholder(R.drawable.ic_profile).into(imagePost)
            titlePost.text = title
            descPost.text = description
        }
    }
}
