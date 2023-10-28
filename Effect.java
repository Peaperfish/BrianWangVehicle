import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Superclass for Effects, which will contain useful behaviours 
 * to affect the way they are displayed.
 * 
 * 
 */
public class Effect extends SuperSmoothMover
{
    protected GreenfootImage image;
    
    // Transparency values ==> 0 (completely transparent) -->255 (opaque)
    protected void fade (int timeLeft, int totalFadeTime){
        double percent = timeLeft / (double)totalFadeTime;
        if (percent > 1.00) return;
        int newTransparency = (int)(percent * 255);
        image.setTransparency (newTransparency);
    }
    
}
