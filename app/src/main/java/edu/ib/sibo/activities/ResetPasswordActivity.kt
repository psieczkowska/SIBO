package edu.ib.sibo.activities

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.ib.sibo.R
import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class ResetPasswordActivity : BaseActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        auth = Firebase.auth
        setupActionBar()
        btn_reset.setOnClickListener {
            val emailAddress = et_email_reset.text.toString()

            auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Email sent.")
                        Toast.makeText(this, "Sprawdź skrzynkę pocztową", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_reset_activity)


        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            actionBar.title = "Resetuj hasło"
        }

        toolbar_reset_activity.setNavigationOnClickListener { onBackPressed() }


    }
}