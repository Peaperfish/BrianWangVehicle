import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * The Car subclass
 */
public class Car extends Vehicle
{
    private static GreenfootSound[] honk;
    private static int honkSoundsIndex = 0; 

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

    public void honkHorn() {
        honkSoundsIndex = 0;

        honk[1] = new GreenfootSound("honk.wav");

        honk[honkSoundsIndex].play();
    }

    /**
     * When a Car hit's a Pedestrian, it should knock it over and honk the horn
     */
    public boolean checkHitPedestrian() {
        Pedestrian p = (Pedestrian)getOneObjectAtOffset((int)speed + getImage().getWidth()/2, 0, Pedestrian.class);
        if (p != null)
        {
            p.knockDown();
            // Call the honkHorn method when hitting a pedestrian
            honk[honkSoundsIndex].play();
            return true;
        }
        return false;
    }
}
