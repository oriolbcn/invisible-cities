package edu.invisiblecities.maps.TopologicalMap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import processing.core.PApplet;
import processing.core.PImage;
import codeanticode.glgraphics.GLConstants;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import edu.invisiblecities.maps.BaseMap;

public class TopoMap extends BaseMap {
    
    public class CTrip {
        //public float mapX;
        //public float mapY;
        public float screenX;
        public float screenY;
        public float screenX2;
        public float screenY2;
        public Route route;

        public CTrip(Route r, float x, float y) {
            //mapX = x;
            //mapY = y;
            float[] xy = map.getScreenPositionFromLocation(new Location(x, y));
            screenX = xy[0];
            screenY = xy[1];
            xy = map2.getScreenPositionFromLocation(new Location(x, y));
            screenX2 = xy[0];
            screenY2 = xy[1];
            route = r;
        }
    }
    
    public class Stop {
        //public float mapX;
        //public float mapY;
        public float screenX;
        public float screenY;
        public float screenX2;
        public float screenY2;
        public String name;
        public int stopId;
        public short frequency;
        public Route route;
        public Stop(int si, float x, float y, String n, Route r) {
            //mapX = x;
            //mapY = y;
            stopId = si;
            float[] xy = map.getScreenPositionFromLocation(new Location(x, y));
            screenX = xy[0];
            screenY = xy[1];
            xy = map2.getScreenPositionFromLocation(new Location(x, y));
            screenX2 = xy[0];
            screenY2 = xy[1];
            name = n;
            frequency = 0;
            route = r;
        }
        public boolean inside(int x, int y) {
            if (x >= screenX - stopsRadius && x <= screenX + stopsRadius
                    && y >= screenY - stopsRadius && y <= screenY + stopsRadius
                    || x >= screenX2 - stopsRadius && x <= screenX2 + stopsRadius
                    && y >= screenY2 - stopsRadius && y <= screenY2 + stopsRadius) {
                return true;
            } else return false;
        }
    }
    
    public class Route {
        public String routeId;
        public String name;
        public short red;
        public short green;
        public short blue;
        public Route(String rid, String col) {
            routeId = rid;
            int color = Integer.parseInt(col, 16);
            blue = (short)(color % 256);
            green = (short)((color / 256) % 256);
            red = (short)(color / 256 / 256);
        }
    }
    
    static FileWriter ofstream;
    static BufferedWriter out;
    
    static FileInputStream ifstream;
    static DataInputStream in;
    static BufferedReader br;
    
    public static final int Before5 = 0; //60 * 60 * 5;
    public static final int Interval = 30;
    public static final String stoptimefilename = "finalExtended.csv";
    public static final String routesfilename = "routeInfo.csv";
    public static final String stopsfilename = "stopsInfo.csv";
    public static final String stoproutefilename = "stopRouteInfo.csv";
    public static final String plyBtnImgfilename = "Menu-Play.png";
    public static final String stpBtnImgfilename = "Menu-Stop.png";
    public static final float vDiameter = 10;
    public static final float vDiameter2 = 20;
    public static final String mapType = GLConstants.GLGRAPHICS;
    public static final int TotalTimeStamps = 2880;
    public static final int FrameRate = 30;
    public static final int MapLeftTopX = 0;
    public static final int MapLeftTopY = 0;
    public static final int MapWidth = 700;
    public static final int MapHeight = 720;
    public static final int MapRightBottomX = MapLeftTopX + MapWidth;
    public static final int MapRightBottomY = MapLeftTopY + MapHeight;
    public static final int Map2LeftTopX = MapWidth;
    public static final int Map2leftTopY = 0;
    public static final int Map2Width = 1200 - MapWidth;
    public static final int Map2Height = MapHeight;
    public static final int Map2RightBottomX = Map2LeftTopX + Map2Width;
    public static final int Map2RightBottomY = Map2leftTopY + Map2Height;
    public static final int DashboardTopX = 0;
    public static final int DashboardTopY = MapHeight;
    public static final int DashboardLength = canvasWidth;
    public static final int DashboardHeight = canvasHeight - MapHeight;
    public static final float CenterMapX = 41.895156f;
    public static final float CenterMapY = -87.69999f;
    public static final float CenterMapX2 = 41.88558f;
    public static final float CenterMapY2 = -87.63415f;
    public static final int ZoomLevel = 11;
    public static final int ZoomLevel2 = 14;
    public static final String API_KEY = "d3e0942376a3438b8d5fce7378307b58";
    public static final int OpenMapID = 47657;
    
    public static final int ProgressBarLeft = 20;
    public static final int ProgressBarY = 750;
    public static final int ProgressBarLength = 24 * 20;
    public static final int ProgressBarRight = ProgressBarLeft + ProgressBarLength;
    public static final int ProgressBarThickness = 10;
    public static final int ProgressBarBottom = ProgressBarY + ProgressBarThickness;
    public static final int ProgressBarRed = 0;
    public static final int ProgressBarGreen = 0;
    public static final int ProgressBarBlue = 0;
    public static final int ProgressBarCircleRed = 255;
    public static final int ProgressBarCircleGreen = 255;
    public static final int ProgressBarCircleBlue = 255;
    public static final float ProgressBarCircleY = ProgressBarY + ProgressBarThickness / 2;
    public static final float ProgressBarCircleD = 20;
    public static final float ProgressBarCircleMovingDelta = (float)ProgressBarLength / TotalTimeStamps;
    public static final int BtnPlayRed = 200;
    public static final int BtnPlayGreen = 100;
    public static final int BtnPlayBlue = 120;
    public static final int BtnPlayCenterX = ProgressBarRight + 30;
    public static final int BtnPlayCenterY = ProgressBarY - 20;
    public static final int BtnPlayDiameters = 20;
    public static final int BtnStopCenterX = BtnPlayCenterX + 70;
    public static final int BtnStopCenterY = BtnPlayCenterY;
    
    public static Stop[] inStops = null;
    public static ArrayList<CTrip>[] mTrips = null;
    public static ArrayList<Route> mRoutes = null;
    public static ArrayList<Stop> mStops = null;
    public static int mTimer;
    public static de.fhpotsdam.unfolding.Map map;
    public static de.fhpotsdam.unfolding.Map map2;
    public static boolean isPlaying = false;
    public static PImage playBtnImg;
    public static PImage stopBtnImg;
    public static PImage btnImg;
    public static float BtnPlyTop;
    public static float BtnPlyBottom;
    public static float BtnPlyLeft;
    public static float BtnPlyRight;
    public static float BtnStpTop;
    public static float BtnStpBottom;
    public static float BtnStpLeft;
    public static float BtnStpRight;
    public static float ProgressBarCircleX = ProgressBarLeft;
    public static int stopsDiameter = 8;
    public static int stopsRadius = stopsDiameter;
    public static float stopsDiameter2 = 10;
    public static int stopClicked = -1;
    
    
    public TopoMap(PApplet p) {
        super(p);
    }
    
    public void preprocessRoutesData() {
        
    }
    
    public void loadRoutesData() {
        try {
            ifstream = new FileInputStream(routesfilename);
            in = new DataInputStream(ifstream);
            br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(";");
                Route r = new Route(split[0], split[5]);
                mRoutes.add(r);
            }
            System.out.println("Total " + mRoutes.size());
            br.close();
            in.close();
        } catch (Exception e) {}
        //finally {
        //    for (Route r : mRoutes) {
        //        System.out.println(r.routeId);
        //    }
        //}
    }
    
    public static void changeMapToScreen(int id) {
        Location loc;
        if (mTrips[id].isEmpty()) return;
        for (CTrip t : mTrips[id]) {
            loc = map.getLocationFromScreenPosition(t.screenX, t.screenY);
            float[] xy = map.getScreenPositionFromLocation(loc);
            t.screenX = xy[0];
            t.screenY = xy[1];
        }
    }

    public static void changeAllMapToScreen() {
        for (int i=0; i<TotalTimeStamps; ++i)
            changeMapToScreen(i);
    }
    
    public static void drawStops() {
        parent.stroke(200);
        parent.strokeWeight(1);
        parent.noFill();
        
        for (Stop s : mStops) {
            parent.stroke(s.route.red, s.route.green, s.route.blue);
            parent.ellipse(s.screenX, s.screenY, stopsDiameter, stopsDiameter);
        }
    }
    
    public static Route findRoute(String rid) {
        for (Route r : mRoutes) {
            if (r.routeId.equalsIgnoreCase(rid)) return r;
        }
        return null;
    }
    
    public void loadStopTimesData() {
        int cnt = 0;
        try {
            ifstream = new FileInputStream(stoptimefilename);
            in = new DataInputStream(ifstream);
            br = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(";");
                int time = Integer.parseInt(split[2]) / Interval;
                if (time >= TotalTimeStamps) continue;
                Route r = findRoute(split[0]);
                CTrip tm = new CTrip(r, Float.parseFloat(split[3]), Float.parseFloat(split[4]));
                mTrips[time].add(tm);
                //if (cnt == 500000) break;
                ++cnt;
            }
            br.close();
            in.close();
            System.out.println("Total " + cnt);
        } catch (Exception e) {
            System.out.print(e.toString());
        }
    }
    
    public static void drawPopupBox() {
        Stop stop = mStops.get(stopClicked);
        String s = stop.name;
        float x = stop.screenX;
        final float height = 50.f;
        float y = stop.screenY;
        parent.fill(stop.route.red, stop.route.green, stop.route.blue);
        parent.ellipse(x, y, vDiameter, vDiameter);
        y -= height;
        float width = parent.textWidth(s);
        if (MapRightBottomX - x < width) x -= width;
        if (MapLeftTopY >= y) y += height; 
        parent.stroke(stop.route.red, stop.route.green, stop.route.blue);
        parent.fill(0, 0, 0, 100);
        parent.rect(x, y, width, height);
        parent.fill(stop.route.red, stop.route.green, stop.route.blue);
        parent.text(s, x, y+20);
    }
    
    public static void drawVehicle() {
        if (mTrips[mTimer].isEmpty()) return;
        parent.noStroke();
        for(CTrip c: mTrips[mTimer]) {
            parent.fill(c.route.red, c.route.green, c.route.blue);
            parent.ellipse(c.screenX, c.screenY, vDiameter, vDiameter);
        }
    }
    
    public static void drawButtons() {
        //parent.strokeWeight(ProgressBarThickness);
        parent.fill(ProgressBarRed, ProgressBarGreen, ProgressBarBlue);
        parent.rect(ProgressBarLeft, ProgressBarY, ProgressBarLength, ProgressBarThickness);
        parent.strokeWeight(1);
        parent.stroke(ProgressBarCircleRed, ProgressBarCircleGreen, ProgressBarCircleBlue);
        parent.ellipse(ProgressBarCircleX, ProgressBarCircleY, ProgressBarCircleD, ProgressBarCircleD);
        parent.image(btnImg, BtnPlayCenterX, BtnPlayCenterY);
    }
    
    public static void drawFilters() {
        int cnt = 0;
        parent.noStroke();
        for (Route r : mRoutes) {
            parent.fill(r.red, r.green, r.blue);
            parent.rect(800, 700 + cnt * 10, 20, 10);
        }
    }
    
    public static boolean isInsideButton(int x, int y) {
        return x >= BtnPlyLeft && x <= BtnPlyRight && y >= BtnPlyTop && y <= BtnPlyBottom;
    }
    
    public static boolean checkButtonClicked(int x, int y) {
        if (isInsideButton(x, y)) {
            isPlaying = !isPlaying;
            if (isPlaying) btnImg = stopBtnImg;
            else btnImg = playBtnImg;
            return true;
        } else return false;
    }
    
    public static boolean checkProgressBarClicked(int x, int y) {
        if (x >= ProgressBarLeft && x <= ProgressBarRight 
                && y >= ProgressBarY && y<= ProgressBarBottom)
            return true;
        else return false;
    }
    
    public static boolean checkStops(int x, int y) {
        int size = mStops.size();
        for (int i=0; i<size; ++i) {
            if (mStops.get(i).inside(x, y)) {
                stopClicked = i;
                System.out.println("Stop " + stopClicked);
                return true;
            }
        }
        return false;
    }
    
    public void loadStops() {
        try {
            ifstream = new FileInputStream(stoproutefilename);
            in = new DataInputStream(ifstream);
            br = new BufferedReader(new InputStreamReader(in));
            String line;
            HashMap<String, String> stoproute = new HashMap<String, String>();
            stoproute.clear();
            while ((line = br.readLine()) != null) {
                String[] split = line.split(";");
                stoproute.put(split[0], split[1]);
            }
            br.close();
            in.close();
            
            ifstream = new FileInputStream(stopsfilename);
            in = new DataInputStream(ifstream);
            br = new BufferedReader(new InputStreamReader(in));
            
            while ((line = br.readLine()) != null) {
                String[] split = line.split(";");
                Route r = findRoute(stoproute.get(split[0]));
                Stop s = new Stop(Integer.parseInt(split[0]), 
                                  Float.parseFloat(split[3]), 
                                  Float.parseFloat(split[4]), 
                                  split[2], r);
                mStops.add(s);
            }
            br.close();
            in.close();
            System.out.println("Total " + mStops.size());
        } catch (Exception e) {}
        //finally {
        //    for (Stop r : mStops) {
        //        System.out.println(r.stopId);
        //    }
        //}
    }
    
    public void drawPopup2() {
        Stop stop = mStops.get(stopClicked);
        String s = stop.name;
        float x = stop.screenX2;
        float y = stop.screenY2;
        parent.fill(stop.route.red, stop.route.green, stop.route.blue);
        parent.ellipse(x, y, vDiameter, vDiameter);
        final float height = 50.f;
        y -= height;
        float width = parent.textWidth(s);
        if (Map2RightBottomX - x < width) x -= width;
        if (Map2RightBottomY >= y) y += height;
        parent.stroke(stop.route.red, stop.route.green, stop.route.blue);
        parent.fill(0, 0, 0, 100);
        parent.rect(x, y, width, height);
        parent.fill(stop.route.red, stop.route.green, stop.route.blue);
        parent.text(s, x, y+20);
    }
    
    public void drawMap2() {
        parent.stroke(200);
        parent.strokeWeight(1);
        parent.noFill();
        for (Stop s : mStops) {
            parent.stroke(s.route.red, s.route.green, s.route.blue);
            parent.ellipse(s.screenX2, s.screenY2, stopsDiameter2, stopsDiameter2);
        }
        
        if (mTrips[mTimer].isEmpty()) return;
        parent.noStroke();
        for(CTrip c: mTrips[mTimer]) {
            parent.fill(c.route.red, c.route.green, c.route.blue);
            parent.ellipse(c.screenX2, c.screenY2, vDiameter2, vDiameter2);
        }
        if (stopClicked != -1)
            drawPopup2();
    }
    
    @Override
    public void init() {
        parent.size(canvasWidth, canvasHeight, GLConstants.GLGRAPHICS);
        parent.frameRate(FrameRate);
        map = new de.fhpotsdam.unfolding.Map
                (parent, MapLeftTopX, MapLeftTopY,
                 MapWidth, MapHeight,
                 new OpenStreetMap.CloudmadeProvider(API_KEY, OpenMapID));
        map.zoomAndPanTo(new Location(CenterMapX, CenterMapY), ZoomLevel);
        map2 = new de.fhpotsdam.unfolding.Map
                (parent, Map2LeftTopX, Map2leftTopY,
                        Map2Width, Map2Height,
                        new OpenStreetMap.CloudmadeProvider(API_KEY, OpenMapID));
        map2.zoomAndPanTo(new Location(CenterMapX2, CenterMapY2), ZoomLevel2);
        //map.innerRotate(-parent.PI/2);
        //EventDispatcher eventDispatcher = MapUtils.createDefaultEventDispatcher(parent, map2);
        //ZoomMapEvent zoomMapEvt = new ZoomMapEvent(parent, map.getId());
        //zoomMapEvt.setZoomLevel(ZoomLevel);
        //eventDispatcher.unregister(map, ZoomMapEvent.TYPE_ZOOM, zoomMapEvt.getScopeId());
        parent.smooth();
        parent.ellipseMode(parent.CENTER);

        
        
        mRoutes = new ArrayList();
        loadRoutesData();
        preprocessRoutesData();
        System.out.println("Done loading routes info");
        
        mTrips = new ArrayList[TotalTimeStamps];
        for (int i=0; i<TotalTimeStamps; ++i) mTrips[i] = new ArrayList<CTrip>();
        loadStopTimesData();
        System.out.println("Done loading stop times info");
        
        mStops = new ArrayList();
        loadStops();
        System.out.println("Done loading stops info");
        
        mTimer = 0;
        
        playBtnImg = parent.loadImage(plyBtnImgfilename);
        BtnPlyLeft = BtnPlayCenterX;
        BtnPlyRight = BtnPlayCenterX + playBtnImg.width;
        BtnPlyTop = BtnPlayCenterY;
        BtnPlyBottom = BtnPlayCenterY + playBtnImg.height;
        
        stopBtnImg = parent.loadImage(stpBtnImgfilename);
        /*BtnStpTop = BtnPlayCenterY;
        BtnStpBottom = BtnPlayCenterY + stopBtnImg.height;
        BtnStpLeft = BtnStopCenterX;
        BtnStpRight = BtnStopCenterX + stopBtnImg.width;*/
        
        btnImg = playBtnImg;
    }

    @Override
    public void draw() {
        //parent.background(255);
        // Map 2
        map2.draw();
        drawMap2();
        
        // Map 1
        map.draw();
        parent.fill(255);
        parent.text("" + parent.frameRate, 20, 20);
        parent.text("" + stopClicked, 20, 40);
        Location loc = map2.getLocationFromScreenPosition(parent.mouseX, parent.mouseY);
        parent.text("X: " + loc.x + " Y: " + loc.y, 20, 60);
        int showTime = mTimer * Interval;
        parent.text((showTime / 3600) + ":" + ((showTime % 3600) / 60), 20, 80);
        if (isPlaying) {
            ++mTimer;
            if (mTimer == TotalTimeStamps) {
                mTimer = 0;
                ProgressBarCircleX = ProgressBarLeft;
                btnImg = playBtnImg;
                isPlaying = false;
            } else {
                ProgressBarCircleX = ProgressBarLeft + (float)mTimer * ProgressBarLength / TotalTimeStamps;
            }
        }
        
        drawStops();
        drawVehicle();
        
        // Dashboard
        parent.fill(255);
        parent.rect(DashboardTopX, DashboardTopY, DashboardLength, DashboardHeight);
        drawButtons();
        drawFilters();
        if (stopClicked != -1) {
            drawPopupBox();
        }
    }
    
    @Override
    public void mousePressed() {
        //isPlaying = false;
        int x = parent.mouseX;
        int y = parent.mouseY;
    }
    
    @Override
    public void mouseReleased() {
        int x = parent.mouseX;
        int y = parent.mouseY;
        //if (!isPlaying) isPlaying = true;
        if (checkButtonClicked(x, y)) {
            
        } else if (checkProgressBarClicked(x, y)) {
            mTimer = (x - ProgressBarLeft) * TotalTimeStamps / ProgressBarLength;
            ProgressBarCircleX = x;
        } else if (checkStops(x, y)) {
            //if (stopClicked != -1) stopClicked = -1;
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

}
