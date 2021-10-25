class KitOverviewFragment : Fragment() {
    private var _binding : FragmentKitOverviewBinding? = null
    private val binding get() = _binding!!
    private lateinit var bindingCondition : RowItemKitBinding
    private lateinit var bindingPlanted : LayoutPlantedCropsBinding
    private lateinit var viewModel : ProfileKitViewModel

    companion object {
        const val TAG = "kitOverViewFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKitOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (requireActivity() as ProfileKitActivity).viewModel
        bindingCondition = binding.kitCondition
        bindingPlanted = binding.kitPlantedCrops
        bindingCondition.apply {
            kitName.visibility = View.GONE
            kitShowDetail.visibility = View.GONE
        }

        binding.apply {
            bindingCondition.apply {
                bindingPlanted.apply {
                    viewModel.getCurrentKit().observe(viewLifecycleOwner, { kit ->
                        kit?.let {
                            (requireActivity() as ProfileKitActivity).supportActionBar?.title = kit.name.toString()
                            if(it.isPlanted == true){
                                kitPlantedRoot.visibility = View.GONE
                            } else {
                                kitPlantedRoot.visibility = View.GONE
                            }
                        }
                    })

                    viewModel.getCurrentMonitoring().observe(viewLifecycleOwner, { monitorings ->
                        monitorings?.let {
                            if(monitorings.size > 0)
                            {
                                monitorings[0].apply {
                                    kitLastMonitoring.visibility = View.VISIBLE
                                    kitTimestamp.visibility = View.VISIBLE
                                    kitTimestamp.text = getString(R.string.updated_on_s, timestamp)
                                    kitTemp.text = temperature.toString()
                                    kitHumid.text = humidity.toString()
                                    kitAcidity.text = ph.toString()
                                    kitWaterTank.text = waterTank.toString()
                                    kitNutrientTank.text = nutrientTank.toString()
                                    kitTurbidity.text = turbidity.toString()
                                }
                            }
                        }
                    })

                    viewModel.getCurrentCrops().observe(viewLifecycleOwner, { crops ->
                        crops?.let{
                            if(crops.size > 0){
                                crops[0].apply {
                                    plantModel?.let {
                                        kitPlantTitle.text = it.name.toString()
                                        kitPlantDate.text = this.timestamp.toString()

                                    }
                                }
                            }
                        } ?: kotlin.run {
                            kitPlantedRoot.visibility = View.GONE
                        }
                    })
                }
            }
        }
    }
}