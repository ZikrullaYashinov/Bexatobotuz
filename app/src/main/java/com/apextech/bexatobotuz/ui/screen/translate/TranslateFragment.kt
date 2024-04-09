package com.apextech.bexatobotuz.ui.screen.translate

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.os.LocaleList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.apextech.bexatobotuz.R
import com.apextech.bexatobotuz.data.local.entity.HistoryEntity
import com.apextech.bexatobotuz.databinding.FragmentTranslateBinding
import com.apextech.bexatobotuz.utils.Assistant
import com.apextech.bexatobotuz.utils.Constants
import com.apextech.bexatobotuz.viewModel.impl.TranslateResource
import com.apextech.bexatobotuz.viewModel.impl.TranslateViewModelImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.coroutines.CoroutineContext


@AndroidEntryPoint
class TranslateFragment : Fragment(), CoroutineScope {

    private lateinit var binding: FragmentTranslateBinding
    private val viewModel by viewModels<TranslateViewModelImpl>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener("data") { _, bundle ->
            val result = bundle.getSerializable(Constants.ARG_HISTORY) as HistoryEntity
            viewModel.translateHistory(result)
        }
    }

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

    @SuppressLint("ResourceType")
    private fun observe() {
        viewModel.stateReplaceTranslator.onEach {
            val first = if (it) getString(R.string.lotin) else getString(R.string.krill)
            val second = if (it) getString(R.string.krill) else getString(R.string.lotin)
            val firstColor = if (it) R.color.colorSecondaryDark else R.color.colorPrimary
            val secondColor = if (it) R.color.colorPrimary else R.color.colorSecondaryDark

            binding.etInputText.setHint(first)
            binding.firstTranslator.text = first
            binding.secondTranslator.text = second
            binding.firstTranslator.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    firstColor
                )
            )
            binding.secondTranslator.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    secondColor
                )
            )

            if (it)
                binding.etInputText.imeHintLocales = LocaleList(Locale("en", "EN"))
            else
                binding.etInputText.imeHintLocales = LocaleList(Locale("ru", "RU"))
            val imeManager: InputMethodManager =
                requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imeManager.restartInput(binding.etInputText)
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
                    setInvisibleAll()
                    binding.progress.isVisible = true
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
                if (binding.tvOutputText.text.toString().trim().isEmpty()) return@setOnClickListener
                Assistant.copyText(requireActivity(), binding.tvOutputText.text.toString())
            }
            imgShare.setOnClickListener {
                val text = binding.tvOutputText.text.toString().trim()
                if (text.isEmpty()) return@setOnClickListener
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
            imgInfo.setOnClickListener {
                findNavController().navigate(R.id.action_translateFragment_to_infoFragment)
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

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}