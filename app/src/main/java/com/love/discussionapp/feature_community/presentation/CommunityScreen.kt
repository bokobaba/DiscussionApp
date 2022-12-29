package com.love.discussionapp.feature_community.presentation

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.love.discussionapp.core.component.LoadingAnimation
import com.love.discussionapp.feature_community.domain.model.Post
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

@Composable
fun CommunityScreen(navController: NavController, vm: CommunityViewModel = hiltViewModel()) {
//    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val snackbarHostState = remember { SnackbarHostState() }
    CommunityScreenContent(
        loading = vm.loading,
        state = vm.state,
        onEvent = vm::onEvent
    )

    LaunchedEffect(key1 = true) {
        vm.eventFlow.collectLatest { event ->
            when (event) {
                is CommunityViewModel.UiEvent.Back -> navController.navigateUp()
                is CommunityViewModel.UiEvent.FetchDataError ->
                    snackbarHostState.showSnackbar(event.message)
                is CommunityViewModel.UiEvent.PostSelected ->
                    Log.d("CommunityScreen", "post Selceted: ${event.id}")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreenContent(
    loading: State<Boolean>,
    state: ICommunityViewState,
    onEvent: (CommunityScreenEvent) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    Button(onClick = { onEvent(CommunityScreenEvent.Back) }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
                    }
                },
                title = {
                    Text(text = state.community.value?.name ?: "")
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (loading.value || state.community.value == null) {
                LoadingAnimation(modifier = Modifier.fillMaxSize())
            } else {
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth(),
                    model = state.community.value!!.bannerUrl,
                    contentScale = ContentScale.Crop,
                    contentDescription = "banner",
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "${state.community.value?.subscribers ?: 0} members")
                JoinCommunityButton(isJoined = state.community.value!!.subscribed, onEvent = onEvent)
                PostList(posts = state.posts, onEvent = onEvent)
            }
        }
    }
}

@Composable
fun JoinCommunityButton(isJoined: Boolean, onEvent: (CommunityScreenEvent) -> Unit) {
    Log.d("JoinCommunityButton", "init")
    Button(
        onClick = { onEvent(CommunityScreenEvent.SubscribeButtonClick) },
    ) {
        Text(text = if (isJoined) "Joined" else "Join")
    }
}

@Composable
fun PostList(posts: SnapshotStateList<Post>, onEvent: (CommunityScreenEvent) -> Unit) {
    Log.d("PostList", "init")
    Text(
        text = "Posts",
        style = MaterialTheme.typography.headlineMedium
    )
    Divider()
    LazyColumn(
        modifier = Modifier
    ) {
        items(posts) { post ->
            Column(
                modifier = Modifier.clickable {
                    onEvent(CommunityScreenEvent.SelectPost(post.id))
                }
            ) {
                Row() {
                    Text(text = post.creatorName, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.width(5.dp))
                    TimeSincePost(postDate = post.timestamp)
                }

                Text(text = post.title, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(5.dp))
                PostInfoSecondary(post = post, onEvent = onEvent)
            }
            Divider(modifier = Modifier.height(5.dp))
        }
    }
}

@Composable
fun TimeSincePost(postDate: LocalDateTime) {
    val style: TextStyle = MaterialTheme.typography.bodySmall
    val currentDate = LocalDateTime.now()

    val minutes: Long = ChronoUnit.MINUTES.between(postDate, currentDate)
    if (minutes < 60) {
        Log.d("PostList", "minutes = $minutes")
        Text(text = "${minutes}m", style = style)
        return
    }

    val hours: Long = ChronoUnit.HOURS.between(postDate, currentDate)
    if (hours < 60) {
        Log.d("PostList", "hours = $hours")
        Text(text = "${hours}h", style = style)
        return
    }

    val days: Long = ChronoUnit.DAYS.between(postDate, currentDate)
    if (days < 365) {
        Log.d("PostList", "days = $days")
        Text(text = "${days}d", style = style)
        return
    }

    val years: Long = ChronoUnit.YEARS.between(postDate, currentDate)
    Log.d("PostList", "years = $years")
    Text(text = "${years}y", style = style)
}

@Composable
fun PostInfoSecondary(post: Post, onEvent: (CommunityScreenEvent) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(0.5f),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val cornerSize = 10.dp
        val modifier = Modifier
            .clip(RoundedCornerShape(cornerSize))
            .border(1.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(cornerSize))
            .padding(3.dp)
        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Filled.ArrowUpward, contentDescription = "likes")
            Text(text = "${post.likes}")
        }
        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Filled.Comment, contentDescription = "comments")
            Text(text = "${post.comments}")
        }
    }
}

@Composable
@Preview
fun CommunityScreenPreview() {
    CommunityScreenContent(
        loading = remember { derivedStateOf { false } },
        state = CommunityState.previewState,
        onEvent = {},
    )
}