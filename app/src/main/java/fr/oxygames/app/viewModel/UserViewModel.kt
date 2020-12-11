package fr.oxygames.app.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.oxygames.app.model.UsersModel

class UserViewModel : ViewModel() {
    var user = MutableLiveData<UsersModel>()
    init {
        user.value = UsersModel("BUOfOpwEB2MzmaLuocTL3gbcwSj2",
            "Oxy",
            "https://firebasestorage.googleapis.com/v0/b/oxygene-studio.appspot.com/o/User%20Images%2F1606454747402.jpg?alt=media&token=28170e86-00be-4055-9606-a09e1274d0ac",
            "https://firebasestorage.googleapis.com/v0/b/oxygene-studio.appspot.com/o/ic_cover.png?alt=media&token=f23b6712-faf7-4bdc-af3e-4195c894f7a9",
            "online",
            "oxy",
            "https://m.facebook.com/OxyGameStudio",
            "https://m.instagram.com/Mifa_Concept",
            "https://serveur.oxygames.fr")
    }
}