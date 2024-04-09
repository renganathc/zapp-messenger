package com.emessage.kotlinmessenger

class ChatMessage(var text : String, var photo : String, var id : String, var from_id : String, var to_id : String, var timeStamp : Long){
    constructor() : this("","", "","","",-1)
}
