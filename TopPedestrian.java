import greenfoot.*;

public class TopPedestrian extends Pedestrian {
    public TopPedestrian() {
        super(1);
        startRotation = 90;
        setRotation(startRotation);
        
        enableStaticRotation();
    }

    public void act()
    {
        // Awake is false if the Pedestrian is "knocked down"
        if (awake){
            // Check in the direction I'm moving vertically for a Vehicle -- and only move if there is no Vehicle in front of me.
            if (getOneObjectAtOffset(0, (int)(direction * getImage().getHeight()/2 + (int)(direction * speed)), Vehicle.class) == null){
                setLocation (getX(), getY() + (int)(speed*direction));
            }
            if (direction == -1 && getY() < 100){
                getWorld().removeObject(this);
            } else if (direction == 1 && getY() > getWorld().getHeight() - 30){
                getWorld().removeObject(this);
            }

        }
    }
}
