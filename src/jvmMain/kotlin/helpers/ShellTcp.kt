package helpers

import com.sun.jna.Platform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Robot
import java.awt.event.KeyEvent
import java.io.BufferedReader
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket

object ShellTcp {
    private var port = 9000
    private var serverSocket: ServerSocket? = null
    private var socketUser: Socket? = null
    private var dataInputStream: DataInputStream? = null
    private var dataOutputStream: DataOutputStream? = null
    private var shellScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    fun start() {
        try {
            serverSocket = ServerSocket(port)
        } catch (er: Exception) {

            println("error start() : $er ${serverSocket?.isBound}")
        }
    }


    fun linked(): Int {
        /*
        * make a random number
        * get an userclient,connect
        * init input and output stream
        * send the password*/
        try {
            socketUser = serverSocket!!.accept()
            dataInputStream = DataInputStream(socketUser!!.getInputStream())
            dataOutputStream = DataOutputStream(socketUser!!.getOutputStream())

            return dataInputStream!!.readInt()
        } catch (_: Exception) {
            stop()
            return 0
        }
    }

    fun stop() {
        try {
            serverSocket!!.close()
            socketUser!!.close()

        } catch (er: Exception) {
            println("server closed: " + serverSocket?.isClosed)
            println("user is closed: ${socketUser?.isClosed}")
        }
        serverSocket = null
        socketUser = null
        dataInputStream = null
        dataOutputStream = null
    }


    fun service(debug: Boolean = false) {
        var command: String
        var executor: ProcessBuilder

        if (Platform.isWindows()) {
            /*windows*/
            dataOutputStream?.writeInt(2)
        } else if (Platform.isMac()) {
            /*mac*/
            dataOutputStream?.writeInt(3)
        } else {
            /*linux*/
            dataOutputStream?.writeInt(1)
        }


        shellScope.launch {
            var count = 5
            while (true) {
                try {
                    /*recieve the command*/
                    command = dataInputStream!!.readUTF()
                    /*split the command and turn it into mutablelist*/
                    var commandstrip = command.split(" ").toMutableList()
                    /*when command is for shortcut keys*/
                    if (commandstrip[0] == "keycuts") {
                        val robot = Robot()
                        for (i in commandstrip) {
                            when (i) {
                                "super" -> robot.keyPress(KeyEvent.VK_WINDOWS)
                                "ctrl" -> robot.keyPress(KeyEvent.VK_CONTROL)
                                "shift" -> robot.keyPress(KeyEvent.VK_SHIFT)
                                "alt" -> robot.keyPress(KeyEvent.VK_ALT)
                                "del" -> robot.keyPress(KeyEvent.VK_DELETE)
                                "esc" -> robot.keyPress(KeyEvent.VK_ESCAPE)
                                "command" -> robot.keyPress(KeyEvent.VK_WINDOWS)
                                else -> {
                                    if (i.length == 1) {
                                        robot.keyPress(i[0].code)
                                    }
                                }
                            }
                        }
                        for (i in commandstrip) {
                            when (i) {
                                "super" -> robot.keyRelease(KeyEvent.VK_WINDOWS)
                                "ctrl" -> robot.keyRelease(KeyEvent.VK_CONTROL)
                                "shift" -> robot.keyRelease(KeyEvent.VK_SHIFT)
                                "alt" -> robot.keyRelease(KeyEvent.VK_ALT)
                                "del" -> robot.keyRelease(KeyEvent.VK_DELETE)
                                "esc" -> robot.keyRelease(KeyEvent.VK_ESCAPE)
                                else -> {
                                    if (i.length == 1) {
                                        robot.keyRelease(i[0].code)
                                    }
                                }
                            }
                        }

                    } else {
                        if (Platform.isLinux()) {
                            val getname = ProcessBuilder("users").start()
                            println(getname)
                            val name = BufferedReader(InputStreamReader(getname.inputStream))
                            commandstrip = commandstrip.apply {
                                add(0, "--user=${name.readLine()}")
                                add(0, "sudo")
                            }
                        } else if (Platform.isWindows()) {
                            commandstrip = commandstrip.apply {
                                add(0, "/C")
                                add(0, "CMD")
                            }
                        }


                        executor = ProcessBuilder(commandstrip)
                        val process = executor.start()
                        if (debug) {
                            val reader = BufferedReader(InputStreamReader(process.errorStream))
                            val builder = StringBuilder()
                            var line: String?
                            while (reader.readLine().also { line = it } != null) {
                                builder.append(line)
                                builder.append(System.getProperty("line.separator"))
                            }
                            val result = builder.toString()
                            println(result)
                        }
                    }
                } catch (er: Exception) {
                    println(er)
                    count--
                    delay(2000)
                    if (count == 0) {
                        break
                    }
                }
            }
        }
    }
}