package com.r.graduateregistration.presentation.login

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.r.graduateregistration.R
import com.r.graduateregistration.databinding.FragmentRegisterBinding
import com.r.graduateregistration.domain.data.general.LocalData.Companion.universityList
import com.r.graduateregistration.presentation.login.util.AuthEvents
import com.r.graduateregistration.presentation.main.MainActivity
import kotlinx.coroutines.flow.collectLatest
import javax.xml.transform.ErrorListener
import javax.xml.transform.TransformerException


class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    private lateinit var dialog: Dialog

    var universityIndex: Int = 0

    var univ : String = "Dr. Babasaheb Ambedkar Marathwada University, Aurangabad"

    private var isDialogShow : Boolean = true
    var id = "";

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.progress_dialog)
        builder.setCancelable(false)
        dialog = builder.create()

        binding.txtLogin.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_registerFragment_to_loginFragment)
        }

        val district = arrayListOf("Select District", "Aurangabad", "Jalna", "Beed", "Osmanabad")




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

        lifecycleScope.launchWhenStarted {
            authViewModel.authEventFlow.collectLatest { authEvents ->
                when (authEvents) {
                    AuthEvents.OnOtpSendUi -> {
                        binding.layoutInfo.visibility = View.GONE
                        binding.btnNext.visibility = View.GONE
                        binding.btnVerify.visibility = View.VISIBLE
                        binding.layoutOtp.visibility = View.VISIBLE
                        binding.txtResendCode.visibility = View.VISIBLE
                        dialog.setCancelable(true)

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

        /*lifecycleScope.launchWhenStarted {
            authViewModel.loginLoading.collectLatest { loading ->
                if (loading) {
                    showProgressBar()
                } else {
                    hideProgressBar()
                }

            }
        }*/

        lifecycleScope.launchWhenStarted {
            authViewModel.talukaList.collectLatest { talukaList ->
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    talukaList
                ).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerTaluka.adapter = adapter
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
            val dist = binding.txtDistrict.text.toString()
            if (dist.isEmpty() || dist == "Select District") {
                showSnackBar("Select District!")
            } else {
                binding.spinnerTaluka.performClick()

            }
        }

        binding.spinnerTaluka.setOnTouchListener { v, event ->
            val dist = binding.txtDistrict.text.toString()
            if ( dist.isEmpty() || dist == "Select District") {
                showSnackBar("Select District!")
            } else {
                binding.spinnerTaluka.performClick()
            }
            true
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
                univ = universityList[position+1].university
                binding.txtUniversity.text = univ


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
                binding.txtDistrict.text = district[position]
                authViewModel.updateTalukaList(district[position])

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
                binding.txtTaluka.text = authViewModel.talukaList.value[position]

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

            if (university.isEmpty()) {
                showSnackBar("Select University")
            } else if (name == null || name.isEmpty()) {
                showSnackBar("Enter Name!")
            } else if (mobile == null || mobile.isEmpty()) {
                showSnackBar("Enter Mobile")
            } else if (dist.toString() == "Select District" || dist.toString().isEmpty()) {
                showSnackBar("Select District!")
            } else if (tal.toString() == "Select Taluka" || tal.toString().isEmpty()) {
                showSnackBar("Select Taluka!")
            } else {
                authViewModel.setUsernameText(binding.etFullName.text.toString())
                authViewModel.setPhoneNumberText(binding.etMobileNum.text.toString())
                authViewModel.setUniversity(univ)
                authViewModel.setDistrict(binding.txtDistrict.text.toString())
                authViewModel.setTaluka(binding.txtTaluka.text.toString())
                authViewModel.onEvent(AuthEvents.GetOtpButtonClick(requireActivity()))
            }
        }

        binding.etOtp.afterTextChanged {
            authViewModel.setOtpText(it.trim())
            showSnackBar(it)
        }

        binding.btnVerify.setOnClickListener {
            uploaddatatodb(binding)
            authViewModel.setOriginId(id)
            authViewModel.onEvent(AuthEvents.RegisterAccountClick)
        }

        binding.txtResendCode.setOnClickListener {
            if (authViewModel.countDownTime.value == "Resend Code") {
                authViewModel.onEvent(AuthEvents.OnResendOtpClick(requireActivity()))
            }
        }

        return binding.root
    }

    private fun uploaddatatodb(binding: FragmentRegisterBinding) {
        var url = "https://padvidhar.com/add-karyakarta";

        val request: StringRequest =
            object : StringRequest(
                Request.Method.POST, url,
                Response.Listener<String?> { response ->

                    id = response.toString()
                    findNavController().navigate(R.id.mainFragment)

                }, object : ErrorListener, Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError) {
                        Toast.makeText(
                            requireActivity(),
                            error.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }

                    override fun warning(p0: TransformerException?) {
                        TODO("Not yet implemented")
                    }

                    override fun error(p0: TransformerException?) {
                        TODO("Not yet implemented")
                    }

                    override fun fatalError(p0: TransformerException?) {
                        TODO("Not yet implemented")
                    }
                }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): MutableMap<String, String>? {
                    val map: MutableMap<String, String> = HashMap()

                    map["university"] = univ
                    map["name"] = binding.etFullName.text.toString()
                    map["mobile"] = binding.etMobileNum.text.toString()
                    map["district"] = binding.txtDistrict.text.toString()
                    map["taluka"] = binding.txtTaluka.text.toString()

                    return map
                }
            }
        val queue = Volley.newRequestQueue(getActivity())
        queue.add(request)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    private fun showProgressBar() {
        if (isDialogShow) {
            dialog.show()
            isDialogShow = false
        }

    }

    private fun hideProgressBar() {
        isDialogShow = false
        dialog.hide()
    }


    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }
        })
    }

}