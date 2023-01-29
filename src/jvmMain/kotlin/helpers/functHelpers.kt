package helpers

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.stream.ImageOutputStream

fun imageShrinker(imageBitmap: ImageBitmap, shrink: Float): BufferedImage {
    val newHeight = (imageBitmap.height * shrink).toInt()
    val newWidth = (imageBitmap.width * shrink).toInt()
    val imageShrinked = imageBitmap
        .toAwtImage()
        .getScaledInstance(
            newWidth,
            newHeight,
            BufferedImage.SCALE_DEFAULT
        )

    return BufferedImage(
        newWidth,
        newHeight,
        BufferedImage.TYPE_4BYTE_ABGR
    ).apply {
        createGraphics().apply {
            drawImage(imageShrinked, 0, 0, null)
            dispose()
        }
    }
}



fun imageCompressortoBytearray(
    imageShot: BufferedImage,
    resolutionW: Int,
    resolutionH: Int,
    compression: Float = 0.1f
): ByteArray {

    /*scaling image*/
    val image = imageShot.getScaledInstance(resolutionW,resolutionH, Image.SCALE_FAST)
    val bufferedImage = BufferedImage(resolutionW,resolutionH,BufferedImage.TYPE_INT_RGB)
    val graphics = bufferedImage.createGraphics().apply {
        drawImage(image,0,0,null)
        dispose()
    }

    /*compression*/
    val compressor = ByteArrayOutputStream()
    val outputStream = ImageIO.createImageOutputStream(compressor)
    val imageWriter = ImageIO.getImageWritersByFormatName("jpg").next()
    val jpegWriteparam = imageWriter.defaultWriteParam.apply {
        compressionMode = ImageWriteParam.MODE_EXPLICIT
        compressionQuality = compression
    }
    imageWriter.output = outputStream
    imageWriter.write(null, IIOImage(bufferedImage, null, null), jpegWriteparam)
    imageWriter.dispose()

    println(compressor.toByteArray().size)
    return compressor.toByteArray()

}

