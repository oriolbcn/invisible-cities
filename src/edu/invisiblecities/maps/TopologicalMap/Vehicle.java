package edu.invisiblecities.maps.TopologicalMap;

import processing.core.PApplet;
import processing.core.PVector;
import de.fhpotsdam.unfolding.geo.Location;
import edu.invisiblecities.core.BaseInfovis;
import edu.invisiblecities.utils.mathFunctions;

//
public class Vehicle implements BaseInfovis {
    
    private PApplet parent;
    
    public float[]    curScreenPosition;    //
    public float[]    preScreenPosition;    //
    public float[]    curMapPosition;       //
    public float[]    preMapPosition;       //
    public PVector    speed;                // Vector
    public float      velocity = 1.2f;      // Scalar
    public Route      route;                // Description of route, start from 0 in order
    public float[][]  screenPositions;
    public boolean    inUse;
    public float      diameter = 10.f;
    private int       index = 1;
    private Location  loc;
    

    public Vehicle(PApplet p, Route r) {
        
        parent = p;
        route = r;
        screenPositions = r.screenPositions;
        curScreenPosition = new float[2];
        curScreenPosition[0] = screenPositions[0][0];
        curScreenPosition[1] = screenPositions[0][1];
        preScreenPosition = new float[2];
        preScreenPosition[0] = curScreenPosition[0];
        preScreenPosition[1] = curScreenPosition[1];
        speed = new PVector();
        loc = new Location(0, 0);
    }
    
    public void freeze() {
        
        // Copy by value, not by reference
        Location loc 
            = route.map.getLocationFromScreenPosition(curScreenPosition[0], 
                                                      curScreenPosition[1]);
        curMapPosition[0] = loc.x;
        curMapPosition[1] = loc.y;
        loc = route.map.getLocationFromScreenPosition(preScreenPosition[0], 
                                                      preScreenPosition[1]);
        preMapPosition[0] = loc.x;
        preMapPosition[1] = loc.y;
    }
    
    // Check if the vehicle is off track
    private boolean isOnline(float[] ps, float[] pe, float[] p) {
        
        float disAll = mathFunctions.getDistance(ps[0], ps[1], pe[0], pe[1]);
        float disSP = mathFunctions.getDistance(ps[0], ps[1], p[0], p[1]);
        float disPE = mathFunctions.getDistance(p[0], p[1], pe[0], pe[1]);
        if (java.lang.Math.abs(disSP + disPE - disAll) < mathFunctions.epsilon)
            return true;
        else return false;
    }
    
    public void move() {
        
        if (isOnline(screenPositions[index-1],
                     screenPositions[index],
                     curScreenPosition)) {
            curScreenPosition[0] += speed.x;
            curScreenPosition[1] += speed.y;
        } else {
            ++index;
            if (index == screenPositions.length) {
                resetVehicle();
                return;
            }
            setCurScreenPosition(screenPositions[index-1]);
            updateSpeed(screenPositions[index], screenPositions[index-1]);
        }
    }
    
    public void updateSpeed(float[] to, float[] from) {
        
        speed.x = to[0] - from[0];
        speed.y = to[1] - from[1];
        speed.normalize();
        speed.mult(velocity);
    }
    
    public void setCurScreenPosition(float[] xy) {
        
        curScreenPosition[0] = xy[0];
        curScreenPosition[1] = xy[1];
    }
    
    public void resetVehicle() {
        
        index = 1;
        float tx = screenPositions[1][0] - screenPositions[0][0];
        float ty = screenPositions[1][1] - screenPositions[0][1];
        updateSpeed(screenPositions[1], screenPositions[0]);
        setCurScreenPosition(screenPositions[0]);
    }
    
    public void recalculateCurScreenPosition() {
        
        loc.x = curMapPosition[0];
        loc.y = curMapPosition[1];
        float[] xy = route.map.getScreenPositionFromLocation(loc);
        curScreenPosition[0] = xy[0];
        curScreenPosition[1] = xy[1];
    }
    
    @Override
    public void init() {
        
        route.addNewVehicle(this);
        inUse = true;
        int length = screenPositions.length;
        if (index >= length-1 || index <= 0) index = 1;
        updateSpeed(screenPositions[index], screenPositions[index-1]);
    }

    @Override
    public void draw() {
        parent.ellipse(curScreenPosition[0], curScreenPosition[1], diameter, diameter);
    }

    @Override
    public void mousePressed() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseReleased() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void keyPressed() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void keyReleased() {
        // TODO Auto-generated method stub
        
    }
}
