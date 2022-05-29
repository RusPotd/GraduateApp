package com.r.graduateregistration.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.r.graduateregistration.presentation.login.util.AuthEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel
@Inject constructor(

) : ViewModel() {

    private val _authEventFlow = MutableSharedFlow<AuthEvents>()
    val authEventFlow = _authEventFlow.asSharedFlow()

    fun onEvent(event: AuthEvents) {
        when(event) {
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
            }
        }
    }

}