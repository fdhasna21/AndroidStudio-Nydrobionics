package com.fdhasna21.nydrobionics.enumclass

enum class SearchObject {
    USER {
        override fun toString(): String {
            return "User"
        }
    },
    PLANT{
        override fun toString(): String {
            return "Plant"
        }
    },
    NONE{
        override fun toString(): String {
            return "None"
        }
    };

    companion object{
        fun getType(tag:String) : SearchObject {
            return when (tag) {
                "user", "createProfile", "profileFarm" -> USER
                "plant", "crops", "addCrops" -> PLANT
                else -> NONE
            }
        }
    }
}