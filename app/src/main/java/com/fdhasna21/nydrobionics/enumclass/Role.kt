package com.fdhasna21.nydrobionics.enumclass

import com.fdhasna21.nydrobionics.R

enum class Role(var role: String){
    OWNER("owner"),
    STAFF("staff");

    override fun toString(): String {
        return role
    }

    companion object{
        fun getType(type: String) : Role? {
            return when(type){
                "owner" -> OWNER
                "female" -> STAFF
                else -> null
            }
        }
    }
}