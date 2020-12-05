package fr.oxygames.app.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import fr.oxygames.app.databinding.ActivityPostBinding
import fr.oxygames.app.model.Users


class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding

    private var selectImage: ImageButton? = null
    private var postTitle: EditText? = null
    private var postDesc: EditText? = null
    private var submitBtn: Button? = null

    private var images: ArrayList<Uri?>? = null
    private var position = 0
    private val pickImagesCode = 0

    private var usersReference: DatabaseReference? = null
    private var firebaseUser: FirebaseUser? = null
    private var user: Users? = null
    private var storageRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // firebase - data user
        //firebaseUser = FirebaseAuth.getInstance().currentUser
        //usersReference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        //storageRef = FirebaseStorage.getInstance().reference.child("User Images")

        val selectImage = binding.imageSelect
        postTitle = binding.titleField
        postDesc = binding.descField
        val submitBtn = binding.submitBtn

        images = ArrayList()

        selectImage.setOnClickListener {
            pickImage()
        }

        submitBtn.setOnClickListener {

        }
    }

    // pickup image
     private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image(s)"), pickImagesCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == pickImagesCode) {
            if (resultCode == Activity.RESULT_OK) {
                if (data!!.clipData != null) {
                    val count = data.clipData!!.itemCount
                    for (i in 0 until count) {
                        val imageUri = data.clipData!!.getItemAt(i).uri
                        images!!.add(imageUri)
                    }
                    selectImage!!.setImageURI(images!![0])
                    position = 0
                }
                else {
                    val imageUri = data.data
                    selectImage!!.setImageURI(imageUri)
                    position = 0
                }
            }
        }
    }

/*
    private fun uploadImageToDatabase() {
        val progressBar = ProgressDialog(applicationContext)
        progressBar.setMessage("image is uploading, please wait....")
        progressBar.show()

        if (imageUri != null)
        {
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")
            val uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri)
            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                if(!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    if (coverChecker == "cover")
                    {
                        val mapCoverImg = HashMap<String, Any>()
                        mapCoverImg["cover"] = url
                        usersReference!!.updateChildren(mapCoverImg)
                        coverChecker = ""
                    }

                    progressBar.dismiss()
                }
            }
        }
    }
*/
}