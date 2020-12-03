package fr.oxygames.app.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.oxygames.app.model.Users

class UserViewModel : ViewModel() {
    var user = MutableLiveData<Users>()
    init {
        user.value = Users("0","Doctrix","bbr.pr971@gmail.com","","","","online","doctrix","","","")
    }
}