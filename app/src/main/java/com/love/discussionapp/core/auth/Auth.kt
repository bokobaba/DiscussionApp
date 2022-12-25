package com.love.discussionapp.core.auth

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import javax.inject.Inject

interface IAuth {
    val user: FirebaseUser?

    fun createAccount(email: String, password: String, onComplete: (AuthEvent) -> Unit)
    fun signIn(email: String, password: String, onComplete: (AuthEvent) -> Unit)
    fun logout()
}

class Auth @Inject constructor() : IAuth {
    private var _auth: FirebaseAuth
    private var _user: MutableState<FirebaseUser?> = mutableStateOf(null)

    override val user: FirebaseUser?
        get() = _user.value

    init {
        Log.d("Auth", "init")
        _auth = Firebase.auth
        _user.value = _auth.currentUser
        Log.d("Auth", "user: $_user")
    }

    override fun createAccount(email: String, password: String, onComplete: (AuthEvent) -> Unit) {
        _auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("AccountScreen", "createUserWithEmail:success")
                    _user.value = _auth.currentUser
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
                    _user.value = _auth.currentUser
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
        _user.value = null
    }
}

sealed class AuthEvent {
    object Success : AuthEvent()
    data class Fail(val message: String) : AuthEvent()
}