package fr.oxygames.app.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import fr.oxygames.app.databinding.ActivityPostBinding
import org.jetbrains.anko.longToast

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding

    private var selectImage: ImageButton? = null
    private var postTitle: EditText? = null
    private var postDesc: EditText? = null
    private var submitBtn: Button? = null
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // firebase - data user
        //firebaseUser = FirebaseAuth.getInstance().currentUser
        //usersReference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        //storageRef = FirebaseStorage.getInstance().reference.child("User Images")

        selectImage = binding.imageSelect
        postTitle = binding.titleField
        postDesc = binding.descField
        submitBtn = binding.submitBtn

        selectImage!!.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED) {
                    // permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    // show popup to request runtime permission
                    requestPermissions(permissions, PermissionCode)
                }
                else {
                    pickImageFromGallery()
                }
            }
            else {
                pickImageFromGallery()
            }
        }

        submitBtn!!.setOnClickListener {
            startPosting()
        }
    }

    private fun startPosting() {
        longToast("send message")
    }

    // pickup image
     private fun pickImageFromGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        startActivityForResult(intent, ImagePickCode)
    }

    companion object {
        // image pick code
        private const val ImagePickCode = 1
        // Permission code
        private const val PermissionCode = 1001
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            PermissionCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery()
                }
                else{
                    longToast("Permission denied")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ImagePickCode && resultCode == RESULT_OK ) {
            imageUri = data!!.data
            selectImage!!.setImageURI(imageUri)
        }
    }
}