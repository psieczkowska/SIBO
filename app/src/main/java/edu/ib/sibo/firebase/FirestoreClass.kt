package edu.ib.projectmanapp.firebase

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.res.Resources
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import edu.ib.projectmanapp.models.User
import edu.ib.projectmanapp.utils.Constants
import edu.ib.sibo.activities.*
import edu.ib.sibo.models.Meal
import edu.ib.sibo.models.Product
import edu.ib.sibo.models.Specialist
import edu.ib.sibo.models.Wellbeing
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()


    fun registerUser(activity: SignUpActivity, userInfo: User) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering user"
                )
            }
    }


    fun createSpecialist(activity: AddSpecialistActivity, specialist: Specialist) {
        mFireStore.collection(Constants.SPECIALISTS)
            .document(specialist.documentId)
            .set(specialist, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(
                    activity.javaClass.simpleName,
                    "Specialist created successfully"
                )
                activity.specialistCreatedSuccessfully()
            }.addOnFailureListener { exception ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating specialist", exception)
            }
    }

    fun updateSpecialist(activity: SpecialistDetailsActivity, specialist: Specialist) {
        mFireStore.collection(Constants.SPECIALISTS)
            .document(specialist.documentId)
            .set(specialist, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(
                    activity.javaClass.simpleName,
                    "Specialist updated successfully"
                )
                activity.hideProgressDialog()
            }.addOnFailureListener { exception ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while updating specialist", exception)
            }
    }

    fun getSpecialistsList(activity: DoctorsActivity) {
        mFireStore.collection(Constants.SPECIALISTS)
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val specialistList: ArrayList<Specialist> = ArrayList()
                for (i in document.documents) {
                    val specialist = i.toObject(Specialist::class.java)!!
                    specialistList.add(specialist)
                }
                activity.populateSpecialistsListToUI(specialistList)
            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while getting specialist list")
            }
    }

    fun createMeal(activity: Activity, meal: Meal) {
        mFireStore.collection(Constants.MEALS)
            .document(meal.documentId)
            .set(meal)
            .addOnSuccessListener {
                Log.e(
                    activity.javaClass.simpleName,
                    "Meal created successfully"
                )
                Toast.makeText(
                    activity,
                    "Produkt ${meal.name} został dodany do posiłku",
                    Toast.LENGTH_LONG
                ).show()

            }.addOnFailureListener { exception ->
                if (activity is BaseActivity)
                    activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating meal", exception)
            }
    }

    fun deleteMeal(activity: FoodActivity, meal: Meal) {
        mFireStore.collection(Constants.MEALS).document(meal.documentId)
            .delete()
            .addOnSuccessListener { Log.d(TAG, "Meal successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting meal", e) }
    }

    fun getMealsList(activity: FoodActivity, date: String) {
        mFireStore.collection(Constants.MEALS)
            .whereEqualTo(Constants.THIS_USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val mealsList: ArrayList<Meal> = ArrayList()
                for (i in document.documents) {
                    val meal = i.toObject(Meal::class.java)!!
                    mealsList.add(meal)
                }

                activity.populateMealsListToUI(mealsList, date)
            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while getting meals list")
            }
    }


    fun getCurrentUserId(): String {
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun updateUserProfileData(activity: MyProfileActivity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Profile Data updated")
                Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                activity.profileUpdateSuccess()
            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while updating profile")
            }
    }


    fun loadUserData(activity: Activity) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)!!
                when (activity) {
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                    is MyProfileActivity -> {
                        activity.setUserDataInUI(loggedInUser)
                    }
                }

            }.addOnFailureListener {

                    e ->
                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error while loading user data")
            }
    }


    fun getProductsList(activity: MainActivity) {
        val productsList: ArrayList<Product> = ArrayList()
        mFireStore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->

                Log.e(activity.javaClass.simpleName, document.documents.toString())

                for (i in document.documents) {

                    val product = i.toObject(Product::class.java)!!


                    productsList.add(product)
                }
                activity.populateProductsListToUI(productsList)
            }
            .addOnFailureListener { e ->

                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while getting products list.", e)
            }

    }

    fun getWellbeingList(activity: FoodActivity, date: String) {
        mFireStore.collection(Constants.WELLBEING)
            .whereEqualTo("user", getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val wellbeingList: ArrayList<Wellbeing> = ArrayList()
                for (i in document.documents) {
                    val wellbeing = i.toObject(Wellbeing::class.java)!!
                    wellbeingList.add(wellbeing)
                }

                activity.populateWellBeingListToUI(wellbeingList, date)
            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while getting wellbeing list")
            }
    }

    fun createWellbeing(activity: FoodActivity, wellbeing: Wellbeing) {
        mFireStore.collection(Constants.WELLBEING)
            .document(wellbeing.documentId)
            .set(wellbeing, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(
                    activity.javaClass.simpleName,
                    "Wellbeing created successfully"
                )
                activity.hideProgressDialog()
            }.addOnFailureListener { exception ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a wellbeing.", exception)
            }
    }
}