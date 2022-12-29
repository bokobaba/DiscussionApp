package com.love.discussionapp.feature_main.presentation

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.love.discussionapp.core.auth.IAuth
import com.love.discussionapp.core.data.repository.ApiRepository
import com.love.discussionapp.core.util.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val _auth: IAuth,
    private val _repository: ApiRepository,
) : ViewModel() {
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    private val _profileImage = mutableStateOf("")
    private val _profileName = mutableStateOf("")

    val auth: IAuth
        get() = _auth

    val eventFlow: MutableSharedFlow<UiEvent>
        get() = _eventFlow
    val profileImage: State<String>
        get() = _profileImage
    val profileName: State<String>
        get() = _profileName

    init {
        if (_auth.user != null) {
            _profileName.value = _auth.user!!.displayName ?: ""
        }
        Log.d(this.TAG(), "displayName = ${_auth.user?.displayName}")
        if (_auth.user != null && (_auth.user?.displayName == null || _auth.user?.displayName == "")) {
            Log.d(this.TAG(), "no displayName found for user. fetching from server")

            viewModelScope.launch {
                val username: String? = getUsername(_auth.user!!.uid)
                if (username != null) {
                    _auth.updateDisplayName(
                        name = username,
                        onSuccess = { _profileName.value = username },
                        onFail = {},
                    )
                } else {
                    _profileName.value = _auth.user!!.email!!
                }
            }
        }

        _profileImage.value = _auth.user?.photoUrl?.toString() ?: ""
    }

    private suspend fun getUsername(id: String): String? {
        val resource = _repository.getUserById(id)
        Log.d(this.TAG(), "username = [${resource.data?.user?.get(0)?.username}]")
        return resource.data?.user?.get(0)?.username
    }

    fun onEvent(event: MainScreenEvent) {
        when (event) {
            is MainScreenEvent.Login -> viewModelScope.launch {
                Log.d("MainViewModel", "emit login")
                _eventFlow.emit(UiEvent.Login)
            }
            is MainScreenEvent.Logout -> viewModelScope.launch {
                _eventFlow.emit(UiEvent.Logout)
            }
            is MainScreenEvent.SaveProfileImage -> viewModelScope.launch {
                _eventFlow.emit(UiEvent.SaveProfileImage(event.url))
            }
            is MainScreenEvent.DismissPopup -> viewModelScope.launch {
                _eventFlow.emit(UiEvent.DismissPopup)
            }
            is MainScreenEvent.ShowPopup -> viewModelScope.launch {
                _eventFlow.emit(UiEvent.ShowPopup)
            }
        }
    }

    fun updateProfileImage(url: String) {
        _profileImage.value = url
    }

    sealed class UiEvent {
        object Login : UiEvent()
        object Logout : UiEvent()
        object ShowPopup : UiEvent()
        object DismissPopup : UiEvent()
        data class SaveProfileImage(val url: String) : UiEvent()
    }
}