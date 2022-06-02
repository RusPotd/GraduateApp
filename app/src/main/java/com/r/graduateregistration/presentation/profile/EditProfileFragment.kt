package com.r.graduateregistration.presentation.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.r.graduateregistration.databinding.FragmentEditProfileBinding
import com.r.graduateregistration.domain.models.UserDetails
import com.r.graduateregistration.presentation.main.MainViewModel
import com.r.graduateregistration.presentation.main.util.MainUiEvents
import kotlinx.coroutines.launch


class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        binding.backArrow.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launch {
            val userDetail = mainViewModel.getUserDetails()
            if (userDetail != null) {
                binding.etFullName.setText(userDetail.fullName)
                binding.etMobileNum.setText(userDetail.mobileNumber)
                binding.etEmailId.setText(userDetail.email)
                binding.etTaluka.setText(userDetail.taluka)
                binding.etUniversity.setText(userDetail.universityName)
                binding.etDistrict.setText(userDetail.district)
            }


        }

        binding.btnSaveChanges.setOnClickListener {
            val fullName = binding.etFullName.text
            val mobNum = binding.etMobileNum.text
            val email = binding.etEmailId.text
            val taluka = binding.etTaluka.text
            val district = binding.etDistrict.text
            val university = binding.etUniversity.text

            val userDetails = UserDetails(
                userId = mainViewModel.getUserId(),
                fullName = fullName.toString(),
                mobileNumber = mobNum.toString(),
                taluka = taluka.toString(),
                district = district.toString(),
                profileImg = "",
                email = email.toString(),
                universityName = university.toString()
            )
            mainViewModel.onEvent(MainUiEvents.UpdateUser(userDetails))

        }

    }


}