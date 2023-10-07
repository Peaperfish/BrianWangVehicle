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

    /**
     * When a Car hit's a Pedestrian, it should knock it over
     */
    public boolean checkHitPedestrian () {
        Pedestrian p = (Pedestrian)getOneObjectAtOffset((int)speed + getImage().getWidth()/2, 0, Pedestrian.class);
        if (p != null)
        {
            p.knockDown();
            return true;
        }
        return false;
    }
    public void honkHorn() {
        try {
            // Load the audio file
            File audioFile = new File("car_horn.wav"); // Replace with your audio file path

            // Create an audio input stream
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);

            // Get the audio format
            AudioFormat format = audioInputStream.getFormat();

            // Create a data line for audio playback
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            // Open the data line and start playback
            SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceDataLine.open(format);
            sourceDataLine.start();

            // Read and play the audio data
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = audioInputStream.read(buffer)) != -1) {
                sourceDataLine.write(buffer, 0, bytesRead);
            }

            // Close the data line and audio input stream
            sourceDataLine.drain();
            sourceDataLine.close();
            audioInputStream.close();
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
