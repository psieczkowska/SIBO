package edu.ib.projectmanapp.firebase

import android.app.Activity
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
                    "Error writing document"
                )
            }
    }

    fun registerProduct(activity: AddProductActivity, productInfo: Product) {
        mFireStore.collection(Constants.PRODUCTS)
            .add(productInfo)
            .addOnSuccessListener {
                Toast.makeText(activity, "Zrobione", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error writing document"
                )
            }
    }

//    fun registerMeal(activity: FoodActivity, mealInfo: Meal) {
//        mFireStore.collection(Constants.MEALS)
//            .add(mealInfo)
//            .addOnSuccessListener {
//                Toast.makeText(activity, "Posiłek został dodany", Toast.LENGTH_SHORT).show()
//            }.addOnFailureListener { e ->
//                Log.e(
//                    activity.javaClass.simpleName,
//                    "Error writing document"
//                )
//            }
//    }

    fun createMeal(activity: FoodActivity, meal: Meal) {
        mFireStore.collection(Constants.MEALS)
            .document()
            .set(meal, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(
                    activity.javaClass.simpleName,
                    "Board created successfully"
                )
                Toast.makeText(activity, "Board created successfully", Toast.LENGTH_LONG).show()
                activity.hideProgressDialog()
                // TODO  activity.mealCreatedSuccessfully()
            }.addOnFailureListener { exception ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", exception)
            }
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
                Log.e(activity.javaClass.simpleName, "Error while creating a boards list")
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
                Log.e(activity.javaClass.simpleName, "Error while creating a board")
                Toast.makeText(activity, "Error when updating the profile", Toast.LENGTH_SHORT)
                    .show()
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
                Log.e(
                    "FirestoreClassSignIn",
                    "Error writing document"
                )
            }
    }


    fun getProductsList(activity: MainActivity) {
        val productsList: ArrayList<Product> = ArrayList()
        mFireStore.collection(Constants.PRODUCTS)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                // Here we get the list of boards in the form of documents.
                Log.e(activity.javaClass.simpleName, document.documents.toString())


                // A for loop as per the list of documents to convert them into Boards ArrayList.
                for (i in document.documents) {

                    val product = i.toObject(Product::class.java)!!
                    //board.documentId = i.id

                    productsList.add(product)
                }

                // Here pass the result to the base activity.
                activity.populateProductsListToUI(productsList)
            }
            .addOnFailureListener { e ->

                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
            }

    }
}