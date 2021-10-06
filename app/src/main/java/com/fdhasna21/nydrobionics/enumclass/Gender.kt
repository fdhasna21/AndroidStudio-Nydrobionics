package com.fdhasna21.nydrobionics.enumclass

enum class Gender(var gender: String){
    MALE("male") {
        override fun getPosition(): Int {
            return 0
        }
    },
    FEMALE("female") {
        override fun getPosition() : Int {
            return 1
        }
    };

    override fun toString(): String {
        return gender
    }

    abstract fun getPosition():Int

    companion object{
        fun getType(type: String) : Gender?{
           return when(type){
               "male" -> MALE
               "female" -> FEMALE
               else -> null
            }
        }
    }
}
