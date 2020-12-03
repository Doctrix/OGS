package fr.oxygames.app.viewModel

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel {
    var userName = ObservableField("")
    var userEmail = ObservableField("")
    var userPassword = ObservableField("")

    var resultData = MutableLiveData<String>()

    constructor() : super()

    fun loginCall(email: String, password: String){
        userEmail.set(email)
        userPassword.set(password)
    }

    fun getResultLogin():MutableLiveData<String>{
        return resultData
    }


}