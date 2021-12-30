package edu.ib.sibo.activities

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.pes.androidmaterialcolorpickerdialog.ColorPicker
import edu.ib.projectmanapp.firebase.FirestoreClass
import edu.ib.sibo.R
import edu.ib.sibo.adapters.MealItemsAdapter
import edu.ib.sibo.models.Meal
import edu.ib.sibo.models.Wellbeing
import kotlinx.android.synthetic.main.activity_food.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.dialog_add_meal.*
import kotlinx.android.synthetic.main.dialog_edit_meal.*
import kotlinx.android.synthetic.main.dialog_smile.*
import kotlinx.android.synthetic.main.main_content.*
import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class FoodActivity : BaseActivity(), GestureDetector.OnGestureListener {
    private var toolbar: Toolbar? = null
    var mSelectedColor: Int = 0
    var formattedDate: String = ""
    lateinit var gestureDetector: GestureDetector
    var x2: Float = 0.0f
    var x1: Float = 0.0f

    companion object {
        const val MIN_DISTANCE = 150
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food)
        gestureDetector = GestureDetector(this, this)
        val c = Calendar.getInstance().time
        val df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formattedDate = df.format(c)
        toolbar = findViewById<View>(R.id.toolbar_food_activity) as Toolbar

        setSupportActionBar(toolbar)

        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            toolbar!!.setNavigationOnClickListener { onBackPressed() }
        }
        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().getMealsList(this, formattedDate)
        FirestoreClass().getWellbeingList(this, formattedDate)
        breakfast_smile.setOnClickListener {
            dialogSetWellbeing("Śniadanie", formattedDate)
        }

        lunch_smile.setOnClickListener {
            dialogSetWellbeing("II śniadanie", formattedDate)
        }

        dinner_smile.setOnClickListener {
            dialogSetWellbeing("Obiad", formattedDate)
        }

        snack_smile.setOnClickListener {
            dialogSetWellbeing("Podwieczorek", formattedDate)
        }

        supper_smile.setOnClickListener {
            dialogSetWellbeing("Kolacja", formattedDate)
        }

        val scrollView: ScrollView = findViewById(R.id.scroll_food)
        scrollView.requestDisallowInterceptTouchEvent(true)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_food_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.action_add -> {
                dialogAddMeal()
                return true
            }
            R.id.action_calendar -> {

                val day: Int = toolbar!!.title.split("/")[0].toInt()
                val month: Int = toolbar!!.title.split("/")[1].toInt() - 1
                val year: Int = toolbar!!.title.split("/")[2].toInt()

                var picker: DatePickerDialog
                picker = DatePickerDialog(
                    this@FoodActivity,
                    { view, yearN, monthOfYear, dayOfMonth ->
                        formattedDate =
                            dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + yearN
                        showProgressDialog(getString(R.string.please_wait))
                        FirestoreClass().getMealsList(this, formattedDate)
                        FirestoreClass().getWellbeingList(this, formattedDate)
                    },
                    year,
                    month,
                    day
                )
                picker.show()

            }
        }
        return if (id == R.id.action_add) {
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun dialogSetWellbeing(typeOfMeal: String, date: String) {
        val dialog = Dialog(this)
        var wellbeing: Wellbeing
        val user = getCurrentUserID()
        val dateStamp = date.replace("/", "")
        dialog.setContentView(R.layout.dialog_smile)
        dialog.tv_date_wellbeing.setText(date)
        dialog.tv_meal_wellbeing.setText(typeOfMeal)
        dialog.the_best_button.setOnClickListener {

            wellbeing = Wellbeing(typeOfMeal, "verygood", date, user, "$typeOfMeal$user$dateStamp")
            FirestoreClass().createWellbeing(this, wellbeing)
            showProgressDialog(getString(R.string.please_wait))
            FirestoreClass().getWellbeingList(this, date)
            dialog.dismiss()
        }
        dialog.good_button.setOnClickListener {
            wellbeing = Wellbeing(typeOfMeal, "good", date, user, "$typeOfMeal$user$dateStamp")
            FirestoreClass().createWellbeing(this, wellbeing)
            showProgressDialog(getString(R.string.please_wait))
            FirestoreClass().getWellbeingList(this, date)
            dialog.dismiss()
        }
        dialog.ok_button.setOnClickListener {
            wellbeing = Wellbeing(typeOfMeal, "ok", date, user, "$typeOfMeal$user$dateStamp")
            FirestoreClass().createWellbeing(this, wellbeing)
            showProgressDialog(getString(R.string.please_wait))
            FirestoreClass().getWellbeingList(this, date)
            dialog.dismiss()
        }
        dialog.sad_button.setOnClickListener {
            wellbeing = Wellbeing(typeOfMeal, "sad", date, user, "$typeOfMeal$user$dateStamp")
            FirestoreClass().createWellbeing(this, wellbeing)
            showProgressDialog(getString(R.string.please_wait))
            FirestoreClass().getWellbeingList(this, date)
            dialog.dismiss()
        }
        dialog.cry_button.setOnClickListener {
            wellbeing = Wellbeing(typeOfMeal, "cry", date, user, "$typeOfMeal$user$dateStamp")
            FirestoreClass().createWellbeing(this, wellbeing)
            showProgressDialog(getString(R.string.please_wait))
            FirestoreClass().getWellbeingList(this, date)
            dialog.dismiss()
        }
        dialog.show()
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
        dialog.et_meal_date.setText(toolbar?.title)
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
                val documentID: String = userID + name + time + date.replace("/", "")
                val meal = Meal(name, amount, date, time, color, type, userID, documentID)

                FirestoreClass().createMeal(this@FoodActivity, meal)

                val c = Calendar.getInstance().time

                val df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                FirestoreClass().getMealsList(this@FoodActivity, date)
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
                adapter.setOnClickListener(object :
                    MealItemsAdapter.OnClickListener { //usage of interface, object expression
                    override fun onClick(position: Int, model: Meal) {
                        dialogEditMeal(model)
                    }
                })
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
                adapter.setOnClickListener(object :
                    MealItemsAdapter.OnClickListener {
                    override fun onClick(position: Int, model: Meal) {
                        dialogEditMeal(model)
                    }
                })
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
                adapter.setOnClickListener(object :
                    MealItemsAdapter.OnClickListener { //usage of interface, object expression
                    override fun onClick(position: Int, model: Meal) {
                        dialogEditMeal(model)
                    }
                })
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
                adapter.setOnClickListener(object :
                    MealItemsAdapter.OnClickListener { //usage of interface, object expression
                    override fun onClick(position: Int, model: Meal) {
                        dialogEditMeal(model)
                    }
                })
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
                adapter.setOnClickListener(object :
                    MealItemsAdapter.OnClickListener {
                    override fun onClick(position: Int, model: Meal) {
                        dialogEditMeal(model)
                    }
                })
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

    private fun dialogEditMeal(model: Meal) {
        val dialog = Dialog(this)
        val user = getCurrentUserID()
        dialog.setContentView(R.layout.dialog_edit_meal)
        var picker: DatePickerDialog
        var pickerT: TimePickerDialog
        var pickerC: ColorPicker
        val meals = arrayOf(
            "Śniadanie",
            "II śniadanie",
            "Obiad",
            "Podwieczorek",
            "Kolacja"
        )
        val spin = dialog.meal_spinner_edit
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            meals
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spin.adapter = adapter

        dialog.et_meal_date_edit.setInputType(InputType.TYPE_NULL)
        val cldr: Calendar = Calendar.getInstance()
        val day: Int = model.date.split("/")[0].toInt()
        val month: Int = model.date.split("/")[1].toInt() - 1
        val year: Int = model.date.split("/")[2].toInt()

        dialog.et_meal_date_edit.setText(model.date)
        dialog.et_meal_date_edit.setOnClickListener(View.OnClickListener {
            // date picker dialog

            picker = DatePickerDialog(
                this@FoodActivity,
                { view, year, monthOfYear, dayOfMonth -> dialog.et_meal_date_edit.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year) },
                year,
                month,
                day
            )
            picker.show()
        })

        val hour: Int = model.time.split(":")[0].toInt()
        val minute: Int = model.time.split(":")[1].toInt()
        var hourTString: String
        var hourString: String
        var minuteTString: String
        var minuteString: String
        dialog.et_meal_time_edit.setText(model.time)
        dialog.et_meal_time_edit.setOnClickListener {
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
                dialog.et_meal_time_edit.setText(hourTString + ":" + minuteTString)
            }, hour, minute, true)
            pickerT.show()
        }
        dialog.et_meal_date_edit.setText(day.toString() + "/" + (month + 1).toString() + "/" + year.toString())
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
        dialog.et_meal_time_edit.setText(hourString + ":" + minuteString)
        dialog.et_meal_name_edit.setText(model.name)
        dialog.et_meal_amount_edit.setText(model.amount)
        dialog.color_meal_view_edit.setBackgroundColor(model.color.toInt())
        var mealName = dialog.et_meal_name_edit.text.toString()
        var mealAmount = dialog.et_meal_amount_edit.text.toString()
        var name: String
        var amount: String
        var timeN: String
        var date: String
        var background: Drawable
        var colorDrawable: ColorDrawable
        var color: String
        var type: String
        var userID = getCurrentUserID()
        var documentID: String = model.documentId
        var meal = model
        dialog.tv_save_edit_meal.setOnClickListener {

            name = dialog.et_meal_name_edit.text.toString()
            amount = dialog.et_meal_amount_edit.text.toString()
            timeN = dialog.et_meal_time_edit.text.toString()
            date = dialog.et_meal_date_edit.text.toString()
            background = dialog.color_meal_view_edit.getBackground()
            colorDrawable = background as ColorDrawable
            color = colorDrawable.getColor().toString()
            type = dialog.meal_spinner_edit.selectedItem.toString()
            meal = Meal(name, amount, date, timeN, color, type, userID, documentID)

            if (mealName.isNotEmpty() && mealAmount.isNotEmpty()) {


                FirestoreClass().createMeal(this@FoodActivity, meal)

                val c = Calendar.getInstance().time

                val df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate: String = df.format(c)

                FirestoreClass().getMealsList(this@FoodActivity, date)
                showProgressDialog(resources.getString(R.string.please_wait))
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please enter name and amount", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.tv_cancel_edit_meal.setOnClickListener {
            dialog.dismiss()
        }
        dialog.color_meal_view_edit.setOnClickListener {
            pickerC = ColorPicker(this@FoodActivity)
            pickerC.show()


            val okColor: Button = pickerC.findViewById(R.id.okColorButton) as Button

            okColor.setOnClickListener(View.OnClickListener {
                mSelectedColor = pickerC.color
                dialog.color_meal_view_edit.setBackgroundColor(mSelectedColor)
                pickerC.dismiss()
            })
        }
        dialog.tv_delete_edit_meal.setOnClickListener {
            FirestoreClass().deleteMeal(this, meal)
            FirestoreClass().getMealsList(this@FoodActivity, formattedDate)
            showProgressDialog(resources.getString(R.string.please_wait))
            dialog.dismiss()
        }
        dialog.show()
    }

    fun populateWellBeingListToUI(wellbeingList: ArrayList<Wellbeing>, date: String) {
        breakfast_smile.setImageResource(R.drawable.ic_smile_40)
        lunch_smile.setImageResource(R.drawable.ic_smile_40)
        dinner_smile.setImageResource(R.drawable.ic_smile_40)
        snack_smile.setImageResource(R.drawable.ic_smile_40)
        supper_smile.setImageResource(R.drawable.ic_smile_40)
        if (wellbeingList.size > 0) {
            for (i in wellbeingList) {
                if (i.date == date) {
                    if (i.typeOfMeal == "Śniadanie") {
                        changeImage(breakfast_smile, i)
                    } else if (i.typeOfMeal == "II śniadanie") {
                        changeImage(lunch_smile, i)
                    } else if (i.typeOfMeal == "Obiad") {
                        changeImage(dinner_smile, i)
                    } else if (i.typeOfMeal == "Podwieczorek") {
                        changeImage(snack_smile, i)
                    } else if (i.typeOfMeal == "Kolacja") {
                        changeImage(supper_smile, i)
                    }
                }
            }

        } else {
            breakfast_smile.setImageResource(R.drawable.ic_smile_40)
            lunch_smile.setImageResource(R.drawable.ic_smile_40)
            dinner_smile.setImageResource(R.drawable.ic_smile_40)
            snack_smile.setImageResource(R.drawable.ic_smile_40)
            supper_smile.setImageResource(R.drawable.ic_smile_40)
        }

    }


    fun changeImage(imageBtn: ImageButton, i: Wellbeing) {
        when (i.wellbeing) {
            "verygood" -> {
                imageBtn.setImageResource(R.drawable.ic_the_best_40)
            }
            "good" -> {
                imageBtn.setImageResource(R.drawable.ic_good_40)
            }
            "ok" -> {
                imageBtn.setImageResource(R.drawable.ic_ok_40)
            }
            "sad" -> {
                imageBtn.setImageResource(R.drawable.ic_sad_40)
            }
            "cry" -> {
                imageBtn.setImageResource(R.drawable.ic_cry_40)
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        super.dispatchTouchEvent(ev)
        when (ev?.action) {
            0 -> {
                x1 = ev.x
            }
            1 -> {
                x2 = ev.x
                val valueX: Float = x2 - x1
                if (abs(valueX) > MIN_DISTANCE) {
                    if (x2 > x1) {
                        val dateS: String = toolbar!!.title.toString()
                        val formatter = DateTimeFormatter.ofPattern("d/MM/yyyy")
                        val date = LocalDate.parse(dateS, formatter)
                        var period = Period.of(0, 0, 1)
                        val newDate = date.minus(period)
                        val newDateS = newDate.format(formatter)
                        showProgressDialog(getString(R.string.please_wait))
                        FirestoreClass().getMealsList(this, newDateS)
                        FirestoreClass().getWellbeingList(this, newDateS)
                    } else {
                        val dateS: String = toolbar!!.title.toString()
                        val formatter = DateTimeFormatter.ofPattern("d/MM/yyyy")
                        val date = LocalDate.parse(dateS, formatter)
                        var period = Period.of(0, 0, 1)
                        val newDate = date.plus(period)
                        val newDateS = newDate.format(formatter)
                        showProgressDialog(getString(R.string.please_wait))
                        FirestoreClass().getMealsList(this, newDateS)
                        FirestoreClass().getWellbeingList(this, newDateS)
                    }
                }
            }
        }
        return gestureDetector.onTouchEvent(ev)
    }


    override fun onDown(p0: MotionEvent?): Boolean {
        //TODO("Not yet implemented")
        return false
    }

    override fun onShowPress(p0: MotionEvent?) {

    }

    override fun onSingleTapUp(p0: MotionEvent?): Boolean {
        // TODO("Not yet implemented")
        return false
    }

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        //   TODO("Not yet implemented")
        return false

    }

    override fun onLongPress(p0: MotionEvent?) {

    }

    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        //  TODO("Not yet implemented")
        return false
    }
}
