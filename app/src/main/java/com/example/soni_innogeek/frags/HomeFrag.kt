package com.example.soni_innogeek.frags

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.soni_innogeek.MainActivity
import com.example.soni_innogeek.R
import com.example.soni_innogeek.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFrag : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseDatabase: FirebaseDatabase

    private lateinit var databaseReference: DatabaseReference
    private lateinit var dbrefpower: DatabaseReference


    private lateinit var retrieveTV: TextView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater,container,false)

        val view= binding.root
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("WeatherData")
        dbrefpower = firebaseDatabase.getReference("SensorData")


        getData()
        binding.tempCard.setOnClickListener{
            callfrommainactivity(tempCardFrag())
        }
        binding.humiditycard.setOnClickListener{
            callfrommainactivity(humidityCardFrag())
        }
        binding.efficiencycard.setOnClickListener{
            callfrommainactivity(efficiencyCardFrag())
        }
        binding.powercard.setOnClickListener{
            callfrommainactivity(powerCardFrag())
        }
        return view
    }
    private fun getData() {
        // Add a value event listener to retrieve data from Firebase
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Check if the snapshot exists and contains the expected data
                if (snapshot.exists()) {
                    // Retrieve humidity and temperature values from the snapshot
                    val humidity = snapshot.child("humidity").getValue(Double::class.java)
                    val temperature = snapshot.child("temperature").getValue(Double::class.java)
                    val power = snapshot.child("Power").getValue(Double::class.java)
                    // Update the TextViews with the retrieved data
                    binding.humidityValue.text = "${humidity?.toString() ?: "N/A"}"
                    binding.tempValue.text = "${temperature?.toString() ?: "N/A"}"
                   binding.powerValue.text = "${power?.toString() ?: "N/A"}"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database retrieval error
                Toast.makeText(requireContext(), "Failed to get data.${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
        dbrefpower.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Check if the snapshot exists and contains the expected data
                if (snapshot.exists()) {
                    val power = snapshot.child("Power").getValue(Double::class.java)
                    binding.powerValue.text = "${power?.toString() ?: "N/A"}"
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle database retrieval error
                Toast.makeText(requireContext(), "Failed to get data.${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun callfrommainactivity(frag:Fragment){
        (activity as MainActivity).replaceFragment(frag)
    }


}