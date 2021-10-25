class KitCropsFragment : Fragment() {
    private var _binding : FragmentKitCropsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : ProfileKitViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKitCropsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (requireActivity() as ProfileKitActivity).viewModel

        val data : ArrayList<CropsModel> = arrayListOf()
        val rowAdapter = AdapterType.PROFILE_CROPS.getAdapter(requireContext(), data)
        viewModel.getCurrentCrops().observe(viewLifecycleOwner, {
            data.clear()
            data.addAll(it ?: arrayListOf())
            rowAdapter.notifyDataSetChanged()
        })

        binding.kitCropsRecyclerView.apply {
            adapter = rowAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}