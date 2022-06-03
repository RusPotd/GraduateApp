package com.r.graduateregistration.presentation.login

import android.app.Activity
import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthProvider
import com.r.graduateregistration.domain.data.general.LocalData.Companion.address
import com.r.graduateregistration.domain.data.login.LoginRepository
import com.r.graduateregistration.domain.data.user_data.UserData
import com.r.graduateregistration.domain.models.UserDetails
import com.r.graduateregistration.domain.util.PhoneCallbacksListener
import com.r.graduateregistration.presentation.login.util.AuthEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@HiltViewModel
class AuthViewModel
@Inject constructor(
    private val repository: LoginRepository,
    private val userDataRepo: UserData,
) : ViewModel(), PhoneCallbacksListener {

    private val _authEventFlow = MutableSharedFlow<AuthEvents>()
    val authEventFlow = _authEventFlow.asSharedFlow()

    private val username = MutableStateFlow("")

    private val phoneNumber = MutableStateFlow("")


    private val universityName = MutableStateFlow("")

    private val districtName = MutableStateFlow("")

    private val talukaName = MutableStateFlow("")

    private val otpNum = MutableStateFlow("")

    val talukaList = MutableStateFlow<List<String>>(emptyList())

    private val _loginLoading = MutableSharedFlow<Boolean>()
    val loginLoading = _loginLoading.asSharedFlow()

    val countDownTime = MutableStateFlow("")

    private val newUserRegistration = MutableStateFlow(false)

    private var timer: CountDownTimer? = null

    init {

        repository.setPhoneCallbacksListener(this)

        viewModelScope.launch {
            if (repository.isUserVerified()) {
                setUiEvent(AuthEvents.UserLoggedIn)
            }
        }

    }


    fun onEvent(event: AuthEvents) {
        when (event) {
            AuthEvents.OnLogin -> {
                viewModelScope.launch {
                    _authEventFlow.emit(
                        AuthEvents.OnLogin
                    )
                }
            }
            AuthEvents.OnRegister -> {
                viewModelScope.launch {
                    _authEventFlow.emit(
                        AuthEvents.OnRegister
                    )
                }
                newUserRegistration.value = true
            }
            is AuthEvents.OnLogInGetOtpButtonClick -> {
                viewModelScope.launch {
                    if (phoneNumber.value.isEmpty() || phoneNumber.value.length < 10) {
                        setUiEvent(AuthEvents.ShowSnackBar("Enter Valid Mobile Number"))
                    } else {
                        //check is there is already account
                        if (userDataRepo.isMobileNumAlreadyRegister(phoneNumber.value)) {
                            sendOtp("+91${phoneNumber.value}", event.activity)
                        } else {
                            setUiEvent(AuthEvents.ShowSnackBar("This Number don't have account"))
                        }
                    }
                }
            }
            is AuthEvents.GetOtpButtonClick -> {
                viewModelScope.launch {

                    if (username.value.isEmpty()) {
                        setUiEvent(AuthEvents.ShowSnackBar("Enter Name"))
                    } else if (phoneNumber.value.isEmpty() || phoneNumber.value.length < 10) {
                        setUiEvent(AuthEvents.ShowSnackBar("Enter Valid Mobile Number"))
                    } else {
                        //check is there don't have account
                        if (userDataRepo.isMobileNumAlreadyRegister(phoneNumber.value)) {
                            setUiEvent(AuthEvents.ShowSnackBar("This Number already have account"))
                        } else {
                            sendOtp("+91${phoneNumber.value}", event.activity)
                        }
                    }
                }
            }
            is AuthEvents.OnResendOtpClick -> {
                viewModelScope.launch {
                    resendOtp("+91${phoneNumber.value}", event.activity)
                }
            }
            is AuthEvents.RegisterAccountClick -> {
                if (otpNum.value.isEmpty() || otpNum.value.length < 6) {
                    setUiEvent(AuthEvents.ShowSnackBar("Enter Valid OTP"))
                } else {
                    runBlocking {
                        verifyOtpCode(event.otp.trim())
                    }
                }
            }
            AuthEvents.LoginAccountClick -> {
                if (otpNum.value.isEmpty() || otpNum.value.length < 6) {
                    setUiEvent(AuthEvents.ShowSnackBar("Enter Valid OTP"))
                } else {
                    verifyOtpCode(otpNum.value)
                }
            }
            else -> Unit
        }
    }

    private fun setUiEvent(event: AuthEvents) {
        viewModelScope.launch {
            _authEventFlow.emit(event)
        }
    }

    fun setUsernameText(usernam: String) {
        username.value = usernam
    }

    fun setPhoneNumberText(phoneNo: String) {
        phoneNumber.value = phoneNo
    }

    fun setOtpText(otp: String) {
        otpNum.value = otp
    }

    fun setUniversity(taluka: String) {
        districtName.value = taluka
    }

    fun setDistrict(taluka: String) {
        districtName.value = taluka
    }

    fun setTaluka(taluka: String) {
        talukaName.value = taluka
    }

    private fun startCountDown() {
        val startTime = 60
        timer?.cancel()
        timer = object : CountDownTimer(startTime * 1000.toLong(), 1000) {
            override fun onTick(p0: Long) {
                countDownTime.value = (p0 / 1000).toInt().toString()
            }

            override fun onFinish() {
                countDownTime.value = "Resend Code"
            }

        }
        timer?.start()

    }

    private suspend fun sendOtp(phoneNo: String, activity: Activity) {
        viewModelScope.launch {
            repository.sendOtpToPhone(phoneNo, activity)
            _loginLoading.emit(true)
        }

    }

    private suspend fun resendOtp(phoneNo: String, activity: Activity) {
        repository.resendOtpCode(phoneNo, activity)
        _loginLoading.emit(true)
    }

    private fun verifyOtpCode(otpCode: String) {
        viewModelScope.launch {
            _loginLoading.emit(true)
            repository.verifyOtpCode(otpCode = otpCode)

        }
    }

    override fun onOtpVerifyCompleted() {
        val currentFirebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        if (newUserRegistration.value) {

            val userDetails = currentFirebaseUser?.let {
                UserDetails(
                    userId = it.uid,
                    universityName = universityName.value,
                    fullName = username.value,
                    mobileNumber = phoneNumber.value,
                    district = districtName.value,
                    taluka = talukaName.value,
                )
            }
            userDetails?.let { addUserToFirestore(it) }
            setUiEvent(AuthEvents.UserLoggedIn)
            viewModelScope.launch {
                _loginLoading.emit(false)
            }

        } else if (!newUserRegistration.value) {
            setUiEvent(AuthEvents.UserLoggedIn)
            viewModelScope.launch {
                _loginLoading.emit(false)
            }
        } else {

            viewModelScope.launch {
                _loginLoading.emit(false)
            }
            setUiEvent(AuthEvents.ShowSnackBar("Unknown error try letter."))
            repository.logOut()
        }
    }

    override fun onOtpVerifyFailed(message: String) {
        setUiEvent(AuthEvents.ShowSnackBar(message))
        viewModelScope.launch {
            _loginLoading.emit(false)
        }
    }

    override fun onVerificationCodeDetected(otpCode: String) {
        setOtpText(otpCode)
        viewModelScope.launch {
            _loginLoading.emit(false)
        }
    }

    override fun onVerificationFailed(message: String) {
        setUiEvent(AuthEvents.ShowSnackBar(message))
        viewModelScope.launch {
            _loginLoading.emit(false)
        }
    }

    override fun onCodeSent(
        verificationId: String?,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        startCountDown()
        setUiEvent(AuthEvents.OnOtpSendUi)
        viewModelScope.launch {
            _loginLoading.emit(false)
        }

    }

    private fun addUserToFirestore(userDetails: UserDetails) {
        runCatching {
            userDataRepo.addUserData(userDetails)
        }
    }

    fun updateTalukaList(district: String) {
        val list: List<String> = address.filter { it.district == district }.map { it.taluka }
        viewModelScope.launch {
            talukaList.emit(emptyList())
            talukaList.emit(list)
        }
    }


}