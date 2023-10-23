import greenfoot.*;

public class TopPedestrian extends Pedestrian {
    public TopPedestrian() {
        super(1); // Pass the direction as 1 for upwards movement
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
