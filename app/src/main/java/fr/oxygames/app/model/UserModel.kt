package fr.oxygames.app.model

class UserModel {
    private var uid: String = ""
    private var username: String = ""
    private var avatar: String = ""
    private var cover: String = ""
    private var status: String = ""
    private var search: String = ""
    private var facebook: String = ""
    private var instagram: String = ""
    private var website: String = ""

    constructor(){}

    constructor(
        uid: String,
        username: String,
        avatar: String,
        cover: String,
        status: String,
        search: String,
        facebook: String,
        instagram: String,
        website: String

    ) {
        this.uid = uid
        this.username = username
        this.avatar = avatar
        this.cover = cover
        this.status = status
        this.search = search
        this.facebook = facebook
        this.instagram = instagram
        this.website = website
    }

    //id
    fun getUID(): String {
        return uid
    }

    fun setUID(uid: String) {
        this.uid = uid
    }

    //username
    fun getUsername(): String {
        return username
    }

    fun setUsername(username: String) {
        this.username = username
    }

    //avatar
    fun getAvatar(): String {
        return avatar
    }

    fun setAvatar(avatar: String) {
        this.avatar = avatar
    }

    //cover
    fun getCover(): String {
        return cover
    }

    fun setCover(cover: String) {
        this.cover = cover
    }

    //status
    fun getStatus(): String {
        return status
    }

    fun setStatus(status: String) {
        this.status = status
    }

    //search
    fun getSearch(): String {
        return search
    }

    fun setSearch(search: String) {
        this.search = search
    }

    //facebook
    fun getFacebook(): String {
        return facebook
    }

    fun setFacebook(facebook: String) {
        this.facebook = facebook
    }

    //instagram
    fun getInstagram(): String {
        return instagram
    }

    fun setInstagram(instagram: String) {
        this.instagram = instagram
    }

    //website
    fun getWebsite(): String {
        return website
    }

    fun setWebsite(website: String) {
        this.website = website
    }
}