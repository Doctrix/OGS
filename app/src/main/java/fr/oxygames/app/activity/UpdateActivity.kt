package fr.oxygames.app.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.squareup.picasso.Picasso
import fr.oxygames.app.R
import fr.oxygames.app.databinding.ActivityUpdateBinding

class UpdateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateBinding

    var firebaseRemoteConfig: FirebaseRemoteConfig? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Init
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder().build()
        firebaseRemoteConfig!!.setConfigSettingsAsync(configSettings)

        //Init default value
        val defaultValue = HashMap<String, Any>()
        defaultValue["image_link"] =
            "https://firebasestorage.googleapis.com/v0/b/oxygene-studio.appspot.com/o/ic_cover.png?alt=media&token=f23b6712-faf7-4bdc-af3e-4195c894f7a9"
        defaultValue["btn_enable"] = true
        defaultValue["btn_text"] = "DEFAULT TEXT"
        firebaseRemoteConfig!!.setDefaultsAsync(defaultValue)

        //Load default image
        Picasso.get()
            .load("https://firebasestorage.googleapis.com/v0/b/oxygene-studio.appspot.com/o/ic_cover.png?alt=media&token=f23b6712-faf7-4bdc-af3e-4195c894f7a9")
            .into(binding.imageView)

        binding.btnUpdate.setOnClickListener {
            firebaseRemoteConfig!!.fetch(0).addOnCompleteListener(this@UpdateActivity) { task ->
                if (task.isSuccessful) {
                    firebaseRemoteConfig!!.activate() // Active all value
                    binding.btnText.isEnabled = firebaseRemoteConfig!!.getBoolean("btn_enable")
                    binding.btnText.text = firebaseRemoteConfig!!.getString("btn_text")
                    Picasso.get().load(firebaseRemoteConfig!!.getString("image_link"))
                        .into(binding.imageView)
                }
                else{
                    Toast.makeText(this@UpdateActivity,""+task.exception!!.message,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}