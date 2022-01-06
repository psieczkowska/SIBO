package edu.ib.sibo.activities

import android.app.*
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.pes.androidmaterialcolorpickerdialog.ColorPicker
import edu.ib.projectmanapp.firebase.FirestoreClass
import edu.ib.projectmanapp.models.User
import edu.ib.sibo.R
import edu.ib.sibo.adapters.ProductItemsAdapter
import edu.ib.sibo.models.Meal
import edu.ib.sibo.models.Product
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.dialog_add_meal.*
import kotlinx.android.synthetic.main.main_content.*
import kotlinx.android.synthetic.main.nav_header_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    var mSelectedColor: Int = 0
    var mProductsList: ArrayList<Product> = ArrayList()
    lateinit var adapter: ProductItemsAdapter

    companion object {
        const val MY_PROFILE_REQUEST_CODE: Int = 11
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupActionBar()

        nav_view.setNavigationItemSelectedListener(this)
        FirestoreClass().loadUserData(this)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_main, menu)

        val myActionMenuItem = menu!!.findItem(R.id.action_search_main)
        val searchView = myActionMenuItem.actionView as SearchView

        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
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


    private fun setupActionBar() {
        setSupportActionBar(toolbar_main_activity)
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        toolbar_main_activity.setNavigationOnClickListener {
            toggleDrawer()


        }
    }


    private fun toggleDrawer() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            doubleBackToExit()
        }
    }

    fun updateNavigationUserDetails(user: User) {
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(nav_user_image);

        tv_username.text = user.name


        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductsList(this@MainActivity)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE) {
            FirestoreClass().loadUserData(this)
        } else {
            Log.e("Cancelled", "Cancelled")
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_profile -> {
                startActivityForResult(
                    Intent(this, MyProfileActivity::class.java),
                    MY_PROFILE_REQUEST_CODE
                )
            }

            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

            R.id.nav_food -> {
                val intent = Intent(this, FoodActivity::class.java)
                startActivity(intent)

            }

            R.id.nav_instruction -> {
                val intent = Intent(this, PdfViewActivity::class.java)
                startActivity(intent)
            }


            R.id.nav_doctors -> {
                val intent = Intent(this, DoctorsActivity::class.java)
                startActivity(intent)

            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)

        return true
    }


    fun populateProductsListToUI(productsList: ArrayList<Product>) {
        hideProgressDialog()
        if (productsList.size > 0) {
            mProductsList = productsList
            rv_products_list.visibility = View.VISIBLE
            tv_no_products_available.visibility = View.GONE

            rv_products_list.layoutManager = LinearLayoutManager(this)
            rv_products_list.setHasFixedSize(true)

            adapter = ProductItemsAdapter(this@MainActivity, productsList)
            rv_products_list.adapter = adapter
            adapter.setOnClickListener(object :
                ProductItemsAdapter.OnClickListener {
                override fun onClick(position: Int, model: Product) {
                    dialogAddMeal(model)

                }
            })

        } else {
            rv_products_list.visibility = View.GONE
            tv_no_products_available.visibility = View.VISIBLE
        }
    }

    private fun dialogAddMeal(model: Product) {
        var picker: DatePickerDialog
        var pickerT: TimePickerDialog
        var pickerC: ColorPicker
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_add_meal)
        val meals = arrayOf(
            "Śniadanie",
            "II śniadanie",
            "Obiad",
            "Podwieczorek",
            "Kolacja"
        )
        val spin = dialog.meal_spinner
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            meals
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spin.adapter = adapter

        dialog.et_meal_date.setInputType(InputType.TYPE_NULL)
        val cldr: Calendar = Calendar.getInstance()
        val day: Int = cldr.get(Calendar.DAY_OF_MONTH)
        val month: Int = cldr.get(Calendar.MONTH) + 1
        val year: Int = cldr.get(Calendar.YEAR)
        dialog.et_meal_date.setOnClickListener(View.OnClickListener {
            picker = DatePickerDialog(
                this@MainActivity,
                { view, year, monthOfYear, dayOfMonth -> dialog.et_meal_date.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year) },
                year,
                month,
                day
            )
            picker.show()
        })
        val hour: Int = cldr.get(Calendar.HOUR_OF_DAY)
        val minute: Int = cldr.get(Calendar.MINUTE)
        var hourTString: String
        var hourString: String
        var minuteTString: String
        var minuteString: String
        dialog.et_meal_time.setOnClickListener {
            pickerT = TimePickerDialog(this@MainActivity, { view, hourT, minuteT ->
                if (hourT.toString().length == 1) {
                    hourTString = "0$hourT"
                } else {
                    hourTString = hourT.toString()
                }
                if (minuteT.toString().length == 1) {
                    minuteTString = "0$minuteT"
                } else {
                    minuteTString = minuteT.toString()
                }
                dialog.et_meal_time.setText(hourTString + ":" + minuteTString)
            }, hour, minute, true)
            pickerT.show()
        }
        dialog.et_meal_date.setText("$day/$month/$year")
        if (hour.toString().length == 1) {
            hourString = "0$hour"
        } else {
            hourString = hour.toString()
        }
        if (minute.toString().length == 1) {
            minuteString = "0$minute"
        } else {
            minuteString = minute.toString()
        }
        dialog.et_meal_time.setText(hourString + ":" + minuteString)
        dialog.et_meal_name.setText(model.name)

        if (model.fermentation == "zakazane") {
            dialog.color_meal_view.setBackgroundColor(resources.getColor(R.color.color_red))
        } else if (model.fermentation == "mocno fermentujące") {
            dialog.color_meal_view.setBackgroundColor(resources.getColor(R.color.color_orange))
        } else if (model.fermentation == "średnio fermentujące") {
            dialog.color_meal_view.setBackgroundColor(resources.getColor(R.color.color_yellow))
        } else {
            dialog.color_meal_view.setBackgroundColor(resources.getColor(R.color.color_green))
        }
        dialog.tv_add.setOnClickListener {
            val mealName = dialog.et_meal_name.text.toString()
            val mealAmount = dialog.et_meal_amount.text.toString()
            if (mealName.isNotEmpty() && mealAmount.isNotEmpty()) {
                val name: String = dialog.et_meal_name.text.toString()
                val amount: String = dialog.et_meal_amount.text.toString()
                val time: String = dialog.et_meal_time.text.toString()
                val date: String = dialog.et_meal_date.text.toString()
                val background: Drawable = dialog.color_meal_view.getBackground()
                val colorDrawable: ColorDrawable = background as ColorDrawable
                val color: String = colorDrawable.getColor().toString()
                val type: String = dialog.meal_spinner.selectedItem.toString()
                val userID = getCurrentUserID()
                val documentID: String = userID + name + time + date.replace("/", "")
                val meal = Meal(name, amount, date, time, color, type, userID, documentID)

                FirestoreClass().createMeal(this, meal)

                val c = Calendar.getInstance().time

                val df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate: String = df.format(c)

                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please enter name and amount", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.tv_cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.color_meal_view.setOnClickListener {
            pickerC = ColorPicker(this@MainActivity)
            pickerC.show()


            val okColor: Button = pickerC.findViewById(R.id.okColorButton) as Button

            okColor.setOnClickListener(View.OnClickListener {
                mSelectedColor = pickerC.color
                dialog.color_meal_view.setBackgroundColor(mSelectedColor)
                pickerC.dismiss()
            })
        }
        dialog.show()
    }
}