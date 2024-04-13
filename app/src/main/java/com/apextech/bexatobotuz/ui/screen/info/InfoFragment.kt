package com.apextech.bexatobotuz.ui.screen.info

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.apextech.bexatobotuz.R
import com.apextech.bexatobotuz.databinding.FragmentInfoBinding

class InfoFragment : Fragment() {

    private lateinit var binding: FragmentInfoBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        load()
        click()
    }

    private fun load() {
    }

    private fun click() {
        binding.apply {
            cardTelegramBot.setOnClickListener {
                goto(getString(R.string.url_telegram_bot))
            }
            cardWebBexato.setOnClickListener {
                goto(getString(R.string.url_web_bexato))
            }
            imgBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun goto(url: String) = startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}