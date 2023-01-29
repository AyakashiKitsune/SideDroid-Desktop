package SideDroid.Desktop;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class ScreenCapture implements Runnable{
    Robot robot;
    Rectangle rectangle;
    Dimension dimension;
    BufferedImage imageCaptured;
    int x = 0;
    int delay;
    public void run() {
        try {
            while(true){
                robot = new Robot();
                imageCaptured = robot.createScreenCapture(rectangle);
                // System.out.println( ++x + " captured!");
                Thread.sleep(delay);
            }
        } catch (AWTException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    ScreenCapture(int delay){
        this.delay = delay;
        dimension = Toolkit.getDefaultToolkit().getScreenSize();
        this.rectangle = new Rectangle(dimension);
        System.out.println("CS built");
    }

    double getHeight(){
        return rectangle.getHeight();
    }
    double getWidth(){
        return rectangle.getWidth();
    }
    BufferedImage image(){
        // try {
        // Image image = imageCaptured.getScaledInstance(1280, 720, Image.SCALE_FAST);
        // ByteArrayOutputStream byteArrayOutputStream =  new ByteArrayOutputStream();
        // BufferedImage buffimage = new BufferedImage(1280,720, BufferedImage.TYPE_INT_RGB);
        // Graphics2D graphics = buffimage.createGraphics();
        // graphics.drawImage(image,0,0,null);
        // graphics.dispose();
        // ImageIO.write(buffimage, "png", byteArrayOutputStream);

        // ByteArrayOutputStream compressor = new ByteArrayOutputStream();
        // ImageOutputStream outputStream = ImageIO.createImageOutputStream(compressor);
        // ImageWriter imageWriter = ImageIO.getImageWritersByFormatName("jpg").next();
        // ImageWriteParam jpegWriteparam = imageWriter.getDefaultWriteParam();
        
        // jpegWriteparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        // jpegWriteparam.setCompressionQuality(0.1f);
    
        // imageWriter.setOutput(outputStream);

        //     imageWriter.write(null, new IIOImage(buffimage, null, null), jpegWriteparam);
        //     imageWriter.dispose();
        //     System.out.println(compressor.toByteArray().length);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        return imageCaptured;

       
    }
}
