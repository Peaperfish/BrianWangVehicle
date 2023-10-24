import greenfoot.*;

public class BottomPedestrian extends Pedestrian {
    public BottomPedestrian() {
        super(-1);
        startRotation = 270;
        setRotation(startRotation);
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
