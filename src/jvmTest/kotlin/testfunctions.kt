package helpers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.junit.Test
import java.io.DataInputStream
import java.net.ServerSocket
import java.net.Socket

class ShellTcpTest{
    private var port = 9000
    private var serverSocket: ServerSocket? = null
    private var socketUser: Socket? = null
    private var dataInputStream: DataInputStream? = null
    private var shellScope: CoroutineScope = CoroutineScope(Dispatchers.IO)


    fun start(): Boolean {
        return try {
            serverSocket = ServerSocket(port)
            true
        } catch (er: Exception) {
            false
        }
    }

    fun linked(): Boolean {
        return if (serverSocket != null || socketUser != null) {
            try {
                socketUser = serverSocket!!.accept()
                dataInputStream = DataInputStream(socketUser!!.getInputStream())
            } catch (_: Exception) {
            }
            true
        } else {
            false
        }
    }

    fun stop(): Boolean {
        return if (serverSocket != null && socketUser != null) {
            try {
                socketUser!!.close()
                socketUser = null
                serverSocket!!.close()
                serverSocket = null
            } catch (_: Exception) {
            }
            return true
        } else {
            false
        }
    }

    suspend fun service() {
        var command: String
        var executor : ProcessBuilder
        withContext(Dispatchers.IO) {
            while (true) {
                try {
                    command = dataInputStream!!.readUTF()
                    executor = ProcessBuilder(command.split(" ")).apply {
                        start()
                    }
                } catch (er: Exception) {
                    break
                }
            }
        }
    }
}
