package edu.ib.sibo.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import edu.ib.projectmanapp.firebase.FirestoreClass
import edu.ib.sibo.R
import edu.ib.sibo.adapters.ProductItemsAdapter
import edu.ib.sibo.adapters.RateItemsAdapter
import edu.ib.sibo.models.Rate
import edu.ib.sibo.models.Specialist
import edu.ib.sibo.models.Wellbeing
import kotlinx.android.synthetic.main.activity_add_specialist.*
import kotlinx.android.synthetic.main.activity_specialist_details.*
import kotlinx.android.synthetic.main.dialog_rate.*
import kotlinx.android.synthetic.main.dialog_smile.*
import kotlinx.android.synthetic.main.dialog_smile.tv_meal_wellbeing
import kotlinx.android.synthetic.main.item_specialist.view.*
import kotlinx.android.synthetic.main.main_content.*

class SpecialistDetailsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_specialist_details)
        val model: Specialist? = intent.getParcelableExtra<Specialist>("model")
        if (model != null) {
            populateRatingListToUI(model)
        } else {
            tv_no_rates_available.visibility = View.VISIBLE
            rv_comments_list.visibility = View.GONE
        }
        btn_close.setOnClickListener {
            finish()
        }
        btn_rate_specialist.setOnClickListener {
            if (model != null) {
                dialogRate(model)
            }
        }
    }

    fun dialogRate(specialist: Specialist) {
        val dialog = Dialog(this)
        val user = getCurrentUserID()
        dialog.setContentView(R.layout.dialog_rate)
        val rates = arrayOf(
            "1", "2", "3", "4", "5"
        )
        val spinRate = dialog.spinner_rate
        val adapterRate: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            rates
        )
        adapterRate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinRate.adapter = adapterRate
        var userId = getCurrentUserID()
        dialog.tv_name_and_surname.setText("${specialist.name} ${specialist.surname}")

        dialog.btn_save_rate.setOnClickListener {
            var comment = dialog.et_comment_rate.text.toString()
            var rateInt: Int = dialog.spinner_rate.selectedItem.toString().toInt()
            var rate: Rate
            var newRateList: ArrayList<Rate> = ArrayList()
            var userRatedAlready: Boolean = false
            for (i in specialist.rating) {
                if (i.documentId == userId) {
                    Toast.makeText(
                        this,
                        "Twoja wcześniejsza opinia zostanie zmieniona",
                        Toast.LENGTH_SHORT
                    ).show()

                    i.rate = rateInt
                    i.comment = comment
                    userRatedAlready = true
                    newRateList = specialist.rating
                }
            }
            if (userRatedAlready == false) {
                rate = Rate(userId, rateInt, comment, userId)
                newRateList = specialist.rating
                newRateList.add(rate)
            }

            var specialistNew = Specialist(
                specialist.name,
                specialist.surname,
                specialist.type,
                specialist.city,
                newRateList,
                specialist.documentId
            )

            FirestoreClass().updateSpecialist(this, specialistNew)
            showProgressDialog(getString(R.string.please_wait))
            populateRatingListToUI(specialistNew)
            dialog.dismiss()
        }
        dialog.btn_close_rate.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun populateRatingListToUI(specialist: Specialist) {
        var value = 0
        var ratesList = specialist.rating
        tv_specialist_city.text = specialist.city
        tv_specialist_name.text = specialist.name
        tv_specialist_surname.text = specialist.surname
        tv_specialist_type.text = specialist.type
        for (i in specialist.rating) {
            value += i.rate
        }

        var valueD = (value / specialist.rating.size).toDouble()
        tv_specialist_rating.text = "$valueD/5"
        if (ratesList.size > 0) {
            rv_comments_list.visibility = View.VISIBLE
            tv_no_rates_available.visibility = View.GONE

            rv_comments_list.layoutManager = LinearLayoutManager(this)
            rv_comments_list.setHasFixedSize(true)

            val adapter = RateItemsAdapter(this@SpecialistDetailsActivity, ratesList)
            rv_comments_list.adapter = adapter

        } else {
            rv_comments_list.visibility = View.GONE
            tv_no_rates_available.visibility = View.VISIBLE
        }
    }

}
