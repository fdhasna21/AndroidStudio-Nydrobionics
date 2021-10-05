package com.fdhasna21.nydrobionics.enumclass

import android.content.Context
import com.fdhasna21.nydrobionics.adapter.HistoryRowAdapter
import com.fdhasna21.nydrobionics.adapter.PlantRowAdapter
import com.fdhasna21.nydrobionics.dataclass.model.Plant
import com.fdhasna21.nydrobionics.dataclass.model.realtime.LogHistory
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

enum class DatabaseType {
    /** For Firebase Real Time **/
    PLANT {
        override fun getAdapter(context: Context, options: FirebaseRecyclerOptions<*>): FirebaseRecyclerAdapter<*, *> {
            return PlantRowAdapter(context, options as FirebaseRecyclerOptions<Plant>)
        }
    },
    HISTORY {
        override fun getAdapter(context: Context, options: FirebaseRecyclerOptions<*>): FirebaseRecyclerAdapter<*, *> {
            return HistoryRowAdapter(context, options as FirebaseRecyclerOptions<LogHistory>)
        }
    };

    abstract fun getAdapter(context: Context, options: FirebaseRecyclerOptions<*>) : FirebaseRecyclerAdapter<*, *>
}