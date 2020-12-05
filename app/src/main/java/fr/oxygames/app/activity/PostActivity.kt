package fr.oxygames.app.activity

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import fr.oxygames.app.databinding.ActivityPostBinding
import org.jetbrains.anko.longToast
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding

    private var selectImage: ImageButton? = null
    private var postTitle: EditText? = null
    private var postDesc: EditText? = null
    private var submitBtn: Button? = null
    private var progressBar: ProgressDialog? = null

    private var pathArray: ArrayList<String>? = null
    private var arrayPosition: Int? = null
    private var imageUri: Uri? = null

    private val imagePickCode = 1000
    private val permissionCode = 1001

    private var auth: FirebaseAuth? = null
    private var storage: FirebaseStorage? = null
    private var storageRef: StorageReference? = null
    private var firebasePost: FirebaseUser? = null
    private var postReference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init Firebase
        storage = FirebaseStorage.getInstance()
        storageRef = storage!!.reference

        selectImage = binding.imageSelect
        postTitle = binding.titleField
        postDesc = binding.descField
        submitBtn = binding.submitBtn
        pathArray = ArrayList()
        progressBar = ProgressDialog(this)

        // Select images
        selectImage!!.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED
                ) {
                    // permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    // show popup to request runtime permission
                    requestPermissions(permissions, permissionCode)
                } else {
                    pickImageFromGallery()
                }
            } else {
                pickImageFromGallery()
            }
        }

        // Post article
        submitBtn!!.setOnClickListener {
            startPosting()
        }
    }

    private fun startPosting() {
        val titleVal = postTitle?.text.toString().trim()
        val descVal = postDesc?.text.toString().trim()
        if (!TextUtils.isEmpty(titleVal) && !TextUtils.isEmpty(descVal) && imageUri != null) {
            val progressBar = ProgressDialog(this)
            progressBar.setTitle("Posting to Blog ....")
            progressBar.show()
            val filepath = storageRef!!.child("images/" + UUID.randomUUID().toString())
            filepath.putFile(imageUri!!)
                .addOnSuccessListener {
                    progressBar.dismiss()
                    longToast("Post Uploaded")
                }
                .addOnFailureListener {
                    progressBar.dismiss()
                    longToast("Failed")
                }
                .addOnProgressListener { taskSnapShot ->
                    val progress =
                        100.0 * taskSnapShot.bytesTransferred / taskSnapShot.totalByteCount
                    progressBar.setMessage("Uploaded " + progress.toInt() + "%...")
                }
        }
    }

    // pickup image from gallery
    private fun pickImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), imagePickCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imagePickCode && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                selectImage!!.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            selectImage!!.setImageURI(imageUri)
        }
    }
}