package com.apextech.bexatobotuz.ui.screen.favourite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.apextech.bexatobotuz.R
import com.apextech.bexatobotuz.databinding.FragmentFavouriteBinding

class FavouriteFragment : Fragment() {

    private lateinit var binding: FragmentFavouriteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
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

    }

    private fun click() {

    }

    companion object {
        @JvmStatic
        fun newInstance() = FavouriteFragment()
    }
}