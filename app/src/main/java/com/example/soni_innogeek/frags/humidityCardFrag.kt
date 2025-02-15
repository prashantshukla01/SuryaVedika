package com.example.soni_innogeek.frags

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.soni_innogeek.databinding.FragmentHumidityCardBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.math.min


class humidityCardFrag : Fragment() {
    private var _binding: FragmentHumidityCardBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    private var maxhumidity: Double? = null
    private var minhumidity: Double? = null
    private var currenthumidity: Double? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHumidityCardBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize Firebase Database
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("WeatherData/humidity")

        // Fetch and update temperature data
        getHumidityData()


        return view
    }
    private fun getHumidityData(){
        // Add a value event listener to get live temperature data
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Get the current humidity value from Firebase
                if(snapshot.exists()) {
                    val humidity = snapshot.getValue(Double::class.java)
                    if (humidity != null) {
                        currenthumidity = humidity

                        // Update max and min temperatures
                        if (maxhumidity == null || humidity > maxhumidity!!) {
                            maxhumidity = humidity
                        }
                        if (minhumidity == null || humidity < minhumidity!!) {
                            minhumidity = humidity
                        }

                        // Update the UI with the new values
                        updateHumidityUI()

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors
                Toast.makeText(requireContext(), "Failed to get humidity data.", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun updateHumidityUI() {
        // Display the current, max, and min temperatures in the TextViews
        binding.currenthumidity.text = "Current Humidity: ${currenthumidity}"
        binding.maxhumidity.text = "Max Humidity: ${minhumidity}"
        binding.minhumidity.text = "Min Humidity: ${maxhumidity}"
    }


}