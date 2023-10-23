import greenfoot.*;

public class TopPedestrian extends Pedestrian {
    public TopPedestrian() {
        super(1);
        setRotation (90);
        enableStaticRotation();
    }

    public void act() {
        if (isAwake()) {
            move(speed);
            if (getY() <= 0) {
                knockDown();
            }
        }
    }
}
