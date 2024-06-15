package com.sugab.parkirin.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sugab.parkirin.ui.home.HomeFragment

class SectionPageAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 3 // Sesuaikan dengan jumlah lantai atau tab yang ingin ditampilkan
    }
    override fun createFragment(position: Int): Fragment {
        return HomeFragment.newInstance(position)
    }
}
