package com.love.discussionapp.core.auth

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.love.discussionapp.core.data.repository.IDataStoreRepository
import com.love.discussionapp.core.util.Constants
import com.love.discussionapp.core.util.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

interface IAuth {
    val user: FirebaseUser?
    val auth: FirebaseAuth
    val accessToken: String?

    fun createAccount(email: String, password: String, onComplete: (AuthEvent) -> Unit)
    fun signIn(email: String, password: String, onComplete: (AuthEvent) -> Unit)
    fun logout()
    fun getAccessToken(onComplete: (String?) -> Unit)
    fun updateProfileImage(url: String, onSuccess: () -> Unit, onFail: () -> Unit)
    fun updateDisplayName(name: String, onSuccess: () -> Unit, onFail: () -> Unit)
}

class Auth @Inject constructor() : IAuth {
    private var _auth: FirebaseAuth
//    private var _user: MutableState<FirebaseUser?> = mutableStateOf(null)
    private var _user: FirebaseUser? = null
    private var _accessToken: MutableState<String?> = mutableStateOf(null)

    override val accessToken: String?
        get() = _accessToken.value

    override val auth: FirebaseAuth
        get() = _auth

    override val user: FirebaseUser?
        get() = _user

    init {
        Log.d("Auth", "init")
        _auth = Firebase.auth
        _user = _auth.currentUser
        Log.d("Auth", "user: $_user")
    }

    override fun getAccessToken(onComplete: (String?) -> Unit) {
        _user?.let {
            it.getIdToken(true)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("Auth", "result: ${Gson().toJson(task.result)}")
                        _accessToken.value = task.result.token
                        Log.d("Auth", "getAccessToken:success token: $_accessToken")
                        onComplete(_accessToken.value)
//                    CoroutineScope(Dispatchers.IO).launch {
//                        dataStore.putString(Constants.ACCESS_TOKEN, _accessToken.value!!)
//                    }
                    } else {
                        Log.w("Auth", "getAccessToken failed", task.exception)
                    }
                }
        }
    }

    override fun createAccount(email: String, password: String, onComplete: (AuthEvent) -> Unit) {
        _auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("AccountScreen", "createUserWithEmail:success")
                    _user = _auth.currentUser
                    onComplete(AuthEvent.Success)
                } else {
                    Log.w("Auth", "Authentication failed", task.exception)
                    onComplete(AuthEvent.Fail("Authentication failed"))
                }
            }
    }

    override fun signIn(email: String, password: String, onComplete: (AuthEvent) -> Unit) {
        _auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Auth", "signInWithEmail:success")
                    _user = _auth.currentUser
                    Log.d("Auth", "user: ${Gson().toJson(user)}")
                    onComplete(AuthEvent.Success)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Auth", "signInWithEmail:failure", task.exception)
                    onComplete(AuthEvent.Fail("Authentication failed"))
                }
            }
    }

    override fun logout() {
        _auth.signOut()
        _user = null
        _accessToken.value = null
    }

    override fun updateProfileImage(
        url: String,
        onSuccess: () -> Unit,
        onFail: () -> Unit,
    ) {
        val profileUpdates = userProfileChangeRequest {
            photoUri = Uri.parse(url)
        }
        _user?.let {
            it.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(this.TAG(), "new photoUrl = ${_user?.photoUrl}")
                        onSuccess()
                    } else {
                        onFail()
                    }
                }
        }
    }

    override fun updateDisplayName(
        name: String,
        onSuccess: () -> Unit,
        onFail: () -> Unit,
    ) {
        val profileUpdates = userProfileChangeRequest {
            displayName = name
        }
        _user?.let {
            it.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(this.TAG(), "new displayName = ${_user?.displayName}")
                        onSuccess()
                    } else {
                        onFail()
                    }
                }
        }
    }
}

sealed class AuthEvent {
    object Success : AuthEvent()
    data class Fail(val message: String) : AuthEvent()
}