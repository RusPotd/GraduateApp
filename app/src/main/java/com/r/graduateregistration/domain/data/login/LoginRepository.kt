package com.r.graduateregistration.domain.data.login

import android.app.Activity
import com.r.graduateregistration.domain.util.PhoneCallbacksListener

interface LoginRepository {
    fun setPhoneCallbacksListener(listener : PhoneCallbacksListener)

    suspend fun sendOtpToPhone(phoneNumber: String, activity: Activity)

    suspend fun resendOtpCode(phoneNumber: String, activity: Activity)

    suspend fun isUserVerified(): Boolean

    suspend fun setVerificationId(verificationId: String)

    fun verifyOtpCode(otpCode: String)

    fun logOut()

}