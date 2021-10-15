package com.fdhasna21.nydrobionics.fragment.mainactivity

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.activity.MainActivity
import com.fdhasna21.nydrobionics.databinding.FragmentMainProfileBinding
import com.fdhasna21.nydrobionics.utility.IntentUtility
import com.fdhasna21.nydrobionics.utility.RequestPermission
import com.fdhasna21.nydrobionics.viewmodel.MainViewModel

class MainProfileFragment : Fragment(), View.OnClickListener {
    private var _binding : FragmentMainProfileBinding? = null
    private val  binding get() = _binding!!
    private lateinit var viewModel : MainViewModel
    private lateinit var drawerLayout : DrawerLayout
    private lateinit var viewsAsButton : ArrayList<View>

    companion object {
        const val TAG = "mainProfile"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        requireActivity().title = getString(R.string.profile)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        drawerLayout = (requireActivity() as MainActivity).drawerLayout

        binding.apply {
            viewsAsButton = arrayListOf(mainProfileEmailGroup,
                                        mainProfilePhoneGroup,
                                        mainProfileAddressGroup,
                                        mainProfilePhoto)
            viewsAsButton.forEach { it.setOnClickListener(this@MainProfileFragment) }

            viewModel.getCurrentUser().observe(requireActivity(),{
                Log.i(TAG, "$it")
                it?.let {
                    mainProfileName.text = it.name
                    mainProfilePhone.text = it.phone
                    mainProfileAddress.text = it.address
                    mainProfileJoinedSince.text = getString(R.string.joined_company_since, it.joinedSince)
                    mainProfileBio.text = it.bio
                    it.performanceRate?.let {
                        mainProfileRate.rating = it
                    }
                    mainProfileEmail.text = it.email
                    mainProfileRole.text = getString(R.string.role_in_profile, it.role!!.replaceFirstChar { it.uppercase() }, viewModel.getCurrentFarm().value?.name.toString())
                    //todo : operational area
                    it.photo_url?.let {
                        Glide.with(requireActivity())
                            .load(it)
                            .centerCrop()
                            .into(mainProfilePhoto)
                    }
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_main_profile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.settings -> {
                if(drawerLayout.isDrawerOpen(GravityCompat.END)){
                    drawerLayout.closeDrawer(GravityCompat.END)
                }
                else{
                    drawerLayout.openDrawer(GravityCompat.END)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when(v){
            binding.mainProfileEmail -> IntentUtility(requireContext()).openEmail(binding.mainProfileEmail.text.toString())
            binding.mainProfilePhoneGroup -> RequestPermission().requestPermission(requireActivity(), Manifest.permission.CALL_PHONE, "Phone call", binding.mainProfilePhone.text.toString())
            binding.mainProfileAddressGroup -> IntentUtility(requireContext()).openMaps(binding.mainProfileAddress.text.toString())
            binding.mainProfilePhoto -> IntentUtility(requireContext()).openImage(binding.mainProfilePhoto, binding.mainProfileName.text.toString())
        }
    }
}
