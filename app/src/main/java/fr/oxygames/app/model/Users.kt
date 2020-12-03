package fr.oxygames.app.model

import android.text.TextUtils
import android.util.Patterns

class Users {
    private var uid: String = ""
    private var username: String = ""
    private var email: String = ""
    private var password: String = ""
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
        email: String,
        password: String,
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
        this.email = email
        this.password = password
        this.avatar = avatar
        this.cover = cover
        this.status = status
        this.search = search
        this.facebook = facebook
        this.instagram = instagram
        this.website = website
    }

    fun getUID(): String?{
        return uid
    }

    fun setUID(uid: String){
        this.uid = uid
    }

    fun getUsername(): String? {
        return username
    }

    fun setUsername(username: String){
        this.username = username
    }

    fun getEmail(): String {
        return email
    }

    fun setEmail(email: String){
        this.email = email
    }

    fun getPassword(): String {
        return password
    }

    fun setPassword(password: String){
        this.password = password
    }

    fun getAvatar(): String?{
        return avatar
    }

    fun setAvatar(avatar: String){
        this.avatar = avatar
    }

    fun getCover(): String?{
        return cover
    }

    fun setCover(cover: String){
        this.cover = cover
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?){
        this.status = status!!
    }

    fun getSearch(): String?{
        return search
    }

    fun setSearch(search: String){
        this.search = search
    }

    fun getFacebook(): String?{
        return facebook
    }

    fun setFacebook(facebook: String){
        this.facebook = facebook
    }

    fun getInstagram(): String?{
        return instagram
    }

    fun setInstagram(instagram: String){
        this.instagram = instagram
    }

    fun getWebsite(): String?{
        return website
    }

    fun setWebsite(website: String){
        this.website = website
    }

    fun isValidEmail(): Boolean {
        if (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return true
        }
        return false
    }

    fun isValidPassword(): Boolean {
        if (this.password.length >= 6){
            return true
        }
        return false
    }

    fun isValidCredential(): Boolean {
        if (this.email.contentEquals("ex@gmail.com") && this.password.equals("123456")){
            return true
        }
        return false
    }

    fun getWelcomeMessage(): String {
        return "Welcome\n"+this.getEmail()
    }
}