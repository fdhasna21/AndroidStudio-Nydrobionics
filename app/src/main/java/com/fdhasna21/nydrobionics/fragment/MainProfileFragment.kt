package com.fdhasna21.nydrobionics.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.activity.EditProfileActivity
import com.fdhasna21.nydrobionics.activity.SignInActivity
import com.fdhasna21.nydrobionics.databinding.FragmentMainProfileBinding
import com.fdhasna21.nydrobionics.viewmodel.MainViewModel

class MainProfileFragment : Fragment(), View.OnClickListener {
    private var _binding : FragmentMainProfileBinding? = null
    private val  binding get() = _binding!!
    private lateinit var viewModel : MainViewModel

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
        binding.signoutSubmit.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_main_profile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.profileEdit -> startActivity(Intent(activity, EditProfileActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when(v){
            binding.signoutSubmit -> {
                val alertDialog = AlertDialog.Builder(requireActivity()).apply {
                    setTitle(getString(R.string.sign_out))
                    setMessage(getString(R.string.sign_out_warning))
                    setPositiveButton(getString(R.string.sign_out)){ _,_ ->
                        //todo : logout
                        startActivity(Intent(requireActivity(), SignInActivity::class.java))
                        requireActivity().finish()
                    }
                    setNegativeButton(getString(R.string.cancel)){_,_ ->}
                }
                alertDialog.create()
                alertDialog.show()
            }
        }
    }
}