package com.example.soni_innogeek.frags

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.soni_innogeek.LoginActivity
import com.example.soni_innogeek.MainActivity
import com.example.soni_innogeek.R
import com.example.soni_innogeek.databinding.FragmentProfileBinding
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileFrag : Fragment() {
    private lateinit var _binding: FragmentProfileBinding
    private val binding get() = _binding!!
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private val database = FirebaseDatabase.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyANIdPTTdL83hKbWCuTZ9yPwraDauxCR-c")
        }

        binding.vertSettings.setOnClickListener {
            showPopupMenu(it)
        }

        binding.profileLocation.setOnClickListener {
            startLocationPicker()
        }

        // Load existing location
        loadLocation()

        return binding.root
    }

    private fun loadLocation() {
        database.getReference("location")
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val location = snapshot.value.toString()
                    binding.profileLocation.text = location
                }
            }
    }

    private fun startLocationPicker() {
        val fields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )

        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN, fields
        ).build(requireContext())

        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        showConfirmationDialog(place)
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Toast.makeText(
                            context,
                            "Error: ${status.statusMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // User canceled the operation
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showConfirmationDialog(place: Place) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Location")
            .setMessage("Set location to: ${place.address}")
            .setPositiveButton("Confirm") { _, _ ->
                binding.profileLocation.text = place.address

                place.latLng?.let { latLng ->
                    view?.findViewById<android.widget.TextView>(R.id.longitude_value)?.text =
                        String.format("%.2f", latLng.longitude)
                    view?.findViewById<android.widget.TextView>(R.id.latitude_value)?.text =
                        String.format("%.2f", latLng.latitude)

                    // Save location to Firebase
                    saveLocationToFirebase(place.address.toString())
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveLocationToFirebase(location: String) {
        database.getReference("location")
            .setValue(location)
            .addOnSuccessListener {
                Toast.makeText(context, "Location saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to save location", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit_profile -> {
                    (activity as MainActivity).replaceFragment(editProfileFrag())
                    true
                }
                R.id.log_out -> {
                    logoutUser()
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }
}