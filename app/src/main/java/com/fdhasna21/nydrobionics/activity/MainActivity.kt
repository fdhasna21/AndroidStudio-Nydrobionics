package com.fdhasna21.nydrobionics.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fdhasna21.nydrobionics.BuildConfig
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.ActivityMainBinding
import com.fdhasna21.nydrobionics.dataclass.model.FarmModel
import com.fdhasna21.nydrobionics.dataclass.model.UserModel
import com.fdhasna21.nydrobionics.fragment.mainactivity.MainAddFragment
import com.fdhasna21.nydrobionics.utility.IntentUtility
import com.fdhasna21.nydrobionics.utility.local.DatabaseHandler
import com.fdhasna21.nydrobionics.viewmodel.MainViewModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), View.OnClickListener,
    SwipeRefreshLayout.OnRefreshListener,
    NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding : ActivityMainBinding
    private lateinit var viewModel : MainViewModel
    private lateinit var navHostFragment : NavHostFragment
    private lateinit var navController : NavController
    private lateinit var actionBarToggle : ActionBarDrawerToggle
    lateinit var swipeRefresh: SwipeRefreshLayout
    lateinit var drawerLayout : DrawerLayout

    companion object{
        const val TAG = "mainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.setCurrentData(
            intent.getParcelableExtra<UserModel>(BuildConfig.CURRENT_USER),
            intent.getParcelableExtra<FarmModel>(BuildConfig.CURRENT_FARM))

        navHostFragment = supportFragmentManager.findFragmentById(R.id.mainFragmentContainer) as NavHostFragment
        navController = navHostFragment.navController

        binding.apply {
            mainBottomNavigation.setupWithNavController(navController)
            mainVersion.text = BuildConfig.VERSION_NAME
            swipeRefresh = mainSwipeRefresh
            mainAddFragment.setOnClickListener(this@MainActivity)
            mainSwipeRefresh.setOnRefreshListener(this@MainActivity)
            mainDrawerNavigation.setNavigationItemSelectedListener(this@MainActivity)
        }

        setupDrawer()
    }

    private fun setupDrawer() {
        drawerLayout = binding.mainDrawerLayout
        actionBarToggle = ActionBarDrawerToggle(this, drawerLayout, 0,0)
        drawerLayout.addDrawerListener(actionBarToggle)
        actionBarToggle.syncState()

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
            binding.mainAddFragment -> MainAddFragment().show(supportFragmentManager, TAG+"FAB")
        }
    }

    override fun onRefresh() {
        when(binding.mainBottomNavigation.selectedItemId){
            R.id.mainHomeFragment -> viewModel.refreshFarm()
            R.id.mainSocialFragment -> viewModel.refreshPosts()
            R.id.mainNotesFragment -> viewModel.refreshNotes()
            R.id.mainProfileFragment -> {}
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.drawer_edit -> {
                val intent = Intent(this, EditProfileUserActivity::class.java)
                intent.putExtra(BuildConfig.CURRENT_USER, viewModel.getCurrentUser().value)
                Log.i(TAG, "onNavigationItemSelected: ${viewModel.getCurrentUser().value}")
                startActivity(intent)
                drawerLayout.closeDrawer(GravityCompat.END)
                true
            }
            R.id.drawer_signout -> {
                val alertDialog = AlertDialog.Builder(this)
                val dialog = alertDialog.create()
//                    val dialogButton : ArrayList<Button> = arrayListOf(dialog.getButton(AlertDialog.BUTTON_POSITIVE),
//                                                                       dialog.getButton(AlertDialog.BUTTON_NEGATIVE))

                alertDialog.apply {
                    setTitle(getString(R.string.sign_out))
                    setMessage(getString(R.string.sign_out_warning))
                    setPositiveButton(getString(R.string.sign_out)){ _,_ ->
//                            dialogButton.forEach { it.isEnabled = false }
                        viewModel.signOut()
                        viewModel.isUserSignOut.observe(this@MainActivity, {
                            it?.let {
                                if(it){
//                                    dialogButton.forEach { it.isEnabled = true }
                                    drawerLayout.closeDrawer(GravityCompat.END)
                                    DatabaseHandler(this@MainActivity).signOut(Firebase.auth.uid!!)
                                    startActivity(Intent(this@MainActivity, SignInActivity::class.java))
                                    finish()
                                } else {
//                                    dialogButton.forEach { it.isEnabled = true }
                                    viewModel.signOutError.observe(this@MainActivity, {
                                        it?.let {
                                            if (it.isNotEmpty()) {
                                                Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
                                                Log.i(TAG, it)
                                                viewModel.signOutError.value = null
                                            }
                                        }
                                    })
                                }
                            }

                        })
                    }
                    setNegativeButton(getString(R.string.cancel)){_,_ ->}
                }
                alertDialog.show()
                true
            }
            R.id.drawer_feedback -> {
                val intent = Intent(this, FeedbackActivity::class.java)
                intent.putExtra(BuildConfig.CURRENT_USER, viewModel.getCurrentUser().value)
                startActivity(intent)
                drawerLayout.closeDrawer(GravityCompat.END)
                true
            }
            R.id.drawer_info -> {
                IntentUtility(this).openAppInfo()
                drawerLayout.closeDrawer(GravityCompat.END)
                true
            }
            R.id.drawer_language ->{
                IntentUtility(this).openLanguageSettings()
                drawerLayout.closeDrawer(GravityCompat.END)
                true
            }
            else -> false
        }
    }
}