package com.r.graduateregistration.presentation.graduate_register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.r.graduateregistration.R
import com.r.graduateregistration.databinding.FragmentGraduateRegistrationBinding
import com.r.graduateregistration.databinding.FragmentGraduteDetailsBinding


class GraduteDetailsFragment : Fragment() {
    private var _binding: FragmentGraduteDetailsBinding? = null
    private val binding get() = _binding!!

    val args: GraduteDetailsFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGraduteDetailsBinding.inflate(inflater, container, false)

        val graduateData = args.graduteData

        binding.backArrow.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.apply {
            etFullName.text = graduateData.name
            etMobileNum.text = graduateData.mobile
            etGender.text = graduateData.gender
            etDistrict.text = graduateData.district
            if (graduateData.taluka != null) {
                etTaluka.text = graduateData.taluka.toString()
            }

        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


}