package com.example.uberdriver.presentation.auth.welcome.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.uberdriver.R
import com.example.uberdriver.databinding.FragmentWelcomeBinding
import com.example.uberdriver.presentation.auth.login.adapter.SliderAdapter
import com.example.uberdriver.presentation.auth.login.adapter.SliderItem
import com.smarteist.autoimageslider.SliderView


class WelcomeFragment : Fragment() {

    private var binding: FragmentWelcomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setImageSlider()
        onContinueBtnClickListener()
    }

    private fun setImageSlider(){
        val imageList = listOf(
            SliderItem(R.drawable.car),
            SliderItem(R.drawable.lady)
        )

        binding?.imageSlider?.apply {
            setSliderAdapter(SliderAdapter(requireContext(), imageList))
            autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR;
            isAutoCycle = true;
            startAutoCycle();
            setCurrentPageListener(imageSliderChangeListener)
        }
    }

    private val imageSliderChangeListener = object : SliderView.OnSliderPageListener {
        override fun onSliderPageChanged(position: Int) {
            if (position == 1){
                binding?.imageSlider?.stopAutoCycle()
                binding?.imageSlider?.isAutoCycle = false
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.imageSlider?.setCurrentPageListener(null)
        binding = null
    }

    private fun onContinueBtnClickListener(){
        binding?.mbContinue?.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
        }
    }


}