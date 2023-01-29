import helpers.ScreenshotHelper
import kotlinx.coroutines.*
import java.io.DataOutputStream
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

class NetworkSharingController {
    private var portNumber: Int = 8080
    var isConnected: Boolean = false
    private val networkScope = CoroutineScope(Dispatchers.IO)
    private val sshelper: ScreenshotHelper = ScreenshotHelper()
    private var serverSocket: ServerSocket? = null
    private var dataOutputStream: DataOutputStream? = null
    private var localDevice: Socket? = null
    private var linkingJob: Job? = null
    private var senderJob: Job? = null

    fun changePortNumber(portNumber: Int){
        this.portNumber = portNumber
    }

    fun starServer(){
        serverSocket = ServerSocket(this.portNumber)
    }

    fun start(resolutionW: Int = 1920, resolutionH: Int = 1080, compression: Float = 0.1f,fps: Long) {
        starServer()
        println("$resolutionW x $resolutionH of $compression")
        linkingJob = CoroutineScope(Dispatchers.IO).launch {
                while (true) {
                    try {
                        localDevice = serverSocket!!.accept()
                        if (localDevice != null){

                            println("local devices connected to ${localDevice?.inetAddress.toString()}")
                            dataOutputStream = DataOutputStream(localDevice?.getOutputStream())
                            sendImage(resolutionW,resolutionH,compression,fps)
                        }else{
                            println("trying")
                        }
                    }
                    catch (er : IOException){
                        println("er")
                        delay(2000)
                    }
                }

        }
    }
    fun sendImage(resolutionW: Int, resolutionH: Int, compression: Float,fps : Long){
        senderJob = networkScope.launch {
            while (true) {
                try {
                    val image = sshelper.shotWithCompression(resolutionW, resolutionH, compression)
                    dataOutputStream?.writeInt(image.size)
                    dataOutputStream?.write(image)
                    println("Sent image")
                } catch (error: Exception) {
                    println("IOException occurred ${error.stackTraceToString()}" )
                    isConnected = false
                    stop()
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