package com.example.uberdriver.presentation.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.uberdriver.R
import com.example.uberdriver.databinding.FragmentRiderNotifiedSheetBinding
import com.example.uberdriver.presentation.driver.map.viewmodel.MapAndCardSharedViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch

class RiderNotifiedSheet : Fragment(R.layout.fragment_rider_notified_sheet) {

    private var bottomSheet: LinearLayout? = null
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    private var binding: FragmentRiderNotifiedSheetBinding? = null
    private val mapAndCardSharedViewModel: MapAndCardSharedViewModel by activityViewModels<MapAndCardSharedViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRiderNotifiedSheetBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBottomSheetStyle()
        observeStartRideBtnClickListener()
    }

    private fun setBottomSheetStyle() {
        bottomSheet = requireActivity().findViewById<LinearLayout>(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet!!)

        bottomSheet?.layoutParams?.height =
            (requireContext().resources.displayMetrics.heightPixels * 0.70).toInt()
        bottomSheetBehavior?.peekHeight =
            (requireContext().resources.displayMetrics.heightPixels * 0.32).toInt()
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior?.isHideable = false
        bottomSheetBehavior?.isDraggable = true
    }

    private fun observeStartRideBtnClickListener() {
        binding?.startRide?.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                mapAndCardSharedViewModel.setStartRideBtnClicked(true)
            }
        }
    }

}