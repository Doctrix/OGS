package fr.oxygames.app.model

/**
 * Created by Doctrix on 06/12/2020.
 */

class PostModel {
    private var title: String = ""
    private var description: String = ""
    private var image: String = ""
    private var date: String = ""

    constructor(){}

    constructor(
        title: String,
        description: String,
        image: String,
        date: String
    ) {
        this.title = title
        this.description = description
        this.image = image
        this.date = date
    }

    // title
    fun getTitle(): String{
        return title
    }
    fun setTitle(title: String) {
        this.title = title
    }

    // description
    fun getDescription(): String{
        return description
    }
    fun setDescription(description: String){
        this.description = description
    }

    // image
    fun getImage(): String{
        return image
    }
    fun setImage(image: String){
        this.image = image
    }

    // date
    fun getDate(): String{
        return date
    }
    fun setDate(date: String){
        this.date = date
    }
}