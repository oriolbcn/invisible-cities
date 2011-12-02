package edu.invisiblecities.maps.IsochronicMap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;
import codeanticode.glgraphics.GLConstants;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.looksgood.ani.Ani;
import de.looksgood.ani.easing.Easing;
import edu.invisiblecities.maps.BaseMap;
import edu.invisiblecities.maps.TopologicalMap.Route;
import edu.invisiblecities.utils.mathFunctions;

// TODO All nodes must be connected, no independent regiment for now
public class IsochronicMap extends BaseMap {
    
    public static float Scale = 300;

    public static de.fhpotsdam.unfolding.Map map;
    public static final int MapLeftTopX = 1000;
    public static final int MapLeftTopY = 0;
    public static final int MapWidth = 400;
    public static final int MapHeight = canvasHeight;
    public static final int canvasHeightHalf = canvasHeight / 2;
    public static final String API_KEY = "d3e0942376a3438b8d5fce7378307b58";
    public static final int OpenMapID = 40077;
    public static final int ZoomLevel = 11;
    
    
    public static final float Duration = 1.5f;
    public static final Easing easing = Ani.EXPO_IN_OUT;
    public static final String routesfilename = "routeInfo.csv";
    public static final String stationinfofilename = "stationrelationship.csv";
    public static final String accfilename = "accdistance.csv";
    public static PFont Font;
    public int selectedNode = -1;            // The id of current selected node, initially -1
    
    public class Station {
        // Graph
        public float x;
        public float y;
        public float diameter = 5000.f;
        public float radius = 2500.f;
        // fill color
        public int fred;
        public int fgreen;
        public int fblue;
        // stroke color
        public int sred;
        public int sgreen;
        public int sblue;
        // text color
        public int tred;
        public int tgreen;
        public int tblue;
        
        // Basic
        public int rid;
        public Route route;
        public String name;
        public int[] connected;
        public int[] distances;
        public int[] accDistances;
        public float screenX, screenY;
        public float lat, lon;
        
        public Station(Route r, String n, int size, int fr, int fg, int fb, int id, float la, float lo) {
            route = r;
            name = n;
            connected = new int[size];
            distances = new int[size];
            fred = fr;
            fgreen = fg;
            fblue = fb;
            rid = id;
            lat = la;
            lon = lo;
            accDistances = new int[NumOfStations];
        }
        
        public void setAni(float _x, float _y) {
            Ani.to(this, Duration, "x", _x, easing);
            Ani.to(this, Duration, "y", _y, easing);
        }
        
        public void draw() {
            parent.fill(fred, fgreen, fblue);
            parent.stroke(sred, sgreen, sblue, salpha);
            parent.ellipse(x, y, diameter/Scale, diameter/Scale);
            parent.fill(tred, tgreen, tblue);
            parent.text(""+name, x, y + 25);
        }
        
        public boolean isInside(float sx, float sy) {
            // AABB
            if (x + radius/Scale < sx || x - radius/Scale > sx || y + radius/Scale < sy || y - radius/Scale > sy)
                return false;
            return true;
            //float distance = mathFunctions.getDistance(x, y, sx, sy);
            //return distance < radius;
        }
    }
    
    public static Station[] stations;
    static ArrayList<Route> mRoutes = null;
    public static int NumOfStations = 170;
    public static int[] routeCnt;
    
    static FileInputStream ifstream;
    static DataInputStream in;
    static BufferedReader br;
    static FileWriter ofstream;
    static BufferedWriter out;
    
    public void loadStations() {
        routeCnt = new int[mRoutes.size()];
        String line = null;
        try {
            stations = new Station[NumOfStations];
            ifstream = new FileInputStream(stationinfofilename);
            in = new DataInputStream(ifstream);
            br = new BufferedReader(new InputStreamReader(in));
            int total = 0;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(";"); // 0: station id; 1: route_id 2: name 3: lat 4: lon
                int stationId = Integer.parseInt(split[0]);
                if (selectedNode < 0) selectedNode = stationId;
                if (stations[stationId] != null) {
                    System.err.println("Create the same station " + stationId);
                    throw new Exception();
                }
                String routeId = split[1];
                String name = split[2].substring(1, split[2].length()-1);
                System.out.println("station " + stationId + " name " + name);
                float lat = Float.parseFloat(split[3]);
                float lon = Float.parseFloat(split[4]);
                System.out.println("lat " + lat + " lon " + lon);
                
                // Read second line
                line = br.readLine();
                split = line.split(";");
                int len = split.length;
                ArrayList<String> al = new ArrayList<String>(len/2 + 1);
                for (int i=0; i<len; i+=2) {
                    if (split[i] == null) break;
                    al.add("" + split[i] + " " + split[i+1]);
                }
                int size = al.size();
                int rid = findRoute(routeId);
                Route r = mRoutes.get(rid);
                stations[stationId] = new Station(r, name, size, r.red, r.green, r.blue, rid, lat, lon);
                int cnt = 0;
                for (String s : al) {
                    String[] twoparts = s.split(" ");
                    int stid = Integer.parseInt(twoparts[0]);
                    int dist = Integer.parseInt(twoparts[1]);
                    stations[stationId].connected[cnt] = stid;
                    stations[stationId].distances[cnt] = dist;
                    ++cnt;
                }
                ++total;
            }
            System.out.println("Total Stations: " + total);
            br.close();
            in.close();
            ifstream.close();
            // Load acc distance
            ifstream = new FileInputStream(accfilename);
            in = new DataInputStream(ifstream);
            br = new BufferedReader(new InputStreamReader(in));
            while ((line = br.readLine()) != null) {
                int stationid = Integer.parseInt(line);
                line = br.readLine(); if (line == null) break;
                String[] split = line.split(";");
                int len = split.length;
                for (int i=0; i<len; i+=2) {
                    if (split[i] == null) break;
                    int sid = Integer.parseInt(split[i]);
                    int acc = Integer.parseInt(split[i+1]);
                    stations[stationid].accDistances[sid] = acc;
                }
            }
            br.close();
            in.close();
            ifstream.close();
            
        } catch (Exception e) {
            System.err.println(e.toString() + "\n" + line);
        }
    }
    
    public void loadRoutesData() {
        try {
            mRoutes = new ArrayList<Route>();
            ifstream = new FileInputStream(routesfilename);
            in = new DataInputStream(ifstream);
            br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(";");
                Route r = new Route(split[0], split[5]);
                mRoutes.add(r);
            }
            System.out.println("Total Routes: " + mRoutes.size());
            br.close();
            in.close();
        } catch (Exception e) {}
    }
    
    public static int findRoute(String rid) {
        int size = mRoutes.size();
        for (int i=0; i<size; ++i) {
            if (mRoutes.get(i).routeId.equalsIgnoreCase(rid)) {
                ++routeCnt[i];
                return i;
            }
        }
        return -1;
    }
    
    public static float[][] AngleRanges;
    static void initAngleRange() {
        int total = 0;
        int len = routeCnt.length;
        for (int i=0; i<len; ++i) {
            total += routeCnt[i];
        }
        float[] ratio = new float[len];
        AngleRanges = new float[len][2];
        for (int i=0; i<len; ++i) {
            ratio[i] = parent.TWO_PI * routeCnt[i] / total;
        }
        AngleRanges[0][0] = 0.0f;
        AngleRanges[0][1] = ratio[0];
        float last = ratio[0];
        for (int i=1; i<len; ++i) {
            AngleRanges[i][0] = last;
            AngleRanges[i][1] = last + ratio[i];
            last += ratio[i];
        }
        for (int i=0; i<len; ++i) {
            System.out.println(AngleRanges[i][0] + " " + AngleRanges[i][1]);
        }
    }
    
    /**********  Basic attributes **********/
    //public int numOfNodes;              // Total number of nodes in this map
    public static float CenterOffSet = 200;
    public final static float centerX = canvasWidth / 2 - CenterOffSet;
    public final static float centerY = canvasHeight / 2;      // The center of this map
    public static float[] RadiusArray;
    public static final int RadiusSize = 10;
    public boolean connections[][];     // The adjacency matrix
    //public Node[] nodes = null;         // Array of nodes in this map
    public static float[][] toPositions;       // New positions after selecting a new center node
    
    /********** Colors & Fonts **********/
    public int sred, sgreen, sblue, salpha;     // Stroke color, if any
    public int fred, fgreen, fblue, falpha;     // Fillin color
    public int tred, tgreen, tblue, talpha;     // Text color
    
   public static int MaxDistance = -1;
    public void updateGraph() {
        MaxDistance = -1;
        Station sta = stations[selectedNode];
        //sta.x = centerX;
        //sta.y = centerY;
        //System.out.println("curr " + selectedNode + " " + sta.x + " " + sta.y);
        toPositions[selectedNode][0] = centerX;
        toPositions[selectedNode][1] = centerY;
        for (int i=0; i<NumOfStations; ++i) 
            if (stations[i] != null && i != selectedNode) {
                Station si = stations[i];
                float theta = parent.random(AngleRanges[si.rid][0], AngleRanges[si.rid][1]);
                toPositions[i][0] = sta.accDistances[i] * parent.cos(theta);
                toPositions[i][1] = sta.accDistances[i] * parent.sin(theta);
                if (MaxDistance < sta.accDistances[i])
                    MaxDistance = sta.accDistances[i];
            }
        Scale = (float)MaxDistance / canvasHeightHalf;
        for (int i=0; i<NumOfStations; ++i) 
            if (stations[i] != null && i != selectedNode) {
                toPositions[i][0] = centerX + toPositions[i][0] / Scale;
                toPositions[i][1] = centerY + toPositions[i][1] / Scale;
            }
    }
    
    public static Location loc = new Location(0, 0);
    public void updateMapRadar() {
        Station sta = stations[selectedNode];
        loc.setLat(sta.lat);
        loc.setLon(sta.lon);
        map.zoomAndPanTo(loc, ZoomLevel);
        float[] xy = map.getScreenPositionFromLocation(loc);
        sta.screenX = xy[0];
        sta.screenY = xy[1];
    }
    
    /********** Override methods **********/
    // Initialize attributes that are not initialized in constructor
    @Override
    public void init() {
        parent.size(canvasWidth, canvasHeight, GLConstants.GLGRAPHICS/*, parent.P3D*/);
        map = new de.fhpotsdam.unfolding.Map
                (parent, MapLeftTopX, MapLeftTopY,
                 MapWidth, MapHeight,
                 new OpenStreetMap.CloudmadeProvider(API_KEY, OpenMapID));
        
        Ani.init(parent);
        parent.smooth();
        Font = parent.loadFont("AmericanTypewriter-16.vlw");
        loadRoutesData();
        loadStations();
        initAngleRange();
        RadiusArray = new float[RadiusSize];
        for (int i=0; i<RadiusSize; ++i) {
            RadiusArray[i] = 15000.f * (i + 1);
        }
        toPositions = new float[NumOfStations][2];
        connections = new boolean[NumOfStations][NumOfStations];
        fred = fgreen = fblue = 0;
        falpha = 100;
        sred = sgreen = sblue = 0;
        salpha = 50;
        updateGraph();
        for (int i=0; i<NumOfStations; ++i) if (stations[i] != null) {
            stations[i].x = toPositions[i][0];
            stations[i].y = toPositions[i][1];
        }
        updateMapRadar();
    }
    
    public int getSelection() {
        for (int i=0; i<NumOfStations; ++i) if (stations[i] != null) {
            if (stations[i].isInside(parent.mouseX, parent.mouseY))
                return i;
        }
        return -1;
    }
        
    @Override
    public void draw() {
        parent.background(255);
        parent.stroke(tred, tgreen, tblue);
        parent.text("Current node " + selectedNode + " " + stations[selectedNode].x + " " + stations[selectedNode].y, 20, 20);
        parent.text("Max distance from center node: " + MaxDistance, 20, 40);
        parent.noStroke();
        for (int i=RadiusSize-1; i>=0; --i) {
            if (i % 2 == 1) parent.fill(200);
            else parent.fill(255);
            parent.ellipse(centerX, centerY, RadiusArray[i] * 2 / Scale, RadiusArray[i] * 2 / Scale);
        }
        parent.stroke(sred, sgreen, sblue, salpha);
        for (int i=0; i<NumOfStations; ++i) if (stations[i] != null) {
            Station sta = stations[i];
            int len = sta.connected.length;
            for (int j=0; j<len; ++j) {
                if (i == selectedNode || sta.connected[j] == selectedNode) {
                    parent.strokeWeight(5);
                } else {
                    parent.strokeWeight(1);
                }
                parent.line(stations[i].x, 
                            stations[i].y, 
                            stations[sta.connected[j]].x, 
                            stations[sta.connected[j]].y);
            }
        }
        parent.strokeWeight(1);
        for (int i=0; i<NumOfStations; ++i) if (stations[i] != null) {
            stations[i].draw();
        }
        drawRadar();
    }
    
    public static final int RadarDiameterMax = 50;
    static int radarDiameter = 0;
    public static final int RadarStrokeWeight = 1;
    public void drawRadar() {
        map.draw();

        Station sta = stations[selectedNode];
        //parent.noFill();
        parent.noStroke();
        //parent.strokeWeight(RadarStrokeWeight);
        radarDiameter = (radarDiameter + 1) % RadarDiameterMax;
        parent.fill(sta.fred, sta.fgreen, sta.fblue, 100);
        parent.ellipse(sta.screenX, sta.screenY, radarDiameter, radarDiameter);
    }
    
    @Override
    public void mousePressed() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseReleased() {
        int id = getSelection();
        System.out.println("Selected" + id);
        if (id >= 0) {
            selectedNode = id;
            updateGraph();
            for (int i=0; i<NumOfStations; ++i) if (stations[i] != null) {
                stations[i].setAni(toPositions[i][0], toPositions[i][1]);
            }
            updateMapRadar();
        }
    }

    @Override
    public void keyPressed() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void keyReleased() {
        // TODO Auto-generated method stub
        
    }
    
    /********** Constructors **********/
    public IsochronicMap(PApplet p) {
        super(p);
    }    

}
