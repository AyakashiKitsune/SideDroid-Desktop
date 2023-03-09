package helpers

import java.awt.Image
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.awt.image.BufferedImageOp
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

class ScreenshotHelper {
    private val robot = Robot()
    private val rect = Rectangle(Toolkit.getDefaultToolkit().screenSize.size)
    fun shotWithCompression(resolutionW: Int, resolutionH: Int, compression: Float): ByteArray {
        /*get one shot*/
        return imageCompressortoBytearray(robot.createScreenCapture(rect), resolutionW, resolutionH, compression)
    }
}