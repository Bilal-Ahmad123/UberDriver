package com.example.uberdriver.presentation.auth.register.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.uberdriver.data.remote.api.backend.driver.vehicle.model.request.CreateVehicleRequest
import com.example.uberdriver.databinding.FragmentVehicleRegisterBinding
import com.example.uberdriver.presentation.auth.inputlayoutadapter.TextInputLayoutAdapter
import com.example.uberdriver.presentation.auth.register.viewmodels.RegisterViewModel
import com.example.uberdriver.presentation.auth.register.viewmodels.VehicleViewModel
import com.example.uberdriver.presentation.driver.MainActivity
import com.google.android.material.textfield.TextInputLayout
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.Validator.ValidationListener
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

@AndroidEntryPoint
class VehicleRegisterFragment : Fragment(), ValidationListener {

    private var validator: Validator? = null
    private val vehicleViewModel: VehicleViewModel by viewModels()
    private val registerViewModel : RegisterViewModel by activityViewModels()
    private var binding: FragmentVehicleRegisterBinding? = null

    @NotEmpty(message = "Vehicle Type is required")
    private var tiVehicleType: TextInputLayout? = null

    @NotEmpty(message = "Vehicle Model is required")
    private var tiVehicleModel: TextInputLayout? = null

    @NotEmpty(message = "Vehicle Number is required")
    private var tiVehicleNumber: TextInputLayout? = null

    @NotEmpty(message = "Vehicle Color is required")
    private var tiVehicleColor: TextInputLayout? = null

    @NotEmpty(message = "Vehicle Year is required")
    private var tiVehicleYear: TextInputLayout? = null

    @NotEmpty(message = "Vehicle Make is required")
    private var tiVehicleMake: TextInputLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVehicleRegisterBinding.inflate(inflater, container, false)
        return binding?.root
    }

    private fun initializeTextInputs(){
        tiVehicleType = binding?.tiVehicleType
        tiVehicleModel = binding?.tiVehicleModel
        tiVehicleNumber = binding?.tiVehicleNumber
        tiVehicleColor = binding?.tiVehicleColor
        tiVehicleYear = binding?.tiVehicleYear
        tiVehicleMake = binding?.tiVehicleMake

    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeTextChangeListeners()
        binding = null
        validator = null
        tiVehicleType = null
        tiVehicleModel = null
        tiVehicleNumber = null
        tiVehicleColor = null
        tiVehicleYear = null
        tiVehicleMake = null
    }

    override fun onValidationSucceeded() {
        showProgressCycle()
        createVehicle()
    }

    override fun onValidationFailed(errors: MutableList<ValidationError>?) {
        for (error in errors!!) {
            val view = error.view
            val message = error.getCollatedErrorMessage(requireContext())
            if (view is TextInputLayout) {
                (view as TextInputLayout).error = message
            } else {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeTextInputs()
        setUpValidator()
        addTextChangeListeners()
        confirmBtnClickListener()
        observeCreateVehicleResponse()
    }

    private fun setUpValidator() {
        validator = Validator(this)
        validator?.registerAdapter(TextInputLayout::class.java, TextInputLayoutAdapter())
        validator?.setValidationListener(this)
    }

    private fun addTextChangeListeners() {
        binding?.tiVehicleType?.editText?.addTextChangedListener(textWatcher)
        binding?.tiVehicleModel?.editText?.addTextChangedListener(textWatcher)
        binding?.tiVehicleNumber?.editText?.addTextChangedListener(textWatcher)
        binding?.tiVehicleColor?.editText?.addTextChangedListener(textWatcher)
        binding?.tiVehicleYear?.editText?.addTextChangedListener(textWatcher)
        binding?.tiVehicleMake?.editText?.addTextChangedListener(textWatcher)
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            (view?.findFocus() as? TextInputLayout)?.error = null
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private fun removeTextChangeListeners() {
        binding?.tiVehicleType?.editText?.removeTextChangedListener(textWatcher)
        binding?.tiVehicleModel?.editText?.removeTextChangedListener(textWatcher)
        binding?.tiVehicleNumber?.editText?.removeTextChangedListener(textWatcher)
        binding?.tiVehicleColor?.editText?.removeTextChangedListener(textWatcher)
        binding?.tiVehicleYear?.editText?.removeTextChangedListener(textWatcher)
        binding?.tiVehicleMake?.editText?.removeTextChangedListener(textWatcher)
    }

    private fun createVehicle() {
        vehicleViewModel.createVehicle(createVehicleRequestObject())
    }

    private fun createVehicleRequestObject(): CreateVehicleRequest {
        Log.d("TAG", registerViewModel.user.value?.data?.driverId.toString())
        return CreateVehicleRequest(
            registerViewModel.user?.value?.data?.driverId!!,
            binding?.tiVehicleType?.editText?.text.toString(),
            binding?.tiVehicleModel?.editText?.text.toString(),
            binding?.tiVehicleNumber?.editText?.text.toString(),
            binding?.tiVehicleColor?.editText?.text.toString(),
            binding?.tiVehicleYear?.editText?.text.toString(),
            binding?.tiVehicleMake?.editText?.text.toString()
        )
    }

    private fun confirmBtnClickListener() {
        binding?.mbContinue?.setOnClickListener {
            validator?.validate()
        }
    }

    private fun showProgressCycle() {
        binding?.progressBarCyclic?.visibility = View.VISIBLE
        binding?.llVehicleDetails?.visibility = View.GONE
    }

    private fun hideProgressCircle() {
        binding?.progressBarCyclic?.visibility = View.GONE
    }

    private fun observeCreateVehicleResponse() {
        viewLifecycleOwner.lifecycleScope.launch {
            vehicleViewModel.apply {
                createVehicle.collectLatest {
                    if (it?.data != null) {
                        hideProgressCircle()
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                }
            }
        }
    }

}