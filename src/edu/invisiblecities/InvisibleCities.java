package edu.invisiblecities;

import processing.core.PApplet;
import edu.invisiblecities.maps.IsochronicMap;

public class InvisibleCities extends PApplet{
    
    public final static int canvasWidth = 1400;
    public final static int canvasHeight = 800;
    public final static int backgroundColor = 255;
    public IsochronicMap isoMap = null;
    
    /*
    public static InvisibleCities singleton = null;
    
    public static InvisibleCities getInstance()
    {
        if(singleton == null) {
            singleton = new InvisibleCities();
            singleton.canvasHeight = 800;
            singleton.canvasWidth = 1000;
        }
        return singleton;
    } 
    */
    
    @Override
    public void setup() {
        size(canvasWidth, canvasHeight);
        textAlign(CENTER);
        ellipseMode(CENTER);
        isoMap = new IsochronicMap(this, 20, width/2, height/2);
        isoMap.init();
    }
    
    @Override
    public void draw() {
        background(backgroundColor);
        isoMap.draw();
    }
    
    @Override
    public void mouseReleased() {
        isoMap.interact();
    }
    
    public static void main(String args[]) {
        PApplet.main(new String[] {"--present", "InvisibleCities"});
    }
}