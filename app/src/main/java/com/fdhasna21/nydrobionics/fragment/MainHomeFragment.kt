package com.fdhasna21.nydrobionics.fragment

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.activity.EditProfileUserActivity
import com.fdhasna21.nydrobionics.activity.ProfileFarmActivity
import com.fdhasna21.nydrobionics.activity.ProfileKitActivity
import com.fdhasna21.nydrobionics.activity.ProfileUserActivity
import com.fdhasna21.nydrobionics.databinding.FragmentMainHomeBinding
import com.fdhasna21.nydrobionics.viewmodel.MainViewModel
import com.google.android.material.tabs.TabLayout

class MainHomeFragment : Fragment() {
    private var _binding : FragmentMainHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        requireActivity().title = getString(R.string.home)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_main_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //todo : trial aja
        when(item.itemId){
            R.id.farmProfile -> startActivity(Intent(activity, ProfileFarmActivity::class.java))
            R.id.userProfile -> startActivity(Intent(activity, ProfileUserActivity::class.java))
            R.id.kitProfile -> startActivity(Intent(activity, ProfileKitActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
}