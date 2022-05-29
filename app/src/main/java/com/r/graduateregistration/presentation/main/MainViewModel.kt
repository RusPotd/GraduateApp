package com.r.graduateregistration.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.r.graduateregistration.presentation.main.util.MainUiEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(

) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<MainUiEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            _eventFlow.emit(
                MainUiEvents.OnLoggedIn
            )
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
                }
            }
        }
    }

}