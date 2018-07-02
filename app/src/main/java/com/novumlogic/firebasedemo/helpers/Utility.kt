package com.novumlogic.firebasedemo.helpers

import android.content.SharedPreferences
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.novumlogic.firebasedemo.model.Member

object Utility {
    fun getCurrentMemberDBReference(sharedPreferences: SharedPreferences): Query {
        val user: Member = Gson().getAdapter(TypeToken.get(Member::class.java)).fromJson(sharedPreferences.getString(AppConstants.PREF_FIREBASE_USER_CONTEXT, ""))
        return FirebaseDatabase.getInstance().getReference("Members").orderByChild("userId").equalTo(user.userId)
    }

    fun getUserInfoDBReference(sharedPreferences: SharedPreferences): Query {
        val user: Member = Gson().getAdapter(TypeToken.get(Member::class.java)).fromJson(sharedPreferences.getString(AppConstants.PREF_FIREBASE_USER_CONTEXT, ""))
        return FirebaseDatabase.getInstance().getReference("Members").orderByChild("UserInfo/userId").equalTo(user.userId)
    }
}