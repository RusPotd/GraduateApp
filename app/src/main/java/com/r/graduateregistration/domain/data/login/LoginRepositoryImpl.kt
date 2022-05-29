package com.r.graduateregistration.domain.data.login

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.r.graduateregistration.domain.util.PhoneCallbacksListener
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepositoryImpl
@Inject
constructor(
    private val auth: FirebaseAuth
) : LoginRepository {

    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    lateinit var verificationID: String

    private lateinit var phoneCallbacksListener: PhoneCallbacksListener

    override fun setPhoneCallbacksListener(listener: PhoneCallbacksListener) {
        this.phoneCallbacksListener = listener

    }


    private val callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                val otpCode = credential.smsCode

                if (!otpCode.isNullOrEmpty()) {
                    phoneCallbacksListener.onVerificationCodeDetected(otpCode = otpCode)
                }

            }

            override fun onVerificationFailed(firebaseException: FirebaseException) {

                when (firebaseException) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        phoneCallbacksListener.onVerificationFailed("Invalid Phone Number")
                    }

                    is FirebaseTooManyRequestsException -> {
                        phoneCallbacksListener.onVerificationFailed("Too many attempts. Retry later.")
                    }

                    is FirebaseNetworkException -> {

                        val errorText = removeSurroundingString(firebaseException.message)
                        phoneCallbacksListener.onVerificationFailed(errorText)


                    }

                    else -> {
                        phoneCallbacksListener.onVerificationFailed(firebaseException.message ?: "")

                    }
                }
            }

            override fun onCodeSent(
                verificationId: String,
                _resendToken: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, _resendToken)
                verificationID = verificationId
                resendToken = _resendToken

                phoneCallbacksListener.onCodeSent(verificationId, resendToken)
            }

            override fun onCodeAutoRetrievalTimeOut(p0: String) {
                super.onCodeAutoRetrievalTimeOut(p0)
            }


        }


    override suspend fun sendOtpToPhone(phoneNumber: String, activity: Activity) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun verifyOtpCode(otpCode: String) {
        signInWithPhoneAuthCredential(PhoneAuthProvider.getCredential(verificationID, otpCode))
    }

    override fun logOut() {
        auth.signOut()
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                phoneCallbacksListener.onOtpVerifyCompleted()
            } else {
                when (it.exception) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        phoneCallbacksListener.onOtpVerifyFailed("Wrong Otp")
                    }
                }
            }
        }
    }

    override suspend fun resendOtpCode(phoneNumber: String, activity: Activity) {
        val resendCodeOptionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
        resendCodeOptionsBuilder.setForceResendingToken(resendToken)
        PhoneAuthProvider.verifyPhoneNumber(resendCodeOptionsBuilder.build())
    }

    override suspend fun isUserVerified(): Boolean = auth.currentUser != null

    override suspend fun setVerificationId(verificationId: String) {
        verificationID = verificationId
    }


    fun removeSurroundingString(str: String?): String {
        var start: Int? = null
        var end: Int? = null


        str!!.forEachIndexed { index, c ->
            if (c.toString() == "(") {
                start = index - 1
            }
            if (c.toString() == ")") {
                end = index + 1
            }
        }

        return if (start != null && end != null) {
            str.removeRange(start!!, end!!)
        } else {
            str
        }
    }

}