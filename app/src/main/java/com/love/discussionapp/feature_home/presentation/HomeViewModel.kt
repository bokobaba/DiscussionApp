package com.love.discussionapp.feature_home.presentation

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.love.discussionapp.GetCommunitiesQuery
import com.love.discussionapp.core.auth.IAuth
import com.love.discussionapp.core.data.repository.ApiRepository
import com.love.discussionapp.core.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val _auth: IAuth,
    private val _state: IHomeState,
) : ViewModel() {
    private val _eventFlow = MutableSharedFlow<UiEvent>()

    val eventFlow: MutableSharedFlow<UiEvent>
        get() = _eventFlow

    val auth: IAuth
        get() = _auth

    val state: IHomeViewState
        get() = _state

    init {
        Log.d("HomeViewModel", "init")
        getCommunities()
    }

    private fun getCommunities() {
        if (auth.accessToken == null) {
            auth.getAccessToken {
                getCommunitiesFromApi()
            }
        } else {
            getCommunitiesFromApi()
        }
    }

    private fun getCommunitiesFromApi() {
        viewModelScope.launch {
            when(val resource: Resource<GetCommunitiesQuery.Data?> =
                apiRepository.getCommunities()) {
                is Resource.Success -> {
                    val data: GetCommunitiesQuery.Data? = resource.data
                    Log.d("HomeViewModel", "data: ${Gson().toJson(data)}")
                    parseData(resource.data)
                }
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.FetchDataError(resource.message))
                }
                else -> {}
            }

        }
    }

    private fun parseData(data: GetCommunitiesQuery.Data?) {
        val communities = data?.communities?.edges

        communities?.forEach { edge ->
            _state.communities.add(edge.node)
        }
    }

    sealed class UiEvent {
        data class FetchDataError(val message: String): UiEvent()
    }
}