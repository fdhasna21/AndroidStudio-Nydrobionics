package com.fdhasna21.nydrobionics.enumclass

import androidx.lifecycle.MutableLiveData
import com.fdhasna21.nydrobionics.service.NumberPickerInterface

enum class NumberPickerDataType : NumberPickerInterface{
    KIT_WIDTH,
    KIT_LENGTH,
    WATER_MIN,
    WATER_MAX,
    NUTRIENT_MIN,
    NUTRIENT_MAX,
    TEMP_MIN,
    TEMP_MAX,
    HUMID_MIN,
    HUMID_MAX,
    PH_MIN,
    PH_MAX,
    TURBIDITY_MIN,
    TURBIDITY_MAX;

    override var data: Float = 0f
    override var max: Float = 100.0f
    override var min: Float = 0f
}