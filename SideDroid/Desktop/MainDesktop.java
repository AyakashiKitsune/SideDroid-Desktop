package SideDroid.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderedImageFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JComponent;
import javax.swing.JFrame;

class MainDesktop extends JFrame{
    String title = "SideDroid-Server";
    static int width = 1280;
    static int height = 720; 
    Dimension windowSize = new Dimension(width+10, height+30);
    
    MainDesktop(){
        setTitle(title);
        setSize(windowSize);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        System.out.println("Frame built");
    }
    static MainDesktop desktop;
    static ScreenCapture screenCapture;
    public static void main(String[] args) throws InterruptedException, IOException{
        desktop = new MainDesktop();
        int delay = 16;
        screenCapture =  new ScreenCapture(delay);
        Thread thread = new Thread(screenCapture, "screencapture");
        
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                thread.start();
            }
        }, 300);
        
        Thread.sleep(1000);
        JComponent canvas = new JComponent() {
            protected void paintComponent(Graphics g) {
                //call repaint(g) here instead of this
                g.drawImage(screenCapture.image().getScaledInstance(width, height,Image.SCALE_FAST),0,0, null);
            };  
        };
        desktop.add(canvas);
        desktop.setVisible(true);
        ServerSocket serverSocket = new ServerSocket(8080);
        Thread sendThread = new Thread(new Runnable() {
            Socket socketclient;
            @Override
            public void run() {
                try {
                    socketclient = serverSocket.accept();
                    // serverSocket.close();
                    // DatagramSocket datagramserver = new DatagramSocket(4560);
                    String format = "jpeg";
                    DataOutputStream dataoutputstream = new DataOutputStream(socketclient.getOutputStream());
                    int height = 720;
                    int width = (height * 16)/9;
                    while(true){
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Image image = screenCapture.image().getScaledInstance(width , height, Image.SCALE_FAST);
                        ByteArrayOutputStream byteArrayOutputStream =  new ByteArrayOutputStream();
                        BufferedImage buffimage = new BufferedImage(width , height, BufferedImage.TYPE_INT_RGB);
                        Graphics2D graphics = buffimage.createGraphics();
                        graphics.drawImage(image,0,0,null);
                        graphics.dispose();
                        ImageIO.write(buffimage, format, byteArrayOutputStream);
                        // byte[] bites = byteArrayOutputStream.toByteArray();
                        // System.out.println("sending " + bites.length);
                        // dataoutputstream.writeInt(bites.length);
                        
                        ByteArrayOutputStream compressor = new ByteArrayOutputStream();
                        ImageOutputStream outputStream = ImageIO.createImageOutputStream(compressor);
                        ImageWriter imageWriter = ImageIO.getImageWritersByFormatName(format).next();
                        ImageWriteParam jpegWriteparam = imageWriter.getDefaultWriteParam();
                            
                        jpegWriteparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                        jpegWriteparam.setCompressionQuality(1f);
                        
                        imageWriter.setOutput(outputStream);
                    
                        imageWriter.write(null, new IIOImage(buffimage, null, null), jpegWriteparam);
                        imageWriter.dispose();
                        System.out.println(compressor.toByteArray().length);
                        dataoutputstream.writeInt(compressor.size());
                        dataoutputstream.write(compressor.toByteArray());;
                        System.out.println("done sent");
                        
                    }

                } catch (IOException e) {
                    System.out.println("error daw " +e.getMessage());
                    while(true){
                        try {
                            // socketclient = datagramserver.accept();
                            // if(socketclient.isConnected()){
                            //     System.out.println("Connected");
                            // }
                        } catch (Exception e1) {
                        }
                    }
                }                    
            } 
        });
        sendThread.start();
        while(true){
            Thread.sleep(delay);
            // System.out.println("repainting");
            canvas.repaint();
        }
    }
}

