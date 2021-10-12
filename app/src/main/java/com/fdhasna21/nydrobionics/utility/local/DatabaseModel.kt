package com.fdhasna21.nydrobionics.utility.local

data class DatabaseModel(
    var id : Int?=null,
    var uid : String?=null,
    var email : String?=null,
    var remember : Boolean=false)
{
    constructor(uid:String?, email:String?, remember:Boolean)
    : this(null, uid, email, remember)
}