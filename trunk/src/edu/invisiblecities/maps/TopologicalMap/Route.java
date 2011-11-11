package edu.invisiblecities.maps.TopologicalMap;

import java.util.ArrayList;

import processing.core.PApplet;
import de.fhpotsdam.unfolding.geo.Location;
import edu.invisiblecities.core.BaseInfovis;

public class Route implements BaseInfovis {
    
    private PApplet parent;
    public float[][] screenPositions;
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
    
    public Route(PApplet p, de.fhpotsdam.unfolding.Map m, 
                 String routeid, String _name, float[][] mapPos,
                 int _red, int _green, int _blue) {
        
        parent = p;
        map = m;
        name = new String(_name);
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
    }
    
    public Route(PApplet p, de.fhpotsdam.unfolding.Map m, 
            String routeid, String _name, float[][] mapPos,
            int _color) {
   
        parent = p;
        map = m;
        name = new String(_name);
        ID = new String(routeid);
        mapPositions = new float[mapPos.length][2];
        for (int i=0; i<mapPos.length; ++i) {
            mapPositions[i][0] = mapPos[i][0];
            mapPositions[i][1] = mapPos[i][1];
        }
        screenPositions = new float[mapPos.length][2];
        vehicleList = new ArrayList<Vehicle>(INITIALCAPACITY);
        color = _color;
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
    
    public void mousePressed() {
        int size = vehicleList.size();
        for (int i=0; i<size; ++i)
            vehicleList.get(i).freeze();
    }
    
    @Override
    public void init() {
        convertMapToScreen();
    }
    
    @Override
    public void draw() {
        
        // Need to re-calculate the screen positions of each stop before
        // drawing the dots and lines
        convertMapToScreen();
        parent.stroke(color);
        parent.strokeWeight(lstrokeWeight);
        for (int i=1; i<screenPositions.length; ++i) {
            parent.line(screenPositions[i-1][0], screenPositions[i-1][1],
                        screenPositions[i][0], screenPositions[i][1]);
        }
        parent.strokeWeight(cstrokeWeight);
        for (int i=0; i<screenPositions.length; ++i) {
            parent.ellipse(screenPositions[i][0], screenPositions[i][1],
                           diameter, diameter);
        }
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
