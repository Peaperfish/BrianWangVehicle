import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>The new and vastly improved 2022 Vehicle Simulation Assignment.</h1>
 * <p> This is the first redo of the 8 year old project. Lanes are now drawn dynamically, allowing for
 *     much greater customization. Pedestrians can now move in two directions. The graphics are better
 *     and the interactions smoother.</p>
 * <p> The Pedestrians are not as dumb as before (they don't want straight into Vehicles) and the Vehicles
 *     do a somewhat better job detecting Pedestrians.</p>
 * 
 * Version Notes - Feb 2023
 * --> Includes grid <--> lane conversion method
 * --> Now starts with 1-way, 5 lane setup (easier)
 * 
 * V2023_021
 * --> Improved Vehicle Repel (still work in progress)
 * --> Implemented Z-sort, disabled paint order between Pedestrians and Vehicles (looks much better now)
 * --> Implemented lane-based speed modifiers for max speed
 * 
 * 
 * this vehicel sim has some fetures, the tanks carries bombs which will kill the
 * pedestrian with expolsion of the bomb when the pedestrian is standing in front of it
 * the bomb will disappear when the bomb is used, every tank will only have 1 bomb on it
 * 
 * the car (honda civic) will run over the pedestrian and as a result will kill them, it will change
 * lanes after killing a pedestrian, it will also change langes seeing a dead pedestrian on the ground
 * it will also honk after killing a pedestrian
 * 
 * the ambulance will revive/heal the dead pedestrians, after getting healed/revived, 
 * the pedestrians will walk as if nothing happened till the other side of the road
 * 
 * the van/bus will kidnap pedestrians in front of it, even bodies
 * 
 * the tank will kill the pedestrians with the bomb, though it only has one, the explosion will
 * also destory the car
 * 
 * credits:
 * 
 * art:
 * 
 * https://sketchfab.com/3d-models/modified-honda-civic-720e9dac28214394b69874cc4641b6df
 * https://sketchfab.com/3d-models/toyota-hiace-passenger-van-l2h3-glx-2020-ec15323d57f04ba4976f0e758298b25c
 * https://sketchfab.com/3d-models/free-ambulance-b78313c16c9d46a7a67643245cf278d2
 * https://sketchfab.com/3d-models/cartoon-tank-4ad6a656ceb4417088fb4caebb4a95e8
 * https://stock.adobe.com/ca/images/realistic-falling-snow-with-snowflakes-and-clouds-winter-transparent-background-for-christmas-or-new-year-card-frost-storm-effect-snowfall-ice-vector-illustration/301013593
 * https://www.reddit.com/media?url=https%3A%2F%2Fi.redd.it%2F0rn1riv25oc51.png
 * https://sketchfab.com/3d-models/cyberpunk-riot-police-van-c5df7bd23f0b416bb3ae7255aece597c
 * 
 * 
 * sounds/sfx:
 * https://www.epidemicsound.com/sound-effects/screams/
 * https://www.youtube.com/watch?v=s5bwBS27A1g
 * https://www.youtube.com/watch?v=YF3pj_3mdMc
 * https://pixabay.com/sound-effects/car-horn-beep-beep-two-beeps-honk-honk-6188/
 * 
 * code:
 * mr. cohen
 * chatgpt:
 * i asked chatgtp about how the sound works becuase i have some problems with playing sfx, though
 * fixed it myself at the end
 * 
 * bugs:
 * sometimes the pedestrians gets stuck between the cars
 * 
 */
public class VehicleWorld extends World
{
    private GreenfootImage background;
    private GreenfootSound wind;

    // Color Constants
    public static Color GREY_BORDER = new Color (108, 108, 108);
    public static Color GREY_STREET = new Color (88, 88, 88);
    public static Color YELLOW_LINE = new Color (255, 216, 0);

    public static boolean SHOW_SPAWNERS = false;

    // Set Y Positions for Pedestrians to spawn
    public static final int TOP_SPAWN = 190; // Pedestrians who spawn on top
    public static final int BOTTOM_SPAWN = 705; // Pedestrians who spawn on the bottom

    // Instance variables / Objects
    private boolean twoWayTraffic, splitAtCenter;
    private int laneHeight, laneCount, spaceBetweenLanes;
    private int[] lanePositionsY;
    private VehicleSpawner[] laneSpawners;
    private List<Vehicle>[] vehiclesInLanes;
    private int nextSnowStormAct; // when to spawn the next SnowStorm
    private int actCount;
    private GreenfootSound city;

    /**
     * Constructor for objects of class MyWorld.
     * 
     * Note that the Constrcutor for the default world is always called
     * when you click the reset button in the Greenfoot scenario screen -
     * this is is basically the code that runs when the program start.
     * Anything that should be done FIRST should go here.
     * 
     */
    public VehicleWorld()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(900, 600, 1, false); 

        // This command (from Greenfoot World API) sets the order in which 
        // objects will be displayed. In this example, Pedestrians will
        // always be on top of everything else, then Vehicles (of all
        // sub class types) and after that, all other classes not listed
        // will be displayed in random order. 
        //setPaintOrder (Pedestrian.class, Vehicle.class); // Commented out to use Z-sort instead

        // set up background -- If you change this, make 100% sure
        // that your chosen image is the same size as the World
        background = new GreenfootImage ("background01.png");
        setBackground (background);

        // Set critical variables - will affect lane drawing
        laneCount = 6;
        laneHeight = 50;
        spaceBetweenLanes = 6;
        splitAtCenter = true;
        twoWayTraffic = false;

        Explosion.init();

        nextSnowStormAct = 300;

        actCount = 0;

        // Init lane spawner objects 
        laneSpawners = new VehicleSpawner[laneCount];

        // Prepare lanes method - draws the lanes
        lanePositionsY = prepareLanes (this, background, laneSpawners, 232, laneHeight, laneCount, spaceBetweenLanes, twoWayTraffic, splitAtCenter);

        laneSpawners[0].setSpeedModifier(0.8);
        laneSpawners[3].setSpeedModifier(1.4);

        setBackground (background);

        city = new GreenfootSound ("city.mp3");

    }

    public void act()
    {
        actCount++;
        spawn(); // Handle vehicle spawning
        zSort ((ArrayList<Actor>)(getObjects(Actor.class)), this);
        // Check for the world-wide effect every 5 seconds
        if (getObjects(Vehicle.class).isEmpty()) {
            // If there are no vehicles currently in the world, apply the blowback effect
            applyBlowBackEffect();
        }
        Explosion.init();
    }

    public void started(){
        city.playLoop(); 
    }

    public void stopped(){
        city.pause();
    }

    private void spawn () {
        // Chance to spawn a vehicle
        if (Greenfoot.getRandomNumber (60) == 0){
            int lane = Greenfoot.getRandomNumber(laneCount);
            if (!laneSpawners[lane].isTouchingVehicle()){
                int vehicleType = Greenfoot.getRandomNumber(4);
                if (vehicleType == 0){
                    addObject(new Car(laneSpawners[lane]), 0, 0);
                } else if (vehicleType == 1){
                    addObject(new Bus(laneSpawners[lane]), 0, 0);
                } else if (vehicleType == 2){
                    addObject(new Ambulance(laneSpawners[lane]), 0, 0);
                }else if (vehicleType == 3){
                    addObject(new Tank(laneSpawners[lane]), 0, 0);
                }
            }
        }

        if (Greenfoot.getRandomNumber(60) == 0) {
            int xSpawnLocation = Greenfoot.getRandomNumber(600) + 100; // random between 100 and 699, avoiding edges
            boolean spawnAtTop = Greenfoot.getRandomNumber(2) == 0;
            if (spawnAtTop) {
                addObject(new TopPedestrian(), xSpawnLocation, TOP_SPAWN);
            } else {
                addObject(new BottomPedestrian(), xSpawnLocation, BOTTOM_SPAWN);
            }
        }

        if (actCount == nextSnowStormAct){
            addObject (new SnowStorm(), 0, getHeight()/2); // actually start the SnowStorm

            // Calculate the INTERVAL - how long between spawns?
            // This presents a minimum of 600 and a maximum of 899 (600 + rand(0-299)
            // which represents about 10 to 15 seconds.
            int actsUntilNextStorm = Greenfoot.getRandomNumber (300) + 600;

            // Add the current actCount (it just keeps counting up) to the desired
            // interval to determing when the next SnowStorm should spawn
            nextSnowStormAct = actCount + actsUntilNextStorm;
        }

    }

    private void applyBlowBackEffect() {
        List<Vehicle> vehicles = getObjects(Vehicle.class);
        for (Vehicle vehicle : vehicles) {
            // Apply a force to move the vehicle backward (adjust as needed)
            vehicle.setLocation(vehicle.getX() - 50, vehicle.getY());
        }
    }

    /**
     *  Given a lane number (zero-indexed), return the y position
     *  in the centre of the lane. (doesn't factor offset, so 
     *  watch your offset, i.e. with Bus).
     *  
     *  @param lane the lane number (zero-indexed)
     *  @return int the y position of the lane's center, or -1 if invalid
     */
    public int getLaneY(int lane) {
        if (lane >= 0 && lane < lanePositionsY.length) {
            return lanePositionsY[lane];
        } 
        return -1;
    }

    /**
     * Given a y-position, return the lane number (zero-indexed).
     * Note that the y-position must be valid, and you should 
     * include the offset in your calculations before calling this method.
     * For example, if a Bus is in a lane at y=100, but is offset by -20,
     * it is actually in the lane located at y=80, so you should send
     * 80 to this method, not 100.
     * 
     * @param y - the y position of the lane the Vehicle is in
     * @return int the lane number, zero-indexed
     * 
     */
    public int getLane (int y){
        for (int i = 0; i < lanePositionsY.length; i++){
            if (y == lanePositionsY[i]){
                return i;
            }
        }
        return -1;
    }

    public static int[] prepareLanes (World world, GreenfootImage target, VehicleSpawner[] spawners, int startY, int heightPerLane, int lanes, int spacing, boolean twoWay, boolean centreSplit, int centreSpacing)
    {
        // Declare an array to store the y values as I calculate them
        int[] lanePositions = new int[lanes];
        // Pre-calculate half of the lane height, as this will frequently be used for drawing.
        // To help make it clear, the heightOffset is the distance from the centre of the lane (it's y position)
        // to the outer edge of the lane.
        int heightOffset = heightPerLane / 2;

        // draw top border
        target.setColor (GREY_BORDER);
        target.fillRect (0, startY, target.getWidth(), spacing);

        // Main Loop to Calculate Positions and draw lanes
        for (int i = 0; i < lanes; i++){
            // calculate the position for the lane
            lanePositions[i] = startY + spacing + (i * (heightPerLane+spacing)) + heightOffset ;

            // draw lane
            target.setColor(GREY_STREET); 
            // the lane body
            target.fillRect (0, lanePositions[i] - heightOffset, target.getWidth(), heightPerLane);
            // the lane spacing - where the white or yellow lines will get drawn
            target.fillRect(0, lanePositions[i] + heightOffset, target.getWidth(), spacing);

            // Place spawners and draw lines depending on whether its 2 way and centre split
            if (twoWay && centreSplit){
                // first half of the lanes go rightward (no option for left-hand drive, sorry UK students .. ?)
                if ( i < lanes / 2){
                    spawners[i] = new VehicleSpawner(false, heightPerLane, i);
                    world.addObject(spawners[i], target.getWidth(), lanePositions[i]);
                } else { // second half of the lanes go leftward
                    spawners[i] = new VehicleSpawner(true, heightPerLane, i);
                    world.addObject(spawners[i], 0, lanePositions[i]);
                }

                // draw yellow lines if middle 
                if (i == lanes / 2){
                    target.setColor(YELLOW_LINE);
                    target.fillRect(0, lanePositions[i] - heightOffset - spacing, target.getWidth(), spacing);

                } else if (i > 0){ // draw white lines if not first lane
                    for (int j = 0; j < target.getWidth(); j += 120){
                        target.setColor (Color.WHITE);
                        target.fillRect (j, lanePositions[i] - heightOffset - spacing, 60, spacing);
                    }
                } 

            } else if (twoWay){ // not center split
                if ( i % 2 == 0){
                    spawners[i] = new VehicleSpawner(false, heightPerLane, i);
                    world.addObject(spawners[i], target.getWidth(), lanePositions[i]);
                } else {
                    spawners[i] = new VehicleSpawner(true, heightPerLane, i);
                    world.addObject(spawners[i], 0, lanePositions[i]);
                }

                // draw Grey Border if between two "Streets"
                if (i > 0){ // but not in first position
                    if (i % 2 == 0){
                        target.setColor(GREY_BORDER);
                        target.fillRect(0, lanePositions[i] - heightOffset - spacing, target.getWidth(), spacing);

                    } else { // draw dotted lines
                        for (int j = 0; j < target.getWidth(); j += 120){
                            target.setColor (YELLOW_LINE);
                            target.fillRect (j, lanePositions[i] - heightOffset - spacing, 60, spacing);
                        }
                    } 
                }
            } else { // One way traffic
                spawners[i] = new VehicleSpawner(true, heightPerLane, i);
                world.addObject(spawners[i], 0, lanePositions[i]);
                if (i > 0){
                    for (int j = 0; j < target.getWidth(); j += 120){
                        target.setColor (Color.WHITE);
                        target.fillRect (j, lanePositions[i] - heightOffset - spacing, 60, spacing);
                    }
                }
            }
        }
        // draws bottom border
        target.setColor (GREY_BORDER);
        target.fillRect (0, lanePositions[lanes-1] + heightOffset, target.getWidth(), spacing);

        return lanePositions;
    }

    /**
     * A z-sort method which will sort Actors so that Actors that are
     * displayed "higher" on the screen (lower y values) will show up underneath
     * Actors that are drawn "lower" on the screen (higher y values), creating a
     * better perspective. 
     */
    public static void zSort (ArrayList<Actor> actorsToSort, World world){
        ArrayList<ActorContent> acList = new ArrayList<ActorContent>();
        // Create a list of ActorContent objects and populate it with all Actors sent to be sorted
        for (Actor a : actorsToSort){
            acList.add (new ActorContent (a, a.getX(), a.getY()));
        }    
        // Sort the Actor, using the ActorContent comparitor (compares by y coordinate)
        Collections.sort(acList);
        // Replace the Actors from the ActorContent list into the World, inserting them one at a time
        // in the desired paint order (in this case lowest y value first, so objects further down the 
        // screen will appear in "front" of the ones above them).
        for (ActorContent a : acList){
            Actor actor  = a.getActor();
            world.removeObject(actor);
            world.addObject(actor, a.getX(), a.getY());
        }
    }

    /**
     * <p>The prepareLanes method is a static (standalone) method that takes a list of parameters about the desired roadway and then builds it.</p>
     * 
     * <p><b>Note:</b> So far, Centre-split is the only option, regardless of what values you send for that parameters.</p>
     *
     * <p>This method does three things:</p>
     * <ul>
     *  <li> Determines the Y coordinate for each lane (each lane is centered vertically around the position)</li>
     *  <li> Draws lanes onto the GreenfootImage target that is passed in at the specified / calculated positions. 
     *       (Nothing is returned, it just manipulates the object which affects the original).</li>
     *  <li> Places the VehicleSpawners (passed in via the array parameter spawners) into the World (also passed in via parameters).</li>
     * </ul>
     * 
     * <p> After this method is run, there is a visual road as well as the objects needed to spawn Vehicles. Examine the table below for an
     * in-depth description of what the roadway will look like and what each parameter/component represents.</p>
     * 
     * <pre>
     *                  <=== Start Y
     *  ||||||||||||||  <=== Top Border
     *  /------------\
     *  |            |  
     *  |      Y[0]  |  <=== Lane Position (Y) is the middle of the lane
     *  |            |
     *  \------------/
     *  [##] [##] [##| <== spacing ( where the lane lines or borders are )
     *  /------------\
     *  |            |  
     *  |      Y[1]  |
     *  |            |
     *  \------------/
     *  ||||||||||||||  <== Bottom Border
     * </pre>
     * 
     * @param world     The World that the VehicleSpawners will be added to
     * @param target    The GreenfootImage that the lanes will be drawn on, usually but not necessarily the background of the World.
     * @param spawners  An array of VehicleSpawner to be added to the World
     * @param startY    The top Y position where lanes (drawing) should start
     * @param heightPerLane The height of the desired lanes
     * @param lanes     The total number of lanes desired
     * @param spacing   The distance, in pixels, between each lane
     * @param twoWay    Should traffic flow both ways? Leave false for a one-way street (Not Yet Implemented)
     * @param centreSplit   Should the whole road be split in the middle? Or lots of parallel two-way streets? Must also be two-way street (twoWay == true) or else NO EFFECT
     * 
     */
    public static int[] prepareLanes (World world, GreenfootImage target, VehicleSpawner[] spawners, int startY, int heightPerLane, int lanes, int spacing, boolean twoWay, boolean centreSplit){
        return prepareLanes (world, target, spawners, startY, heightPerLane, lanes, spacing, twoWay, centreSplit, spacing);
    }

}

/**
 * Container to hold and Actor and an LOCAL position (so the data isn't lost when the Actor is temporarily
 * removed from the World).
 */
class ActorContent implements Comparable <ActorContent> {
    private Actor actor;
    private int xx, yy;
    public ActorContent(Actor actor, int xx, int yy){
        this.actor = actor;
        this.xx = xx;
        this.yy = yy;
    }

    public void setLocation (int x, int y){
        xx = x;
        yy = y;
    }

    public int getX() {
        return xx;
    }

    public int getY() {
        return yy;
    }

    public Actor getActor(){
        return actor;
    }

    public String toString () {
        return "Actor: " + actor + " at " + xx + ", " + yy;
    }

    public int compareTo (ActorContent a){
        return this.getY() - a.getY();
    }

}
