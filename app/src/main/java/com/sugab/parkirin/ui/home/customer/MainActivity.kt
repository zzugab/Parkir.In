package com.sugab.parkirin.ui.home.customer

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.sugab.parkirin.customview.ParkingViewModel
import com.sugab.parkirin.databinding.ActivityMainBinding
import com.sugab.parkirin.utils.SectionPageAdapter

class MainActivity : AppCompatActivity() {
    private val viewModel: ParkingViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val displayName = currentUser?.displayName

        val viewPager = binding.viewPage
        val tabs = binding.tabs

        val sectionsPagerAdapter = SectionPageAdapter(this)
        viewPager.adapter = sectionsPagerAdapter

        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = "Floor ${position + 1}"
        }.attach()

        viewModel.parkingByName.observe(this, Observer { parking ->
            parking?.let {
                // Update the UI with parking data
                binding.tvParkingName.text = it.name
                binding.tvParkingId.text = it.id.toString()
                binding.tvPlat.text = it.plat ?: "No Plate"
            }
        })

        // Simulate getting a parking by name
        displayName?.let {
            viewModel.getParkingByName(it)
        }
    }
}
