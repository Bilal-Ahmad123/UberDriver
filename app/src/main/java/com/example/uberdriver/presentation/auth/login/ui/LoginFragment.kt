package com.example.uberdriver.presentation.auth.login.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.uberdriver.R
import com.example.uberdriver.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        setUpBottomSheet()
        googleBtnClickListener()
        emailBtnClickListener()
    }

    private fun googleBtnClickListener(){
        _binding?.signInWithGoogle?.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_splashFragment)
        }
    }

    private fun emailBtnClickListener(){

    }

    private fun setUpBottomSheet(){

    }

}