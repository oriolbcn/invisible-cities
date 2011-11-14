package edu.invisiblecities.maps.TopologicalMap;

import java.util.ArrayList;

import processing.core.PApplet;
import de.fhpotsdam.unfolding.geo.Location;
import edu.invisiblecities.core.BaseInfovis;
import edu.invisiblecities.utils.mathFunctions;

public class Route implements BaseInfovis {
    
    private PApplet parent;
    public float[][] screenPositions;
    public float[] distanceBetweenStops;
    public float[][] mapPositions;
    public String ID;
    public String name;
    public int lred;
    public int lgreen;
    public int lblue;
    public int lalpha;
    public float cstrokeWeight = 1;     // Stop circles' stroke weight
    public float lstrokeWeight = 3;     // Lines' stroke weight
    public float diameter = 4.f;
    public int cfred = 255;             // Stop circles' fill-in color
    public int cfgreen = 255;
    public int cfblue = 255;
    public int cfalpha;
    public int csred;                   // Stop circles' stroke color
    public int csgreen;
    public int csblue;
    public int csaplphe;
    public static final int INITIALCAPACITY = 50;
    public int color;
    public ArrayList<Vehicle> vehicleList;
    public static de.fhpotsdam.unfolding.Map map;
    public Vehicle[] vehiclePool = new Vehicle[numOfVehicleReserved];
    public static final int numOfVehicleReserved = 20;
    public boolean mapZoomed = false;
    
    // Testing
    public static final int[] timeStamps = {0, 1000, 2000, 1000, 2100, 1500, 1300, 
                                            1000, 800, 1500, 1700, 2000, 3000, 1200,
                                            1400, 1000, 1000, 900, 750, 1200, 1700,
                                            2400, 1200, 1100, 1250, 1520, 1330, 1230};
    
    public Route(PApplet p, de.fhpotsdam.unfolding.Map m, 
                 String routeid, String _name, float[][] mapPos,
                 int _red, int _green, int _blue) {
        parent = p;
        map = m;
        name = new String(_name);
        mapZoomed = false;
        ID = new String(routeid);
        mapPositions = new float[mapPos.length][2];
        for (int i=0; i<mapPos.length; ++i) {
            mapPositions[i][0] = mapPos[i][0];
            mapPositions[i][1] = mapPos[i][1];
        }
        screenPositions = new float[mapPos.length][2];
        csred = lred = _red;
        csgreen = lgreen = _green;
        csblue = lblue = _blue;
        vehicleList = new ArrayList<Vehicle>(INITIALCAPACITY);
        for (int i=0; i<numOfVehicleReserved; ++i) {
            vehiclePool[i] = new Vehicle(parent, this, _red, _green, _blue);
        }
    }
    
    public void calculateSectionDistances() {
        for (int i=1; i<screenPositions.length; ++i) {
            distanceBetweenStops[i] 
                    = mathFunctions.getDistance(screenPositions[i][0],
                                                screenPositions[i][1],
                                                screenPositions[i-1][0],
                                                screenPositions[i-1][1]);
        }
    }
    
    public Route(PApplet p, de.fhpotsdam.unfolding.Map m, 
            String routeid, String _name, float[][] mapPos,
            int _color) {
        parent = p;
        map = m;
        name = new String(_name);
        ID = new String(routeid);
        mapPositions = new float[mapPos.length][2];
        mapZoomed = false;
        for (int i=0; i<mapPos.length; ++i) {
            mapPositions[i][0] = mapPos[i][0];
            mapPositions[i][1] = mapPos[i][1];
        }
        screenPositions = new float[mapPos.length][2];
        vehicleList = new ArrayList<Vehicle>(INITIALCAPACITY);
        lblue = _color % 256;
        _color /= 256;
        lgreen = _color % 256;
        _color /= 256;
        lred = _color;
        for (int i=0; i<numOfVehicleReserved; ++i) {
            vehiclePool[i] = new Vehicle(parent, this, lred, lgreen, lblue);
        }
    }
    
    public void addNewVehicle(Vehicle vehicle) {
        vehicleList.add(vehicle);
    }
    
    private Location loc = new Location(0, 0);
    public void convertMapToScreen() {
        for (int i=0; i<mapPositions.length; ++i) {
            loc.setLat(mapPositions[i][0]);
            loc.setLon(mapPositions[i][1]);
            float[] xy = map.getScreenPositionFromLocation(loc);
            screenPositions[i][0] = xy[0];
            screenPositions[i][1] = xy[1];
        }
    }
    
    public static int convertTimeToFrameCnt(long milliseconds) {
        // TODO
        return 0;
    }
    
    @Override
    public void mousePressed() {
        for (int i=0; i<numOfVehicleReserved; ++i) if (vehiclePool[i].inUse)
            vehiclePool[i].freeze();
    }
    
    @Override
    public void init() {
        convertMapToScreen();
        distanceBetweenStops = new float[screenPositions.length];
        calculateSectionDistances();
        for (int i=0; i<1; ++i) {
            vehiclePool[i].timeStamps = timeStamps;
            vehiclePool[i].init();
        }
    }
    
    @Override
    public void draw() {
        // Need to re-calculate the screen positions of each stop before
        // drawing the dots and lines
        convertMapToScreen();
        calculateSectionDistances();
        parent.stroke(lred, lgreen, lblue);
        //parent.strokeWeight(lstrokeWeight);
        //for (int i=1; i<screenPositions.length; ++i) {
        //    parent.line(screenPositions[i-1][0], screenPositions[i-1][1],
        //                screenPositions[i][0], screenPositions[i][1]);
        //}
        parent.strokeWeight(cstrokeWeight);
        for (int i=0; i<screenPositions.length; ++i) {
            parent.ellipse(screenPositions[i][0], screenPositions[i][1],
                           diameter, diameter);
        }
        
        for (int i=0; i<numOfVehicleReserved; ++i) if (vehiclePool[i].inUse) {
            vehiclePool[i].calculateSectionSpeeds();
            vehiclePool[i].draw();
        }
    }

    @Override
    public void mouseReleased() {
    }

    @Override
    public void keyPressed() {
    }

    @Override
    public void keyReleased() {
    }
}
