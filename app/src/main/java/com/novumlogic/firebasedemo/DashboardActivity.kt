package com.novumlogic.firebasedemo

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val mNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_contacts -> {
                    inflateFragment(AddUserFragment.newInstance(), "Contacts")
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_leads -> {
                    inflateFragment(AddUserFragment.newInstance(), "Leads")
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_opportunities -> {
                    inflateFragment(AddUserFragment.newInstance(), "Opportunity")
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

        bottomNavigationDashboard.setOnNavigationItemSelectedListener(mNavigationItemSelectedListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dashboard_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_view_all_users -> {
                inflateFragment(UsersListFragment())
                return true
            }
        }
        return false
    }

    private fun inflateFragment(fragment: Fragment, type: String) {
        val transaction = supportFragmentManager.beginTransaction()
        val bundle = Bundle()
        bundle.putString(AddUserFragment.USER_TYPE, type)
        fragment.arguments = bundle
        transaction.replace(R.id.fragmentFrame, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun inflateFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentFrame, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}