package com.fdhasna21.nydrobionics.enumclass

import com.fdhasna21.nydrobionics.R

enum class ProfileType {
    USER {
        override fun getDefaultPicture(): Int {
            return R.drawable.ic_farmer
        }

        override fun toString(): String {
            return "User"
        }
    },
    PLANT{
        override fun getDefaultPicture(): Int {
            TODO("Not yet implemented")
        }

        override fun toString(): String {
            return "Plant"
        }
    },
    NONE{
        override fun getDefaultPicture(): Int {
            return R.drawable.ic_launcher_foreground
        }

        override fun toString(): String {
            return "None"
        }
    };

    abstract fun getDefaultPicture() : Int

    companion object{
        fun getType(tag:String) : ProfileType {
            return when (tag) {
                "user", "createProfile", "profileFarm" -> USER
                "plant", "crops", "addCrops" -> PLANT
                else -> NONE
            }
        }
    }
}