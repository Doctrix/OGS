package fr.oxygames.app.listener

import fr.oxygames.app.model.PostModel

interface IFirebaseLoadDone {
    fun onPostLoadSuccess(postsList:List<PostModel>)
    fun onPostLoadFailed(message:String)
}