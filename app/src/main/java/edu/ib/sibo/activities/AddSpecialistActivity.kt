package edu.ib.sibo.activities

import android.app.Activity
import android.os.Bundle
import android.widget.ArrayAdapter
import edu.ib.projectmanapp.firebase.FirestoreClass
import edu.ib.sibo.R
import edu.ib.sibo.models.Rate
import edu.ib.sibo.models.Specialist
import kotlinx.android.synthetic.main.activity_add_specialist.*


class AddSpecialistActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_specialist)

        btn_add_specialist.setOnClickListener {
            showProgressDialog(resources.getString(R.string.please_wait))
            specialistCreatedSuccess()
        }

        btn_cancel_specialist.setOnClickListener {
            finish()
        }

        val types = arrayOf(
            getString(R.string.dietetician),
            getString(R.string.doctor)
        )
        val rates = arrayOf(
            "1", "2", "3", "4", "5"
        )
        val spinType = spinner_type_specialist
        val spinRate = spinner_rate_specialist
        val adapterType: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            types
        )
        val adapterRate: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            rates
        )
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapterRate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinType.adapter = adapterType
        spinRate.adapter = adapterRate
    }


    fun specialistCreatedSuccess() {
        val name: String = et_name_specialist.text.toString().trim { it <= ' ' }
        val surname: String = et_surname_specialist.text.toString().trim { it <= ' ' }
        val city: String = et_city_specialist.text.toString().trim { it <= ' ' }
        val rate: String = spinner_rate_specialist.selectedItem.toString().trim { it <= ' ' }
        val comment: String = et_comment_specialist.text.toString().trim { it <= ' ' }
        val type: String = spinner_type_specialist.selectedItem.toString().trim { it <= ' ' }
        val userId: String = getCurrentUserID()
        val rateInt = rate.toInt()
        val rating = Rate(userId, rateInt, comment, userId)
        val documentId: String = userId + surname + name
        var ratingList: ArrayList<Rate> = ArrayList()
        ratingList.add(rating)

        val specialist = Specialist(name, surname, type, city, ratingList, documentId)
        FirestoreClass().createSpecialist(this, specialist)

    }


    fun specialistCreatedSuccessfully() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
}
