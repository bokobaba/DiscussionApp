package com.love.discussionapp.core.data.repository

import android.util.Log
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation
import com.love.discussionapp.*
import com.love.discussionapp.core.data.remote.Api
import com.love.discussionapp.core.util.Resource
import com.love.discussionapp.core.util.TAG
import com.love.discussionapp.feature_community.presentation.CommunityScreenEvent
import java.util.UUID
import javax.inject.Inject

class ApiRepository @Inject constructor(
    private val api: Api
){
    suspend fun getCommunities(): Resource<GetCommunitiesQuery.Data?> {
        return apiRequest(
            request = { api.getCommunities() },
            errorMessage = "An error occurred loading Communities"
        )
    }

    suspend fun getCommunity(name: String): Resource<GetCommunityQuery.Data?> {
        return apiRequest(
            request = { api.getCommunity(name) },
            errorMessage = "An error occurred retrieving community: $name"
        )
    }

    suspend fun likePost(id: UUID): Resource<LikePostMutation.Data?> {
        return apiRequest(
            request = { api.likePost(id) },
            errorMessage = "Unable to like post"
        )
    }

    suspend fun getPost(id: UUID): Resource<GetPostQuery.Data?> {
        return apiRequest(
            request = { api.getPost(id) },
            errorMessage = "An error occurred retrieving post: $id"
        )
    }

    suspend fun subscribeToCommunity(name: String): Resource<SubscribeToCommunityMutation.Data?> {
        return apiRequest(
            request = { api.subscribeToCommunity(name) },
            errorMessage = "Unable to join $name. Please try again later"
        )
    }

    suspend fun getUsersByName(name: String): Resource<GetUserByNameQuery.Data?> {
        return apiRequest(
            request = { api.getUsersByName(name) },
            errorMessage = "Error fetching users",
        )
    }

    suspend fun createUser(name: String): Resource<CreateUserMutation.Data?> {
        return apiRequest(
            request = { api.createUser(name) },
            errorMessage = "An error occurred creating user",
        )
    }

    suspend fun getUserById(id: String): Resource<GetUserByIdQuery.Data?> {
        return apiRequest(
            request = { api.getUserById(id) },
            errorMessage = "An error occurred"
        )
    }

    private suspend fun<T : Operation.Data> apiRequest(
        request: suspend () -> ApolloResponse<T>,
        errorMessage: String
    ): Resource<T?> {
        request.javaClass.enclosingMethod?.let { Log.d(this.TAG(), it.name) }
        val response = try {
            request()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return Resource.Error(errorMessage)
        }

        if (response.errors != null) {
            Log.d(this.TAG(), "${response.errors}")
            return Resource.Error(errorMessage)
        }

        Log.d(this.TAG(), "Success ${response.data}")
        return Resource.Success(response.data)
    }
}