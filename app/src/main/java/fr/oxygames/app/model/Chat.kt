package fr.oxygames.app.model

class Chat {
    private var sender: String = ""
    private var message: String = ""
    private var receiver: String = ""
    private var isSeen: Boolean = false
    private var url: String = ""
    private var messageId: String = ""

    constructor()

    constructor(
        sender: String,
        message: String,
        receiver: String,
        isSeen: Boolean,
        url: String,
        messageId: String
    ) {
        this.sender = sender
        this.message = message
        this.receiver = receiver
        this.isSeen = isSeen
        this.url = url
        this.messageId = messageId
    }

    // sender
    fun getSender(): String?{
        return sender
    }
    fun setSender(sender: String?){
        this.sender = sender!!
    }

    // message
    fun getMessage(): String?{
        return message
    }
    fun setMessage(message: String?){
        this.message = message!!
    }

    // receiver
    fun getReceiver(): String?{
        return receiver
    }
    fun setReceiver(receiver: String?){
        this.receiver = receiver!!
    }

    // isSeen
    fun getIsSeen(): Boolean {
        return isSeen
    }
    fun setIsSeen(isSeen: Boolean?){
        this.isSeen = isSeen!!
    }

    // url
    fun getUrl(): String?{
        return url
    }
    fun setUrl(url: String?){
        this.url = url!!
    }

    // message id
    fun getMessageId(): String?{
        return messageId
    }
    fun setMessageId(messageId: String?){
        this.messageId = messageId!!
    }
}
