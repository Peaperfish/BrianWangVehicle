import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
import java.util.List;

/**
 * This is the superclass for Vehicles.
 * 
 */
public abstract class Vehicle extends SuperSmoothMover
{
    protected double maxSpeed;
    protected double saveMaxSpeed;
    protected double speed;
    protected int direction; // 1 = right, -1 = left
    protected boolean moving;
    protected boolean isNew;
    protected int yOffset;
    protected VehicleSpawner origin;
    protected int followingDistance;
    protected int myLaneNumber;
    protected boolean isSwitchingLanes = false;
    protected boolean switchingLeft = false;
    protected boolean switchingRight = false;
    protected boolean checkedLeft = false;
    protected boolean checkedRight = false;
    protected boolean gotHeight = false;
    protected int laneYCoord;
    protected int carRotation;
    protected int rotation = 0;
    protected int nextLaneY;
    protected int turnCoolDown;
    private int actCount;
    
    
    
    private GreenfootSound honk; 

    private int[] lanePositionsY = {
            262, 316, 424, 478, 532, 586, 640 
        };

    protected boolean checkHitPedestrian() {
        int frontX = getX() + (int)(direction * getImage().getWidth() / 2); // Center point
        int leftX = getX() + (int)(direction * getImage().getWidth() / 2); // Left side point
        int rightX = getX() + (int)(direction * getImage().getWidth() / 2); // Right side point
        int y = getY();

        // Check for collisions at all three points
        boolean hitFront = isTouching(Pedestrian.class, frontX, 0);
        boolean hitLeft = isTouching(Pedestrian.class, leftX, -getImage().getHeight()/2);
        boolean hitRight = isTouching(Pedestrian.class, rightX, getImage().getHeight()/2);

        // If any of the points have a collision, return true
        return hitFront || hitLeft || hitRight;
    }

    // Helper method to check if a point is touching a Pedestrian
    private boolean isTouching(Class<?> cls, int x, int y) {
        Actor actor = getOneObjectAtOffset(x, y, cls);
        return actor != null;
    }

    public Vehicle (VehicleSpawner origin) {
        // remember the VehicleSpawner I came from. This includes information
        // about which lane I'm in and which direction I should face
        this.origin = origin;
        moving = true;
        // ask the Spawner that spawned me what my lane number is
        myLaneNumber = origin.getLaneNumber();
        // Determine if this lane is facing towards the right and
        // set the direction accordingly
        if (origin.facesRightward()){ // Right facing vehicles
            direction = 1;        
        } else { // left facing Vehicles
            direction = -1;
            // Reverse the image so it appears correct when moving the opposite direction
            getImage().mirrorHorizontally();
        }
        // If speed modifiers were set for lanes, this will change the max speed
        // accordingly. If speed modifiers are not set, this multiplies by 1.0 (as in,
        // does nothing).
        maxSpeed *= origin.getSpeedModifier();
        speed = maxSpeed;
        isNew = true;
        honk = new GreenfootSound("honk.wav");
    }

    /**
     * This method is called automatically when the Vehicle is added to the World, and places
     * the Vehicle just off screen (centered 100 pixels beyond the center of the lane spawner)
     * so it will appear to roll onto the screen smoothly.
     */
    public void addedToWorld (World w){
        if (isNew){
            setLocation (origin.getX() - (direction * 100), origin.getY() - yOffset);
            isNew = false;
        }
    }

    public Vehicle(VehicleSpawner origin, int laneNumber) {
        this.origin = origin;
        moving = true;
        myLaneNumber = laneNumber; // Set the lane number
        // The rest of your constructor logic
    }

    protected int getLaneNumber() {
        return myLaneNumber;
    }

    public List<Vehicle> getVehiclesInLane(int lane) {
        List<Vehicle> vehiclesInLane = new ArrayList<>();

        //Iterate through all Vehicle objects and add those in the specified lane
        for (Vehicle vehicle : getWorld().getObjects(Vehicle.class)) {
            if (vehicle.getLaneNumber() == lane) {
                vehiclesInLane.add(vehicle);
            }
        }

        return vehiclesInLane;
    }

    protected boolean isLaneCongested(int laneNumber) {
        int maxLaneCapacity = 5; // Define the maximum number of vehicles allowed in a lane
        int vehiclesInLane = getVehiclesInLane(laneNumber).size();
        return vehiclesInLane >= maxLaneCapacity;
    }

    protected boolean isOpenLane() {
        int adjacentLaneNumber = myLaneNumber + direction; // Calculate the adjacent lane
        return !isLaneCongested(adjacentLaneNumber); // Check if the adjacent lane is not congested
    }

    protected boolean shouldChangeLane() {
        boolean congested = isLaneCongested(myLaneNumber);
        boolean openLane = isOpenLane();

        // Decide if it's a good time to change lanes
        return congested && openLane;
    }

    /**
     * The superclass Vehicle's act() method. This can be called by a Vehicle subclass object 
     * (for example, by a Car) in two ways:
     * - If the Vehicle simply does NOT have a method called public void act(), this will be called
     *   instead. 
     * - subclass' act() method can invoke super.act() to call this, as is demonstrated here.
     */
    protected void changeLane() {
        // Check lane change conditions and perform the change if needed
        boolean congested = isLaneCongested(myLaneNumber); 
        boolean openLane = isOpenLane(); // Implement this method as per your simulation

        if (congested && openLane) {
            int newLaneNumber = myLaneNumber + (direction > 0 ? 1 : -1);
            if (isValidLaneNumber(newLaneNumber)) {
                myLaneNumber = newLaneNumber;
                //setLocation(getX(), getWorld().getLaneY(myLaneNumber));
                // Update any other properties as needed
            }
        }
    }

    public int getLaneY(int lane) {
        // Define the Y-coordinates for each lane (you may need to adjust these values)
        int[] lanePositionsY = {262, 316, 424, 478, 532, 586, 640};

        // Check if the provided lane is within a valid range
        if (lane >= 0 && lane < lanePositionsY.length) {
            return lanePositionsY[lane];
        }

        // Return a default value (e.g., -1) if the lane is out of bounds
        return -1;
    }

    private boolean isValidLaneNumber(int laneNumber) {
        return laneNumber >= 0 && laneNumber < lanePositionsY.length;
    }

    public void act() {
        drive(); // Your existing driving logic
        if (!checkHitPedestrian()) {
            repelPedestrians();
        }

        if (checkEdge()) {
            getWorld().removeObject(this);
            return;
        }

        // Call the lane change method here when needed
        if (shouldChangeLane()) {
            changeLane();
        }
    }

    /**
     * A method used by all Vehicles to check if they are at the edge.
     * 
     * Note that this World is set to unbounded (The World's super class is (int, int, int, FALSE) which means
     * that objects should not be stopped from leaving the World. However, this introduces a challenge as there
     * is the potential for objects to disappear off-screen but still be fully acting and thus wasting resources
     * and affecting the simulation even though they are not visible.
     */
    protected boolean checkEdge() {
        if (direction == 1)
        { // if moving right, check 200 pixels to the right (above max X)
            if (getX() > getWorld().getWidth() + 200){
                return true;
            }
        } 
        else 
        { // if moving left, check 200 pixels to the left (negative values)
            if (getX() < -200){
                return true;
            }
        }
        return false;
    }

    public void repelPedestrians() {
        ArrayList<Pedestrian> pedsTouching = (ArrayList<Pedestrian>)getIntersectingObjects(Pedestrian.class);

        ArrayList<Actor> actorsTouching = new ArrayList<Actor>();

        // this works, but doesn't ignore knocked down Pedestrians
        //actorsTouching.addAll(pedsTouching);
        for (Pedestrian p : pedsTouching){
            if (p.isAwake()){
                actorsTouching.add(p);
            }
        }

        pushAwayFromObjects(actorsTouching, 4);
    }

    /**
     * New repel method! Seems to work well. Can be used in both directions, but for now
     * commented out movement on x so players are only "repelled" in a y-direction.
     * 
     * @author Mr Cohen
     * @since February 2023
     */
    public void pushAwayFromObjects(ArrayList<Actor> nearbyObjects, double minDistance) {
        // Get the current position of this actor
        int currentX = getX();
        int currentY = getY();

        // Iterate through the nearby objects
        for (Actor object : nearbyObjects) {
            // Get the position and bounding box of the nearby object
            int objectX = object.getX();
            int objectY = object.getY();
            int objectWidth = object.getImage().getWidth();
            int objectHeight = object.getImage().getHeight();

            // Calculate the distance between this actor and the nearby object's bounding oval
            double distance = Math.sqrt(Math.pow(currentX - objectX, 2) + Math.pow(currentY - objectY, 2));

            // Calculate the effective radii of the bounding ovals
            double thisRadius = Math.max(getImage().getWidth() / 2.0, getImage().getHeight() / 2.0);
            double objectRadius = Math.max(objectWidth / 2.0, objectHeight / 2.0);

            // Check if the distance is less than the sum of the radii
            if (distance < (thisRadius + objectRadius + minDistance)) {
                // Calculate the direction vector from this actor to the nearby object
                int deltaX = objectX - currentX;
                int deltaY = objectY - currentY;

                // Calculate the unit vector in the direction of the nearby object
                double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                double unitX = deltaX / length;
                double unitY = deltaY / length;

                // Calculate the amount by which to push the nearby object
                double pushAmount = (thisRadius + objectRadius + minDistance) - distance;

                // Update the position of the nearby object to push it away

                object.setLocation(objectX, objectY + (int)(pushAmount * unitY));

                // 2d version, allows pushing on x and y axis, commented out for now but it works, just not the
                // effect I'm after:
                //object.setLocation(objectX + (int)(pushAmount * unitX), objectY + (int)(pushAmount * unitY));
            }
        }
    }

    /**
     * Method that deals with movement. Speed can be set by individual subclasses in their constructors

    public void drive() 
    {
    // Ahead is a generic vehicle - we don't know what type BUT
    // since every Vehicle "promises" to have a getSpeed() method,
    // we can call that on any vehicle to find out it's speed
    Vehicle ahead = (Vehicle) getOneObjectAtOffset (direction * (int)(speed + getImage().getWidth()/2 + 6), 0, Vehicle.class);
    double otherVehicleSpeed = -1;
    if (ahead != null) {

    otherVehicleSpeed = ahead.getSpeed();
    }

    // Various things that may slow down driving speed 
    // You can ADD ELSE IF options to allow other
    // factors to reduce driving speed.

    if (otherVehicleSpeed > 0 && otherVehicleSpeed < maxSpeed){ // Vehicle ahead is slower?
    speed = otherVehicleSpeed;
    } else {
    speed = maxSpeed; // nothing impeding speed, so go max speed
    }

    move (speed * direction);
    }   
     */
    
    // I got help starting from here
    
    public void drive() 
    {
        if(gotHeight == false){
            laneYCoord = getY(); 
            gotHeight = true;
        }

        if (shouldChangeLane()) {
            LaneChecker lcLeftLane = (LaneChecker)getOneObjectAtOffset(0, -48-6, LaneChecker.class);
            LaneChecker lcRightLane = (LaneChecker)getOneObjectAtOffset(0, +48+6, LaneChecker.class);    
            LaneChecker lcFront = (LaneChecker)getOneObjectAtOffset(direction * (int)(speed + getImage().getWidth()/2 + maxSpeed), 0, LaneChecker.class);
        
            
            if(!isSwitchingLanes){
                Vehicle ahead = (Vehicle) getOneObjectAtOffset (direction * (int)(speed + getImage().getWidth()/2 + maxSpeed), 0, Vehicle.class);
                VehicleWorld world = (VehicleWorld) getWorld();
                //change to if turning left and right instead of turning left
                if (ahead == null)
                {
                    speed = maxSpeed;
                }
                
                else if(ahead.getIsSwitchingLanes() == false && turnCoolDown > 120 ){//&& world.getActs() > 120){
                    lcLeftLane = (LaneChecker)getOneObjectAtOffset(0, -48-6, LaneChecker.class);
                    lcRightLane = (LaneChecker)getOneObjectAtOffset(0, +48+6, LaneChecker.class);        
                    if(lcLeftLane == null){
                        putLeftLaneChecker(getY());
                        lcLeftLane = (LaneChecker)getOneObjectAtOffset(0, -48-6, LaneChecker.class);
                        if(lcLeftLane != null){
                            if(lcLeftLane.amTouchingVehicle() == true){
                                getWorld().removeObject(lcLeftLane);
                            }
                            else{
                                isSwitchingLanes = true;
                                switchingLeft = true;
                                laneYCoord = getY()-48 - 6;
                                //maxSpeed+= 10;
                                setRotation(-45);
                                //return true;
                            }
                        }
                    }
                    
                    if(!isSwitchingLanes && lcRightLane == null){
                        putRightLaneChecker(getY());
                        lcRightLane = (LaneChecker)getOneObjectAtOffset(0, +48+6, LaneChecker.class);
                        if(lcRightLane != null){
                            if(lcRightLane.amTouchingVehicle() == true){
                                getWorld().removeObject(lcRightLane);
                            }
                            else{
                                isSwitchingLanes = true;
                                switchingRight = true;
                                laneYCoord = getY()+48 + 6;
                                //maxSpeed+= 10;
                                setRotation(45);
                                //return true;
                            }
                        }

                    }
                    if(!isSwitchingLanes && getRotation() == 0){
                        //if lanes happen to have cars on them
                        honk.play();
                        speed = 0;
                    }
                    //ends here
                }
                else{
                    //if vehicle infront is switching lanes
                    speed = 0;
                }
                if(lcFront != null && isTouching(LaneChecker.class) && getRotation() == 0 && !isSwitchingLanes && turnCoolDown > 120)
                    speed = lcFront.getSpeed()*2;
                else if(lcFront != null && !isTouching(LaneChecker.class) && getRotation() == 0 && !isSwitchingLanes && turnCoolDown > 120)
                    speed = lcFront.getSpeed();
            }
            else if(isSwitchingLanes && switchingLeft){        
                if(checkSwitchedLeftLane(laneYCoord, maxSpeed) == true){
                    getWorld().removeObject(lcFront);
                }
                turnCoolDown = 0;
            }
            else if(isSwitchingLanes && switchingRight){
                if(checkSwitchedRightLane(laneYCoord, maxSpeed) == true){
                    getWorld().removeObject(lcFront);
                }
                turnCoolDown = 0;
            }

            turnCoolDown++;
            move (speed * direction);

        }   
    }
    // got help ends
    
    
    //Returns if the vehicle is switching lanes
    public boolean getIsSwitchingLanes(){
        return isSwitchingLanes;
    }

    //returns whether if the vehicle is moving to the left lane
    public boolean checkSwitchedLeftLane(int destinationY, double speed){
        if(getY() -speed <= destinationY){
            setLocation(getX(),destinationY);
            setRotation(0);
            isSwitchingLanes = false;
            switchingLeft = false;
            maxSpeed = saveMaxSpeed;
            return true;
        }
        move(direction*maxSpeed);
        return false;
    }
    //returns whether if the vehicle is moving to the right lane
    public boolean checkSwitchedRightLane(int destinationY, double speed){
        if(getY() + speed >= destinationY){
            setLocation(getX(),destinationY);
            setRotation(0);
            isSwitchingLanes = false;
            switchingRight = false;
            maxSpeed = saveMaxSpeed;
            return true;
        }
        move(direction*maxSpeed);

        return false;
    }
    //Puts a left lane checker if it is in a viable spot (if its to be placed off road it will not place)
    public boolean putLeftLaneChecker(int y){
        VehicleWorld world = (VehicleWorld) getWorld();
        int lane = world.getLane(y);
        int laneY = world.getLaneY(lane);
        if(lane != 0){
            getWorld().addObject(new LaneChecker(speed, direction,(int)(getImage().getWidth()*3), "left"),getX(),getY()-48-6);
            return true;
        }
        return false;

    }
    //Puts a right lane checker if it is in a viable spot (if its to be placed off road it will not place)
    public boolean putRightLaneChecker(int y){
        VehicleWorld world = (VehicleWorld) getWorld();
        int lane = world.getLane(y);
        int laneY = world.getLaneY(lane);
        if(lane != 5){
            getWorld().addObject(new LaneChecker(speed, direction,(int)(getImage().getWidth()*3), "right"),getX(),getY()+48+6);
            return true;
        }
        return false;
    }
    /**
     * An accessor that can be used to get this Vehicle's speed. Used, for example, when a vehicle wants to see
     * if a faster vehicle is ahead in the lane.
     */
    public double getSpeed(){
        return speed;
    }
    
    public int getActs(){
        return actCount;
    }
}
