package com.fdhasna21.nydrobionics.enumclass

import android.content.Context
import com.fdhasna21.nydrobionics.adapter.HistoryRowAdapter
import com.fdhasna21.nydrobionics.adapter.PlantRowAdapter
import com.fdhasna21.nydrobionics.dataclass.model.PlantModel
import com.fdhasna21.nydrobionics.dataclass.model.HistoryModel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

enum class AdapterRealTimeType {
    /** For Firebase Real Time **/
    PLANT {
        override fun getAdapter(context: Context, options: FirebaseRecyclerOptions<*>): FirebaseRecyclerAdapter<*, *> {
            return PlantRowAdapter(context, options as FirebaseRecyclerOptions<PlantModel>)
        }
    },
    HISTORY {
        override fun getAdapter(context: Context, options: FirebaseRecyclerOptions<*>): FirebaseRecyclerAdapter<*, *> {
            return HistoryRowAdapter(context, options as FirebaseRecyclerOptions<HistoryModel>)
        }
    };

    abstract fun getAdapter(context: Context, options: FirebaseRecyclerOptions<*>) : FirebaseRecyclerAdapter<*, *>
}