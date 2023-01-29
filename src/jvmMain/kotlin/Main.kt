import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.useResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import helpers.imageShrinker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit


val windowSize = DpSize(1000.dp, 800.dp)

@Composable
fun mainViewMenu(
    modelNetwork: NetworkSharingController = NetworkSharingController(),
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val fontSize = remember { mutableStateOf(30.sp) }
    val monitorImage = painterResource("images/desktop.png")
    val networkImageNeural = imageShrinker(
        useResource("images/neural.png") { loadImageBitmap(it) },
        0.5f
    ).toComposeImageBitmap()
    val showSettings = remember { mutableStateOf(false) }
    val isStreaming = remember { mutableStateOf(false) }
    var testPhoto by remember {
        mutableStateOf(
            Robot().createScreenCapture(Rectangle(Toolkit.getDefaultToolkit().screenSize)).toComposeImageBitmap()
        )
    }
    val portNumber = remember { mutableStateOf("8080") }
    val compressionNumber = remember { mutableStateOf("0.1") }
    val videoWidth = remember { mutableStateOf(1280) }
    val videoHeight = remember { mutableStateOf(720) }
    val fps = remember { mutableStateOf(32L) }
    LaunchedEffect(true) {
        withContext(Dispatchers.IO) {
            while (true) {
                testPhoto = Robot().createScreenCapture(Rectangle(Toolkit.getDefaultToolkit().screenSize))
                    .toComposeImageBitmap()
                delay(32)
            }
        }
    }

    Row(modifier = modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .weight(1f)
                .fillMaxSize()
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Top
            ) {
                Image(
                    painter = monitorImage,
                    contentDescription = null,
                    modifier = modifier.scale(0.7f)
                        .drawBehind {
                            drawImage(
                                image = useResource("images/desktop.png") { loadImageBitmap(it) },
                            )
                        }
                        .drawWithContent {
                            drawImage(
                                image = networkImageNeural,
                                srcOffset = IntOffset.Zero,
                                dstOffset = IntOffset(networkImageNeural.width, 0),
                            )
                        }
                )
            }
            Text(
                buildAnnotatedString {
                    withStyle(
                        style = ParagraphStyle(
                            lineHeight = fontSize.value,
                            textAlign = TextAlign.Center,
                        )
                    ) {
                        withStyle(
                            style = SpanStyle(
                                color = Color.Red,
                                fontSize = fontSize.value.times(2.5),
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("SideDroid\n")
                        }
                        withStyle(style = SpanStyle(fontSize = fontSize.value)) {
                            append("Host Desktop")
                        }
                    }
                }
            )
        }


        Column(
            modifier = modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Top,
                modifier = modifier
                    .padding(12.dp)
                    .weight(1f)
                    .fillMaxWidth()

            ) {
                IconButton(
                    onClick = {
                        showSettings.value = !showSettings.value
                        if(isStreaming.value){
                            modelNetwork.stop()
                            isStreaming.value = false
                        }
                    },
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        modifier = modifier.size(40.dp)
                    )
                }
            }

            AnimatedVisibility(
                visible = showSettings.value,
                enter = fadeIn(
                    initialAlpha = 0.3f
                ),
                exit = fadeOut(
                    targetAlpha = 0f
                ),
                modifier = modifier.weight(2f)
            ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    itemSettings(
                        textString = "Port Number",
                        textFieldOnChange = {
                            portNumber.value = it
                            modelNetwork.changePortNumber(it.toInt())
                        },
                        textFieldValue = portNumber.value
                    )
                    itemSettings(
                        textString = "Frames per second",
                        textFieldOnChange = {
                            fps.value = it.toLong()
                        },
                        textFieldValue = fps.value.toString()
                    )
                    itemSettings(
                        textString = "Video Compression",
                        textFieldOnChange = {
                            compressionNumber.value = it
                        },
                        textFieldValue = compressionNumber.value
                    )
                    itemSettings(
                        textString = "Video Width",
                        textFieldOnChange = { videoWidth.value = it.toInt() },
                        textFieldValue = videoWidth.value.toString()
                    )
                    itemSettings(
                        textString = "Video Height",
                        textFieldOnChange = { videoHeight.value = it.toInt() },
                        textFieldValue = videoHeight.value.toString()
                    )

                }
            }

            AnimatedVisibility(
                visible = !showSettings.value,
                enter = fadeIn(
                    initialAlpha = 0.3f
                ),
                exit = fadeOut(
                    targetAlpha = 0f
                ),
                modifier = modifier.weight(2f)
            ) {
                Box(
                    modifier = modifier
                        .weight(2f)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                        AnimatedVisibility(
                            visible = isStreaming.value,
                            enter = fadeIn(
                                initialAlpha = 0.3f
                            ) + slideInVertically {
                                with(density) { -40.dp.roundToPx() }
                            },
                        ) {
                            Image(
                                bitmap = testPhoto,
                                contentDescription = null,
                            )
                        }

                        Button(
                            onClick = {
                                isStreaming.value = true
                                modelNetwork.start(
                                    videoWidth.value,
                                    videoHeight.value,
                                    compressionNumber.value.toFloat(),
                                    fps.value
                                )

                            },
                            contentPadding = PaddingValues(12.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {

                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "Start stream",
                                    modifier = modifier.padding(horizontal = 8.dp)
                                )
                                Text("Start Server")
                            }
                        }
                        Button(
                            onClick = {
                                isStreaming.value = false
                                modelNetwork.stop()
                            },
                            contentPadding = PaddingValues(12.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Start stream",
                                    modifier = modifier.padding(horizontal = 8.dp)
                                )

                                Text("Stop Server")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun itemSettings(
    modifier: Modifier = Modifier,
    textString: String = "DefaultString",
    textFieldValue: String,
    textFieldOnChange: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.padding(horizontal = 8.dp, vertical = 12.dp).fillMaxWidth()
    ) {
        Text(
            textString
        )
        TextField(
            value = textFieldValue,
            onValueChange = textFieldOnChange,
        )
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "SideDroid Server Desktop",
        state = rememberWindowState(
            placement = WindowPlacement.Floating,
            isMinimized = false,
            position = WindowPosition.PlatformDefault,
            size = windowSize
        )
    ) {
        mainViewMenu()
    }
}

