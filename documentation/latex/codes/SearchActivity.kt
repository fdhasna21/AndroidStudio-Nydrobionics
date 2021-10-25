class SearchActivity : AppCompatActivity(), SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding : ActivitySearchBinding
    private lateinit var viewModel : SearchViewModel
    private lateinit var objectSearch : ProfileType
    private lateinit var rowAdapter : RecyclerView.Adapter<*>

    companion object {
        const val TAG = "searchActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        objectSearch = ProfileType.getType(intent.getStringExtra("search").toString())

        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        supportActionBar?.title = getString(R.string.search) + " $objectSearch"
        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        val searchManager : SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        binding.searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        binding.searchView.setOnQueryTextListener(this)
        binding.searchRefresh.setOnRefreshListener(this)

        when(objectSearch){
            ProfileType.PLANT -> setupRecyclerViewPlant()
            ProfileType.USER -> setupRecyclerViewUser()
        }
    }

    private fun setupRecyclerViewPlant(){
        val plantModels : ArrayList<PlantModel> = arrayListOf()
        val allUsers : ArrayList<UserModel> = viewModel.getAllUsers().value ?: arrayListOf()
        viewModel.getAllPlants()
        rowAdapter = AdapterType.SEARCH_PLANT.getAdapter(this, plantModels,
            allUsers = allUsers,
            type = AdapterType.Companion.SearchSelectType.SEARCH)
        viewModel.getPlants().observe(this, {
            plantModels.clear()
            plantModels.addAll(it ?: arrayListOf())
            rowAdapter.notifyDataSetChanged()
            (rowAdapter as PlantModelAdapter).setOnItemClickListener(
                object : PlantModelAdapter.OnItemClickListener{
                    override fun onItemClicked(
                        position: Int,
                        itemView: View,
                        v: RowItemSearchBinding
                    ) {
                        when(itemView){
                            v.searchRoot -> {
                                val intent = Intent()
                                intent.putExtra(BuildConfig.SELECTED_PLANT, viewModel.getPlant(position))
                                setResult(RESULT_OK, intent)
                                this@SearchActivity.onBackPressed()
                                finish()
                            }
                        }

                    }
                }
            )
            binding.searchRefresh.isRefreshing = false
            Log.i(TAG, "setupRecyclerViewPlant: ${rowAdapter.itemCount}")
        })
        viewModel.getAllUsers().observe(this, {
            allUsers.clear()
            allUsers.addAll(it ?: arrayListOf())
            rowAdapter.notifyDataSetChanged()
        })
        setupRecyclerView()
    }

    private fun setupRecyclerViewUser(){
        viewModel.setExceptUsers(intent.getParcelableArrayListExtra<UserModel>(BuildConfig.EXCEPT_USERS))
        val data : ArrayList<UserModel> = arrayListOf()
        rowAdapter = AdapterType.SEARCH_USER.getAdapter(this, data,
            type = AdapterType.Companion.SearchSelectType.SEARCH)
        viewModel.getUsers().observe(this, {
            data.clear()
            data.addAll(it ?: arrayListOf())
            rowAdapter.notifyDataSetChanged()
            (rowAdapter as UserModelAdapter).setOnItemClickListener(
                object : UserModelAdapter.OnItemClickListener{
                    override fun onItemClicked(
                        userModel: UserModel,
                        position: Int,
                        itemView: View,
                        v: RowItemSearchBinding
                    ) {
                        when(itemView){
                            v.searchRoot -> {
                                val intent = Intent()
                                intent.putExtra(BuildConfig.SELECTED_USER, viewModel.getUser(position))
                                setResult(RESULT_OK, intent)
                                this@SearchActivity.onBackPressed()
                                finish()
                            }
                        }
                    }
                })
            binding.searchRefresh.isRefreshing = false
            Log.i(TAG, "setupRecyclerViewUser: ${rowAdapter.itemCount}")
        })
        setupRecyclerView()
    }

    private fun setupRecyclerView(){
        binding.searchRecyclerView.adapter = rowAdapter
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.searchRecyclerView.addItemDecoration(object : DividerItemDecoration(this, VERTICAL) {})
        binding.searchRecyclerView.setHasFixedSize(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        return true
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun searchKeyword(key:String?) : Boolean{
        when(objectSearch){
            ProfileType.PLANT -> viewModel.searchPlants(key)
            ProfileType.USER -> viewModel.searchUsers(key)
            else -> Log.i(TAG, "enum class not found")
        }
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        binding.searchView.clearFocus()
        return searchKeyword(query)
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return searchKeyword(newText)
    }

    override fun onRefresh() {
        when(objectSearch){
            ProfileType.PLANT -> {
                viewModel.getAllPlants(binding.searchView.query.toString())
            }
            ProfileType.USER -> viewModel.getAllUsers(binding.searchView.query.toString())
            else -> Log.i(TAG, "enum class not found")
        }
    }
}
