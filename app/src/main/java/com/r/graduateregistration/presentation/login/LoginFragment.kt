package com.r.graduateregistration.presentation.login

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import com.r.graduateregistration.R
import com.r.graduateregistration.databinding.FragmentLoginBinding
import com.r.graduateregistration.presentation.login.util.AuthEvents
import com.r.graduateregistration.presentation.main.MainActivity
import kotlinx.coroutines.flow.collectLatest


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    lateinit var dialog: Dialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.progress_dialog)
        builder.setCancelable(false)
        dialog = builder.create()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtRegister.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_loginFragment_to_registerFragment)

        }

        lifecycleScope.launchWhenStarted {
            authViewModel.authEventFlow.collectLatest { authEvents ->

                when (authEvents) {
                    AuthEvents.OnOtpSendUi -> {
                        binding.btnLogin.visibility = View.VISIBLE
                        binding.txtResendCode.visibility = View.VISIBLE
                        binding.btnGetOtp.visibility = View.INVISIBLE
                    }
                    is AuthEvents.ShowSnackBar -> {
                        showSnackBar(authEvents.uiText)
                    }
                    AuthEvents.UserLoggedIn -> {
                        val openMainActivity = Intent(requireActivity(), MainActivity::class.java)
                        startActivity(openMainActivity)
                        requireActivity().finish()
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

        lifecycleScope.launchWhenStarted {
            authViewModel.loginLoading.collectLatest { loading ->
                if (loading) {
                    showProgressBar()
                } else {
                    hideProgressBar()
                }

            }
        }

        binding.btnGetOtp.setOnClickListener {
            authViewModel.setPhoneNumberText(binding.etMobileNum.text.toString())
            authViewModel.onEvent(AuthEvents.OnLogInGetOtpButtonClick(requireActivity()))
        }

        binding.btnLogin.setOnClickListener {
            authViewModel.setOtpText(binding.etOtp.text.toString())
            authViewModel.onEvent(AuthEvents.LoginAccountClick)

        }

        binding.txtResendCode.setOnClickListener {
            if (authViewModel.countDownTime.value == "Resend Code") {
                authViewModel.onEvent(AuthEvents.OnResendOtpClick(requireActivity()))
            }
        }

    }


    private fun showProgressBar() {
        dialog.show()

    }

    private fun hideProgressBar() {
        dialog.hide()
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}