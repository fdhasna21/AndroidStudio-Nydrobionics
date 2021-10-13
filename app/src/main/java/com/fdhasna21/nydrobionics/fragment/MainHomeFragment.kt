package com.fdhasna21.nydrobionics.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.fdhasna21.nydrobionics.BuildConfig
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.activity.EditProfileFarmActivity
import com.fdhasna21.nydrobionics.activity.MainActivity
import com.fdhasna21.nydrobionics.activity.ProfileKitActivity
import com.fdhasna21.nydrobionics.activity.ProfileUserActivity
import com.fdhasna21.nydrobionics.adapter.AdapterType
import com.fdhasna21.nydrobionics.adapter.KitModelAdapter
import com.fdhasna21.nydrobionics.databinding.FragmentMainHomeBinding
import com.fdhasna21.nydrobionics.dataclass.model.KitModel
import com.fdhasna21.nydrobionics.viewmodel.MainViewModel
import com.yuyakaido.android.cardstackview.*

class MainHomeFragment : Fragment(), View.OnClickListener, CardStackListener{
    private var _binding : FragmentMainHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : MainViewModel
    private lateinit var viewsAsButton : ArrayList<View>
    private lateinit var cardStackLayoutManager: CardStackLayoutManager
    private lateinit var cardStackAdapter : RecyclerView.Adapter<*>

    companion object {
        const val TAG = "mainHomeFragment"
    }

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

        binding.apply {
            viewsAsButton = arrayListOf(mainCardPrevious, mainCardNext)
            viewsAsButton.forEach { it.setOnClickListener(this@MainHomeFragment) }

            viewModel.getCurrentFarm().observe(viewLifecycleOwner,{
                it?.let {
                    mainHomeFarmName.text = it.name.toString()
                    Log.i(TAG, "onViewCreated: update")
                }
            })
        }

        setupCardStack()
    }

    private fun setupCardStack() {
        val data : ArrayList<KitModel> = arrayListOf()
        cardStackAdapter = AdapterType.DATA_MONITORING.getAdapter(requireContext(), data)
        viewModel.getCurrentKits().observe(viewLifecycleOwner, {
            data.clear()
            data.addAll(it ?: arrayListOf())
             cardStackAdapter.notifyDataSetChanged()
            (cardStackAdapter as KitModelAdapter).setOnItemClickListener(
                object : KitModelAdapter.OnItemClickListener{
                    override fun onItemClicked(position: Int) {
                        Toast.makeText(requireContext(), "position $position", Toast.LENGTH_SHORT).show()
                    }
                }
            )
            Log.i(TAG, "adapter count ${cardStackAdapter.itemCount}")
            (requireActivity() as MainActivity).swipeRefresh.isRefreshing = false
        })


        val swipeSetting = SwipeAnimationSetting.Builder()
            .setDirection(Direction.Right)
            .setDuration(Duration.Normal.duration)
            .build()

        val rewindSetting = RewindAnimationSetting.Builder()
            .setDirection(Direction.Left)
            .setDuration(Duration.Normal.duration)
            .build()

        cardStackLayoutManager = CardStackLayoutManager(requireContext(), this@MainHomeFragment).apply {
            setStackFrom(StackFrom.None)
            setDirections(Direction.HORIZONTAL)
            setCanScrollHorizontal(true)
            setCanScrollVertical(false)
            setSwipeAnimationSetting(swipeSetting)
            setRewindAnimationSetting(rewindSetting)
        }

        binding.mainHomeCardStack.apply {
            adapter = cardStackAdapter
            layoutManager = cardStackLayoutManager
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_main_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //todo : trial aja
        when(item.itemId){
            R.id.farmProfile -> {
                val intent = Intent(activity, EditProfileFarmActivity::class.java)
                intent.putExtra(BuildConfig.CURRENT_FARM, viewModel.getCurrentFarm().value)
                startActivity(intent)
            }
//            R.id.userProfile -> startActivity(Intent(activity, ProfileUserActivity::class.java))
            R.id.kitProfile -> startActivity(Intent(activity, ProfileKitActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when(v){
            binding.mainCardNext -> {
                if(cardStackLayoutManager.topPosition < cardStackAdapter.itemCount-1){
                    binding.mainHomeCardStack.swipe()
                } else {
                    binding.mainHomeCardStack.smoothScrollToPosition(0)
                }
            }
            binding.mainCardPrevious -> {
                if(cardStackLayoutManager.topPosition != 0){
                    binding.mainHomeCardStack.rewind()
                } else {
                    binding.mainHomeCardStack.smoothScrollToPosition(cardStackAdapter.itemCount-1)
                }
            }
        }
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {}

    override fun onCardSwiped(direction: Direction?) {
        if(cardStackLayoutManager.topPosition == cardStackAdapter.itemCount){
            binding.mainHomeCardStack.scrollToPosition(0)
        }
    }

    override fun onCardRewound() {}

    override fun onCardCanceled() {}

    override fun onCardAppeared(view: View?, position: Int) {
        Log.i(TAG, "appear $position")
    }

    override fun onCardDisappeared(view: View?, position: Int) {
    }
}