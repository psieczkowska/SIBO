package edu.ib.sibo.activities

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import edu.ib.projectmanapp.firebase.FirestoreClass
import edu.ib.projectmanapp.models.User
import edu.ib.sibo.R
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
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

        btn_sign_up.setOnClickListener { registerUser() }
    }

    fun userRegisteredSuccess() {
        Toast.makeText(
            this,
            "Teraz możesz się zalogować",
            Toast.LENGTH_SHORT
        ).show()
        FirebaseAuth.getInstance().signOut()
        finish()
    }


    private fun setupActionBar() {
        setSupportActionBar(toolbar_sign_up_activity)


        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar_sign_up_activity.setNavigationOnClickListener { onBackPressed() }


    }

    private fun registerUser() {
        val name: String = et_name.text.toString().trim { it <= ' ' }
        val email: String = et_email_sign_up.text.toString().trim { it <= ' ' }
        val password: String = et_password_sign_up.text.toString().trim { it <= ' ' }

        fun validateForm(name: String, email: String, password: String): Boolean {
            return when {
                TextUtils.isEmpty(name) -> {
                    showErrorSnackBar("Wprowadź imię")
                    false
                }
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

        if (validateForm(name, email, password)) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val registeredEmail = firebaseUser.email!!
                        val user = User(firebaseUser.uid, name, registeredEmail)
                        FirestoreClass().registerUser(this, user)

                    } else {
                        Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
                    }
                }

        }
    }


}