package fr.oxygames.app.viewModel

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel() : ViewModel() {
    var userName = ObservableField("")
    var userEmail = ObservableField("")
    var userPassword = ObservableField("")
    var mAuth: FirebaseAuth? = null


    private var resultData = MutableLiveData<String>()

    fun loginCall(email: String, password: String) {
        mAuth = FirebaseAuth.getInstance()
        var result: String = ""
        userEmail.set(email)
        userPassword.set(password)
        when {
            userEmail.get() == "oxy" -> {
                result = "Access"
            }
            userPassword.get() == "1234567" -> {
                result = "Denied"
            }
            email == "" -> {
                result = "Please write Email"
            }
            password == "" -> {
                result = "Please write Password"
            }
            else -> {
            mAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    when {
                        task.isSuccessful -> {
                            result = "Access"
                        }
                        else -> {
                            val message = task.exception!!.toString()
                            result =  "Error $message"
                            FirebaseAuth.getInstance().signOut()
                        }
                    }
                }
            }
        }
        resultData.value = result
    }

    fun getResultLogin():MutableLiveData<String>{
        return resultData

    }
}
