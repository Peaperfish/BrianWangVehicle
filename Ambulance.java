import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

/**
 * The Ambulance subclass
 */
public class Ambulance extends Vehicle
{
    public Ambulance(VehicleSpawner origin){
        super (origin); // call the superclass' constructor first
        
        maxSpeed = 2.5;
        speed = maxSpeed;
    }

    /**
     * Act - do whatever the Ambulance wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        super.act();
    }
    
    public boolean checkHitPedestrian() {
        Pedestrian pedestrian = (Pedestrian)getOneIntersectingObject(Pedestrian.class);
        
        if (pedestrian != null) {
            pedestrian.healMe();
            return true; // Collision detected
        }
        
        return false; // No collision
}


}
