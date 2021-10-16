package com.fdhasna21.nydrobionics.enumclass

import com.fdhasna21.nydrobionics.R

enum class ProfileType {
    USER {
        override fun getDefaultPicture(): Int {
            return R.drawable.bg_farmer
        }

        override fun toString(): String {
            return "User"
        }
    },
    PLANT{
        override fun getDefaultPicture(): Int {
            return R.drawable.bg_plant
        }

        override fun toString(): String {
            return "Plant"
        }
    },
    NONE{
        override fun getDefaultPicture(): Int {
            return R.mipmap.ic_launcher
        }

        override fun toString(): String {
            return "None"
        }
    };

    abstract fun getDefaultPicture() : Int

    companion object{
        fun getType(tag:String) : ProfileType {
            return when (tag) {
                "user", "createProfile", "editProfileFarm" -> USER
                "plant", "crops", "addCrops", "addDataMonitoring" -> PLANT
                else -> NONE
            }
        }
    }
}