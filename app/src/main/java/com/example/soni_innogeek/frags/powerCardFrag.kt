package com.example.soni_innogeek.frags

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.soni_innogeek.R
import com.example.soni_innogeek.databinding.FragmentPowerCardBinding
import com.example.soni_innogeek.databinding.FragmentTempCardBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.math.max


class powerCardFrag : Fragment() {

    private var _binding: FragmentPowerCardBinding? = null
    private val binding get() = _binding!!
    private var currentPower: Int? = null
    private var maxPower: Int? = null
    private var minPower: Int? = null


    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPowerCardBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize Firebase Database
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("SensorData/Power")

        // Fetch and update temperature data
        getPowerData()


        return view
        }

    private fun getPowerData() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Get the current temperature value from Firebase
                val power = snapshot.getValue(Int::class.java)

                if (power != null) {
                    currentPower = power
                    if (power != null) {
                        currentPower = power

                        // Update max and min temperatures
                        if (maxPower == null || power > maxPower!!) {
                            maxPower = power
                        }
                        if (minPower == null || power < minPower!!) {
                            minPower = power
                        }
                    }
                    // Update the UI with the new values
                    updatePowerUI()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors
                Toast.makeText(requireContext(), "Failed to get temperature data.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updatePowerUI() {
            binding.currentpower.text = "Current Power: ${currentPower}W"
        binding.maxpow.text = "Max Power: ${maxPower}W"
        binding.minpow.text = "Min Power: ${minPower}W"

    }


}