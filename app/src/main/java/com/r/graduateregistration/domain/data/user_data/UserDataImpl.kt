package com.r.graduateregistration.domain.data.user_data

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.r.graduateregistration.domain.models.UserDetails
import com.r.graduateregistration.domain.util.getAwaitResult
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class UserDataImpl
@Inject
constructor(
    private val firestore: FirebaseFirestore,
    @Named("details") private val userRef: CollectionReference,
    @Named("withId") private val userRefWithId: CollectionReference,
    private val userMapper: UserMapper
) : UserData {

    override suspend fun isMobileNumAlreadyRegister(phoneNumber: String): Boolean {
        try {
            val user = firestore.document("user_details/$phoneNumber")
                .getAwaitResult(userMapper::toUserDetails)
            return !user.userId.isEmpty()

        } catch (e: Exception) {
            return false
        }
    }

    override fun addUserData(userDetails: UserDetails) {
        userRefWithId.document(userDetails.userId).set(userDetails)
        userRef.document(userDetails.mobileNumber).set(userDetails)
    }


    override suspend fun getUserData(userId: String): UserDetails {
        return firestore.document("user_details_with_id/$userId")
            .getAwaitResult(userMapper::toUserDetails)
    }

}

class UserMapper @Inject constructor() {
    fun toUserDetails(userDetails: UserDetails): UserDetails {
        return UserDetails(
            userId = userDetails.userId,
            fullName = userDetails.fullName,
            taluka = userDetails.taluka,
            profileImg = userDetails.profileImg,
            mobileNumber = userDetails.mobileNumber,
            email = userDetails.email,
            dist = userDetails.dist,
            city = userDetails.city,
        )
    }
}