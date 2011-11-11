package edu.invisiblecities;

import processing.core.PApplet;
import edu.invisiblecities.core.BaseInfovis;
import edu.invisiblecities.maps.BaseMap;
import edu.invisiblecities.maps.IsochronicMap.IsochronicMap;
import edu.invisiblecities.maps.TopologicalMap.TopologicalMap;

public class InvisibleCities extends PApplet{
    
    public final static int canvasWidth = 1400;
    public final static int canvasHeight = 800;
    public final static int backgroundColor = 255;
    public IsochronicMap isoMap = null;
    public BaseInfovis infovis = null;
    public static BaseMap showMap = null;
    
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
        //showMap = new IsochronicMap(this, 20);
        showMap = new TopologicalMap(this);
        showMap.init();
        textAlign(CENTER);
        ellipseMode(CENTER);
        smooth();
    }
    
    @Override
    public void draw() {
        background(backgroundColor);
        showMap.draw();
    }
    
    @Override
    public void mouseReleased() {
        showMap.mouseReleased();
    }
    
    @Override
    public void keyPressed() {
        
    }
    
    public static void main(String args[]) {
        PApplet.main(new String[] {"--present", "InvisibleCities"});
    }
}