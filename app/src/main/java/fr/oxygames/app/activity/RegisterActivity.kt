package fr.oxygames.app.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import fr.oxygames.app.databinding.ActivityRegisterBinding
import org.jetbrains.anko.longToast
import java.util.*
import kotlin.collections.HashMap

class RegisterActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private var firebaseUserID: String = ""

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // toolbar
        val toolbar: Toolbar = binding.toolbarRegister
        toolbar.title = "Register"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent (this@RegisterActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth = FirebaseAuth.getInstance()

        binding.buttonRegister.setOnClickListener {
            registerUser()
        }

        binding.textLogin.setOnClickListener {
            longToast("Login Loading ...")
            val intent = Intent (this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registerUser() {
        val username: String = binding.usernameRegister.text.toString()
        val email: String = binding.emailRegister.text.toString()
        val password: String = binding.passwordRegister.text.toString()
        val passwordValidate: String = binding.passwordConfirmRegister.text.toString()

        when {
            username == "" -> {
                longToast("Please write Username")
            }
            email == "" -> {
                longToast("Please write Email")
            }
            password == "" -> {
                longToast("Please write Password")
            }
            passwordValidate == "" -> {
                longToast("Please write Password Confirmation")
            }
            passwordValidate == password -> {
                longToast("The password is not the same")
            }
            else -> {
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        firebaseUserID = mAuth.currentUser!!.uid
                        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserID)

                        val userHashMap = HashMap<String, Any>()
                        userHashMap["uid"] = firebaseUserID
                        userHashMap["username"] = username
                        userHashMap["image_profile"] = "https://firebasestorage.googleapis.com/v0/b/oxygene-studio.appspot.com/o/ic_profile.jpg?alt=media&token=fca47cd7-050e-454d-8344-3908a7e77acf"
                        userHashMap["cover"] = "https://firebasestorage.googleapis.com/v0/b/oxygene-studio.appspot.com/o/ic_cover.png?alt=media&token=f23b6712-faf7-4bdc-af3e-4195c894f7a9"
                        userHashMap["status"] = "offline"
                        userHashMap["search"] = username.toLowerCase(locale = Locale.ROOT)
                        userHashMap["facebook"] = "https://m.facebook.com"
                        userHashMap["instagram"] = "https://m.instagram.com"
                        userHashMap["website"] = "https://oxygames.fr"

                        refUsers.updateChildren(userHashMap)
                            .addOnCompleteListener {
                            if (task.isSuccessful)
                            {
                                val intent = Intent (this@RegisterActivity, LoginActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                                longToast("Inscription ok !!!")
                            }
                            else
                            {
                                longToast("Error Message")
                            }
                        }
                    }
                    else
                    {
                        longToast("Error")
                    }
                }
            }
        }
    }
}
