package com.apextech.bexatobotuz.ui.screen.translate

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.os.LocaleList
import android.util.Log
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
import com.apextech.bexatobotuz.utils.Constants.TAG
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
            binding.apply {
                keyboard.isVisible = !it
            }

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

        viewModel.stateKeyboardMore.onEach {
            binding.keyboardButtons.isVisible = it
            if (it) {
                binding.keyboardMore.rotation = 180F
            } else {
                binding.keyboardMore.rotation = 0F
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    @SuppressLint("SetTextI18n")
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
                viewModel.addFavourite()
                etInputText.setText("")
            }
            imgReplaceTranslator.setOnClickListener {
                viewModel.replaceTranslator()
                etInputText.setSelection(etInputText.text?.length ?: 0)
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
            imgBack.setOnClickListener {
                findNavController().navigate(R.id.action_translateFragment_to_favouriteFragment)
            }
            imgPaste.setOnClickListener {
                val pasteText = Assistant.pasteText(requireActivity())
                etInputText.setText(pasteText)
                Log.d(TAG, "click: $pasteText")
                etInputText.setSelection(etInputText.text?.length ?: 0)
            }
            cardKeyboardMore.setOnClickListener {
                viewModel.replaceKeyboardMore()
            }
            cardKeyboardK.setOnClickListener {
                setLetter(getString(R.string.k_low))
            }
            cardKeyboardKUp.setOnClickListener {
                setLetter(getString(R.string.k_up))
            }
            cardKeyboardG.setOnClickListener {
                setLetter(getString(R.string.g_low))
            }
            cardKeyboardGUp.setOnClickListener {
                setLetter(getString(R.string.g_up))
            }
            cardKeyboardO.setOnClickListener {
                setLetter(getString(R.string.o_low))
            }
            cardKeyboardOUp.setOnClickListener {
                setLetter(getString(R.string.o_up))
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setLetter(letter: String) {
        binding.apply {
            val selectionStart = etInputText.selectionStart
            val selectionEnd = etInputText.selectionEnd
            val text = etInputText.text.toString()
            etInputText.setText(
                "${text.substring(0, selectionStart)}$letter" +
                        text.substring(selectionEnd, text.length)
            )
            etInputText.setSelection(selectionStart + 1)
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