# Vehicle Simulation

A traffic and pedestrian simulation in Java, built with [Greenfoot](https://www.greenfoot.org) for a high-school computer science course (2023). The road, lanes and spawning come from the course's starter framework; the vehicles, interactions, effects and sound are my additions.

## What happens

- **Tanks** each carry one bomb. When a pedestrian stands in front of a tank, the bomb goes off with an explosion and the bomb is used up.
- **Cars** (a Honda Civic) knock down pedestrians they run into.
- **Ambulances** revive knocked-down pedestrians, who then get up and finish crossing the road.
- **Buses** share the road with everything else.
- **Snowstorms** roll in from time to time as a visual effect.
- Pedestrians cross from both sides of the road.
- Sound: city ambience, wind, honks, sirens and explosions.

## Running it

1. Install [Greenfoot](https://www.greenfoot.org/download).
2. Open `project.greenfoot` in this folder.
3. Press **Run**.

The main classes are `VehicleWorld` (the world and lanes), `Vehicle` and its subclasses (`Car`, `Bus`, `Ambulance`, `Tank`), `Pedestrian`, and the effects (`Explosion`, `SnowStorm`).
