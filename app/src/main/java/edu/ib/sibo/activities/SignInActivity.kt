package edu.ib.sibo.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.ib.projectmanapp.models.User
import edu.ib.sibo.R
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_in.et_email_sign_in


class SignInActivity : BaseActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        setupActionBar()

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        btn_sign_in.setOnClickListener { signInUser() }
        auth = Firebase.auth
    }

    fun signInSuccess(user: User) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_sign_in_activity)


        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar_sign_in_activity.setNavigationOnClickListener { onBackPressed() }


    }


    private fun signInUser() {
        val email: String = et_email_sign_in.text.toString().trim { it <= ' ' }
        val password: String = et_password_sign_in.text.toString().trim { it <= ' ' }

        fun validateForm(email: String, password: String): Boolean {
            return when {
                TextUtils.isEmpty(email) -> {
                    showErrorSnackBar("Wprowadź adres email")
                    false
                }
                TextUtils.isEmpty(password) -> {
                    showErrorSnackBar("Wprowadź hasło")
                    false
                }
                else -> {
                    true
                }
            }
        }

        if (validateForm(email, password)) {
            showProgressDialog(resources.getString(R.string.please_wait))
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        startActivity(Intent(this, MainActivity::class.java))

                    } else {
                        Toast.makeText(
                            baseContext, task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
        }
    }


}