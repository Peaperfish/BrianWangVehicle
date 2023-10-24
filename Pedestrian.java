import greenfoot.*; 

/**
 * An abstract Pedestrian class that serves as a base for concrete subclasses.
 */
public abstract class Pedestrian extends SuperSmoothMover {
    public double speed;
    private double maxSpeed;
    private int direction; // direction is always -1 or 1, for moving down or up, respectively
    private boolean awake;
    protected int startRotation;
    
    public Pedestrian(int direction) {
        maxSpeed = Math.random() * 2 + 1;
        speed = maxSpeed;
        awake = true;
        this.direction = direction;
    }

    /**
     * Abstract method for the behavior of the Pedestrian.
     */
    public abstract void act();

    public void knockDown() {
        speed = 0;
        setRotation(90);
        awake = false;
    }

    public void healMe() {
        speed = maxSpeed;
        setRotation(startRotation);
        awake = true;
    }

    public boolean isAwake() {
        return awake;
    }
}
