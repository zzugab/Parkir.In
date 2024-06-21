package com.sugab.parkirin.ui.home.customer

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sugab.parkirin.customview.ParkingViewModel
import com.sugab.parkirin.databinding.ActivityMainBinding
import com.sugab.parkirin.ui.valet.ValetCardDialog
import com.sugab.parkirin.utils.SectionPageAdapter

class MainActivity : AppCompatActivity() {
    private val viewModel: ParkingViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
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

        viewModel.parkingByName.observe(this) { parking ->
            parking?.let {
                // Update the UI with parking data
                binding.tvUserName.text = it.namePlaced
                binding.tvParkingName.text = it.name
                binding.tvPlat.text = it.plat ?: "No Plate"
                binding.tvStartTime.text = it.startTime!!.toDate().toString()
            }
        }
        // Simulate getting a parking by name
        displayName?.let {
            viewModel.getParkingByName(it)
        }

        // Fetch valet data for the current user and show FAB if valet exists
        currentUser?.uid?.let { userId ->
            fetchValetsByUserId(userId)
        }

        // Set click listener for FAB to show com.sugab.parkirin.ui.valet.ValetCardDialog
        binding.extendedFab.setOnClickListener {
            ValetCardDialog().show(supportFragmentManager, "com.sugab.parkirin.ui.valet.ValetCardDialog")
        }
    }

    private fun fetchValetsByUserId(userId: String) {
        db.collection("valet")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // Show the FAB if any valet entries are found
                    binding.extendedFab.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener { exception ->
                // Handle error
                Log.e("Firestore", "Error fetching valets: ", exception)
            }
    }
}
