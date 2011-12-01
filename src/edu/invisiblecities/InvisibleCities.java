package edu.invisiblecities;

import processing.core.PApplet;
import edu.invisiblecities.core.BaseInfovis;
import edu.invisiblecities.maps.BaseMap;
import edu.invisiblecities.maps.IsochronicMap.IsochronicMap;
import edu.invisiblecities.maps.TopologicalMap.TopoMap;
import edu.invisiblecities.maps.rain.Rain;

public class InvisibleCities extends PApplet {
    
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
        showMap = new IsochronicMap(this);
        //showMap = new Rain(this);
        //showMap = new TopoMap(this);
        showMap.init();
        textAlign(LEFT);
        ellipseMode(CENTER);
        //colorMode(HSB);
        smooth();
    }
    
    @Override
    public void draw() {
        //background(backgroundColor);
        showMap.draw();
    }
    
    @Override
    public void mouseReleased() {
        showMap.mouseReleased();
    }

    @Override
    public void mousePressed() {
        showMap.mousePressed();
    }
    
    @Override
    public void keyPressed() {
        showMap.keyPressed();
    }
    
    @Override
    public void keyReleased() {
        showMap.keyReleased();
    }
    
    
    public static void main(String args[]) {
        PApplet.main(new String[] {"--present", "InvisibleCities"});
    }
}