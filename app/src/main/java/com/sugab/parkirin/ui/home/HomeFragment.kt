package com.sugab.parkirin.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sugab.parkirin.R
import com.sugab.parkirin.customview.ParkingView

class HomeFragment : Fragment() {
    private var floorId: Int? = null
    private var parkingView: ParkingView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            floorId = it.getInt(ARG_FLOOR_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
        parkingView = rootView.findViewById(R.id.parkingView)

        // Set floorId pada parkingView
        floorId?.let {
            parkingView?.floorId = it
        }

        return rootView
    }

    companion object {
        private const val ARG_FLOOR_ID = "floor_id"

        @JvmStatic
        fun newInstance(floorId: Int) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_FLOOR_ID, floorId)
                }
            }
    }
}

