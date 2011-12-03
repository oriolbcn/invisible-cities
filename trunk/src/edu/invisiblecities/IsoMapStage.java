package edu.invisiblecities;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import processing.core.PApplet;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.looksgood.ani.Ani;
import de.looksgood.ani.easing.Easing;

public class IsoMapStage extends PApplet {
    
    public static final int     CanvasWidth = 800;
    public static final int     CanvasHeight = 600;
    public static final int     PictureWidth = 600;
    public static final int     PictureHeight = CanvasHeight;
    public static final int     PictureCenterX = PictureWidth / 2;
    public static final int     PictureCenterY = PictureHeight / 2;
    public static final int     MapLeftX = PictureWidth;
    public static final int     MapWidth = CanvasWidth - PictureWidth;
    public static final int     MapHeight = CanvasHeight / 2;
    public static final int     MapBottomY = MapHeight;
    public static final int     MapRightX = MapLeftX + MapWidth;
    public static final int     SideTableLeftX = MapLeftX;
    public static final int     SideTableTopY = MapHeight;
    public static final int     SideTableWidth = MapWidth;
    public static final int     SideTableHeight = MapHeight;
    public static final String  API_KEY = "d3e0942376a3438b8d5fce7378307b58";
    public static final int     OpenMapID = 40077;
    public static final int     ZoomLevel = 11;
    
    public static final String  Routeinfofilename = "routes.csv";
    public static final String  Stationinfofilename = "stationrelationship.csv";
    public static final String  Bfsinfofilename = "bfsinfo.csv";
    public static final String  SSSPfilename = "SSSP.csv";
    public static final float   FixedScale = 400;
    public static final int     RadarDiameterMax = 50;
    public static final int     RadarStrokeWeight = 1;
    
    // Animation
    public static final float   Duration = 1.f;
    public static final Easing  Easing = Ani.QUINT_IN_OUT;//EXPO_IN_OUT;
        
    public static Route[]       mRoutes;
    public static int           NumOfRoutes;
    public static Station[]     mStations;
    public static int           NumOfStations = 170; // Hard code for this case 
    public static int           SelectedNode = -1;
    public static int[]         RouteCount;
    public static int           MaxDistance;
    public static float[][]     toPositions;
    public static float[][]     AngleRanges;
    public static int           radarDiameter = 0;
    public static de.fhpotsdam.unfolding.Map map;
    
    public int getSelection() {
        for (int i=0; i<NumOfStations; ++i) if (mStations[i] != null) {
            if (mStations[i].isInside(mouseX, mouseY))
                return i;
        }
        return -1;
    }
    
    public static void updateGraph() {
        Station sta = mStations[SelectedNode];
        sta.isSelected = true;
        for (int i=0; i<NumOfStations; ++i) if (mStations[i] != null) {
            toPositions[i][0] = sta.bfsPosition[i][0];
            toPositions[i][1] = sta.bfsPosition[i][1];
        }
    }
    
    @Override
    public void setup() {
        size(CanvasWidth, CanvasHeight);
        smooth();
        strokeWeight(1);
        Ani.init(this);
        map = new de.fhpotsdam.unfolding.Map
                (this, MapLeftX, 0, MapWidth, MapHeight,
                 new OpenStreetMap.CloudmadeProvider(API_KEY, OpenMapID));
        loadRoutes();
        loadStations();
        toPositions = new float[NumOfStations][2];
        updateGraph();
        for (int i=0; i<NumOfStations; ++i) if (mStations[i] != null) {
            mStations[i].curX = toPositions[i][0];
            mStations[i].curY = toPositions[i][1];
        }
       
    }
    
    
    public static Location loc = new Location(0, 0);
    public void drawSideMap() {
        map.draw();
        Station sta = mStations[SelectedNode];
        //parent.noFill();
        noStroke();
        //parent.strokeWeight(RadarStrokeWeight);
        loc.setLat(sta.lat);
        loc.setLon(sta.lon);
        map.zoomAndPanTo(loc, ZoomLevel);
        float[] xy = map.getScreenPositionFromLocation(loc);
        sta.screenX = xy[0];
        sta.screenY = xy[1];
        radarDiameter = (radarDiameter + 1) % RadarDiameterMax;
        fill(sta.fred, sta.fgreen, sta.fblue, 100);
        ellipse(sta.screenX, sta.screenY, radarDiameter, radarDiameter);
    }
    
    
    public static final int StationNameOffsetX = SideTableLeftX + 20;
    public static final int StationNameOffsetY = SideTableTopY + 20;
    public static final int StationCapacityOffsetX = StationNameOffsetX;
    public static final int StationCapacityOffsetY = StationNameOffsetY + 20;
    public void drawSideTable() {
        fill(255);
        rect(SideTableLeftX, SideTableTopY, SideTableWidth, SideTableHeight);
        fill(0);
        Station sta = mStations[SelectedNode];
        text("Name: " + sta.name, StationNameOffsetX, StationNameOffsetY);
        text("Capacity: " + sta.diameter, StationCapacityOffsetX, StationCapacityOffsetY);
    }
    
    public static int hoverId = -1;
    
    @Override
    public void draw() {
        background(255);
        
        stroke(0, 100);
        for (int i=0; i<NumOfStations; ++i) if (mStations[i] != null) {
            Station sta = mStations[i];
            int len = sta.numOfConneted;
            for (int j=0; j<len; ++j) {
                Station sj = mStations[sta.connected[j]];
                line(sta.curX, sta.curY, sj.curX, sj.curY);
            }
        }
        for (int i=0; i<NumOfStations; ++i) if (mStations[i] != null)
            mStations[i].draw();
        
        hoverId = getSelection();
        if (hoverId != -1) {
            Station sta = mStations[SelectedNode];
            int len = sta.
                    sssp[hoverId].
                    length;
            for (int i=1; i<len; ++i) {
                Station ssp = mStations[sta.sssp[hoverId][i]];
                Station ssb = mStations[sta.sssp[hoverId][i-1]];
                strokeWeight(5);
                line(ssp.curX, ssp.curY, ssb.curX, ssb.curY);
            }
            
            for (int i=0; i<len; ++i) {
                Station ssp = mStations[sta.sssp[hoverId][i]];
                fill(ssp.fred, ssp.fgreen, ssp.fblue);
                strokeWeight(0);
                stroke(0);
                ellipse(ssp.curX, ssp.curY, ssp.diameter, ssp.diameter);
                //fill(0);
                //text(ssp.name, + ssp.bfsDistance[SelectedNode], ssp.curX, ssp.curY + 25);
            }
        }
        
        drawSideMap();
        drawSideTable();
        
        fill(0);
        text("Mouse x: " + mouseX + " y: " + mouseY, 20, 40);
        stroke(0);
        strokeWeight(0);
        line(PictureWidth, 0, PictureWidth, PictureHeight);
        line(MapLeftX, MapBottomY, MapRightX, MapBottomY);
    }
    
    @Override
    public void mouseReleased() {
        int id = getSelection();
        System.out.println("Mouse click on " + id);
        if (id >= 0) {
            mStations[SelectedNode].isSelected = false;
            SelectedNode = id;
            updateGraph();
            for (int i=0; i<NumOfStations; ++i) if (mStations[i] != null)
                mStations[i].setAni(toPositions[i][0], toPositions[i][1]);
        }
    }

    @Override
    public void mousePressed() {
    }
    
    @Override
    public void keyPressed() {
    }
    
    @Override
    public void keyReleased() {
    }
    
    @Override
    public void mouseDragged() {
    }
    
    public static void main(String args[]) {
        PApplet.main(new String[] {"--present", "InvisibleCities"});
    }
    
    
    /*************** Subclasses ***************/
    public class Station {
        public float curX;
        public float curY;
        public float lat;
        public float lon;
        public float screenX;
        public float screenY;
        public int diameter;
        public int radius;
        public int fred;
        public int fgreen;
        public int fblue;
        public int alpha = 50;
        public Route route;
        public int  rid;
        public String name;
        public int[] connected;
        public int[] distance;
        public int   numOfConneted;
        public boolean isSelected = false;
        
        public int[] bfsDistance = new int[NumOfStations];
        public float[][] bfsPosition = new float[NumOfStations][2];
        public int [][] sssp = new int[NumOfStations][];
        
        public Station(Route rt, String nm, int size, 
                int r, int g, int b, 
                int id, float la, float lo) {
            route = rt;
            name = nm;
            numOfConneted = size;
            connected = new int[numOfConneted];
            distance = new int[numOfConneted];
            fred = r;
            fgreen = g;
            fblue = b;
            lat = la;
            lon = lo;
            diameter = 20;
            radius = diameter / 2;
            rid = id;
        }
        
        public void setAni(float _x, float _y) {
            Ani.to(this, Duration, "curX", _x, Easing);
            Ani.to(this, Duration, "curY", _y, Easing);
        }
        
        public void draw() {
            if (isSelected) {
                stroke(0);
                fill(fred, fgreen, fblue);
            }
            else {
                fill(fred, fgreen, fblue, alpha);
                stroke(0, alpha);
            }
            ellipse(curX, curY, diameter, diameter);
            //fill(0);
            //text(name, curX, curY + 25);
        }
        
        public boolean isInside(int sx, int sy) {
            // AABB
            if (curX + radius >= sx && curX - radius <= sx && curY + radius >= sy && curY - radius <= sy) {
                return true;
            }
            return false;
        }
    }
    
    public class Route {
        public String id;
        public String name;
        public String url;
        public int red;
        public int green;
        public int blue;
        public Route(String _id, String _name, String _url, String col) {
            id = _id;
            name = _name;
            url = _url;
            int color = Integer.parseInt(col, 16);
            blue = color % 256;
            green = (color / 256) % 256;
            red = color / 256 / 256;
        }
        public Route(String _id, String _name, String _url, int r, int g, int b) {
            id = _id;
            name = _name;
            url = _url;
            red = r;
            green = g;
            blue = b;
        }
    }

    /*************** Load Data ***************/
    public static FileInputStream ifstream;
    public static DataInputStream in;
    public static BufferedReader br;
   
    private static HashMap<String, Integer> route2Int;
    public static int findRoute(String rid) {
        for (int i=0; i<NumOfRoutes; ++i) {
            if (mRoutes[i].id.equalsIgnoreCase(rid)) {
                ++RouteCount[i]; 
                return i;
            }
        }
        return -1;
    }
    
    public static void initAngleRange() {
        int total = 0;
        for (int i=0; i<NumOfRoutes; ++i) {
            total += RouteCount[i];
        }
        float[] ratio = new float[NumOfRoutes];
        AngleRanges = new float[NumOfRoutes][2];
        for (int i=0; i<NumOfRoutes; ++i) {
            ratio[i] = TWO_PI * RouteCount[i] / total;
        }
        AngleRanges[0][0] = 0.0f;
        AngleRanges[0][1] = ratio[0];
        float last = ratio[0];
        for (int i=1; i<NumOfRoutes; ++i) {
            AngleRanges[i][0] = last;
            AngleRanges[i][1] = last + ratio[i];
            last += ratio[i];
        }
        for (int i=0; i<NumOfRoutes; ++i) {
            System.out.println(AngleRanges[i][0] + " " + AngleRanges[i][1]);
        }
    }
    
    public void loadRoutes() {
        try {
            route2Int = new HashMap<String, Integer>();
            ArrayList<Route> alr = new ArrayList<Route>();
            ifstream = new FileInputStream(Routeinfofilename);
            in = new DataInputStream(ifstream);
            br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(";");
                Route r = new Route(split[0], split[2].substring(1), split[4], split[5]);
                alr.add(r);
            }
            int size = alr.size();
            //mRoutes = (Route[]) alr.toArray(); //??
            mRoutes = new Route[size];
            for (int i=0; i<size; ++i) {
                Route r = alr.get(i);
                mRoutes[i] = new Route(r.id, r.name, r.url, r.red, r.green, r.blue);
                route2Int.put(r.id, new Integer(i));
            }
            NumOfRoutes = mRoutes.length;
            System.out.println("Load routes done\nTotal Routes: " + NumOfRoutes);
            RouteCount = new int[NumOfRoutes];
            br.close();
            in.close();
            ifstream.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void loadStations() {
        String line = null;
        try {
            mStations = new Station[NumOfStations];
            ifstream = new FileInputStream(Stationinfofilename);
            in = new DataInputStream(ifstream);
            br = new BufferedReader(new InputStreamReader(in));
            int total = 0;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(";"); // 0: station id; 1: route_id 2: name 3: lat 4: lon
                int stationId = Integer.parseInt(split[0]);
                if (SelectedNode < 0) SelectedNode = stationId;
                if (mStations[stationId] != null) throw new Exception();
                String routeId = split[1];
                String name = split[2].substring(1, split[2].length()-1);
                float lat = Float.parseFloat(split[3]);
                float lon = Float.parseFloat(split[4]);
                
                // Read second line
                line = br.readLine(); if (line == null) throw new Exception();
                split = line.split(";");
                int len = split.length;
                ArrayList<String> al = new ArrayList<String>(len);
                for (int i=0; i<len; i+=2) {
                    if (split[i] == null) break;
                    al.add("" + split[i] + " " + split[i+1]);
                }
                int size = al.size();
                int rid = findRoute(routeId);
                Route r = mRoutes[rid];
                mStations[stationId] = new Station(r, name, size, r.red, r.green, r.blue, rid, lat, lon);
                int cnt = 0;
                for (String s : al) {
                    String[] twoparts = s.split(" ");
                    int stid = Integer.parseInt(twoparts[0]);
                    int dist = Integer.parseInt(twoparts[1]);
                    mStations[stationId].connected[cnt] = stid;
                    mStations[stationId].distance[cnt] = dist;
                    ++cnt;
                }
                ++total;
            }
            System.out.println("Total Stations: " + total);
            br.close();
            in.close();
            ifstream.close();
            
            // Load bfs distance
            ifstream = new FileInputStream(Bfsinfofilename);
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
                    mStations[stationid].bfsDistance[sid] = acc;
                }
            }
            System.out.println("BFS distance done");
            br.close();
            in.close();
            ifstream.close();
            
            // Load SSSP
            ifstream = new FileInputStream(SSSPfilename);
            in = new DataInputStream(ifstream);
            br = new BufferedReader(new InputStreamReader(in));
            
            line = br.readLine();
            while (true) {
                if (line == null) break;
                int sid = Integer.parseInt(line);
                while ((line = br.readLine()) != null) {
                    String[] split = line.split(";");
                    int len = split.length;
                    if (len < 2) break;
                    int toid = Integer.parseInt(split[0]); // Get the destination
                    if (len == 2 && split[1].equals("-1")) {
                        mStations[sid].sssp[toid] = new int[1];
                        mStations[sid].sssp[toid][0] = sid;
                        continue;
                    }
                    mStations[sid].sssp[toid] = new int[len];
                    // Since the path in csv is in reversed order
                    for (int i=len-1; i>=0; --i) {
                        mStations[sid].sssp[toid][len-1-i] = Integer.parseInt(split[i]);
                    }
                }
            }

            System.out.println("SSSP done");
            br.close();
            in.close();
            ifstream.close();
            
            initAngleRange();
            // Load bfs positions
            int[] accAngle = new int[NumOfRoutes];
            for (int i=0; i<NumOfStations; ++i) if (mStations[i] != null) {
                Station sta = mStations[i];
                for (int j=0; j<NumOfStations; ++j) if (mStations[j] != null) {
                    Station sj = mStations[j];
                    float theta = random(AngleRanges[sj.rid][0], AngleRanges[sj.rid][1]);
                    //float theta = (AngleRanges[sj.rid][1] - AngleRanges[sj.rid][0]) / 
                    //        RouteCount[sj.rid] * accAngle[sj.rid] + AngleRanges[sj.rid][0];
                    sta.bfsPosition[j][0] = PictureCenterX + sta.bfsDistance[j] * cos(theta) / FixedScale;
                    sta.bfsPosition[j][1] = PictureCenterY + sta.bfsDistance[j] * sin(theta) / FixedScale;
                    //System.out.println("id " + sj.rid + " x " + sta.bfsPosition[j][0] + " y " + sta.bfsPosition[j][1]);
                    //++accAngle[sj.rid];
                }
            }
            System.out.println("BFS positions done");
            
        } catch (Exception e) {
            System.err.println(e.toString() + "\n" + line);
        }
    }

    
    private static final long serialVersionUID = 8832918302189021L;

}