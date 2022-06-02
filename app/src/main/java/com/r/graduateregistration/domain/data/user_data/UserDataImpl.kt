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
        return try {
            val user = firestore.document("user_details/$phoneNumber")
                .getAwaitResult(userMapper::toUserDetails)
            user.userId.isNotEmpty()
        } catch (e: Exception) {
            false
        }

    }

    override fun addUserData(userDetails: UserDetails) {
        userRefWithId.document(userDetails.userId).set(userDetails)
        userRef.document(userDetails.mobileNumber).set(userDetails)
    }

    var count = 5

    override suspend fun getUserData(userId: String): UserDetails? {
        return try {
            firestore.document("user_details_with_id/$userId")
                .getAwaitResult(userMapper::toUserDetails)
        } catch (e: Exception) {
//            if (count > 1) {
//                count--
//                getUserData(userId)
//            } else {
//                null
//            }
            null
        }

    }

    override fun updateUserData(userDetails: UserDetails) {
        TODO("Not yet implemented")
    }

}

class UserMapper @Inject constructor() {
    fun toUserDetails(userDetails: UserDetails): UserDetails {
        return UserDetails(
            userId = userDetails.userId,
            universityName = userDetails.universityName,
            fullName = userDetails.fullName,
            mobileNumber = userDetails.mobileNumber,
            email = userDetails.email,
            district = userDetails.district,
            taluka = userDetails.taluka,
            profileImg = userDetails.profileImg,
            uniqueID = userDetails.uniqueID,
        )
    }

}