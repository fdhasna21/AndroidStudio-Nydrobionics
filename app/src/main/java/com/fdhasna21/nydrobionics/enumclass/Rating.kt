package com.fdhasna21.nydrobionics.enumclass

import com.fdhasna21.nydrobionics.utils.ViewUtility

enum class Rating {
    VERY_BAD,
    BAD,
    GOOD,
    GREAT,
    EXCELLENT;

    override fun toString(): String {
        return ViewUtility().capitalizeEachWord(super.toString(), delimiter = "_")
    }

    companion object {
        fun getType(float: Float?): Rating? {
            float?.let {
                return when (it) {
                    in 0f..1f -> VERY_BAD
                    in 1.0f..2f -> BAD
                    in 2.0f..3f -> GOOD
                    in 3.0f..4f -> GREAT
                    else -> EXCELLENT
                }
            }
            return null
        }
    }
}