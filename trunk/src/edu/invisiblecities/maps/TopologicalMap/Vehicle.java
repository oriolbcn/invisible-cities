package edu.invisiblecities.maps.TopologicalMap;

import processing.core.PApplet;
import processing.core.PVector;
import de.fhpotsdam.unfolding.geo.Location;
import edu.invisiblecities.core.BaseInfovis;
import edu.invisiblecities.utils.mathFunctions;

//
public class Vehicle implements BaseInfovis {
    
    protected static PApplet parent;
    
    public float[]    curScreenPosition;    //
    public float[]    preScreenPosition;    //
    public float[]    curMapPosition;       //
    public float[]    preMapPosition;       //
    public PVector    speed;                // Vector
    public float      velocity = 1.2f;      // Scalar
    public Route      route;                // Description of route, start from 0 in order
    public float[][]  screenPositions;
    public int        stopSize;
    public boolean    inUse = false;
    public float      diameter = 10.f;
    public int[]      timeStamps;
    public float[]    sectionSpeed;
    public int        fred;
    public int        fgreen;
    public int        fblue;
    
    private int       index;
    private Location  loc;
    
    private int       step;
    private boolean   reversed = false;
    private int       startIndex;
    private int       endIndex;
    
    private static final float epsilon = 0.0001f;

    public Vehicle(PApplet p, Route r, int red, int green, int blue) {
        parent = p;
        route = r;
        
        curScreenPosition = new float[2];
        preScreenPosition = new float[2];
        
        curMapPosition = new float[2];
        preMapPosition = new float[2];
        speed = new PVector();
        loc = new Location(0, 0);
        fred = red;
        fgreen = green;
        fblue = blue;
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
        if (java.lang.Math.abs(disSP + disPE - disAll) < epsilon)
            return true;
        else return false;
    }
    
    public void move() {
        if (isOnline(screenPositions[index-step],
                     screenPositions[index],
                     curScreenPosition)) {
            curScreenPosition[0] += speed.x;
            curScreenPosition[1] += speed.y;
        } else {
            index += step;
            if (index == endIndex) {
                resetVehicle();
                TopologicalMap.globalTimer = 0;
                return;
            }
            setCurScreenPosition(screenPositions[index-step]);
            updateVelocity();
            updateSpeed(screenPositions[index], screenPositions[index-step]);
        }
    }
    
    public void updateVelocity() {
        velocity = sectionSpeed[index] * (1000 / parent.frameRate);
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
        index = startIndex;
        updateVelocity();
        updateSpeed(screenPositions[index], screenPositions[index-step]);
        setCurScreenPosition(screenPositions[index-step]);
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
        screenPositions = route.screenPositions;
        stopSize = screenPositions.length;
        
        step = 1;
        startIndex = 1;
        endIndex = stopSize;
        
        if (reversed) {
            step = -1;
            startIndex = stopSize - 2;
            endIndex = -1;
        }
        index = startIndex;
        
        curScreenPosition[0] = screenPositions[startIndex-step][0];
        curScreenPosition[1] = screenPositions[startIndex-step][1];
        preScreenPosition[0] = curScreenPosition[0];
        preScreenPosition[1] = curScreenPosition[1];
        
        inUse = true;
        index = startIndex;
        sectionSpeed = new float[route.distanceBetweenStops.length];
        calculateSectionSpeeds();
        updateVelocity();
        updateSpeed(screenPositions[index], screenPositions[index-step]);
    }
    
    public void calculateSectionSpeeds() {
        for (int i=0; i<route.distanceBetweenStops.length; ++i) {
            sectionSpeed[i] = route.distanceBetweenStops[i] / timeStamps[i];
        }
    }

    @Override
    public void draw() {
        if (parent.mousePressed && TopologicalMap.pause) {
            recalculateCurScreenPosition();
        } else {
            move();
        }
        parent.fill(fred, fgreen, fblue);
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
