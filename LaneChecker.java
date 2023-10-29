import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * A Spawner object is a place where a Vehicle can spawn. Each spawner is
 * able to check if there is already a Vehicle in the spot to avoid spawning
 * multiple Vehicles on top of each other.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class LaneChecker extends SuperSmoothMover
{
    public static final Color TRANSPARENT_RED = new Color (255, 0, 0, 128);

    private GreenfootImage image;
    private boolean rightward;
    private boolean visible;
    private int height, width;
    private double speed;
    private int direction;
    private String topOrBottomLane;

    public LaneChecker (double speed, int direction, int vehicleWidth, String topOrBottomLane)
    {
        //Sets up lane's prpoerties such as length,width, speed, direction, and visibily
        this.topOrBottomLane = topOrBottomLane;
        this.speed = speed;
        this.direction = direction;
        this.rightward = rightward;
        VehicleWorld world = (VehicleWorld) getWorld();
        this.height = (int)(48 * 0.75);
        width = vehicleWidth;
        visible = false;
        image = new GreenfootImage (width, height);
        if(visible){
            image.setColor(TRANSPARENT_RED);
            image.fillRect(0, 0, width-1, height - 1);
        }
        setImage(image);
    }

    public void act(){
        move(speed*direction);
        if(isAtEdge()){
            getWorld().removeObject(this);
        }
    }
    public double getSpeed(){
        return speed;
    }
    public boolean amTouchingVehicle(){
        if(topOrBottomLane.equals("left")){
            Bus busInfrontTopHalf = (Bus)getOneObjectAtOffset((int)speed + getImage().getWidth()/2, -4, Bus.class);
            Vehicle vInfrontBottomLane = (Vehicle)getOneObjectAtOffset((int)speed + getImage().getWidth()/2, +48, Vehicle.class);
            Vehicle vInfrontSameLane = (Vehicle)getOneObjectAtOffset((int)speed + getImage().getWidth()/2, 0, Vehicle.class);
            
            //null = ethier there is no bus or it's the top half of the bus

            //If it is a vehicle exclusing bus's offset
            if(isTouchingVehicle() && (busInfrontTopHalf == null) && (vInfrontBottomLane != null && vInfrontBottomLane.getClass() == Bus.class)){
                return true;
            }
            //if its a bus on the same lane
            else if (isTouchingVehicle()){
                return true;
            }
            else{
                return false;
            }
        }
        else{
            if(!isTouchingVehicle()){
                return true;
            }
            else{
                return false;
            }
        }
    }
    //Returns if is touching a vehicle or not
    public boolean isTouchingVehicle () {
        return this.isTouching(Vehicle.class);
    }
}
