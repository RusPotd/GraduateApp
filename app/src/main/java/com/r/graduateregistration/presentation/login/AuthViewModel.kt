package com.r.graduateregistration.presentation.login

import android.app.Activity
import android.os.CountDownTimer
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthProvider
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

    private val otpNum = MutableStateFlow("")

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
            AuthEvents.RegisterAccountClick -> {
                if (otpNum.value.isEmpty() || otpNum.value.length < 6) {
                    setUiEvent(AuthEvents.ShowSnackBar("Enter Valid OTP"))
                } else {
                    verifyOtpCode(otpNum.value)
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
            _loginLoading.emit(true)
            repository.sendOtpToPhone(phoneNo, activity)
        }

    }

    private suspend fun resendOtp(phoneNo: String, activity: Activity) {
        _loginLoading.emit(true)
        repository.resendOtpCode(phoneNo, activity)
    }

    private fun verifyOtpCode(otpCode: String) {
        viewModelScope.launch {
            _loginLoading.emit(true)

        }
        repository.verifyOtpCode(otpCode = otpCode)
    }

    override fun onOtpVerifyCompleted() {
        val currentFirebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        if (newUserRegistration.value && currentFirebaseUser != null) {

            val userDetails = UserDetails(
                userId = currentFirebaseUser.uid,
                fullName = username.value,
                mobileNumber = phoneNumber.value,
            )
            addUserToFirestore(userDetails)
            setUiEvent(AuthEvents.UserLoggedIn)

        } else if (!newUserRegistration.value) {
            setUiEvent(AuthEvents.UserLoggedIn)
        } else {
            setUiEvent(AuthEvents.ShowSnackBar("Unknown error try letter."))
            repository.logOut()
        }
        viewModelScope.launch {
            _loginLoading.emit(false)
        }
    }

    override fun onOtpVerifyFailed(message: String) {
        viewModelScope.launch {
            _loginLoading.emit(false)
        }
        setUiEvent(AuthEvents.ShowSnackBar(message))
    }

    override fun onVerificationCodeDetected(otpCode: String) {
        setOtpText(otpCode)
    }

    override fun onVerificationFailed(message: String) {
        viewModelScope.launch {
            _loginLoading.emit(false)
        }
        setUiEvent(AuthEvents.ShowSnackBar(message))
    }

    override fun onCodeSent(
        verificationId: String?,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        viewModelScope.launch {
            _loginLoading.emit(false)
        }
        startCountDown()
        setUiEvent(AuthEvents.OnOtpSendUi)

    }

    private fun addUserToFirestore(userDetails: UserDetails) {
        userDataRepo.addUserData(userDetails)
    }


}