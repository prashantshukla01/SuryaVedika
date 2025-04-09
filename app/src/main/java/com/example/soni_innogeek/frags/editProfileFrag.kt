package com.example.soni_innogeek.frags


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.soni_innogeek.R
import com.example.soni_innogeek.databinding.FragmentEditProfileBinding


class editProfileFrag : Fragment() {
    private lateinit var binding:FragmentEditProfileBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(inflater,container,false)
        val view = binding.root

        // Inflate the layout for this fragment
        return view
    }
}