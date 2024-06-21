package com.sugab.parkirin.ui.home.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.sugab.parkirin.R
import com.sugab.parkirin.databinding.ActivityAdminBinding
import com.sugab.parkirin.ui.parking.ParkingsFragment
import com.sugab.parkirin.ui.valet.ValetsFragment
import com.sugab.parkirin.utils.DialogImagePicker
import com.sugab.parkirin.utils.SectionPageAdapter

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Variable to setup tablayout and viewpager coy
        val viewPager = binding.viewPage
        val tabs = binding.tabs

        //Setup view pager biar gokil parah
        val sectionsPagerAdapter = SectionPageAdapter(this)
        viewPager.adapter = sectionsPagerAdapter

        //Ini tab layout
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = "Floor ${position + 1}"
        }.attach()

        // Setup BottomNavigationView
        binding.bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.parkings -> {
                    replaceFragment(ParkingsFragment())
                    true
                }

                R.id.valets -> {
                    replaceFragment(ValetsFragment())
                    true
                }

                else -> false
            }
        }

        binding.extendedFab.setOnClickListener {
            val dialog = DialogImagePicker()
            dialog.show(supportFragmentManager, "ImagePickerDialogFragment")
        }

        // Set initial fragment
        replaceFragment(ParkingsFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }

    companion object {
        private const val REQUEST_CODE_GALLERY = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            val selectedImageUri = data?.data
            // Handle the selected image URI as needed
        }
    }
}
