import greenfoot.*;

/**
 * The Car subclass
 */
public class Car extends Vehicle
{
    private static GreenfootSound honk; // Change to a single GreenfootSound instance

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
    }

    public void act() {
        // Check for a car in front
        Vehicle carInFront = (Vehicle) getOneObjectAtOffset(0, -15, Vehicle.class);

        if (carInFront != null) {
            // There is a car in front within 15 units
            setLocation(getX(), getY() - 54); // Move the car up 54 units
        }
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
            
            return true;
        }
        return false;
    }
}
