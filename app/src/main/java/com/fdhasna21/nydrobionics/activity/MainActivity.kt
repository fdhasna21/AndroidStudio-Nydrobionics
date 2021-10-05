package com.fdhasna21.nydrobionics.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.fdhasna21.nydrobionics.BuildConfig
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.ActivityMainBinding
import com.fdhasna21.nydrobionics.fragment.MainAddFragment
import com.fdhasna21.nydrobionics.viewmodel.MainViewModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivityMainBinding
    private lateinit var viewModel : MainViewModel
    private lateinit var navHostFragment : NavHostFragment
    private lateinit var navController : NavController
    private lateinit var actionBarToggle : ActionBarDrawerToggle
    lateinit var drawerLayout : DrawerLayout
    private var auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.mainFragmentContainer) as NavHostFragment
        navController = navHostFragment.navController

        binding.apply {
            mainBottomNavigation.setupWithNavController(navController)
            mainAddFragment.setOnClickListener(this@MainActivity)
            mainVersion.text = BuildConfig.VERSION_NAME
        }

        setupDrawer()
    }

    private fun setupDrawer() {
        drawerLayout = binding.mainDrawerLayout
        actionBarToggle = ActionBarDrawerToggle(this, drawerLayout, 0,0)
        drawerLayout.addDrawerListener(actionBarToggle)
        actionBarToggle.syncState()
        binding.mainDrawerNavigation.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.drawer_edit -> {
                    startActivity(Intent(this, EditProfileUserActivity::class.java))
                    drawerLayout.closeDrawer(GravityCompat.END)
                    true
                }
                R.id.drawer_signout -> {
                    val alertDialog = AlertDialog.Builder(this).apply {
                        setTitle(getString(R.string.sign_out))
                        setMessage(getString(R.string.sign_out_warning))
                        setPositiveButton(getString(R.string.sign_out)){ _,_ ->
                            //todo : logout
                            drawerLayout.closeDrawer(GravityCompat.END)
                            startActivity(Intent(this@MainActivity, SignInActivity::class.java))
                            finish()
                        }
                        setNegativeButton(getString(R.string.cancel)){_,_ ->}
                    }
                    alertDialog.create()
                    alertDialog.show()
                    true
                }
                else -> false
            }
        }
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