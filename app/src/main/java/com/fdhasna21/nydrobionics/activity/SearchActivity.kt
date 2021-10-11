package com.fdhasna21.nydrobionics.activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.adapter.AdapterType
import com.fdhasna21.nydrobionics.adapter.PlantModelAdapter
import com.fdhasna21.nydrobionics.adapter.UserModelAdapter
import com.fdhasna21.nydrobionics.databinding.ActivitySearchBinding
import com.fdhasna21.nydrobionics.dataclass.model.PlantModel
import com.fdhasna21.nydrobionics.dataclass.model.UserModel
import com.fdhasna21.nydrobionics.enumclass.ProfileType
import com.fdhasna21.nydrobionics.viewmodel.SearchViewModel

class SearchActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    private lateinit var binding : ActivitySearchBinding
    private lateinit var viewModel : SearchViewModel
    private lateinit var objectSearch : ProfileType

    companion object {
        const val TAG = "searchActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        objectSearch = ProfileType.getType(intent.getStringExtra("search").toString())

        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        supportActionBar?.title = getString(R.string.search) + " $objectSearch"
        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        val searchManager : SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        binding.searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        binding.searchView.setOnQueryTextListener(this)
        setupRecyclerView()
    }

    private fun setupRecyclerView(){
        if (objectSearch == ProfileType.PLANT) {
            viewModel.getAllUsers()
            viewModel.getPlants().observe(this, {
                val rowAdapter = AdapterType.SEARCH_PLANT.getAdapter(this, it, AdapterType.Companion.SearchSelectType.SEARCH)
                (rowAdapter as PlantModelAdapter).setOnItemClickListener(
                    object : PlantModelAdapter.OnItemClickListener{
                        override fun onItemClicked(data: PlantModel) {
                            val intent = Intent()
                            intent.putExtra("selectedPlantModel", data)
                            setResult(RESULT_OK, intent)
                            this@SearchActivity.onBackPressed()
                            finish()
                        }
                    }
                )
                binding.searchRecyclerView.adapter = rowAdapter
            })
        }
        else if (objectSearch == ProfileType.USER) {
            viewModel.getAllPlants()
            viewModel.getUsers().observe(this, {
                val rowAdapter = AdapterType.SEARCH_USER.getAdapter(this, it, AdapterType.Companion.SearchSelectType.SEARCH)
                (rowAdapter as UserModelAdapter).setOnItemClickListener(
                    object : UserModelAdapter.OnItemClickListener{
                        override fun onItemClicked(data: UserModel) {
                            val intent = Intent()
                            intent.putExtra("selectedUserModel", data)
                            setResult(RESULT_OK, intent)
                            this@SearchActivity.onBackPressed()
                            finish()
                        }
                })
                binding.searchRecyclerView.adapter = rowAdapter
            })
        }

        binding.searchRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.searchRecyclerView.addItemDecoration(object : DividerItemDecoration(this, VERTICAL) {})
        binding.searchRecyclerView.setHasFixedSize(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
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

    private fun searchKeyword(key:String?) : Boolean{
        when(objectSearch){
            ProfileType.PLANT -> viewModel.searchPlants(key)
            ProfileType.USER -> viewModel.searchUsers(key)
            else -> Log.i(TAG, "enum class not found")
        }
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        binding.searchView.clearFocus()
        return searchKeyword(query)
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return searchKeyword(newText)
    }
}
