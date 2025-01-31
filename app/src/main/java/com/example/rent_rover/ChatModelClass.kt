package com.example.rent_rover

class ChatModelClass {
    internal var sender: String =""
    private var message: String =""
    internal var receiver: String =""
    private var isSeen  = false
    private var messageId: String =""
    private var time: String =""

    constructor()

    constructor(
        sender: String,
        message: String,
        receiver: String,
        isSeen: Boolean,
        messageId: String,
        time: String,
    ) {
        this.sender = sender
        this.message = message
        this.receiver = receiver
        this.isSeen = isSeen
        this.messageId = messageId
        this.time = time
    }

    fun getSender(): String?{
        return sender
    }
    fun setSender(sender: String?){
        this.sender=sender!!
    }



    fun getMessage(): String?{
        return message
    }
    fun setMessage(message: String?){
        this.message=message!!
    }



    fun getReceiver(): String?{
        return receiver
    }
    fun setReceiver(receiver: String?){
        this.receiver=receiver!!
    }



    fun getMessageId(): String?{
        return messageId
    }
    fun setMessageId(messageId: String?){
        this.messageId=messageId!!
    }



    fun getTime(): String?{
        return time
    }
    fun setTime(time: String?){
        this.time=time!!
    }



    fun isIsSeen(): Boolean{
        return isSeen
    }
    fun setIsSeen(isSeen: Boolean?){
        this.isSeen=isSeen!!
    }


}