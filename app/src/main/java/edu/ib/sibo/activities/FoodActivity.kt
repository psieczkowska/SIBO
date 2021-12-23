package edu.ib.sibo.activities

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.pes.androidmaterialcolorpickerdialog.ColorPicker
import edu.ib.projectmanapp.firebase.FirestoreClass
import edu.ib.sibo.R
import edu.ib.sibo.adapters.MealItemsAdapter
import edu.ib.sibo.models.Meal
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.activity_food.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.dialog_add_meal.*
import kotlinx.android.synthetic.main.main_content.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class FoodActivity : BaseActivity() {
    private var toolbar: Toolbar? = null
    var mSelectedColor: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food)
        val c = Calendar.getInstance().time
        val df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate: String = df.format(c)
        toolbar = findViewById<View>(R.id.toolbar_food_activity) as Toolbar
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            toolbar!!.setNavigationOnClickListener { onBackPressed() }
        }
        showProgressDialog(getString(R.string.please_wait))


        FirestoreClass().getMealsList(this, formattedDate)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.activity_food_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        when (id) {
            R.id.action_add -> {
                dialogAddMeal()
                return true
            }
            R.id.action_calendar -> {
                val cldr: Calendar = Calendar.getInstance()
                val day: Int = cldr.get(Calendar.DAY_OF_MONTH)
                val month: Int = cldr.get(Calendar.MONTH)
                val year: Int = cldr.get(Calendar.YEAR)
                var formattedDate: String
                var picker: DatePickerDialog
                picker = DatePickerDialog(
                    this@FoodActivity,
                    { view, yearN, monthOfYear, dayOfMonth ->
                        formattedDate = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + yearN
                        showProgressDialog(getString(R.string.please_wait))
                        FirestoreClass().getMealsList(this, formattedDate)
                    },
                    year,
                    month,
                    day
                )
                picker.show()

            }
//            R.id.action_backward -> {
//                val dateS: String = toolbar!!.title.toString()
//                val formatter = DateTimeFormatter.ofPattern("d/MM/yyyy")
//                val date = LocalDate.parse(dateS, formatter)
//                var period = Period.of(0, 0, 1)
//                val newDate = date.minus(period)
//                val newDateS = newDate.format(formatter)
//                showProgressDialog(getString(R.string.please_wait))
//                FirestoreClass().getMealsList(this, newDateS)
//            }
//            R.id.action_forward -> {
//                val dateS: String = toolbar!!.title.toString()
//                val formatter = DateTimeFormatter.ofPattern("d/MM/yyyy")
//                val date = LocalDate.parse(dateS, formatter)
//                var period = Period.of(0, 0, 1)
//                val newDate = date.plus(period)
//                val newDateS = newDate.format(formatter)
//                showProgressDialog(getString(R.string.please_wait))
//                FirestoreClass().getMealsList(this, newDateS)
//            }
        }
        return if (id == R.id.action_add) {
            true
        } else super.onOptionsItemSelected(item)
    }


    private fun dialogAddMeal() {
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
        val month: Int = cldr.get(Calendar.MONTH)
        val year: Int = cldr.get(Calendar.YEAR)
        dialog.et_meal_date.setOnClickListener(View.OnClickListener {
            // date picker dialog

            picker = DatePickerDialog(
                this@FoodActivity,
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
            pickerT = TimePickerDialog(this@FoodActivity, { view, hourT, minuteT ->
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
        dialog.et_meal_date.setText(day.toString() + "/" + (month + 1).toString() + "/" + year.toString())
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
                val meal = Meal(name, amount, date, time, color, type, userID)

                FirestoreClass().createMeal(this@FoodActivity, meal)

                val c = Calendar.getInstance().time

                val df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate: String = df.format(c)

                FirestoreClass().getMealsList(this@FoodActivity, formattedDate)
                showProgressDialog(resources.getString(R.string.please_wait))
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please enter name and amount", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.tv_cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.color_meal_view.setOnClickListener {
            pickerC = ColorPicker(this@FoodActivity)
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

    fun populateMealsListToUI(mealsList: ArrayList<Meal>, date: String) {
        hideProgressDialog()
        toolbar!!.title = date
        var breakfastList: ArrayList<Meal> = ArrayList()
        var lunchList: ArrayList<Meal> = ArrayList()
        var dinnerList: ArrayList<Meal> = ArrayList()
        var snackList: ArrayList<Meal> = ArrayList()
        var supperList: ArrayList<Meal> = ArrayList()

        if (mealsList.size > 0) {
            for (i in mealsList) {
                if (i.date == date) {
                    if (i.type == "Śniadanie") {
                        breakfastList.add(i)
                    } else if (i.type == "II śniadanie") {
                        lunchList.add(i)
                    } else if (i.type == "Obiad") {
                        dinnerList.add(i)
                    } else if (i.type == "Podwieczorek") {
                        snackList.add(i)
                    } else if (i.type == "Kolacja") {
                        supperList.add(i)
                    }
                }
            }

            if (breakfastList.size > 0) {
                breakfast_rv.visibility = View.VISIBLE
                no_breakfast.visibility = View.GONE

                breakfast_rv.layoutManager = LinearLayoutManager(this)
                breakfast_rv.setHasFixedSize(true)

                val adapter = MealItemsAdapter(this@FoodActivity, breakfastList)
                breakfast_rv.adapter = adapter
            } else {
                breakfast_rv.visibility = View.GONE
                no_breakfast.visibility = View.VISIBLE
            }

            if (lunchList.size > 0) {
                lunch_rv.visibility = View.VISIBLE
                no_lunch.visibility = View.GONE

                lunch_rv.layoutManager = LinearLayoutManager(this)
                lunch_rv.setHasFixedSize(true)

                val adapter = MealItemsAdapter(this@FoodActivity, lunchList)
                lunch_rv.adapter = adapter
            } else {
                lunch_rv.visibility = View.GONE
                no_lunch.visibility = View.VISIBLE
            }
            if (dinnerList.size > 0) {
                dinner_rv.visibility = View.VISIBLE
                no_dinner.visibility = View.GONE

                dinner_rv.layoutManager = LinearLayoutManager(this)
                dinner_rv.setHasFixedSize(true)

                val adapter = MealItemsAdapter(this@FoodActivity, dinnerList)
                dinner_rv.adapter = adapter
            } else {
                dinner_rv.visibility = View.GONE
                no_dinner.visibility = View.VISIBLE
            }

            if (snackList.size > 0) {
                snack_rv.visibility = View.VISIBLE
                no_snack.visibility = View.GONE

                snack_rv.layoutManager = LinearLayoutManager(this)
                snack_rv.setHasFixedSize(true)

                val adapter = MealItemsAdapter(this@FoodActivity, snackList)
                snack_rv.adapter = adapter
            } else {
                snack_rv.visibility = View.GONE
                no_snack.visibility = View.VISIBLE
            }

            if (supperList.size > 0) {
                //productList = productsList
                supper_rv.visibility = View.VISIBLE
                no_supper.visibility = View.GONE

                supper_rv.layoutManager = LinearLayoutManager(this)
                supper_rv.setHasFixedSize(true)

                val adapter = MealItemsAdapter(this@FoodActivity, supperList)
                supper_rv.adapter = adapter
            } else {
                supper_rv.visibility = View.GONE
                no_supper.visibility = View.VISIBLE
            }
        } else {
            supper_rv.visibility = View.GONE
            snack_rv.visibility = View.GONE
            dinner_rv.visibility = View.GONE
            lunch_rv.visibility = View.GONE
            breakfast_rv.visibility = View.GONE
            no_supper.visibility = View.VISIBLE
            no_snack.visibility = View.VISIBLE
            no_dinner.visibility = View.VISIBLE
            no_lunch.visibility = View.VISIBLE
            no_breakfast.visibility = View.VISIBLE
        }


    }


}