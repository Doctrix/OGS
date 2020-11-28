package fr.oxygames.app.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import fr.oxygames.app.R
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.longToast

class RegisterActivity : AppCompatActivity()
{
    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private var firebaseUserID: String = ""

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val toolbar: Toolbar = findViewById(R.id.toolbar_register)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Register"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent (this@RegisterActivity, WelcomeActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        mAuth = FirebaseAuth.getInstance()

        text_login.setOnClickListener {
            longToast("Input provided")
            val intent = Intent (this@RegisterActivity, LoginActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        button_register.setOnClickListener {
            RegisterUser()
        }
    }

    private fun RegisterUser() {
        val username: String = username_register.text.toString()
        val email: String = email_register.text.toString()
        val password: String = password_register.text.toString()
        val passwordValidate: String = passwordConfirm_register.text.toString()

        when {
            username == "" -> {
                longToast("Input Username")
            }
            email == "" -> {
                longToast("Input Email")
            }
            password == "" -> {
                longToast("Input Password")
            }
            passwordValidate == "" -> {
                longToast("Input Password Confirm")
            }
            else -> {
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        firebaseUserID = mAuth.currentUser!!.uid
                        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserID)

                        val userHashMap = HashMap<String, Any>()
                        userHashMap["uid"] = firebaseUserID
                        userHashMap["username"] = username
                        userHashMap["image_profile"] = "https://firebasestorage.googleapis.com/v0/b/oxygene-studio.appspot.com/o/ic_profile.jpg?alt=media&token=fca47cd7-050e-454d-8344-3908a7e77acf"
                        userHashMap["cover"] = "https://firebasestorage.googleapis.com/v0/b/oxygene-studio.appspot.com/o/ic_cover.png?alt=media&token=f23b6712-faf7-4bdc-af3e-4195c894f7a9"
                        userHashMap["status"] = "offline"
                        userHashMap["search"] = username.toLowerCase()
                        userHashMap["facebook"] = "https://m.facebook.com"
                        userHashMap["instagram"] = "https://m.instagram.com"
                        userHashMap["website"] = "https://oxygames.fr"

                        refUsers.updateChildren(userHashMap)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        longToast("Inscription ok !!!")
                                        val intent = Intent (this@RegisterActivity, LoginActivity::class.java)
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                    } else {
                        longToast("Error")
                    }
                }
            }
        }
    }
}
