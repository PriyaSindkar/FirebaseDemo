package com.novumlogic.firebasedemo

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.novumlogic.firebasedemo.helpers.AppConstants
import com.novumlogic.firebasedemo.helpers.UserListAdapter
import com.novumlogic.firebasedemo.helpers.Utility
import com.novumlogic.firebasedemo.model.User
import kotlinx.android.synthetic.main.fragment_list_users.*


class UsersListFragment : Fragment(), UserListAdapter.OnEditClickListener {
    var users = ArrayList<User>()
    var userListAdapter: UserListAdapter? = null
    private val PICK_IMAGE_REQUEST: Int = 101
    private var currentAdapterPosition: Int = -1

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater!!.inflate(R.layout.fragment_list_users, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setItemViewCacheSize(0)
        recyclerView.setHasFixedSize(false)

        val memberReference = Utility.getUserInfoDBReference(activity.getSharedPreferences(AppConstants.PREF_FIREBASE, 0))
        memberReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val nodeKey = it.key!!
                    val userReference = memberReference.ref.child(nodeKey)
                    userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {}
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            dataSnapshot.children.forEach {
                                val type = it.key
                                if (!type.equals("UserInfo")) {
                                    val typeRef = memberReference.ref.child("$nodeKey/" + it.key.toString())
                                    typeRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onCancelled(p0: DatabaseError) {}

                                        override fun onDataChange(dataSnapShot: DataSnapshot) {
                                            dataSnapShot.children.forEach {
                                                val user: User = it.getValue(User::class.java)!!
                                                user.userTypeLabel = type
                                                users.add(user)
                                                userListAdapter = UserListAdapter(users, this@UsersListFragment)
                                                recyclerView.adapter = userListAdapter
                                            }
                                        }
                                    })
                                }
                            }
                        }
                    })
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.data != null) {
            val filePath = data.data
            users[currentAdapterPosition].usrImageURL = filePath.toString()
            userListAdapter?.notifyItemChanged(currentAdapterPosition)
//            userListAdapter?.setImage(currentAdapterPosition)

            //upload to firebase database and firestore
            uploadImage(filePath, currentAdapterPosition)

        }
    }

    private fun uploadImage(filePath: Uri, currentAdapterPosition: Int) {
        val firebaseReference = FirebaseStorage.getInstance().getReference("images/${System.currentTimeMillis()}")
        val uploadTask = firebaseReference.putFile(filePath)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                throw task.exception!!
            }

            // Continue with the task to get the download URL
            firebaseReference.downloadUrl
        }.addOnCompleteListener({ task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                Log.d("FIREBASE_DEMO", downloadUri.toString())
                val user = users[currentAdapterPosition]

                // save the download URL in firebase database
//                val sharedPreferences = activity.getSharedPreferences(AppConstants.PREF_FIREBASE, 0)
//                val currentUser: Member = Gson().getAdapter(TypeToken.get(Member::class.java)).fromJson(sharedPreferences.getString(AppConstants.PREF_FIREBASE_USER_CONTEXT, ""))
                val userType: String = user.userTypeLabel!!
                val memberReference = Utility.getUserInfoDBReference(activity.getSharedPreferences(AppConstants.PREF_FIREBASE, 0))

                memberReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(p0: DataSnapshot) {
                        p0.children.first {
                            val key = it.key!!
                            val userReference = FirebaseDatabase.getInstance().getReference("Members/$key")
//                            val userImageURL = downloadUri.toString()
                            val typeReference = userReference.child(userType)

//                            val typeNodeKey = typeReference.push().key
                            typeReference.child("${user.userId}/usrImageURL").setValue(downloadUri.toString())
                                    .addOnSuccessListener {
                                        userListAdapter?.setImage(currentAdapterPosition, downloadUri.toString())
                                        Toast.makeText(activity, "User Image Updated!", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                                    }
                            return
                        }

                    }
                })

            } else {
                // Handle failures
            }
        })
    }


    override fun onEditClick(position: Int) {
        //check permission
        currentAdapterPosition = position
        chooseImage()
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }
}