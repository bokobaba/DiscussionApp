package com.love.discussionapp.feature_community.presentation

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.love.discussionapp.GetCommunitiesQuery
import com.love.discussionapp.GetCommunityQuery
import com.love.discussionapp.data.PreviewData
import com.love.discussionapp.feature_community.domain.model.Community
import com.love.discussionapp.feature_community.domain.model.Post
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

interface ICommunityViewState {
    val community: State<Community?>
    val posts: SnapshotStateList<Post>
}

interface ICommunityState : ICommunityViewState{
    fun setCommunityData(data: GetCommunityQuery.Community, userId: String)
    fun setPosts(posts: List<GetCommunityQuery.Post>)
    fun subscribe()
}

class CommunityState @Inject constructor() : ICommunityState {
    private val _posts = mutableStateListOf<Post>()
    private val _community = mutableStateOf<Community?>(null)

    override val posts: SnapshotStateList<Post>
        get() = _posts
    override val community: State<Community?>
        get() = _community

    override fun setCommunityData(data: GetCommunityQuery.Community, userId: String) {
        _community.value = Community(
            name = data.name,
            bannerUrl = data.bannerImage ?: "",
            subscribed = data.subscriberIds.contains(userId),
            subscribers = data.subscriberIds.size
        )
    }

    override fun setPosts(posts: List<GetCommunityQuery.Post>) {
        _posts.clear()
        _posts.addAll(posts.map {
            Post(
                id = UUID.fromString(it.id as String),
                title = it.title,
                timestamp = LocalDateTime.parse((it.timestamp as String).dropLast(1)),
                creatorName = it.creator.username,
                creatorId = it.creator.id,
                likes = it.likeIds.size,
                comments = it.commentIds.size
            )
        })
    }

    override fun subscribe() {
        val subscribed: Boolean = _community.value?.subscribed ?: false
        _community.value = _community.value?.copy(subscribed = !subscribed)
    }

    companion object {
        val previewState = object: ICommunityViewState {
            override val community: State<Community>
                get() = mutableStateOf(PreviewData.CommunityScreenData.community)

            override val posts: SnapshotStateList<Post>
                get() = PreviewData.CommunityScreenData.posts.toMutableStateList()

        }
    }
}