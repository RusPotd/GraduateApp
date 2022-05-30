package com.r.graduateregistration.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        when(event) {
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
        }
    }

    private fun setUiEvent(event: MainUiEvents) {
        viewModelScope.launch {
            _eventFlow.emit(event)
        }
    }

    fun updateUserData(userDetails:UserDetails) {
        userDataRepo.updateUserData(userDetails)
    }

}