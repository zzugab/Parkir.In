package com.sugab.parkirin.ui.valet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuth
import com.sugab.parkirin.databinding.ValetCardBinding

class ValetCardDialog : DialogFragment() {
    private var _binding: ValetCardBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    private val valetViewModel: ValetViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ValetCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTitle.text = "Valet Card"

        valetViewModel.valet.observe(viewLifecycleOwner) { valet ->
            valet?.let {
                binding.tvId.text = "Valet ID: ${it.id}"
                binding.tvUserName.text =
                    "Nama Customer: ${currentUser!!.displayName}"
                binding.tvPlat.text = "Plat: ${it.plat}"
                binding.tvIsParked.text = "Is Parked: ${it.isParked}"
                if (it.parkingId < 21) {
                    binding.tvParkingId.text = "Parked At: A${it.parkingId + 1}"
                } else if (it.parkingId in 21..40) {
                    binding.tvParkingId.text = "Parked At: B${it.parkingId + 1}"
                } else if (it.parkingId in 41..60) {
                    binding.tvParkingId.text = "Parked At: C${it.parkingId + 1}"
                }
            }
        }

        valetViewModel.employeeName.observe(viewLifecycleOwner) { employeeName ->
            binding.tvEmployeeName.text = "Employee Name: $employeeName"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
