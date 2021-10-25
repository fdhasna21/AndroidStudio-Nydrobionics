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
            val temp = it ?: arrayListOf()
            data.clear()
            data.addAll(temp)
             cardStackAdapter.notifyDataSetChanged()
            (cardStackAdapter as KitModelAdapter).setOnItemClickListener(
                object : KitModelAdapter.OnItemClickListener{
                    override fun onItemClicked(position: Int) {
                        val intent = Intent(requireContext(), ProfileKitActivity::class.java)
                        intent.putExtra(BuildConfig.CURRENT_USER, viewModel.getCurrentUser().value)
                        intent.putExtra(BuildConfig.CURRENT_FARM, viewModel.getCurrentFarm().value)
                        intent.putExtra(BuildConfig.SELECTED_KIT, viewModel.getKit(position))
                        startActivity(intent)
                    }
                }
            )
            Log.i(TAG, "adapter count ${cardStackAdapter.itemCount}")
            (requireActivity() as MainActivity).swipeRefresh.isRefreshing = false

            if(temp.size ==0){
                binding.mainCardPrevious.visibility = View.GONE
                binding.mainCardNext.visibility = View.GONE
            } else {
                binding.mainCardPrevious.visibility = View.VISIBLE
                binding.mainCardNext.visibility = View.VISIBLE
            }
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
        when(item.itemId){
            R.id.farmProfile -> {
                val intent = Intent(activity, EditProfileFarmActivity::class.java)
                intent.putExtra(BuildConfig.CURRENT_FARM, viewModel.getCurrentFarm().value)
                intent.putExtra(BuildConfig.CURRENT_USER, viewModel.getCurrentUser().value)
                startActivity(intent)
            }
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