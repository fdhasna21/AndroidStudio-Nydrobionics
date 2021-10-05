package com.fdhasna21.nydrobionics.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fdhasna21.nydrobionics.enumclass.DatabaseType
import com.fdhasna21.nydrobionics.fragment.TabLayoutFragment
import com.firebase.ui.database.FirebaseRecyclerOptions

class ViewPagerAdapter(private var activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    private var totalFragments: Int = 0
    private var fragments : ArrayList<TabLayoutFragment> = arrayListOf()
    private lateinit var dataFragment : ArrayList<ArrayList<*>>
    private lateinit var databaseType : DatabaseType
    private lateinit var options: ArrayList<FirebaseRecyclerOptions<*>>

    constructor(activity: AppCompatActivity, options: ArrayList<FirebaseRecyclerOptions<*>>, databaseType: DatabaseType):this(activity){
        this.totalFragments = options.size
        this.activity = activity
        this.databaseType = databaseType
        this.options = options
    }

    override fun getItemCount(): Int = totalFragments

    override fun createFragment(position: Int): Fragment {
        val fragment = TabLayoutFragment(databaseType.getAdapter(activity, options[position]))
        fragments.add(fragment)
        return fragment
    }

    private fun getFragment(tab: Int) : TabLayoutFragment?{
        return when {
            tab < fragments.size -> fragments[tab]
            tab == fragments.size -> fragments[tab-1]
            else -> null
        }
    }

    fun updateAdapter(tab:Int){
        getFragment(tab)?.setUpdateData()
    }

    fun setFragmentError(tab:Int, visible:Boolean, drawableID:Int=0, messageID:Int=0, code:Int?=null){
        getFragment(tab)?.let {
            if(visible){
                it.setError(drawableID, messageID, code)
            } else {
                it.setNotError()
            }
        }
    }
}