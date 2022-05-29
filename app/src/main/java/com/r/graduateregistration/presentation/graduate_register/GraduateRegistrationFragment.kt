package com.r.graduateregistration.presentation.graduate_register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.r.graduateregistration.databinding.FragmentGraduateRegistrationBinding


class GraduateRegistrationFragment : Fragment() {
    private var _binding: FragmentGraduateRegistrationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGraduateRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.uploadDegreeBtn.setOnClickListener {

        }

    }

}