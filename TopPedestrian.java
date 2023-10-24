import greenfoot.*;

public class TopPedestrian extends Pedestrian {
    public TopPedestrian() {
        super(1);
        setRotation(90);
        enableStaticRotation();
    }

    public void act() {
        if (isAwake()) {
            move(speed);
            if (getY() >= getWorld().getHeight()) {
                getWorld().removeObject(this); // Remove the TopPedestrian when it goes out of the screen
            }
        }
    }
}
