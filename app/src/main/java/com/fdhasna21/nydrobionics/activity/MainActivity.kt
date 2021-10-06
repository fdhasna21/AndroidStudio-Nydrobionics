package com.fdhasna21.nydrobionics.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.UserHandle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.canhub.cropper.CropImage
import com.fdhasna21.nydrobionics.BuildConfig
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.ActivityMainBinding
import com.fdhasna21.nydrobionics.dataclass.model.UserModel
import com.fdhasna21.nydrobionics.fragment.MainAddFragment
import com.fdhasna21.nydrobionics.viewmodel.MainViewModel
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.lang.Exception


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivityMainBinding
    private lateinit var viewModel : MainViewModel
    private lateinit var navHostFragment : NavHostFragment
    private lateinit var navController : NavController
    private lateinit var actionBarToggle : ActionBarDrawerToggle
    lateinit var drawerLayout : DrawerLayout

    companion object{
        const val TAG = "mainActivity"
    }

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

//        viewModel.isCurrentUserExist.observe(this, {
//
//            if(it == false){
//                startActivity(Intent(this, CreateProfileActivity::class.java))
//            }
//            Log.i(TAG, "currentExist : $it")
//        })

        viewModel.setCurrentUser(intent.getParcelableExtra<UserModel>("currentUserModel"))
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
                    val intent = Intent(this, EditProfileUserActivity::class.java)
                    intent.putExtra("currentUserModel", viewModel.currentUserModel.value)
                    startForResult.launch(intent)
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
                                if(it){
//                                    dialogButton.forEach { it.isEnabled = true }
                                    drawerLayout.closeDrawer(GravityCompat.END)
                                    startActivity(Intent(this@MainActivity, SignInActivity::class.java))
                                    finish()
                                } else {
//                                    dialogButton.forEach { it.isEnabled = true }
                                    viewModel.signOutError.observe(this@MainActivity, {
                                        if(it.isNotEmpty()){
                                            Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
                                            Log.i(TAG, it)
                                            viewModel.signOutError.value = ""
                                        }
                                    })
                                }
                            })
                        }
                        setNegativeButton(getString(R.string.cancel)){_,_ ->}
                    }
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
            binding.mainAddFragment -> MainAddFragment().show(supportFragmentManager, TAG+"FAB")
        }
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { data ->
        Log.i(CreateProfileActivity.TAG, "$data")
        try{
            if(data?.resultCode == Activity.RESULT_OK){
                data.data?.getParcelableExtra<UserModel>("currentUserModel")?.let {
                    viewModel.setCurrentUser(it)
                }
            }

        } catch (e: Exception){
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}