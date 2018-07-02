package com.novumlogic.firebasedemo.helpers

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.novumlogic.firebasedemo.R
import com.novumlogic.firebasedemo.model.User
import com.squareup.picasso.Picasso
import java.io.IOException

class UserListAdapter(private var users: ArrayList<User>, var _onEditClickListener: OnEditClickListener) : RecyclerView.Adapter<UserListAdapter.UserListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): UserListViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_user, parent, false)
        return UserListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UserListViewHolder?, position: Int) {
        val currentUser = users[position]
        holder?.txtUsername?.text = currentUser.userName
        holder?.txtUserPhoneNo?.text = currentUser.userPhoneNumber
        when {
            !currentUser.usrImageURL.isNullOrEmpty() -> {
                try {
                    holder!!.imageView.background = null
                    Picasso.get().load(currentUser.usrImageURL).into(holder.imageView)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            else -> {
                holder!!.imageView.setBackgroundResource(R.drawable.circle)
                when {
                    currentUser.userTypeLabel.equals("Contacts") -> holder.imageView.setImageResource(R.drawable.ic_people_black)
                    currentUser.userTypeLabel.equals("Leads") -> holder.imageView.setImageResource(R.drawable.ic_contacts_black)
                    else -> holder.imageView.setImageResource(R.drawable.ic_perm_contact_calendar)
                }
            }
        }

        holder?.imageView?.setOnClickListener {
            _onEditClickListener.onEditClick(position)
        }
    }

    fun setImage(currentAdapterPosition: Int, donwloadUri: String) {
        users[currentAdapterPosition].usrImageURL = donwloadUri
        notifyItemChanged(currentAdapterPosition)
    }

    class UserListViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var txtUsername: TextView = itemView!!.findViewById(R.id.txtUserName)
        var txtUserPhoneNo: TextView = itemView!!.findViewById(R.id.txtUserPhoneNo)
        var imageView: ImageView = itemView!!.findViewById(R.id.imageView)
    }

    interface OnEditClickListener {
        fun onEditClick(position: Int)
    }
}