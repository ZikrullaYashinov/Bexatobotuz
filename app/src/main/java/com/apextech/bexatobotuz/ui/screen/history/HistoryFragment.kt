package com.apextech.bexatobotuz.ui.screen.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.apextech.bexatobotuz.R
import com.apextech.bexatobotuz.databinding.FragmentHistoryBinding
import com.apextech.bexatobotuz.ui.adapter.HistoryAdapter
import com.apextech.bexatobotuz.utils.Constants
import com.apextech.bexatobotuz.viewModel.impl.HistoryViewModelImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private val viewModel by viewModels<HistoryViewModelImpl>()
    private val adapter by lazy { HistoryAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        load()
        observe()
        click()
    }

    private fun load() {
        binding.apply {
            recyclerView.adapter = adapter
        }

    }

    private fun observe() {
        viewModel.stateStatus.onEach {
            adapter.submitList(it.reversed())
            binding.recyclerView.scrollToPosition(0)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun click() {
        adapter.itemClick = {
            setFragmentResult("data", bundleOf(Constants.ARG_HISTORY to it))
            Navigation.findNavController(binding.root).popBackStack()
        }
    }
}