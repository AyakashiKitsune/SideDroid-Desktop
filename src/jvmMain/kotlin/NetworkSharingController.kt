import helpers.ScreenshotHelper
import helpers.ShellTcp
import kotlinx.coroutines.*
import org.jetbrains.skiko.FPSCounter
import java.io.DataOutputStream
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.time.LocalTime

class NetworkSharingController {
    private var portNumber: Int = 8080
    private val networkScope = CoroutineScope(Dispatchers.IO)
    private val sshelper: ScreenshotHelper = ScreenshotHelper()
    private var serverSocket: ServerSocket? = null
    private var dataOutputStream: DataOutputStream? = null
    private var localDevice: Socket? = null
    private var linkingJob: Job? = null
    private var senderJob: Job? = null

    fun starServer() {
        try {
            serverSocket = ServerSocket(this.portNumber)
        } catch (_: Exception) {
        }
    }

    fun start(resolutionW: Int = 1920, resolutionH: Int = 1080, compression: Float = 0.1f) {
        starServer()
        println("$resolutionW x $resolutionH of $compression")
        linkingJob = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                try {
                    localDevice = serverSocket!!.accept()
                    if (localDevice != null) {

                        println("local devices connected to ${localDevice?.inetAddress.toString()}")
                        dataOutputStream = DataOutputStream(localDevice?.getOutputStream())
                        break
                    } else {
                        println("trying")
                    }
                } catch (er: IOException) {
                    dataOutputStream = null
                    serverSocket?.close()
                    serverSocket = null
                    localDevice = null
                    cancel()
                    println("er")
                    delay(2000)
                }
            }
            try {
                sendImage(resolutionW, resolutionH, compression)
            } catch (er: Exception) {
                dataOutputStream = null
                serverSocket?.close()
                serverSocket = null
                localDevice = null
            }
        }
    }

    fun sendImage(resolutionW: Int, resolutionH: Int, compression: Float) {
        senderJob = networkScope.launch {
            while (true) {
                try {
                    val image = sshelper.shotWithCompression(resolutionW, resolutionH, compression)
                    dataOutputStream?.writeInt(image.size)
                    dataOutputStream?.write(image)
                } catch (error: Exception) {
                    println("IOException occurred $error")
                    linkingJob?.cancel()
                    ShellTcp.stop()
                    stop()
                    cancel()
                    break
                }
                withContext(Dispatchers.IO) {
                    dataOutputStream!!.flush()
                }
            }
        }
    }

    fun stop() {
        serverSocket?.close()
        serverSocket = null
        linkingJob?.cancel()
        senderJob?.cancel()
        println("stop(): cancelled")
    }// make a stopping functions

}