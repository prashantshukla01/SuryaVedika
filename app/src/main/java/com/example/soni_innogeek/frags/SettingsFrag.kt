package com.example.soni_innogeek.frags

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import com.example.soni_innogeek.R
import com.example.soni_innogeek.databinding.FragmentSettingsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SettingsFrag : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var modeReference: DatabaseReference

    // Flag to prevent infinite loops when updating UI
    private var isUpdatingFromFirebase = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize Firebase database references
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("panelAngle")
        modeReference = firebaseDatabase.getReference("mode")

        // Set initial values from Firebase
        fetchAngleDataFromFirebase()
        setupModeListeners()

        // Material switch listener
        binding.materialSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (!isUpdatingFromFirebase) {
                if (isChecked) {
                    modeReference.setValue(1)
                    binding.toggle.isChecked = false
                } else {
                    modeReference.setValue(0)
                }
            }
        }

        // Toggle switch listener
        binding.toggle.setOnCheckedChangeListener { _, isChecked ->
            if (!isUpdatingFromFirebase) {
                if (isChecked) {
                    modeReference.setValue(2)
                    binding.materialSwitch.isChecked = false
                } else {
                    modeReference.setValue(0)
                }
            }
        }

        // Click listeners for change buttons
        binding.changehoriangle.setOnClickListener {
            showNumberPickerDialog("horizontal")
        }

        binding.changevertiangle.setOnClickListener {
            showNumberPickerDialog("vertical")
        }

        return view
    }

    private fun setupModeListeners() {
        modeReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mode = snapshot.getValue(Int::class.java) ?: 0
                isUpdatingFromFirebase = true

                when (mode) {
                    0 -> {
                        // Apparatus OFF
                        binding.materialSwitch.isChecked = false
                        binding.toggle.isChecked = false
                    }
                    1 -> {
                        // Apparatus ON
                        binding.materialSwitch.isChecked = true
                        binding.toggle.isChecked = false
                    }
                    2 -> {
                        // Cleaning Mode
                        binding.materialSwitch.isChecked = false
                        binding.toggle.isChecked = true
                    }
                }

                isUpdatingFromFirebase = false
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load mode status.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchAngleDataFromFirebase() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val horizontalAngle = dataSnapshot.child("x_axis").getValue(Int::class.java)
                val verticalAngle = dataSnapshot.child("y_axis").getValue(Int::class.java)

                // Update UI with angles from Firebase
                if (horizontalAngle != null) {
                    binding.horiAngle.text = "$horizontalAngle°"
                }
                if (verticalAngle != null) {
                    binding.vertiAngle.text = "$verticalAngle°"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, "Failed to load angles.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showNumberPickerDialog(angleType: String) {
        // Build the dialog
        val builder = AlertDialog.Builder(requireContext())
        val inflater: LayoutInflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_number_picker, null)
        builder.setView(dialogView)

        val numberPicker = dialogView.findViewById<NumberPicker>(R.id.numberPicker)
        numberPicker.minValue = 1
        numberPicker.maxValue = 360

        val btnOk = dialogView.findViewById<Button>(R.id.btnOk)
        val dialog = builder.create()

        btnOk.setOnClickListener {
            val selectedAngle = numberPicker.value

            when (angleType) {
                "horizontal" -> {
                    databaseReference.child("x_axis").setValue(selectedAngle)
                    binding.horiAngle.text = "$selectedAngle°"
                }
                "vertical" -> {
                    databaseReference.child("y_axis").setValue(selectedAngle)
                    binding.vertiAngle.text = "$selectedAngle°"
                }
            }

            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}