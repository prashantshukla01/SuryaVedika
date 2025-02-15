package com.example.soni_innogeek

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.soni_innogeek.databinding.ActivityMainBinding
import com.example.soni_innogeek.frags.HomeFrag
import com.example.soni_innogeek.frags.ProfileFrag
import com.example.soni_innogeek.frags.SettingsFrag
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        replaceFragment(HomeFrag())
        binding.bottomNavView.setOnItemSelectedListener { item ->
            handleBottomNavigation(item)
            true
        }

    }

    private fun handleBottomNavigation(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> replaceFragment(HomeFrag())
            R.id.nav_settings -> replaceFragment(SettingsFrag())
            R.id.nav_profile -> replaceFragment(ProfileFrag())
        }
        return true
    }


     fun replaceFragment(fragment: Fragment) {
        val fragTrans = supportFragmentManager.beginTransaction()
        fragTrans.replace(R.id.frame, fragment)
        fragTrans.commit()
    }

}