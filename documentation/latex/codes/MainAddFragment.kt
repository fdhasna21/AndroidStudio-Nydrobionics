class MainAddFragment : BottomSheetDialogFragment(), NavigationView.OnNavigationItemSelectedListener {
    private var _binding : FragmentMainAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainAddBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        binding.mainAddMenu.itemIconTintList = null
        binding.mainAddMenu.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.addDataMonitoring  -> gotoActivity(AddDataMonitoringActivity::class.java)
            R.id.addCrops           -> gotoActivity(AddCropsActivity::class.java)
            R.id.addNote            -> gotoActivity(AddNoteActivity::class.java)
            R.id.addKit             -> gotoActivity(AddKitActivity::class.java)
            R.id.addPlant           -> gotoActivity(AddPlantActivity::class.java)
            else                    -> false
        }
    }

    private fun gotoActivity(destination : Class<*>) : Boolean{
        dismiss()
        val intent = Intent(requireActivity(), destination)
        intent.putExtra(BuildConfig.CURRENT_USER, viewModel.getCurrentUser().value)
        intent.putExtra(BuildConfig.CURRENT_FARM, viewModel.getCurrentFarm().value)
        startActivity(intent)
        return true
    }
}