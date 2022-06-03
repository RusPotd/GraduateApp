package com.r.graduateregistration.presentation.login.util

import android.app.Activity

sealed class AuthEvents{
    object OnRegister: AuthEvents()
    object OnLogin: AuthEvents()
    data class ShowSnackBar(val uiText: String) : AuthEvents()
    data class GetOtpButtonClick(val activity: Activity, val otp: String) : AuthEvents()
    data class OnLogInGetOtpButtonClick(val activity: Activity) : AuthEvents()
    object LoginAccountClick : AuthEvents()
    data class RegisterAccountClick(val otp: String) : AuthEvents()
    data class OnResendOtpClick(val activity: Activity) : AuthEvents()
    object UserLoggedIn : AuthEvents()
    object OnOtpSendUi : AuthEvents()



}
