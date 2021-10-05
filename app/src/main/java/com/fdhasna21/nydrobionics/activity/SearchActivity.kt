package com.fdhasna21.nydrobionics.activity

import android.app.SearchManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.ActivitySearchBinding
import com.fdhasna21.nydrobionics.enumclass.SearchObject
import com.fdhasna21.nydrobionics.viewmodel.SearchViewModel

class SearchActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    private lateinit var binding : ActivitySearchBinding
    private lateinit var viewModel : SearchViewModel
    private lateinit var objectSearch : SearchObject

    companion object {
        const val TAG = "searchActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        objectSearch = SearchObject.getType(intent.getStringExtra("search").toString())

        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        supportActionBar?.title = getString(R.string.search) + " $objectSearch"
        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        val searchManager : SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        binding.searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        binding.searchView.setOnQueryTextListener(this)
        //todo : observer & select data
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun searchKeyword(key:String) : Boolean{
        when(objectSearch){
            SearchObject.PLANT -> viewModel.searchPlants(key)
            SearchObject.USER -> viewModel.searchUsers(key)
            else -> Log.i(TAG, "enum class not found")
        }
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        binding.searchView.clearFocus()
        return searchKeyword(query!!)
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return searchKeyword(newText!!)
    }
}