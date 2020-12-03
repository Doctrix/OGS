package fr.oxygames.app.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import fr.oxygames.app.activity.ui.main.SectionsPagerAdapter
import fr.oxygames.app.databinding.ActivityTutorialBinding

class TutorialActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTutorialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Support", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }
}