package com.r.graduateregistration.presentation.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.r.graduateregistration.R
import com.r.graduateregistration.databinding.FragmentMainBinding
import com.r.graduateregistration.presentation.login.WelcomeActivity
import com.r.graduateregistration.presentation.main.util.MainUiEvents
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editProfile.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_editProfileFragment)
        }

        lifecycleScope.launch {

            mainViewModel.eventFlow.collect { authEvents ->
                when (authEvents) {
                    MainUiEvents.OnWelcome -> {
                        val openWelcomeActivity = Intent(requireActivity(), WelcomeActivity::class.java)
                        startActivity(openWelcomeActivity)
                        requireActivity().finish()
                    }

                    else -> Unit
                }
            }

        }

        binding.txtLogout.setOnClickListener {
            mainViewModel.onEvent(MainUiEvents.OnWelcome)
        }

        binding.addNewGraduate.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_graduateRegistrationFragment)
        }

        binding.showAllGraduate.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_allGraduatesFragment)
        }

    }

}