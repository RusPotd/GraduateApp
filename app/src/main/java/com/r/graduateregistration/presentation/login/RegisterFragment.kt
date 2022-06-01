package com.r.graduateregistration.presentation.login

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import com.r.graduateregistration.R
import com.r.graduateregistration.databinding.FragmentRegisterBinding
import com.r.graduateregistration.domain.data.general.LocalData.Companion.address
import com.r.graduateregistration.domain.data.general.LocalData.Companion.universityList
import com.r.graduateregistration.presentation.login.util.AuthEvents
import com.r.graduateregistration.presentation.main.MainActivity
import kotlinx.coroutines.flow.collectLatest


class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    lateinit var dialog: Dialog

    var universityIndex: Int? = null
    var districtIndex: Int? = null
    var districtStr: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.progress_dialog)
        builder.setCancelable(false)
        dialog = builder.create()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtLogin.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_registerFragment_to_loginFragment)
        }

        val district =
            arrayListOf<String>("Select District", "Aurangabad", "Jalna", "Beed", "Osmanabad")
        val taluka = arrayListOf<String>()




        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.University,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerUniversity.adapter = adapter
        }

        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            district
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerDistrict.adapter = adapter
        }

        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            taluka
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerTaluka.adapter = adapter
        }





        lifecycleScope.launchWhenStarted {
            authViewModel.authEventFlow.collectLatest { authEvents ->
                when (authEvents) {
                    AuthEvents.OnOtpSendUi -> {
                        binding.layoutInfo.visibility = View.GONE
                        binding.btnNext.visibility = View.GONE
                        binding.btnVerify.visibility = View.VISIBLE
                        binding.layoutOtp.visibility = View.VISIBLE

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
            authViewModel.setUsernameText(binding.etFullName.text.toString())
            authViewModel.setPhoneNumberText(binding.etMobileNum.text.toString())
            authViewModel.onEvent(AuthEvents.GetOtpButtonClick(requireActivity()))

        }

        binding.etUniversity.setOnClickListener {
            binding.spinnerUniversity.performClick()
        }
        binding.etDistrict.setOnClickListener {
            binding.spinnerDistrict.performClick()

        }
        binding.etTaluka.setOnClickListener {
            if (districtIndex == null) {
                showSnackBar("Select District!")
            } else {
                binding.spinnerTaluka.performClick()

            }
        }


        binding.spinnerUniversity.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                universityIndex = position
                binding.txtUniversity.text = universityList[position + 1].university


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        binding.spinnerDistrict.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                districtIndex = position
                binding.txtDistrict.text = district[position]
                taluka.removeAll(taluka.toSet())
                taluka.addAll(address.filter { it.district == district[districtIndex!!] }
                    .map { it.taluka })
                districtStr = district[position]
                showSnackBar(district[position])


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spinnerTaluka.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                val a = address.filter { it.taluka == districtStr }.map { it.taluka }
                binding.txtTaluka.setText(a[position])

            }


            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        binding.btnNext.setOnClickListener {

            val university = binding.txtUniversity.text
            val dist = binding.txtDistrict.text
            val tal = binding.txtTaluka.text
            val name = binding.etFullName.text
            val mobile = binding.etMobileNum.text

            if (universityIndex == null || university.isEmpty()) {
                showSnackBar("Select University")
            } else if (name == null || name.isEmpty()) {
                showSnackBar("Enter Name")
            } else if (mobile == null || mobile.isEmpty()) {
                showSnackBar("Enter Mobile")
            } else if (dist.toString() == "Select District" || dist.toString().isEmpty()) {
                showSnackBar("Select District")
            } else {
                authViewModel.setUsernameText(binding.etFullName.text.toString())
                authViewModel.setPhoneNumberText(binding.etMobileNum.text.toString())
                authViewModel.onEvent(AuthEvents.GetOtpButtonClick(requireActivity()))
            }

        }

        binding.btnVerify.setOnClickListener {
            authViewModel.setOtpText(binding.etOtp.text.toString())
            authViewModel.onEvent(AuthEvents.RegisterAccountClick)
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