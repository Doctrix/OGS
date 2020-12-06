package fr.oxygames.app.model

class Blog {
    private var title: String = ""
    private var desc: String = ""
    private var image: String = ""

    constructor(){}

    constructor(
        title: String,
        desc: String,
        image: String,
    ) {
        this.title = title
        this.desc = desc
        this.image = image
    }

    // title
    fun getTitle(): String?{
        return title
    }
    fun setTitle(title: String?){
        this.title = title!!
    }

    // description
    fun getDesc(): String?{
        return desc
    }
    fun setDesc(desc: String?){
        this.desc = desc!!
    }

    // image
    fun getImage(): String?{
        return image
    }
    fun setImage(image: String?){
        this.image = image!!
    }
}