package com.love.discussionapp.feature_home.presentation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.love.discussionapp.GetCommunitiesQuery
import com.love.discussionapp.data.PreviewData
import javax.inject.Inject

interface IHomeViewState {
    val communities: SnapshotStateList<GetCommunitiesQuery.Node>
}

interface IHomeState: IHomeViewState {
    fun addCommunity(community: GetCommunitiesQuery.Node)
}

class HomeState @Inject constructor() : IHomeState {
    private val _communities = mutableStateListOf<GetCommunitiesQuery.Node>()

    override val communities: SnapshotStateList<GetCommunitiesQuery.Node>
        get() = _communities

    override fun addCommunity(community: GetCommunitiesQuery.Node) {
        _communities.add(community)
    }

    companion object {
        val previewState = object: IHomeViewState {
            override val communities: SnapshotStateList<GetCommunitiesQuery.Node>
                get() = PreviewData.HomeScreenData.communities.toMutableStateList()

        }
    }
}