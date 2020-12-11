package fr.oxygames.app.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import fr.oxygames.app.databinding.ActivityVisitUserProfileBinding
import fr.oxygames.app.model.UsersModel

class VisitUserProfileActivity : AppCompatActivity()
{
    private var userVisitId: String = ""
    var user: UsersModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityVisitUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userVisitId = intent.getStringExtra("visit_id")

        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(userVisitId)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    user = snapshot.getValue(UsersModel::class.java)

                    binding.usernameDisplay.text = user!!.getUsername()
                    Picasso.get().load(user!!.getAvatar()).into(binding.profileImageDisplay)
                    Picasso.get().load(user!!.getCover()).into(binding.coverDisplay)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        // open page facebook
        binding.facebookDisplay.setOnClickListener {
            val uri = Uri.parse(user!!.getFacebook())
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        // open page instagram
        binding.instDisplay.setOnClickListener {
            val uri = Uri.parse(user!!.getInstagram())
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        // open website
        binding.websiteDisplay.setOnClickListener {
            val uri = Uri.parse(user!!.getWebsite())
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        binding.sendMsgBtn.setOnClickListener {
            val intent = Intent(this@VisitUserProfileActivity, MessageChatActivity::class.java)
            intent.putExtra("visit_id", user!!.getUID())
            startActivity(intent)
        }
    }
}