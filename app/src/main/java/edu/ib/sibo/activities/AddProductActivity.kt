package edu.ib.sibo.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import edu.ib.projectmanapp.firebase.FirestoreClass
import edu.ib.projectmanapp.models.User
import edu.ib.sibo.R
import edu.ib.sibo.models.Product
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class AddProductActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        btn_add_product.setOnClickListener {



            productRegisteredSuccess()
        }
    }


    fun productRegisteredSuccess() {
        val name: String = et_name_product.text.toString().trim { it <= ' ' }
        val amount: String = et_amount.text.toString().trim { it <= ' ' }
        val fermentation: String = et_fermentation.text.toString().trim { it <= ' ' }
        val category: String = et_category.text.toString().trim { it <= ' ' }


        val product = Product(name, category, fermentation, amount)
        FirestoreClass().registerProduct(this, product)
        finish()

    }
}