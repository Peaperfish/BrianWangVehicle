import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
/**
 * A Sandstorm consists of a visual image and a method for moving it around to look
 * "stormy" as well as the behaviours to affect Pedestrians and manage it's own removal
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class SnowStorm extends Effect
{
    private int actsLeft;
    private double speed;
    private boolean turnedAround;
    private GreenfootSound wind;
    
    
    public SnowStorm () {
        actsLeft = 300; // 300 acts = ~5 seconds
        speed = 3.5;
        turnedAround = false;
        
        wind = new GreenfootSound("wind.wav");


    }

    public void addedToWorld (World w){
        if (image == null){
            image = drawSnow (getWorld().getWidth() * 2, getWorld().getHeight(), 1000);
        }
        setImage(image);

    }

    /**
     * Act - do whatever the Sandstorm wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        
        wind.play();
        
        actsLeft--;
        if (actsLeft <=60){
            fade (actsLeft, 60);
        }
        // vary speed
        int randomChange = Greenfoot.getRandomNumber(7) - 3; // -3 to 3
        double change = randomChange / 10.0; // -0.3 to 3

        if (getX() > 700 && !turnedAround && Greenfoot.getRandomNumber(120) == 0){
          speed *= -1;
           turnedAround = true;
        }

        if (getX() > 600 && !turnedAround){
            speed *= -1;
            turnedAround = true;
        }

        if (getX() > 0 && change < 0 || getX() < image.getWidth() && change > 0){
            //  speed += change;
        }
                

        setLocation (getX() + speed, getY());

        ArrayList<Pedestrian> peds = (ArrayList<Pedestrian>)getWorld().getObjects(Pedestrian.class);
        for (Pedestrian p : peds){
            p.windPush (speed);
        }

        if (actsLeft == 0){
            getWorld().removeObject(this);
        }
        // If acts left hits zero, I'm finished
    }

    /**
     * density should be 1-100. 100 will be almost completely white
     */
    public static GreenfootImage drawSnow (int width, int height, int density){

        Color[] swatch = new Color [32];

        int green = 255;

        // Build a color pallete out of shades of near-white yellow and near-white blue      
        for (int i = 0; i < swatch.length; i++){ // first half blue tones
            swatch[i] = new Color (255, green, 255);
            green += 0;
        }

        // The temporary image, my canvas for drawing
        GreenfootImage temp = new GreenfootImage (width, height);

        // Run this loop one time per "density"
        for (int i = 0; i < density; i++){
            for (int j = 0; j < 100; j++){ // draw 100 circles
                int randSize;
                // Choose a random colour from my swatch, and set its tranparency randomly
                int randColor = Greenfoot.getRandomNumber(swatch.length);
                //int randTrans = Greenfoot.getRandomNumber(220) + 35; // around half transparent
                temp.setColor (swatch[randColor]);

                //setTransparency(randTrans);
                // random locations for our dot
                int randX = Greenfoot.getRandomNumber (width);
                int randY = Greenfoot.getRandomNumber (height);

                int tempVal = Greenfoot.getRandomNumber(250);
                if (tempVal >= 1){
                    //randSize = 2;
                    temp.drawRect (randX, randY, 0, 0);
                }else{
                    randSize = Greenfoot.getRandomNumber (2) + 6;
                    temp.fillOval (randX, randY, randSize, randSize);
                }
                // silly way to draw a dot..
            }
        }

        return temp;
    }

}
