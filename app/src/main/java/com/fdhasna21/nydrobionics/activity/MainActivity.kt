package com.fdhasna21.nydrobionics.activity

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.ActivityMainBinding
import com.fdhasna21.nydrobionics.fragment.MainAddFragment
import com.fdhasna21.nydrobionics.viewmodel.MainViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivityMainBinding
    private lateinit var viewModel : MainViewModel
    private lateinit var navHostFragment : NavHostFragment
    private lateinit var navController : NavController
    private var auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.mainFragmentContainer) as NavHostFragment
        navController = navHostFragment.navController
        binding.mainBottomNavigation.setupWithNavController(navController)

        binding.mainAddFragment.setOnClickListener(this)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onClick(v: View?) {
        when(v){
            binding.mainAddFragment -> {
                MainAddFragment().show(supportFragmentManager, TAG+"FAB")
            }
        }
    }

    companion object{
        const val TAG = "mainActivity"
    }
}