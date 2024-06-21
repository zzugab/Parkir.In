package com.sugab.parkirin.ui.parking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sugab.parkirin.customview.ParkingViewModel
import com.sugab.parkirin.databinding.FragmentParkingsBinding
import com.sugab.parkirin.utils.ParkingAdapter

class ParkingsFragment : Fragment() {

    private var _binding: FragmentParkingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var parkingAdapter: ParkingAdapter
    private lateinit var viewModel: ParkingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentParkingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        parkingAdapter = ParkingAdapter(emptyList())
        binding.rvParkings.layoutManager = LinearLayoutManager(requireContext())
        binding.rvParkings.adapter = parkingAdapter

        // Initialize ParkingViewModel
        viewModel = ViewModelProvider(this).get(ParkingViewModel::class.java)

        // Observe changes in floors LiveData
        viewModel.floors.observe(viewLifecycleOwner) { floors ->
            val filteredParkingList = floors?.flatMap { floor -> floor.parkingList }
                ?.filter { it.isPlaced } ?: emptyList()
            parkingAdapter.updateItems(filteredParkingList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
