package fr.oxygames.app.viewModel

import android.text.TextUtils
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel() : ViewModel() {
    var userEmail = ObservableField("")
    var userPassword = ObservableField("")
    var mAuth: FirebaseAuth? = null
    private var resultData = MutableLiveData<String>()

    fun loginCall(email: String, password: String) {
        var result: String = ""
        userEmail.set(email)
        userPassword.set(password)
        mAuth = FirebaseAuth.getInstance()

        when {
            userEmail.get() == "oxy"-> {
                result = "Access"
            }
            userPassword.get() == "1234567" -> {
                result = "Access"
            }
            userEmail.get() == "" -> {
                result = "Please write Email"
            }
            userPassword.get() == "" -> {
                result = "Please write Password"
            }
            else -> {
                mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        resultData.value = result
                    } else {
                        val message = task.exception!!.toString()
                        result = "Error $message"
                    }
                }
            }
        }
//        resultData.value = result
    }

    fun getResultLogin(): MutableLiveData<String> {
        return resultData
    }

    fun isValidEmail(): Boolean {
        if (!TextUtils.isEmpty(userEmail.toString())) {
            return true
        }
        return false
    }

    fun isValidPassword(): Boolean {
        if (userPassword.toString().length >= 6) {
            return true
        }
        return false
    }

    fun isValidCredential(): Boolean {
        if (this.userEmail.toString()
                .contentEquals("ex@gmail.com") && this.userPassword.equals("123456")
        ) {
            return true
        }
        return false
    }

    fun getWelcomeMessage(): String {
        return "Welcome\n" + this.userEmail.toString()
    }
}
