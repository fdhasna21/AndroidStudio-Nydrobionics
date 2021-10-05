package com.fdhasna21.nydrobionics.enumclass

import com.fdhasna21.nydrobionics.dataclass.ScoreLevel

enum class MonitoringLevel() {
    AVAILABLE, //green
    WARNING, //yellow
    NOT_AVAILABLE; //red

    companion object{
        fun getType(value:Float, scoreLevel: ScoreLevel){

        }
    }
}