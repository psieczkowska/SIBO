package edu.ib.sibo.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import edu.ib.projectmanapp.firebase.FirestoreClass
import edu.ib.sibo.R
import edu.ib.sibo.adapters.SpecialistItemsAdapter
import edu.ib.sibo.models.Specialist
import kotlinx.android.synthetic.main.activity_doctors.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.main_content.*

class DoctorsActivity : BaseActivity() {
    var mSpecialistList: ArrayList<Specialist> = ArrayList()
    lateinit var adapter: SpecialistItemsAdapter
    companion object {
        const val SPECIALIST_DETAILS_REQUEST_CODE: Int = 2
        const val ADD_SPECIALIST_REQUEST_CODE: Int = 3
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_in_toolbar, menu)

        val myActionMenuItem = menu!!.findItem(R.id.action_search)
        val mapActionItem = menu!!.findItem(R.id.action_map)
        val searchView = myActionMenuItem.actionView as androidx.appcompat.widget.SearchView

        searchView.setOnQueryTextListener(
            object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    adapter.filter(query)
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    adapter.filter(newText)
                    return true
                }
            }
        )


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        val id = item.itemId

        when (id) {
            R.id.action_map -> {
                val intent = Intent(this, MapsActivity::class.java)
                intent.putExtra("specialistList", mSpecialistList)
                startActivity(intent)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctors)
        setupActionBar()
        FirestoreClass().getSpecialistsList(this)
        fab_create_specialist.setOnClickListener {
            val intent = Intent(this, AddSpecialistActivity::class.java)
            startActivityForResult(intent, ADD_SPECIALIST_REQUEST_CODE)
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_doctors_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)

        }

        toolbar_doctors_activity.setNavigationOnClickListener { onBackPressed() }
    }


    fun populateSpecialistsListToUI(specialistsList: ArrayList<Specialist>) {
        if (specialistsList.size > 0) {
            mSpecialistList = specialistsList
            rv_specialists_list.visibility = View.VISIBLE
            tv_no_specialists_available.visibility = View.GONE

            rv_specialists_list.layoutManager = LinearLayoutManager(this)
            rv_specialists_list.setHasFixedSize(true)

            adapter = SpecialistItemsAdapter(this@DoctorsActivity, specialistsList)
            adapter.setOnClickListener(object :
                SpecialistItemsAdapter.OnClickListener {

                override fun onClick(position: Int, model: Specialist) {
                    val intent = Intent(this@DoctorsActivity, SpecialistDetailsActivity::class.java)
                    intent.putExtra("model", model)
                    startActivityForResult(intent, SPECIALIST_DETAILS_REQUEST_CODE)
                }
            })
            rv_specialists_list.adapter = adapter



        } else {
            rv_specialists_list.visibility = View.GONE
            tv_no_specialists_available.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == ADD_SPECIALIST_REQUEST_CODE) {
            FirestoreClass().getSpecialistsList(this)
        } else {
            Log.e("Cancelled", "Cancelled")
        }
    }
}