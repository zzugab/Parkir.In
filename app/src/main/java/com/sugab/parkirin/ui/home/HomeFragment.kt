package com.sugab.parkirin.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sugab.parkirin.R
import com.sugab.parkirin.customview.ParkingView
import com.sugab.parkirin.customview.ParkingViewModel

class HomeFragment : Fragment() {
    private var floorId: Int = 0
    private var parkingView: ParkingView? = null
    private lateinit var viewModel: ParkingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            floorId = it.getInt(ARG_FLOOR_ID)
        }
        viewModel = ViewModelProvider(this).get(ParkingViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
        parkingView = rootView.findViewById(R.id.parkingView)

        // Set floorId on parkingView
        parkingView?.floorId = floorId

        // Observe changes to the floors data and update the parking view
        viewModel.floors.observe(viewLifecycleOwner) { floors ->
            floors?.getOrNull(floorId)?.let {
                parkingView?.invalidate()
            }
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
