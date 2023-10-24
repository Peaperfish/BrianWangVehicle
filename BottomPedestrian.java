import greenfoot.*;

public class BottomPedestrian extends Pedestrian {
    public BottomPedestrian() {
        super(-1);
        setRotation(270);
        enableStaticRotation();
    }

    public void act() {
        if (isAwake()) {
            move(speed);
            if (getY() <= 0) {
                getWorld().removeObject(this); // Remove the Pedestrian when it goes out of the screen
            }
        }
    }
}
