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
import kotlinx.android.synthetic.main.activity_firebase_signup.*

class FirebaseSignUpActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_signup)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        mAuth = FirebaseAuth.getInstance()

        btnSignup.setOnClickListener {
            when {
                edtUsername.text.isNullOrEmpty() -> edtUsername.error = "Username cannot be empty"
                edtpassword.text.isNullOrEmpty() -> edtpassword.error = "Password cannot be empty"
                edtConfirmPassword.text.isNullOrEmpty() -> edtConfirmPassword.error = "Confirm Password cannot be empty"
                edtpassword.text.toString() != edtConfirmPassword.text.toString() -> {
                    edtConfirmPassword.error = "Passwords do not match"
                    edtpassword.error = "Passwords do not match"
                }
                else -> {
                    mAuth?.createUserWithEmailAndPassword(edtUsername.text.toString(), edtpassword.text.toString())
                            ?.addOnCompleteListener {
                                if (it.isSuccessful) {
                                    val user = mAuth?.currentUser

                                    val firebaseDatabase = FirebaseDatabase.getInstance()
                                    val userReference = firebaseDatabase.getReference("Members")
                                    val membersKey = userReference.push().key
                                    val member = Member(user?.email, user?.uid)
                                    userReference.child("$membersKey/UserInfo").setValue(member)
                                            .addOnSuccessListener {
                                                Toast.makeText(this, "Member added", Toast.LENGTH_SHORT).show()
                                                val prefs = getSharedPreferences(AppConstants.PREF_FIREBASE, 0)
                                                prefs.edit().putString(AppConstants.PREF_FIREBASE_USER_CONTEXT, Gson().toJson(member)).apply()
                                                startActivity(Intent(FirebaseSignUpActivity@ this, DashboardActivity::class.java))
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(this, "Member add failed ( " + it.message + " )", Toast.LENGTH_SHORT).show()
                                            }
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

        txtSignIn.setOnClickListener {
            startActivity(Intent(FirebaseSignUpActivity@ this, FirebaseSignInActivity::class.java))
        }
    }
}