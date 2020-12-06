@file:Suppress("DEPRECATION")

package fr.oxygames.app.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import fr.oxygames.app.databinding.ActivityPostBinding
import org.jetbrains.anko.longToast
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class PostActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityPostBinding

    private var selectImage: ImageButton? = null
    private var postTitle: EditText? = null
    private var postDesc: EditText? = null
    private var submitBtn: Button? = null

    @Suppress("DEPRECATION")
    private var progressBar: ProgressDialog? = null

    private var pathArray: ArrayList<String>? = null
    private var filePath: Uri? = null

    private val imagePickCode = 1234
    private val permissionCode = 1001

    private var storage: FirebaseStorage? = null
    private var storageRef: StorageReference? = null
    private var database: DatabaseReference? = null

    override fun onClick(p0: View) {
        if (p0 === selectImage)
            pickImageFromGallery()
        else if (p0 === submitBtn)
            startPosting()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init Firebase
        storage = FirebaseStorage.getInstance()
        storageRef = storage!!.reference
        database = FirebaseDatabase.getInstance().reference.child("Blog")

        selectImage = binding.imageSelect
        postTitle = binding.titleField
        postDesc = binding.descField
        submitBtn = binding.submitBtn
        pathArray = ArrayList()
        @Suppress("DEPRECATION")
        progressBar = ProgressDialog(this)

        // toolbar
        val toolbar: Toolbar = binding.toolbarPost
        toolbar.title = "New Post"
        binding.titlePost.text = "add"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, BlogActivity::class.java)
            startActivity(intent)
            finish()
        }
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

    // posting
    private fun startPosting() {
        val titleVal = postTitle!!.text.toString().trim()
        val descVal = postDesc!!.text.toString().trim()
        when {
            !TextUtils.isEmpty(titleVal) && !TextUtils.isEmpty(descVal) && filePath != null -> {

                @Suppress("DEPRECATION")
                val progressBar = ProgressDialog(this)
                progressBar.setTitle("Posting to Blog ....")
                progressBar.show()

                val imageRef = storageRef!!.child("Blog Images/" + UUID.randomUUID().toString())
                val uploadTask: StorageTask<*>
                uploadTask = imageRef.putFile(filePath!!)
                    .addOnFailureListener {
                        longToast("Failed")
                        progressBar.dismiss()
                    }
                    .addOnProgressListener { taskSnapShot ->
                        val progress =
                            100.0 * taskSnapShot.bytesTransferred / taskSnapShot.totalByteCount
                        @Suppress("DEPRECATION")
                        progressBar.setMessage("Uploaded " + progress.toInt() + "% ...")
                    }
                    .addOnSuccessListener {
                        longToast("Post Uploaded")
                        progressBar.dismiss()
                        val intent = Intent(this@PostActivity, BlogActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation imageRef.downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        longToast("Post Success")

                        val newPost = database?.push()

                        val downloadUrl = task.result
                        val url = downloadUrl.toString()

                        newPost!!.child("title").setValue(titleVal)
                        newPost.child("description").setValue(descVal)
                        newPost.child("image").setValue(url)

                        progressBar.dismiss()
                    }
                }
            }
            filePath == null -> {
                longToast("images failed")
            }
            TextUtils.isEmpty(titleVal) -> {
                longToast("title failed")
            }
            TextUtils.isEmpty(descVal) -> {
                longToast("description failed")
            }
        }
    }

    // pickup image from gallery
    private fun pickImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "SELECT PICTURE"), imagePickCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imagePickCode && resultCode == RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            try {
                @Suppress("DEPRECATION")
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                selectImage!!.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
