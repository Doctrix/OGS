package fr.oxygames.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import fr.oxygames.app.R
import fr.oxygames.app.model.PostModel

/**
 * Created by Doctrix on 06/12/2020.
 */

class PostsAdapter (
    internal var context: Context,
    internal var data:List<PostModel>) : PagerAdapter() {
    internal var layoutInflater: LayoutInflater

    init {
        layoutInflater = LayoutInflater.from(context)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view==`object`
    }

    override fun getCount() : Int {
        return data.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        //Inflate view
        val view = layoutInflater.inflate(R.layout.activity_posts_details,container,false)
        //View
        val postImage = view.findViewById<View>(R.id.iv_image_posts_details) as ImageView
        val postTitle = view.findViewById<View>(R.id.tv_title_posts_details) as TextView
        val postDate = view.findViewById<View>(R.id.tv_relative_time_posts_details) as TextView
        val postDescription = view.findViewById<View>(R.id.tv_description_posts_details) as TextView
        val postBtnFav = view.findViewById<View>(R.id.fab_fav_posts) as FloatingActionButton

        //Set Data
        Picasso.get().load(data[position].getImage()).into(postImage)
        postTitle.text = data[position].getTitle()
        postDescription.text = data[position].getDescription()
        postDate.text = data[position].getDate()

        postBtnFav.setOnClickListener {
            Toast.makeText(context,"Btn Fav Clicked", Toast.LENGTH_SHORT).show()
        }

        view.setOnClickListener {
            Toast.makeText(context,""+data[position].getTitle(), Toast.LENGTH_SHORT).show()
        }

        container.addView(view)
        return view
    }
}


