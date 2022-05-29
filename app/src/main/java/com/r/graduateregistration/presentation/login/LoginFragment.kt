package com.r.graduateregistration.presentation.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.r.graduateregistration.R
import com.r.graduateregistration.databinding.FragmentLoginBinding
import com.r.graduateregistration.presentation.main.MainActivity


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtRegister.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.action_loginFragment_to_registerFragment)

        }

        binding.btnLogin.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}