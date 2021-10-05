package com.fdhasna21.nydrobionics.viewmodel

import androidx.lifecycle.ViewModel
import com.fdhasna21.nydrobionics.dataclass.ScoreLevel

class AddDataMonitoringViewModel : ViewModel() {

    init {
//        this.temp = temperature
    }

//    fun setTemperature(temp : Float?=null, type:DataMonitoringType?=null){
////        this.temp.value?.score = if(temp != null){
////            temp
////        } else {
////            type!!.getValue(this.temp.value?.score!!)
////        }
////        //mekanisme warna tooltip
////        if (this.temp.value?.score!! > this.temp.value?.max!!){
////            //tooltip ngubah warna
////        }
//    }
}

//enum class DataMonitoringType(){
//    INCREASE() {},
//    DECREASE() {
//        override fun getValue(currentValue: Float, gap:Float): Float {
//            return super.getValue(currentValue, -gap)
//        }
//    },
//    AUTO_SET(){
//        override fun getValue(currentValue: Float, gap:Float?): Float {
//            return super.getValue(currentValue, null)
//        }
//    };
//    open fun getValue(currentValue:Float, gap:Float?=null) : Float{
//        return currentValue
//    }
//    open fun getValue(currentValue:Float, gap:Float) : Float{
//        return currentValue+gap
//    }
//}