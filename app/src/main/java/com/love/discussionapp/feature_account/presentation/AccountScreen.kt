package com.love.discussionapp.feature_account.presentation

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.love.discussionapp.R
import com.love.discussionapp.core.auth.AuthEvent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun AccountScreen(navController: NavController, vm: AccountViewModel = hiltViewModel()) {
    Log.d("AccountScreen", "init")
    val scaffoldState = rememberScaffoldState()
    AccountScreenContent(scaffoldState = scaffoldState, onEvent = vm::onEvent)

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        vm.eventFlow.collectLatest { event ->
            Log.d("AccountScreen", "collect event")
            when (event) {
                is AccountViewModel.UiEvent.Back -> navController.navigateUp()
                is AccountViewModel.UiEvent.CreateAccount -> {
                    vm.createAccount(
                        username = event.username,
                        email = event.email,
                        password = event.password,
                        onSuccess = { navController.navigateUp() },
                        onFail = { message ->
                            coroutineScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(message)
                            }
                        }
                    )
                }
                is AccountViewModel.UiEvent.Login -> {
                    vm.login(
                        email = event.email,
                        password = event.password,
                        onSuccess = { navController.navigateUp() },
                        onFail = { message ->
                            coroutineScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(message)
                            }
                        }
                    )
                }
                is AccountViewModel.UiEvent.UsernameAlreadyExists -> {
                    scaffoldState.snackbarHostState.showSnackbar("Username already exists")
                }
                is AccountViewModel.UiEvent.Error -> {
                    Log.d("AccountScreen", event.message)
                    scaffoldState.snackbarHostState.showSnackbar("An error occurred")
                }
            }
        }
    }
}

@Composable
fun AccountScreenContent(scaffoldState: ScaffoldState, onEvent: (AccountScreenEvent) -> Unit) {
    Log.d("AccountScreenContent", "init")
    var isLogin: Boolean by remember { mutableStateOf(false) }
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    Button(onClick = { onEvent(AccountScreenEvent.Back) }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
                    }
                },
                title = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_account),
                        contentDescription = "account"
                    )
                },
                actions = {
                    Button(onClick = {
                        isLogin = !isLogin
                        Log.d("click", "isLogin = $isLogin")
                    }) {
                        Text(text = if (isLogin) "Create Account" else "Login")
                    }
                },
            )
        }
    ) { innerPadding ->
        AccountInfo(
            modifier = Modifier.padding(innerPadding),
            onEvent = onEvent,
            isLogin = isLogin
        )
    }
}

@Composable
fun AccountInfo(
    modifier: Modifier = Modifier,
    onEvent: (AccountScreenEvent) -> Unit,
    isLogin: Boolean
) {
    Log.d("AccountInfo", "init")
    Column(
        modifier = modifier.padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var email = rememberSaveable { mutableStateOf("") }
        var password = rememberSaveable { mutableStateOf("") }
        var username = rememberSaveable { mutableStateOf("") }

        Text(
            text = if (isLogin) "Log In" else "Create Account",
            style = MaterialTheme.typography.h3
        )

        Divider()

        Spacer(modifier = Modifier.height(10.dp))

        if (!isLogin)
            UsernameTextField(username = username)

        EmailTextField(email = email)

        PasswordTextField(password = password)

        Spacer(modifier = Modifier.height(50.dp))

        ContinueButton(
            username = username,
            email = email,
            password = password,
            isLogin = isLogin,
            onEvent = onEvent
        )
    }
}

@Composable
fun UsernameTextField(username: MutableState<String>) {
    OutlinedTextField(
        label = { Text(text = "Username") },
        value = username.value,
        onValueChange = { username.value = it }
    )
}

@Composable
fun EmailTextField(email: MutableState<String>) {
    OutlinedTextField(
        label = { Text(text = "Email") },
        value = email.value,
        onValueChange = { email.value = it }
    )
}

@Composable
fun PasswordTextField(password: MutableState<String>) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        value = password.value,
        onValueChange = { password.value = it },
        label = { Text(text = "Password") },
        visualTransformation = if (passwordVisible) VisualTransformation.None
        else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisible)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, contentDescription = "passwordVisibility")
            }
        }
    )
}

@Composable
fun ContinueButton(
    email: MutableState<String>,
    password: MutableState<String>,
    username: MutableState<String>,
    isLogin: Boolean,
    onEvent: (AccountScreenEvent) -> Unit
) {
    Button(
        onClick = {
            if (isLogin)
                onEvent(
                    AccountScreenEvent.Login(
                        email = email.value,
                        password = password.value
                    )
                )
            else
                onEvent(
                    AccountScreenEvent.CreateAccount(
                        email = email.value,
                        password = password.value,
                        username = username.value!!
                    )
                )
        }
    ) {
        Text(text = "Continue")
    }
}

@Composable
@Preview
fun AccountScreenPreview() {
    AccountScreenContent(
        scaffoldState = rememberScaffoldState(),
        onEvent = {})
}