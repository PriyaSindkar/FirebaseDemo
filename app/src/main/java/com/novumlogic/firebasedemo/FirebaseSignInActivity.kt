package com.novumlogic.firebasedemo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.novumlogic.firebasedemo.helpers.AppConstants
import com.novumlogic.firebasedemo.model.Member
import kotlinx.android.synthetic.main.activity_firebase_signin.*

class FirebaseSignInActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_signin)
        mAuth = FirebaseAuth.getInstance()

        btnSignin.setOnClickListener {
            when {
                edtUsernameIn.text.isNullOrEmpty() -> edtUsernameIn.error = "Username cannot be empty"
                edtpasswordIn.text.isNullOrEmpty() -> edtpasswordIn.error = "Password cannot be empty"
                else -> {
                    mAuth?.signInWithEmailAndPassword(edtUsernameIn.text.toString(), edtpasswordIn.text.toString())
                            ?.addOnCompleteListener {
                                if (it.isSuccessful) {
                                    val user = mAuth?.currentUser
                                    val userReference = FirebaseDatabase.getInstance().getReference("Members")
                                    val member = Member(user?.email, user?.uid)
                                    val prefs = getSharedPreferences(AppConstants.PREF_FIREBASE, 0)
                                    prefs.edit().putString(AppConstants.PREF_FIREBASE_USER_CONTEXT, Gson().toJson(member)).apply()

                                    startActivity(Intent(FirebaseSignUpActivity@ this, DashboardActivity::class.java))
                                } else {
                                    Toast.makeText(this, "on failure: " + it.exception!!.message, Toast.LENGTH_SHORT).show()
//                                    try {
//                                        throw it.exception!!
//                                    } catch (emailAuthException: FirebaseAuthInvalidCredentialsException) {
//                                        Toast.makeText(this, "Custom message: " + emailAuthException.message, Toast.LENGTH_SHORT).show()
//                                    } catch (firebaseAuthUserCollisionException: FirebaseAuthUserCollisionException) {
//                                        Toast.makeText(this, "Custom message: " + firebaseAuthUserCollisionException.message, Toast.LENGTH_SHORT).show()
//                                    }
                                }
                            }
//                            ?.addOnFailureListener {
//                                Toast.makeText(this, "on failure: " + it.message, Toast.LENGTH_SHORT).show()
//                            }
                            ?.addOnCanceledListener {
                                Toast.makeText(this, "On cancelled...", Toast.LENGTH_SHORT).show()
                            }
                }
            }
        }
    }
}