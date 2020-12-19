package fr.oxygames.app.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import fr.oxygames.app.R
import fr.oxygames.app.core.server.DataBase

class StoreActivity : AppCompatActivity() {
    internal var data: DataBase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)

        val imagePost = findViewById<View>(R.id.imagePost) as ImageView
        val textTitle = findViewById<View>(R.id.textTitle) as TextView
        val textDescription = findViewById<View>(R.id.textDescription) as TextView

        //DataBase.posts()
        //textDescription.text = "description du jeu"
        //Picasso.get().load(R.drawable.ic_cover).into(imagePost)
    }
}