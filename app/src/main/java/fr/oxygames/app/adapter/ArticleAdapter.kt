package fr.oxygames.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.oxygames.app.R
import fr.oxygames.app.model.Article

/**
 * Created by Doctrix on 18/12/2020.
 */
class ArticleAdapter (var articles:ArrayList<Article>) : RecyclerView.Adapter<ArticleAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var vue = LayoutInflater.from(parent.context).inflate(R.layout.activity_store,parent,false)
        return MyViewHolder(vue)
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var article = articles.get(position)
        holder.textTitle.text = article.titre.toString()
        holder.textDescription.text = article.description.toString()
    }

    class MyViewHolder(var vue: View): RecyclerView.ViewHolder(vue){
        var imageArticle = vue.findViewById<ImageView>(R.id.imagePost)
        var textTitle = vue.findViewById<TextView>(R.id.textTitle)
        var textDescription = vue.findViewById<TextView>(R.id.textDescription)
    }
}