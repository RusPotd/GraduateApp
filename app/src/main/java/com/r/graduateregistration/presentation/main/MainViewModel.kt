package com.r.graduateregistration.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.r.graduateregistration.domain.data.login.LoginRepository
import com.r.graduateregistration.domain.data.user_data.UserData
import com.r.graduateregistration.domain.models.UserDetails
import com.r.graduateregistration.presentation.main.util.MainUiEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    private val repository: LoginRepository,
    private val userDataRepo: UserData,
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<MainUiEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            if (repository.isUserVerified()) {
                setUiEvent(MainUiEvents.OnLoggedIn)
            } else {
                setUiEvent(MainUiEvents.OnWelcome)
            }
        }
    }

    fun onEvent(event: MainUiEvents) {
        when (event) {
            MainUiEvents.OnLoggedIn -> {
                viewModelScope.launch {
                    _eventFlow.emit(
                        MainUiEvents.OnLoggedIn
                    )
                }
            }
            MainUiEvents.OnWelcome -> {
                viewModelScope.launch {
                    _eventFlow.emit(
                        MainUiEvents.OnWelcome
                    )
                    repository.logOut()
                }
            }
            is MainUiEvents.UpdateUser -> {
                updateUser(event.userDetails)
            }
        }
    }

    private fun setUiEvent(event: MainUiEvents) {
        viewModelScope.launch {
            _eventFlow.emit(event)
        }
    }

    private fun updateUser(userDetails: UserDetails) {
        userDataRepo.addUserData(userDetails)
    }

    suspend fun getUserDetails() : UserDetails {
        return userDataRepo.getUserData(getUserId())
    }

    fun getUserId(): String {
        val currentFirebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        return currentFirebaseUser?.uid ?: getUserId()
    }

    suspend fun isUserLoggedIn() : Boolean = repository.isUserVerified()

    fun updateUserData(userDetails:UserDetails) {
        userDataRepo.updateUserData(userDetails)
    }

}