package edu.invisiblecities.maps.TopologicalMap;

import java.util.ArrayList;

import processing.core.PApplet;
import codeanticode.glgraphics.GLConstants;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import edu.invisiblecities.maps.BaseMap;
import edu.invisiblecities.utils.mathFunctions;


public class TopologicalMap extends BaseMap {

    public de.fhpotsdam.unfolding.Map map;
    public  int numOfRoutes = 25;
    private int mapLeftTopX = 0;
    private int mapLeftTopY = 0;
    private int mapWidth = 800;
    private int mapHeight = 800;
    public static String mapType = GLConstants.GLGRAPHICS;
    public Route[] routes;
    public static final int INITIALCAPACITY = 100;
    public final static int frameRate = 30;
    
    public static boolean pause;
    
    public static long globalTimer = 0;
    
    private static int[] colors = {
            0x00A1DE, 0x00A1DE, 0xC60C30, 0xC60C30, 
            0x009B3A, 0x009B3A, 0x009B3A, 0x009B3A, 
            0x62361B, 0x62361B, 0x62361B, 0x522398, 
            0x522398, 0x522398, 0x62361B, 0xF9E300, 
            0xF9461C, 0xE27EA6, 0x522398, 0x62361B, 
            0x00A1DE, 0x00A1DE, 0x00A1DE, 0x62361B, 
            0x00A1DE, 0x522398};
    
    public TopologicalMap(PApplet p) {
        super(p);
        routes = new Route[numOfRoutes];
        pause = false;   
    }
    
    public static boolean homeInit = false;
    //
    public Route parseData(int index) {
        System.out.println("Parsing file out" + index + ".txt");
        String[] lines = parent.loadStrings("out" + index + ".txt");
        float[][] cood = new float[lines.length][2];
        String []splt = lines[lines.length/2].split(",");
        String routeid = new String(splt[5]);
        String routename = new String(splt[5]);
        
        map.zoomAndPanTo(new Location(Float.parseFloat(splt[1]), Float.parseFloat(splt[2])), 11);
        for (int i=0; i<lines.length; ++i) {
            if (parent.trim(lines[i]).length() == 0) continue;
            String[] pieces = lines[i].split(",");
            cood[i][0] = (float)Float.parseFloat(pieces[1]);
            cood[i][1] = (float)Float.parseFloat(pieces[2]);
        }
        cood = simplifyCoordinates(cood);
        Route route = new Route(parent, map, routeid, routename, cood, colors[index]);
        return route;
    }
    
    // Get rid of redundant coordinates
    public static float[][] simplifyCoordinates(float[][] cood) {
        int p1 = 0;
        int p2 = 1;
        int length = 0;
        float IMPOSSIBLE = 1000000000.f;
        for (int i=2; i<cood.length; ++i) {
            int pc = i;
            if (((cood[pc][1] - cood[p2][1]) * (cood[p2][0] - cood[p1][0]) - 
                    (cood[p2][1] - cood[p1][1]) * (cood[pc][0] - cood[p2][0])) 
                    < mathFunctions.epsilon) {
                cood[p2][0] = cood[p2][1] = IMPOSSIBLE;
            } else p1 = p2;
            p2 = pc;
        }
        
        float[][] ret = new float[cood.length][2];
        for (int i=0; i<cood.length; ++i) {
            if (cood[i][0] == IMPOSSIBLE) continue;
            ret[length][0] = cood[i][0];
            ret[length][1] = cood[i][1];
            ++length;
        }
        float[][] ret1 = new float[length][2];
        for (int i=0; i<length; ++i) {
            ret1[i][0] = ret[i][0];
            ret1[i][1] = ret[i][1];
        }
        return ret1;
    }
    
    @Override
    public void init() {
        parent.size(canvasWidth, canvasHeight, GLConstants.GLGRAPHICS);
        parent.frameRate(frameRate);
        map = new de.fhpotsdam.unfolding.Map
                (parent, mapLeftTopX, mapLeftTopY,
                 mapWidth, mapHeight,
                 new OpenStreetMap.CloudmadeProvider("d3e0942376a3438b8d5fce7378307b58", 15153));
        MapUtils.createDefaultEventDispatcher(parent, map);
        
        for (int i=0; i<numOfRoutes; ++i) {
            routes[i] = parseData(i);
            routes[i].init();
        }
        globalTimer = 0;
    }

    @Override
    public void draw() {
        super.draw();
        map.draw();
        ++globalTimer;
        parent.fill(0);
        parent.text("FPS: " + parent.frameRate, 20, 20);
        for (int i=0; i<routes.length; ++i) {
            routes[i].draw();
        }
    }

    @Override
    public void mouseReleased() {
        if (pause) pause = false;
    }
    
    @Override
    public void mousePressed() {
        if (map.isHit(parent.mouseX, parent.mouseY)) {
            pause = true;
            for (int i=0; i<routes.length; ++i) {
                routes[i].mousePressed();
            }
        }
    }

    @Override
    public void keyPressed() {

    }

    @Override
    public void keyReleased() {
        switch(parent.keyCode) {
        case 83: // 's'
            System.out.println("s pressed");
            parent.noLoop();
            break;
        case 82: // 'r'
            parent.loop();
            break;
        }
    }
}
