package fr.oxygames.app.model

class ChatListModel {
    private var id: String = ""

    constructor(){}

    constructor(id: String) {
        this.id = id
    }

    // id
    fun getId(): String? {
        return id
    }
    fun setId(id: String?) {
        this.id = id!!
    }
}