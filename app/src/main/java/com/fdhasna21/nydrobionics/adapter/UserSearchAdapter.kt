package com.fdhasna21.nydrobionics.adapter

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cursoradapter.widget.CursorAdapter
import com.fdhasna21.nydrobionics.databinding.RowItemSearchBinding
import com.fdhasna21.nydrobionics.dataclass.model.User

class UserSearchAdapter(context: Context, cursor: Cursor, var data:ArrayList<User>) : CursorAdapter(context, cursor) {
    private lateinit var binding : RowItemSearchBinding
    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        binding = RowItemSearchBinding.inflate(LayoutInflater.from(context), parent, false)
        return binding.root
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        val item = data[cursor.position]
        binding.rowSearch.setOnClickListener {
            //add into recyclerview
        }
    }
}