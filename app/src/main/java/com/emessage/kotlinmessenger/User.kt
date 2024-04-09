package com.emessage.kotlinmessenger

class User(val uid : String, val username : String, val photoUrl : String, val email : String, val status : String){
    constructor() : this("","", "", "", "")
}