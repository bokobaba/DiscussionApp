package com.love.discussionapp.core.data.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.love.discussionapp.*
import com.love.discussionapp.di.Authorization
import com.love.discussionapp.di.NoAuthorization
import java.util.UUID
import javax.inject.Inject


class Api @Inject constructor(
    @Authorization private val apolloClientAuth: ApolloClient,
    @NoAuthorization private val apolloClient: ApolloClient,
) {

    suspend fun getCommunities(): ApolloResponse<GetCommunitiesQuery.Data> {
        return apolloClient.query(GetCommunitiesQuery()).execute()
    }

    suspend fun getCommunity(name: String): ApolloResponse<GetCommunityQuery.Data> {
        return apolloClient.query(GetCommunityQuery(name = name)).execute()
    }

    suspend fun likePost(id: UUID): ApolloResponse<LikePostMutation.Data> {
        return apolloClientAuth.mutation(LikePostMutation(id = id)).execute()
    }

    suspend fun getPost(id: UUID): ApolloResponse<GetPostQuery.Data> {
        return apolloClient.query(GetPostQuery(id = id)).execute()
    }

    suspend fun subscribeToCommunity(name: String): ApolloResponse<SubscribeToCommunityMutation.Data> {
        return apolloClientAuth.mutation(SubscribeToCommunityMutation(name = name)).execute()
    }

    suspend fun getUsersByName(name: String): ApolloResponse<GetUserByNameQuery.Data> {
        return apolloClient.query(GetUserByNameQuery(name = name)).execute()
    }

    suspend fun createUser(name: String) : ApolloResponse<CreateUserMutation.Data> {
        return apolloClientAuth.mutation(CreateUserMutation(name = name)).execute()
    }

    suspend fun getUserById(id: String): ApolloResponse<GetUserByIdQuery.Data> {
        return apolloClient.query(GetUserByIdQuery(id = id)).execute()
    }
}