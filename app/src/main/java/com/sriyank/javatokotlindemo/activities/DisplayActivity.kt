package com.sriyank.javatokotlindemo.activities

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.sriyank.javatokotlindemo.R
import com.sriyank.javatokotlindemo.adapters.DisplayAdapter
import com.sriyank.javatokotlindemo.app.Constants
import com.sriyank.javatokotlindemo.app.showErrorMessage
import com.sriyank.javatokotlindemo.app.toast
import com.sriyank.javatokotlindemo.retrofit.GithubAPIService
import com.sriyank.javatokotlindemo.retrofit.RetrofitClient
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_display.*
import kotlinx.android.synthetic.main.header.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class DisplayActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mDisplayAdapter: DisplayAdapter
    private var browsedRepositories
            : List<Repository> = mutableListOf()

    private val githubAPIService: GithubAPIService by lazy {
        RetrofitClient.githubAPIService
    }
   // private var mRealm: Realm? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)
        //  val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(supportActionBar)!!.setTitle("Showing Browsed Results")
        }
        // mRecyclerView = findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView!!.layoutManager = layoutManager

        setAppUserName();

//        mRealm = Realm.getDefaultInstance()
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        //   mDrawerLayout = findViewById(R.id.drawer_layout)
        val drawerToggle = ActionBarDrawerToggle(this,
                drawer_layout, toolbar, R.string.drawer_open,
                R.string.drawer_close)
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        val intent = intent
        if (intent.getIntExtra(Constants.KEY_QUERY_TYPE, -1) == Constants.SEARCH_BY_REPO) {
            val queryRepo = intent.getStringExtra(Constants.KEY_REPO_SEARCH)
            val repoLanguage = intent.getStringExtra(Constants.KEY_LANGUAGE)

            fetchRepositories(queryRepo!!, repoLanguage!!)

        } else {
            val githubUser = intent.getStringExtra(Constants.KEY_GITHUB_USER)
            fetchUserRepositories(githubUser)
        }
    }

    private fun setAppUserName() {
        val sp = getSharedPreferences(Constants.APP_SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val personName = sp.getString(Constants
                .KEY_PERSON_NAME, "user")
        val headerView   = nav_view.getHeaderView(0)
        headerView.txvName.text = personName
    }

    private fun fetchUserRepositories(githubUser
                                      : String?) {

        githubAPIService.searchRepositoriesByUser(githubUser).enqueue(object : Callback<List<Repository>> {
            override fun onResponse(call: Call<List<Repository>>?,
                                    response: Response<List<Repository>>) {
                if (response.isSuccessful) {
                    Log.i(TAG, "posts loaded from API $response")

                    response.body()?.let{
                        browsedRepositories = mutableListOf()
                    }

                } else {
                    Log.i(TAG, "Error $response")
                    if (browsedRepositories.isEmpty())
                        setupRecyclerView(browsedRepositories)
                    else
                        toast("No Items Found")
                    showErrorMessage(response.errorBody()!!)
                }
            }

            override fun onFailure(call: Call<List<Repository>>?, t: Throwable) {
                toast("Error Fectching files")
            }


        })
    }

    private fun fetchRepositories(queryRepo
                                  : String,
                                  repoLanguage:
                                  String) {
        var queryRepo_ = queryRepo
        val query = HashMap<String, String>()

        if (repoLanguage.isNotEmpty()){
            queryRepo_ += "language: " + repoLanguage
            query.put("q", queryRepo_)
        }
        githubAPIService.searchRepositories(query).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {

                if (response.isSuccessful) {
                    Log.i(TAG, "posts loaded from API $response")

                    response.body()?.items?.let {
                        browsedRepositories = it
                    }
                    if (browsedRepositories.isEmpty())
                        setupRecyclerView(browsedRepositories)
                    else
                        toast("No Items Found", Toast.LENGTH_LONG)

                } else {
                    Log.i(TAG, "error $response")
                    showErrorMessage(response.errorBody()!!)

                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                toast(t.toString())
            }
        })
    }

    private fun setupRecyclerView(items: List<Repository>) {
        mDisplayAdapter = DisplayAdapter(this, items)
        recyclerView.adapter = mDisplayAdapter
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        menuItem.isChecked = true
        when (menuItem.itemId) {
            R.id.item_bookmark ->
            {consumeMenuEvent({ showBookmarks() }
                    , "Showing Bookmarks")}

            R.id.item_browsed_results ->
            {consumeMenuEvent({ showBrowsedResults()},
                    "Showing Browsed Results"
            )}
        }
        return true
    }

   private inline fun consumeMenuEvent(myFunc: () -> Unit,
                         title:String){
        myFunc()
        closeDrawer()

        supportActionBar!!.title = title

    }

    private fun showBrowsedResults() {
        mDisplayAdapter.swap(browsedRepositories)
    }

    private fun showBookmarks() {
        val realm = Realm.getDefaultInstance()
       realm.executeTransaction { realm ->
           val bookmarkedList = realm
                   .where(Repository::class.java)
                   .findAll()
            mDisplayAdapter.swap(bookmarkedList)
        }
    }

    private fun closeDrawer() {
        drawer_layout.closeDrawer(GravityCompat.START)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) closeDrawer() else {
            super.onBackPressed()
           // mRealm!!.close()
        }
    }

    companion object {
        private val TAG = DisplayActivity::class.java.simpleName
    }
}