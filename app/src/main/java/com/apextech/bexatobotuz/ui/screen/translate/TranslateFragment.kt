package com.apextech.bexatobotuz.ui.screen.translate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.apextech.bexatobotuz.databinding.FragmentTranslateBinding
import com.apextech.bexatobotuz.utils.Assistant
import com.apextech.bexatobotuz.viewModel.impl.TranslateResource
import com.apextech.bexatobotuz.viewModel.impl.TranslateViewModelImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
import kotlin.coroutines.CoroutineContext


@AndroidEntryPoint
class TranslateFragment : Fragment(), CoroutineScope {

    private lateinit var binding: FragmentTranslateBinding
    private val viewModel by viewModels<TranslateViewModelImpl>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTranslateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        load()
        observe()
        click()
    }

    private fun load() {
    }

    private fun observe() {
        viewModel.stateReplaceTranslator.onEach {
            val first = if (it) "Lotin" else "Krill"
            val second = if (it) "Krill" else "Lotin"

            binding.firstTranslator.text = first
            binding.firstTranslatorCard.text = first
            binding.secondTranslator.text = second
            binding.secondTranslatorCard.text = second
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.stateResult.onEach {
            binding.tvOutputText.text = it
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.stateStatus.onEach {
            when (it) {
                is TranslateResource.Error -> {
                    setError()
                }

                TranslateResource.Loading -> {
//                    setInvisibleAll()
//                    binding.progress.isVisible = true
                }

                TranslateResource.NotInternet -> {
                    setError()
                }

                is TranslateResource.Success -> {
                    setInvisibleAll()
                    binding.bodyView.isVisible = true
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.stateInput.onEach {
            binding.etInputText.setText(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun click() {
        binding.apply {
            imgCopy.setOnClickListener {
                Assistant.copyText(requireActivity(), binding.tvOutputText.text.toString())
            }
            imgShare.setOnClickListener {
                var text = binding.etInputText.text.toString()
                text += "\n\n${binding.tvOutputText.text}\n\nPowered by Bexato"
                Assistant.shareItem(requireContext(), text)
            }
            imgClear.setOnClickListener {
                etInputText.setText("")
            }
            imgReplaceTranslator.setOnClickListener {
                viewModel.replaceTranslator()
            }
            etInputText.doOnTextChanged { text, _, _, _ ->
                viewModel.translate(text.toString())
            }
            imgRefresh.setOnClickListener {
                launch {
                    viewModel.fetchLatins()
                    viewModel.fetchCyrills()
                }
            }
        }
    }

    private fun setError() {
        setInvisibleAll()
        binding.tvError.isVisible = true
        binding.imgRefresh.isVisible = true
    }

    private fun setInvisibleAll() {
        binding.apply {
            tvError.isVisible = false
            bodyView.isVisible = false
            progress.isVisible = false
            imgRefresh.isVisible = false
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = TranslateFragment()
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}