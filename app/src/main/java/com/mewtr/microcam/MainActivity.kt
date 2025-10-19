package com.mewtr.microcam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mewtr.microcam.ui.theme.MicrocamTheme
import androidx.compose.runtime.getValue
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.compose.ui.Alignment

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Displaying content behind the system UI is called going edge-to-edge
        setContent {
            Main()
        }
    }
}
data class Message(val author: String, val body: String)
@Composable
fun MessageCard(msg: Message) {
    // Add padding around our message
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Image(
            painter = painterResource(R.drawable.profile_picture),
            contentDescription = "Contact profile picture",
            alpha = 0.9f,
            modifier = Modifier
                .size(40.dp) // Set image size to 40 dp
                .clip(CircleShape) // Clip image to be shaped as a circle
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )

        // Add a horizontal space between the image and the column
        Spacer(modifier = Modifier.width(8.dp))
        var isExpanded by remember { mutableStateOf(false)}
        val surfaceColor by animateColorAsState(
            if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        )
        Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
            Text(text = msg.author,
                color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.titleSmall)
            // Add a vertical space between the author and the message texts
            Spacer(modifier = Modifier.height(4.dp))

            Surface(shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.dp,
                color = surfaceColor,
                modifier = Modifier
                    .animateContentSize()
                    .padding(1.dp))
            {
                Text(
                    text = msg.body,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(all = 4.dp),
                    // If the message is expanded, we display all its content
                    // otherwise we only display the first line
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,

                )
            }
        }
    }
}

@Composable
fun Conversation(messages: List<Message>, modifier: Modifier) {
    LazyColumn (modifier = modifier) {
        items(messages) { message ->
            MessageCard(message)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActivityTopBar(darkTheme: Boolean, onClick: () -> Unit){
    TopAppBar(title = {Text("")},
        actions = {MainActivityTopBarActions(darkTheme, onClick)})
}

@Composable
fun RowScope.MainActivityTopBarActions(darkTheme: Boolean, onClick: () -> Unit){
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.visibility3));
    val progress: Float by animateFloatAsState(if (darkTheme) 1f else 0.0f)
    LottieAnimation(
        composition = composition,
        progress = {progress},
        modifier = Modifier
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) // Disable ripple effect onbutton click. Needs interactionSource to be defined
        .padding(4.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActivityBottomBar() {
    BottomAppBar(actions = {MainActivityBottomBarActions()})
}

@Composable
fun RowScope.MainActivityBottomBarActions()
{

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.microphone));
    var muted by remember {mutableStateOf(false)}
    val progress: Float by animateFloatAsState(if (muted) 0.0f else 1f)
    Row(
        modifier= Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    )
    {
        Button(onClick = {muted = !muted} ) {
        LottieAnimation(
            composition = composition,
            progress = {progress},
            modifier = Modifier
                .padding(4.dp)
        )
        }
    }
}

@Composable
fun Main(){
    var darkTheme by remember {mutableStateOf(true)}
    // Local function to capture variable in outer scope
    fun onEyeClick() {
        darkTheme = !darkTheme;
    }

    MicrocamTheme (darkTheme){
        Surface {
            Scaffold(modifier = Modifier.fillMaxSize(),
                // topBar = { MainActivityTopBar(darkTheme, onClick = {(::onEyeClick)()}) }) { // <- this also works
                topBar = { MainActivityTopBar(darkTheme, onClick = ::onEyeClick) },
                bottomBar = { MainActivityBottomBar ()})
            {
                innerPadding ->
                Conversation(SampleData.conversationSample, Modifier.padding(innerPadding))
            }
        }
    }
}