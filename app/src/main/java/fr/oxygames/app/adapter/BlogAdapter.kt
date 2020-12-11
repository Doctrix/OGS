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
import fr.oxygames.app.model.BlogModel

/**
 * Created by Doctrix on 06/12/2020.
 */
class BlogAdapter (
    private var context: Context,
    private var listBlogModel: MutableList<BlogModel>,
) : RecyclerView.Adapter<BlogAdapter.ViewHolder>() {

    val lastItemId:String?

    get() = listBlogModel[listBlogModel.size - 1].getUID()

    init {
        this.listBlogModel = ArrayList()
    }

    fun addAll(newPost:List<BlogModel>) {
        val init: Int = listBlogModel.size
        listBlogModel.addAll(newPost)
        notifyItemRangeChanged(init,newPost.size)
    }

    fun removeLastItem() {
        listBlogModel.removeAt(listBlogModel.size - 1)
    }

    fun blogAdapter(context: Context, listBlogModel: MutableList<BlogModel>) {
        this.context = context
        this.listBlogModel = listBlogModel
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(context).inflate(R.layout.blog_row, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = listBlogModel[position].getImage()
        val title = listBlogModel[position].getTitle()
        val description = listBlogModel[position].getDesc()

        Picasso.get().load(image).placeholder(R.drawable.ic_profile).into(holder.imagePost)
        holder.titlePost.text = title
        holder.descPost.text = description
    }

    override fun getItemCount(): Int {
        return listBlogModel.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imagePost: ImageView = itemView.findViewById(R.id.post_image)
        val titlePost: TextView = itemView.findViewById(R.id.post_title)
        val descPost: TextView = itemView.findViewById(R.id.post_desc)
    }
}
