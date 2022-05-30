package com.r.graduateregistration.domain.data.user_data

import com.google.firebase.firestore.CollectionReference
import com.r.graduateregistration.domain.models.UserDetails
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataImpl
@Inject
constructor(
    private val userRef: CollectionReference
) : UserData {

    override fun isMobileNumAlreadyRegister(phoneNumber: String): Boolean {
        var haveMobileNum = false
        userRef.document(phoneNumber).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val doc = task.result
                if (doc.exists()) {
                    haveMobileNum = true
                }
            }
        }

        return haveMobileNum

    }

    override fun addUserData(userDetails: UserDetails) {
        userRef.document(userDetails.mobileNumber).set(userDetails)
    }


    override fun getUserData(phoneNum: String): UserDetails {
        var userDetails = UserDetails()

        userRef.document(phoneNum).get().addOnSuccessListener { docSnap ->
            userDetails = docSnap.toObject(UserDetails::class.java)!!

        }
        return userDetails
    }

    override fun updateUserData(userDetails: UserDetails){
        userRef.document(userDetails.mobileNumber).set(userDetails)
    }

}