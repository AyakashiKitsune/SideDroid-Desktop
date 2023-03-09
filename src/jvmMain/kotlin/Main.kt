import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import helpers.ShellTcp
import kotlinx.coroutines.*
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.net.InetAddress
import java.time.LocalTime


@Composable
fun mainViewMenu(
    modelNetwork: NetworkSharingController = NetworkSharingController(),
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val fontSize = remember { mutableStateOf(30.sp) }
    val monitorImage = painterResource("images/desktop.png")
    val androidhead = painterResource("images/androidhead.png")
    val showSettings = remember { mutableStateOf(false) }
    val isStreaming = remember { mutableStateOf(false) }
    var testPhoto by remember {
        mutableStateOf(
            Robot().createScreenCapture(Rectangle(Toolkit.getDefaultToolkit().screenSize)).toComposeImageBitmap()
        )
    }
    val compressionNumber = remember { mutableStateOf("0.1") }
    val videoWidth = remember { mutableStateOf(1280) }
    val videoHeight = remember { mutableStateOf(720) }
    val randomnumber = remember { mutableStateOf(0) }
    val myLocalIP = InetAddress.getLocalHost().hostAddress
    var fps = remember { mutableStateOf(0) }
    var localfps = remember { mutableStateOf(0) }

    LaunchedEffect(isStreaming.value){
           withContext(Dispatchers.IO) {
               var timestart = LocalTime.now()
               while (true) {
                   testPhoto = Robot().createScreenCapture(Rectangle(Toolkit.getDefaultToolkit().screenSize))
                       .toComposeImageBitmap()
                   localfps.value++
                   delay(8L)
                   val endtime = LocalTime.now()
                    if (timestart.plusSeconds(1) < endtime){
                        timestart = endtime
                        fps.value = localfps.value
                        localfps.value = 0
                    }
               }
           }
    }

    /*logo of the application
    * text title*/
    /*
    * settings
    * video
    * buttons start and stop
    * */
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(Color(220, 220, 220, 255))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = modifier
                .weight(1f)
                .fillMaxSize()
        ) {
            /*
            * logo
            * */
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .weight(4f)
                    .padding(30.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = modifier.fillMaxSize()
                ) {
                    Image(
                        painter = androidhead,
                        contentDescription = null,
                        modifier = modifier.wrapContentSize(Alignment.BottomCenter).offset(0.dp, 80.dp),
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.Center
                    )
                    Image(
                        painter = monitorImage,
                        contentDescription = null,
                        modifier = modifier.fillMaxSize(2f),
                        alignment = Alignment.Center,
                        contentScale = ContentScale.FillHeight
                    )
                }

            }
            /*
            * Text title
            * */
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
                                color = Color(232, 72, 85, 255),
                                fontSize = fontSize.value.times(2.5),
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("SideDroid\n")
                        }
                        withStyle(
                            style = SpanStyle(
                                fontSize = fontSize.value,
                                color = Color(219, 43, 57, 255)
                            )
                        ) {
                            append("Host Desktop")
                        }
                    }
                },
                modifier = modifier.weight(2f)
            )
        }

        /*setting
        * video
        * buttons*/
        Column(
            modifier = modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .padding(12.dp)
                    .weight(1f)
                    .fillMaxWidth()

            ) {
                Spacer(modifier.weight(1f))
                if(isStreaming.value){
                    Text("${(fpsaccuracy(fps.value) / 30.00 * 100).toInt()}%", fontWeight = FontWeight.Bold, fontSize = 32.sp)
                }
                Spacer(modifier.weight(1f))

                /*settings*/
                IconButton(
                    onClick = {
                        showSettings.value = !showSettings.value
                        if (isStreaming.value) {
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
            /*show settings*/
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

            /*video
            * buttons*/
            AnimatedVisibility(
                visible = !showSettings.value,
                enter = fadeIn(
                    initialAlpha = 0.3f
                ),
                exit = fadeOut(
                    targetAlpha = 0f
                ),
                modifier = modifier.weight(5f)
            ) {
                Box(
                    modifier = modifier
                        .weight(2f)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                        AnimatedVisibility(
                            visible = isStreaming.value,
                            enter = fadeIn(
                                initialAlpha = 0.5f
                            ) + slideInVertically {
                                with(density) { -40.dp.roundToPx() }
                            },
                            modifier = modifier.fillMaxWidth()
                        ) {
                            Image(
                                bitmap = testPhoto,
                                contentDescription = null,
                                modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            )
                        }

                        if (!isStreaming.value) {
                            Spacer(modifier.weight(2f))
                        }

                        OutlinedButton(
                            shape = RoundedCornerShape(100),
                            border = BorderStroke(4.dp, Color(75, 59, 64, 255)),
                            colors = if (isStreaming.value){
                                ButtonDefaults.outlinedButtonColors(Color.Gray)
                            }else{ ButtonDefaults.outlinedButtonColors(Color.Transparent)},
                            modifier = modifier.background(Color.Transparent),
                            enabled = !isStreaming.value,
                            onClick = {
                                randomnumber.value = (Math.random() * 1_000_000).toInt()
                                isStreaming.value = true
                                CoroutineScope(Dispatchers.IO).launch {
                                    ShellTcp.start()
                                    when (ShellTcp.linked()) {
                                        randomnumber.value -> {
                                            CoroutineScope(Dispatchers.IO).launch {

                                                ShellTcp.service(true)
                                            }
                                            CoroutineScope(Dispatchers.IO).launch {
                                                modelNetwork.start(
                                                    videoWidth.value,
                                                    videoHeight.value,
                                                    compressionNumber.value.toFloat(),
                                                )
                                            }
                                        }

                                        else -> {
                                            isStreaming.value = false
                                            ShellTcp.stop()
                                        }
                                    }
                                }
                            },
                            contentPadding = PaddingValues(12.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "Start stream",
                                    modifier = modifier.padding(horizontal = 8.dp).size(24.dp),
                                    Color.Green
                                )
                                Text(
                                    "Start Server",
                                    color = Color.Black,
                                    fontSize = 16.sp

                                )
                            }
                        }
                        OutlinedButton(
                            shape = RoundedCornerShape(100),
                            border = BorderStroke(4.dp, Color(75, 59, 64, 255)),
                            colors = if (!isStreaming.value){
                                ButtonDefaults.outlinedButtonColors(Color.Gray)
                            }else{ ButtonDefaults.outlinedButtonColors(Color.Transparent)},
                            modifier = modifier.background(Color.Transparent),
                            enabled = isStreaming.value,
                            onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    isStreaming.value = false
                                    modelNetwork.stop()
                                    ShellTcp.stop()
                                }
                            },
                            contentPadding = PaddingValues(12.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically

                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Start stream",
                                    modifier = modifier.padding(horizontal = 8.dp).size(24.dp),
                                    Color.Red
                                )
                                Text(
                                    "Stop Server",
                                    color = Color.Black,
                                    fontSize = 16.sp
                                )
                            }
                        }
                        Card(
                            modifier = modifier.padding(32.dp),
                            border = BorderStroke(4.dp, Color(75, 59, 64, 255)),
                            backgroundColor = Color.Transparent,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = modifier.padding(16.dp)
                            ) {
                                Card(
                                    modifier = modifier.fillMaxWidth().padding(8.dp),
                                    border = BorderStroke(4.dp, Color(75, 59, 64, 255)),
                                    backgroundColor = Color.Transparent,

                                    ) {
                                    Text(
                                        text = "Password : ${randomnumber.value}",
                                        fontSize = 24.sp,
                                        modifier = modifier.padding(8.dp)

                                    )
                                }

                                Card(
                                    modifier = modifier.fillMaxWidth().padding(8.dp),
                                    border = BorderStroke(4.dp, Color(75, 59, 64, 255)),
                                    backgroundColor = Color.Transparent,
                                ) {
                                    Text(
                                        text = "Local IP address : $myLocalIP",
                                        fontSize = 24.sp,
                                        modifier = modifier.padding(8.dp)
                                    )
                                }
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
            textString,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        TextField(
            value = textFieldValue,
            onValueChange = textFieldOnChange,
        )
    }
}
private fun fpsaccuracy(fps : Int) :Int{
    return when(fps.toInt()){
        24,25,26,27,28,29,30 -> 30 //100
        21,22,23 -> 29 //96
        17,18,19,20, -> 28//93
        16,15,14, -> 27//90
        13,12,11,10,9,8,-> 26
        7,6,5,4,3,2,1,0 -> 25
        else-> fps.toInt()
    }
}


fun main() = application(exitProcessOnExit = true) {
    val windowSize = DpSize(900.dp, 800.dp)
    val windowState = rememberWindowState(
        placement = WindowPlacement.Floating,
        isMinimized = false,
        position = WindowPosition.PlatformDefault,
        size = windowSize
    )
    Window(
        onCloseRequest = ::exitApplication,
        title = "SideDroid Server Desktop",
        state = windowState
    ) {
        mainViewMenu()
    }
}

