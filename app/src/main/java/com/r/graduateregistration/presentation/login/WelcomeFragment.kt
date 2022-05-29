package com.r.graduateregistration.presentation.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.r.graduateregistration.R
import com.r.graduateregistration.databinding.FragmentWelcomeBinding
import com.r.graduateregistration.presentation.login.util.AuthEvents
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class WelcomeFragment : Fragment() {
    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    private val authViewModel : AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogin.setOnClickListener {
            authViewModel.onEvent(AuthEvents.OnLogin)
        }

        binding.btnRegistration.setOnClickListener {
            authViewModel.onEvent(AuthEvents.OnRegister)
        }

        lifecycleScope.launch {

            authViewModel.authEventFlow.collectLatest { authEvents ->
                when(authEvents) {
                    AuthEvents.OnLogin -> {
                        NavHostFragment.findNavController(this@WelcomeFragment).navigate(R.id.action_welcomeFragment_to_loginFragment)
                    }
                    AuthEvents.OnRegister -> {
                        NavHostFragment.findNavController(this@WelcomeFragment).navigate(R.id.action_welcomeFragment_to_registerFragment)

                    }
                    else -> Unit
                }
            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}