import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class BombTruck here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Tank extends Vehicle
{
    private GreenfootImage image, emptyImage;
    private boolean hasBomb;

    public Tank(VehicleSpawner origin) {
        super(origin); // call the superclass' constructor
        maxSpeed = 1.5 + ((Math.random() * 30)/5);
        speed = maxSpeed;
        yOffset = 22;
        followingDistance = 6;
        image = new GreenfootImage("Tank.png");
        emptyImage = new GreenfootImage("Tank(1).png");
        setImage(image);
        hasBomb = true;
    }

    public void act()
    {
        super.act();
    }

    /**
     * When a Car hit's a Pedestrian, it should knock it over
     */
    public boolean checkHitPedestrian () {
        if (hasBomb){
            Pedestrian p = (Pedestrian)getOneObjectAtOffset((int)speed + getImage().getWidth()/2, 0, Pedestrian.class);
            if (p != null)
            {
                hasBomb = false;
                setImage(emptyImage);
                getWorld().addObject(new Explosion(300), getX(), getY());
            }
        }
        return false;
    }
}
