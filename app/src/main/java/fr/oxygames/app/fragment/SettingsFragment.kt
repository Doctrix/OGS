package fr.oxygames.app.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import fr.oxygames.app.R
import fr.oxygames.app.databinding.FragmentSettingsBinding
import fr.oxygames.app.model.UsersModel

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private var usersReference: DatabaseReference? = null
    private var firebaseUser: FirebaseUser? = null
    private val RequestCode = 438
    private var imageUri: Uri? = null
    private var storageRef: StorageReference? = null
    private var coverChecker: String? = ""
    private var socialChecker: String? = ""
    private var user: UsersModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSettingsBinding.inflate(layoutInflater)

        // firebase - data user
        firebaseUser = FirebaseAuth.getInstance().currentUser
        usersReference =
            FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")
        usersReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    user = p0.getValue(UsersModel::class.java)

                    if (context != null) {
                        binding.usernameSettings.text = user!!.getUsername()
                        Picasso.get().load(user!!.getCover()).placeholder(R.drawable.ic_cover)
                            .into(binding.coverImageSettings)
                        Picasso.get().load(user!!.getAvatar()).placeholder(R.drawable.ic_avatar)
                            .into(binding.profileImageSettings)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
        binding.profileImageSettings.setOnClickListener {
            coverChecker = "profile"
            pickImage()
        }
        binding.coverImageSettings.setOnClickListener {
            coverChecker = "cover"
            pickImage()
        }
        binding.setFacebook.setOnClickListener {
            socialChecker = "facebook"
            setSocialLinks()
        }
        binding.setInst.setOnClickListener {
            socialChecker = "instagram"
            setSocialLinks()
        }
        binding.setWebsite.setOnClickListener {
            socialChecker = "website"
            setSocialLinks()
        }

        return binding.root
    }

    private fun setSocialLinks() {
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(context, R.style.ThemeOverlay_AppCompat_Dialog_Alert)
        if (socialChecker == "website") {
            builder.setTitle("Write URL:")
        } else {
            builder.setTitle("Write name:")
        }

        val editText = EditText(context)

        if (socialChecker == "website") {
            editText.hint = "e.g www.OxyGameS.fr"
        } else {
            editText.hint = "e.g OxyGame Studio"
        }
        builder.setView(editText)
        builder.setPositiveButton("Create", DialogInterface.OnClickListener { dialog, which ->
            val str = editText.text.toString()
            if (str == "") {
                Toast.makeText(context, "Please write something...", Toast.LENGTH_LONG).show()
            } else {
                saveSocialLink(str)
            }
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
        })
        builder.show()
    }

    private fun saveSocialLink(str: String) {
        val mapSocial = HashMap<String, Any>()

        when (socialChecker) {
            "facebook" -> {
                mapSocial["facebook"] = "https://m.facebook.com/$str"
            }
            "instagram" -> {
                mapSocial["instagram"] = "https://m.instagram.com/$str"
            }
            "website" -> {
                mapSocial["website"] = "https://$str"
            }
        }
        usersReference!!.updateChildren(mapSocial).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "updated Successfully.", Toast.LENGTH_LONG).show()
            }
        }
    }

    // pickup image
    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == requestCode && resultCode == Activity.RESULT_OK && data!!.data != null) {
            imageUri = data.data
            Toast.makeText(context, "", Toast.LENGTH_LONG).show()
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase() {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("image is uploading, please wait....")
        progressBar.show()

        if (imageUri != null) {
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")
            val uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    if (coverChecker == "cover") {
                        val mapCoverImg = HashMap<String, Any>()
                        mapCoverImg["cover"] = url
                        usersReference!!.updateChildren(mapCoverImg)
                        coverChecker = ""
                    } else {
                        val mapProfileImg = HashMap<String, Any>()
                        mapProfileImg["profile"] = url
                        usersReference!!.updateChildren(mapProfileImg)
                        coverChecker = ""
                    }
                    progressBar.dismiss()
                }
            }
        }
    }
}
