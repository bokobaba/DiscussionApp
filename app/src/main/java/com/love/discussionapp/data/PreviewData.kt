package com.love.discussionapp.data

import com.love.discussionapp.GetCommunitiesQuery
import com.love.discussionapp.GetCommunityQuery
import com.love.discussionapp.feature_community.domain.model.Community
import com.love.discussionapp.feature_community.domain.model.Post
import java.time.LocalDateTime
import java.util.UUID

object PreviewData {
    object CommunityScreenData {
        val posts = (0..10).map {
            Post(
                id = UUID.randomUUID(),
                title = "Post $it",
                creatorName = "Creator $it",
                creatorId = "$it",
                timestamp = LocalDateTime.now(),
                comments = 20,
                likes = 20,
            )
        }

        val community = Community(
            name = "Test Community",
            bannerUrl = "https://s3.amazonaws.com/prod-media.gameinformer.com/styles/full/s3/2019/04/01/4ae1f96b/ffvitop.png",
            subscribers = 20,
            subscribed = true
        )
    }

    object HomeScreenData {
        val communities = (0..10).map {
            GetCommunitiesQuery.Node(
                bannerImage = "https://s3.amazonaws.com/prod-media.gameinformer.com/styles/full/s3/2019/04/01/4ae1f96b/ffvitop.png",
                name = "Community $it"
            )
        }
    }
}