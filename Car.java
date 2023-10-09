import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * The Car subclass
 */
public class Car extends Vehicle
{
    
    public Car(VehicleSpawner origin) {
        super(origin); // call the superclass' constructor
        maxSpeed = 1.5 + ((Math.random() * 30)/5);
        speed = maxSpeed;
        yOffset = 4;
        followingDistance = 6;
    }

    public void act()
    {
        super.act();
    }


        // public void honkHorn() {
        // try {
            // // Load the horn audio file
            // File hornAudioFile = new File("carHorn.mp3"); // Replace with your horn audio file path

            // // Create an audio input stream for the horn
            // AudioInputStream hornAudioInputStream = AudioSystem.getAudioInputStream(hornAudioFile);

            // // Get the audio format for the horn
            // AudioFormat format = hornAudioInputStream.getFormat();

            // // Create a data line for horn audio playback
            // DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            // // Open the data line for horn audio and start playback
            // SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
            // sourceDataLine.open(format);
            // sourceDataLine.start();

            // // Read and play the horn audio data
            // byte[] buffer = new byte[4096];
            // int bytesRead;

            // while ((bytesRead = hornAudioInputStream.read(buffer)) != -1) {
                // sourceDataLine.write(buffer, 0, bytesRead);
            // }

            // // Close the data line and horn audio input stream
            // sourceDataLine.drain();
            // sourceDataLine.close();
            // hornAudioInputStream.close();
        // } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            // e.printStackTrace();
        // }
    // }

    /**
     * When a Car hit's a Pedestrian, it should knock it over and honk the horn
     */
    public boolean checkHitPedestrian() {
        Pedestrian p = (Pedestrian)getOneObjectAtOffset((int)speed + getImage().getWidth()/2, 0, Pedestrian.class);
        if (p != null)
        {
            p.knockDown();
            //honkHorn(); // Call the honkHorn method when hitting a pedestrian
            return true;
        }
        return false;
    }
}
