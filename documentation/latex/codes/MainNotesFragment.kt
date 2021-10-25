class MainNotesFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private var _binding : FragmentMainNotesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : MainViewModel

    companion object {
        const val TAG = "mainNotesFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainNotesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = getString(R.string.notes)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val data : ArrayList<NoteModel> = arrayListOf()
        val rowAdapter = AdapterType.NOTE.getAdapter(requireContext(), data)
        viewModel.getCurrentNotes().observe(viewLifecycleOwner,{
            val currentData : ArrayList<NoteModel> = it ?: arrayListOf()
            data.clear()
            data.addAll(currentData)
            rowAdapter.notifyDataSetChanged()
            (rowAdapter as NoteModelAdapter).setOnItemClickListener(
                object : NoteModelAdapter.OnItemClickListener{
                    override fun onItemClicked(
                        position: Int,
                        itemView: View,
                        v: RowItemNoteBinding
                    ) {
                        gotoNote(position)
                    }

                    override fun onItemLongClicked(position: Int) {
                        val items = arrayOf("Edit", "Delete")
                        MaterialAlertDialogBuilder(context!!)
                            .setItems(items){_, which ->
                                when(which){
                                    0 -> gotoNote(position)
                                    1 -> {
                                        viewModel.deleteNote(position)
                                        viewModel.isNoteDeleted.observe(requireActivity(), {
                                            when(it){
                                                true -> Toast.makeText(requireContext(), "Note deleted.", Toast.LENGTH_SHORT).show()
                                            }
                                        })
                                    }
                                }
                            }
                            .show()
                    }
                }
            )
            Log.i(TAG, "${rowAdapter.itemCount}")
            (requireActivity() as MainActivity).swipeRefresh.isRefreshing = false
            binding.noteLine.visibility = if(currentData.size==0){
                 View.GONE
            } else {
                View.VISIBLE
            }
        })

        binding.mainNotesRecyclerView.apply {
            adapter = rowAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onRefresh() {
        viewModel.refreshNotes()
    }

    private fun gotoNote(position:Int){
        val intent = Intent(requireContext(), AddNoteActivity::class.java)
        intent.putExtra(BuildConfig.SELECTED_NOTE, viewModel.getNote(position))
        startActivity(intent)
    }
}