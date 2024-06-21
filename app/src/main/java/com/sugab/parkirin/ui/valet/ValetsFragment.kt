package com.sugab.parkirin.ui.valet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sugab.parkirin.databinding.FragmentValetsBinding
import com.sugab.parkirin.utils.ValetsAdapter

class ValetsFragment : Fragment() {

    private lateinit var viewModel: ValetViewModel
    private lateinit var binding: FragmentValetsBinding
    private lateinit var valetsAdapter: ValetsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentValetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        viewModel = ViewModelProvider(this).get(ValetViewModel::class.java)

        observeViewModel()

        viewModel.fetchValets() // Pastikan ini dipanggil untuk mengambil data dari Firestore
    }


    private fun setupRecyclerView() {
        valetsAdapter = ValetsAdapter(emptyList()) // Mulai dengan data kosong
        binding.rvValets.apply {
            adapter = valetsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeViewModel() {
        viewModel.valets.observe(viewLifecycleOwner) { valets ->
            valetsAdapter.updateItems(valets)
        }
    }

}
