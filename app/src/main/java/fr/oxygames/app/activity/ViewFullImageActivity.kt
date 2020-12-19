package fr.oxygames.app.activity

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import fr.oxygames.app.databinding.ActivityViewFullImageBinding

class ViewFullImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewFullImageBinding

    private var imageView: ImageView? = null
    private var imageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewFullImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageUrl = intent.getStringExtra("url")
        imageView = binding.imageViewer

        Picasso.get().load(imageUrl).into(imageView)
    }
}