package com.r.graduateregistration.presentation.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import com.r.graduateregistration.R
import com.r.graduateregistration.databinding.FragmentRegisterBinding
import com.r.graduateregistration.presentation.login.util.AuthEvents
import com.r.graduateregistration.presentation.main.MainActivity
import kotlinx.coroutines.flow.collectLatest


class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtLogin.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_registerFragment_to_loginFragment)
        }

        lifecycleScope.launchWhenStarted {
            authViewModel.authEventFlow.collectLatest { authEvents ->
                when (authEvents) {
                    AuthEvents.OnOtpSendUi -> {
                        binding.txtResendCode.visibility = View.VISIBLE
                        binding.btnGetOtp.visibility = View.INVISIBLE
                        binding.btnRegisterAccount.visibility = View.VISIBLE
                    }
                    AuthEvents.UserLoggedIn -> {
                        val openMainActivity = Intent(requireActivity(), MainActivity::class.java)
                        startActivity(openMainActivity)
                        requireActivity().finish()
                    }
                    is AuthEvents.ShowSnackBar -> {
                        showSnackBar(authEvents.uiText)
                    }
                    else -> Unit
                }

            }


        }
        lifecycleScope.launchWhenStarted {
            authViewModel.countDownTime.collectLatest { countDown ->
                binding.txtResendCode.text = countDown
            }
        }

        binding.btnGetOtp.setOnClickListener {
            authViewModel.setUsernameText(binding.etFullName.text.toString())
            authViewModel.setPhoneNumberText(binding.etMobileNum.text.toString())
            authViewModel.onEvent(AuthEvents.GetOtpButtonClick(requireActivity()))

        }

        binding.btnRegisterAccount.setOnClickListener {
            authViewModel.setOtpText(binding.etOtp.text.toString())
            authViewModel.onEvent(AuthEvents.RegisterAccountClick)
        }



        binding.txtResendCode.setOnClickListener {
            if (authViewModel.countDownTime.value == "Resend Code") {
                authViewModel.onEvent(AuthEvents.OnResendOtpClick(requireActivity()))
            }
        }

    }


    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}