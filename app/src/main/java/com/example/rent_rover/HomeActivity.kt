package com.example.rent_rover

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.ArrayDeque
import java.util.Deque

class HomeActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var addMenuFab: FloatingActionButton


    private val backStack: Deque<Int> = ArrayDeque(3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Set status bar color
        window.statusBarColor = ContextCompat.getColor(this@HomeActivity, R.color.white)

        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // .................................Initialize the BottomNavigationView.............................................
        bottomNavigationView = findViewById(R.id.bottomNavigationView)


        // Initialize the FloatingActionButton
        addMenuFab = findViewById(R.id.add_menu_fab)


        //................................. Set the default fragment............................................
        backStack.push(R.id.home)
        loadFragment(HomeFragment())



        // ................................Set up the BottomNavigationView item selection listener...........................................
        bottomNavigationView.setOnItemSelectedListener { item ->
            val itemId = item.itemId

            //.............. Avoid reloading the same fragment........................
            if (backStack.peek() == itemId) {
                return@setOnItemSelectedListener true
            }

            //.................. Push to back stack and load fragment........................
            backStack.push(itemId)
            loadFragment(getFragmentByMenuId(itemId))
            true
        }


        // Set an OnClickListener for the FloatingActionButton
        addMenuFab.setOnClickListener {
            //val intent = Intent(this, AddActivity::class.java)
            //startActivity(intent)
            Toast.makeText(this, "Floating action bar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFragmentByMenuId(menuId: Int): Fragment {
        return when (menuId) {
            R.id.home -> HomeFragment()
            R.id.notification -> NotificationFragment()
            R.id.menu -> MenuFragment()
            else -> HomeFragment()
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment, fragment.javaClass.simpleName)
            .commit()
    }

    override fun onBackPressed() {
        if (backStack.size > 1) {
            //Remove the current fragment's menu ID
            backStack.pop()
            // Get the previous fragment's menu ID
            val previousFragmentId = backStack.peek()

            // Update the BottomNavigationView and load the previous fragment
            bottomNavigationView.selectedItemId = previousFragmentId
            loadFragment(getFragmentByMenuId(previousFragmentId))
        } else {
            // If only one fragment is left, show an exit dialog
            AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes") { dialog, _ -> finish() }
                .setNegativeButton("No", null)
                .show()
        }
    }
}
