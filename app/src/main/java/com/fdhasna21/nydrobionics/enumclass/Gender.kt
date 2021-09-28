package com.fdhasna21.nydrobionics.enumclass

enum class Gender(var gender: String){
    MALE("male"),
    FEMALE("female");

    override fun toString(): String {
        return gender
    }
}
