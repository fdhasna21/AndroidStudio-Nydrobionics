package com.fdhasna21.nydrobionics.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.fdhasna21.nydrobionics.dataclass.model.DataMonitoringModel
import com.fdhasna21.nydrobionics.dataclass.model.NoteModel
import com.fdhasna21.nydrobionics.dataclass.model.PlantModel
import com.fdhasna21.nydrobionics.dataclass.model.UserModel

enum class AdapterType {
    SEARCH_PLANT {
        override fun getAdapter(
            context: Context,
            adapterData: ArrayList<*>,
            allUsers: ArrayList<UserModel>?,
            allPlants: ArrayList<PlantModel>?,
            type: SearchSelectType?
        ): RecyclerView.Adapter<*> {
            return PlantModelAdapter(context, adapterData as ArrayList<PlantModel>, type!!)
        }
    },
    SEARCH_USER {
        override fun getAdapter(
            context: Context,
            adapterData: ArrayList<*>,
            allUsers: ArrayList<UserModel>?,
            allPlants: ArrayList<PlantModel>?,
            type: SearchSelectType?
        ): RecyclerView.Adapter<*> {
            return UserModelAdapter(context, adapterData as ArrayList<UserModel>, type!!)
        }
    },
    POST_PLANT{
        override fun getAdapter(
            context: Context,
            adapterData: ArrayList<*>,
            allUsers: ArrayList<UserModel>?,
            allPlants: ArrayList<PlantModel>?,
            type: SearchSelectType?
        ): RecyclerView.Adapter<*> {
            return PostModelAdapter(context, adapterData as ArrayList<PlantModel>, allUsers ?: arrayListOf())
        }

    },
    NOTE{
        override fun getAdapter(
            context: Context,
            adapterData: ArrayList<*>,
            allUsers: ArrayList<UserModel>?,
            allPlants: ArrayList<PlantModel>?,
            type: SearchSelectType?
        ): RecyclerView.Adapter<*> {
            return NoteModelAdapter(adapterData as ArrayList<NoteModel>)
        }
    },
    DATA_MONITORING {
        override fun getAdapter(
            context: Context,
            adapterData: ArrayList<*>,
            allUsers: ArrayList<UserModel>?,
            allPlants: ArrayList<PlantModel>?,
            type: SearchSelectType?
        ): RecyclerView.Adapter<*> {
            return DataMonitoringModelAdapter(context, adapterData as ArrayList<DataMonitoringModel>)
        }
    };

    abstract fun getAdapter(context: Context,
                            adapterData:ArrayList<*>,
                            allUsers:ArrayList<UserModel>?=null,
                            allPlants:ArrayList<PlantModel>?=null,
                            type:SearchSelectType?=null) : RecyclerView.Adapter<*>

    companion object{
        enum class SearchSelectType{
            SEARCH,
            SELECT
        }
    }
}