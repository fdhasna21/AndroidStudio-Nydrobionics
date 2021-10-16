package com.fdhasna21.nydrobionics.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fdhasna21.nydrobionics.BuildConfig
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.adapter.AdapterType
import com.fdhasna21.nydrobionics.adapter.PostModelAdapter
import com.fdhasna21.nydrobionics.databinding.ActivityShowRecyclerBinding
import com.fdhasna21.nydrobionics.databinding.RowItemPostBinding
import com.fdhasna21.nydrobionics.dataclass.model.PlantModel
import com.fdhasna21.nydrobionics.dataclass.model.UserModel
import com.fdhasna21.nydrobionics.fragment.mainactivity.MainPostsFragment
import com.fdhasna21.nydrobionics.viewmodel.ShowRecyclerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ShowRecyclerActivity : AppCompatActivity() {
    private lateinit var binding : ActivityShowRecyclerBinding
    private lateinit var viewModel : ShowRecyclerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowRecyclerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ShowRecyclerViewModel::class.java)
        viewModel.setCurrentPosts(intent.getParcelableArrayListExtra(BuildConfig.ALL_PLANTS),
            intent.getParcelableExtra<UserModel>(BuildConfig.CURRENT_USER))
        supportActionBar?.title = getString(R.string.history)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val plantModels : ArrayList<PlantModel> = arrayListOf()
        val userModels : ArrayList<UserModel> = arrayListOf()
        val rowAdapter = AdapterType.POST_PLANT.getAdapter(this, plantModels, allUsers = userModels)
        viewModel.getCurrentPosts().observe(this,{
            plantModels.clear()
            plantModels.addAll(it ?: arrayListOf())
            rowAdapter.notifyDataSetChanged()
            (rowAdapter as PostModelAdapter).setOnItemClickListener(
                object : PostModelAdapter.OnItemClickListener{
                    override fun onItemClicked(
                        position: Int,
                        itemView: View,
                        v: RowItemPostBinding
                    ) {
                        when(itemView){
                            v.postRoot -> {
                                gotoPost(position)
                            }
                            v.postOptions -> {
                                val items = arrayOf("Edit", "Delete")
                                MaterialAlertDialogBuilder(this@ShowRecyclerActivity)
                                    .setItems(items){_, which ->
                                        when(which){
                                            0 -> gotoPost(position)
                                            1 -> {
                                                viewModel.deletePost(position)
                                                viewModel.isPostDeleted.observe(this@ShowRecyclerActivity, {
                                                    when(it){
                                                        true -> Toast.makeText(this@ShowRecyclerActivity, "Post deleted.", Toast.LENGTH_SHORT).show()
                                                    }
                                                })
                                            }
                                        }
                                    }
                                    .show()
                            }
                        }
                    }

                }
            )
            Log.i(MainPostsFragment.TAG, "${rowAdapter.itemCount}")
        })

        viewModel.getCurrentUser().observe(this, {
            userModels.clear()
            userModels.addAll(arrayListOf(it))
            rowAdapter.notifyDataSetChanged()
        })

        binding.recyclerView.apply {
            adapter = rowAdapter
            layoutManager = LinearLayoutManager(this@ShowRecyclerActivity)
            addItemDecoration(object : DividerItemDecoration(this@ShowRecyclerActivity, VERTICAL) {})
            setHasFixedSize(true)
        }
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

    private fun gotoPost(position:Int){
        val intent = Intent(this, AddPlantActivity::class.java)
        intent.putExtra(BuildConfig.SELECTED_PLANT, viewModel.getPost(position))
        startActivity(intent)
    }
}