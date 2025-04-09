package com.example.soni_innogeek.frags

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.soni_innogeek.R
import com.example.soni_innogeek.databinding.FragmentTempCardBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class tempCardFrag : Fragment() {
    private var _binding: FragmentTempCardBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    private var maxTemperature: Float? = null
    private var minTemperature: Float? = null
    private var currentTemperature: Float? = null




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTempCardBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize Firebase Database
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("WeatherData")

        // Fetch and update temperature data
        getTemperatureData()


        return view
    }

    private fun getTemperatureData() {
        // Add a value event listener to get live temperature data
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Get the current temperature value from Firebase
                currentTemperature = snapshot.child("temperature").getValue(Float::class.java)
                maxTemperature = snapshot.child("max_temperature").getValue(Float::class.java)
                minTemperature = snapshot.child("min_temperature").getValue(Float::class.java)
                updateTemperatureUI()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors
                Toast.makeText(requireContext(), "Failed to get temperature data.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateTemperatureUI() {
        // Display the current, max, and min temperatures in the TextViews
        binding.currentTemp.text = "Current Temp: ${currentTemperature}°C"
        binding.maxtemp.text = "Max Temp: $maxTemperature°C"
        binding.minimumTemp.text = "Min Temp: $minTemperature°C"
    }


}