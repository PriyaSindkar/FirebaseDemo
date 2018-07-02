package com.novumlogic.firebasedemo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.novumlogic.firebasedemo.helpers.AppConstants
import com.novumlogic.firebasedemo.helpers.Utility
import com.novumlogic.firebasedemo.model.User
import kotlinx.android.synthetic.main.fragment_add_user.*

class AddUserFragment : Fragment() {

    lateinit var userType: String

    companion object {
        const val USER_TYPE = "USER_TYPE"
        fun newInstance(): AddUserFragment = AddUserFragment()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater!!.inflate(R.layout.fragment_add_user, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userType = arguments.get(USER_TYPE).toString()
        txtTitle.text = userType

        val memberReference = Utility.getUserInfoDBReference(activity.getSharedPreferences(AppConstants.PREF_FIREBASE, 0))

        btnAddUser.setOnClickListener {
            memberReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.first {
                        val key = it.key!!
                        val userReference = FirebaseDatabase.getInstance().getReference("Members/$key")
                        val newUser = User(edtUserName.text.toString(), edtPhoneNumber.text.toString())
                        val typeReference = userReference.child(userType)

                        val typeNodeKey = typeReference.push().key
                        newUser.userId = typeNodeKey!!
                        typeReference.child(typeNodeKey).setValue(newUser)
                                .addOnSuccessListener {
                                    Toast.makeText(activity, "User Added Successfully!", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                                }
                        return
                    }

                }
            })

        }
    }
}