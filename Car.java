import greenfoot.*;

/**
 * The Car subclass
 */
public class Car extends Vehicle
{
    private GreenfootSound honk; 
    private GreenfootSound speeding;
    
    
    public Car(VehicleSpawner origin) {
        super(origin); // Call the superclass' constructor
        maxSpeed = 1.5 + ((Math.random() * 30) / 5);
        speed = maxSpeed;
        yOffset = 4;
        followingDistance = 6;
        
        // Initialize the honk sound
        if (honk == null) {
            honk = new GreenfootSound("honk.wav");
        }
        
        if (speeding == null) {
            speeding = new GreenfootSound("speeding.wav");
        }
    }

    public void act() {
        super.act();
    }

    /**
     * When a Car hits a Pedestrian, it should knock it over and honk the horn.
     */
    public boolean checkHitPedestrian() {
        Pedestrian p = (Pedestrian) getOneObjectAtOffset((int) speed + getImage().getWidth() / 2, 0, Pedestrian.class);
        if (p != null) {
            p.knockDown();
            
            // Play the honk sound
            honk.play();
            speeding.play();
            if (getY() == 259) {
                setLocation(getX(), getY() + 56);
            } else if (getY() == 536){
                setLocation(getX(), getY() - 56);
            } else {
                setLocation(getX(), getY() - 56);
            }
            
            return true;
        }
        return false;
    }
}
