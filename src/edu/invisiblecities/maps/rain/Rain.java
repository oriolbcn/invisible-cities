package edu.invisiblecities.maps.rain;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import processing.core.PApplet;
import processing.core.PImage;
import de.looksgood.ani.Ani;
import edu.invisiblecities.maps.BaseMap;
import edu.invisiblecities.maps.TopologicalMap.Route;

public class Rain extends BaseMap {

    public class Drop {
        Route route;
        float x;
        float y;
        float diameter;
        int red;
        int green;
        int blue;
        public Drop(float _x, float _y, Route rt, int dis) {
            route = rt;
            red = rt.red;
            green = rt.green;
            blue = rt.blue;
            diameter = dis / SIZEDIV;
            x = _x;
            y = _y;
        }
    }
    /*
    public class DropCircle {
        float diameter = StartDiameter;
        float x = parent.random(0, canvasWidth);
        float y = parent.random(0, canvasHeight);
        int red, green, blue;
        String name;
        Ani diameterAni;
        public DropCircle(String s, int r, int g, int b, float duration, float dia) {
            name = s;
            red = r;
            green = g;
            blue = b;
            diameterAni = new Ani(parent, 
                    duration, 
                    0.f, 
                    "diameter", 
                    dia,
                    Ani.EXPO_IN_OUT,
                    "onEnd:getNext");
                    diameterAni.setPlayMode(Ani.YOYO);
        }
        
        public void getNext(Ani _ani) {
            try {
                QueryQueueLock.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            if (totalUsed == mDrops[mTimer].size()) totalUsed = 0;
            Drop d = mDrops[mTimer].get(totalUsed);
            ++totalUsed;

            QueryQueueLock.release();
            red = d.red;
            green = d.green;
            blue = d.blue;
            // restart
            diameterAni.start();

            // move to new position
            Ani.to(this, 0.f, "x", parent.random(0, canvasWidth), Ani.EXPO_IN_OUT);
            Ani.to(this, 0.f, "y", parent.random(0, canvasHeight), Ani.EXPO_IN_OUT);
        }
        
        public void draw() {
            parent.fill(red, green, blue);
            parent.ellipse(x, y, StartDiameter, StartDiameter);
            parent.fill(0);
            parent.text(name, x, y + StartDiameter);
        }
    }
    */
    
    public static int totalUsed;
    public static int mTimer = 0;
    public static int StartDiameter = 1;
    public static final float SIZEDIV = 3000;
    //public static final int canvasWidth = 1200;
    //public static final int canvasHeight = 800;
    public static final float FrameRate = 150;
    public static final String rstdfilename = "rainrtdInsert.csv";
    public static final String routesfilename = "routeInfo.csv";
    public static final int Interval = 15;
    public static final int TotalTimeStamps = 6801;
    public static final int TimeLineLeftX = 50;
    public static final int TimeLineRightX = canvasWidth - 50;
    public static final int TimeLineWidth = TimeLineRightX - TimeLineLeftX;
    public static final int TimeLineTopY = 20;
    public static final int TimeLineBottomY = canvasHeight - 20;
    public static final int TimeLineHeight = TimeLineBottomY - TimeLineTopY;
    public static final float StrokeWeight = (float)(TimeLineRightX - TimeLineLeftX) / TotalTimeStamps;
    
    static int maxDuration = 0, minDuration = 1000000;
    
    static FileInputStream ifstream;
    static DataInputStream in;
    static BufferedReader br;
    
    //static ArrayList<DropCircle> mCircles;
    static ArrayList<Drop>[] mDrops = null;
    static ArrayList<Route> mRoutes = null;
    //static float[] freqArray = null;
    static HashMap<String, Integer> route2int;
    static int NumOfRoutes = 0;
    static PImage pimage;
    
    
    public Rain(PApplet p) {
        super(p);
    }
    
    public static Route findRoute(String rid) {
        for (Route r : mRoutes) {
            if (r.routeId.equalsIgnoreCase(rid)) return r;
        }
        return null;
    }
    
    public void loadRoutesData() {
        try {
            route2int = new HashMap<String, Integer>();
            mRoutes = new ArrayList<Route>();
            ifstream = new FileInputStream(routesfilename);
            in = new DataInputStream(ifstream);
            br = new BufferedReader(new InputStreamReader(in));
            String line;
            NumOfRoutes = 0;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(";");
                Route r = new Route(split[0], split[5]);
                mRoutes.add(r);
                route2int.put(split[0], new Integer(NumOfRoutes));
                ++NumOfRoutes;
            }
            System.out.println("Total " + mRoutes.size());
            br.close();
            in.close();
        } catch (Exception e) {}
    }
    
    public void loadDropsData() {
        int cnt = 0; 
        try {
            mDrops = new ArrayList[TotalTimeStamps];
            for (int i=0; i<TotalTimeStamps; ++i) mDrops[i] = new ArrayList<Drop>();
            ifstream = new FileInputStream(rstdfilename);
            in = new DataInputStream(ifstream);
            br = new BufferedReader(new InputStreamReader(in));
            String line;
            line = br.readLine();
            ++cnt;
            while (true) {
                if (line == null) break;
                String[] presplit = line.split(";");
                int length = 1, time = Integer.parseInt(presplit[1]);
                time /= Interval;
                float x = parent.map(time, 0, TotalTimeStamps, TimeLineLeftX, TimeLineRightX);//parent.random(TimeLineLeftX, TimeLineRightX);
                float y = parent.random(TimeLineTopY, TimeLineBottomY);
                Route r = findRoute(presplit[0]);
                Drop d = new Drop(x, y, r, length);
                mDrops[time].add(d);
                while ((line = br.readLine()) != null) {
                    String[] split = line.split(";");
                    int dist = Integer.parseInt(split[2]);
                    if (dist == 0) break;
                    time = Integer.parseInt(split[1]) / Interval;
                    //x = parent.map(time, 0, TotalTimeStamps, TimeLineLeftX, TimeLineRightX);//parent.random(0, canvasWidth);
                    Drop e = new Drop(x, y, r, dist);
                    mDrops[time].add(e);
                    ++cnt;
                }
            }
            System.out.println("Total Drops " + cnt);
            br.close();
            in.close();
            int[] freqArray = new int[NumOfRoutes];
            /*parent.background(255);
            //parent.strokeWeight(StrokeWeight);
            parent.noStroke();
            for (int i=0; i<TotalTimeStamps; ++i) {
                for (int j=0; j<NumOfRoutes; ++j) {
                    freqArray[j] = 0;
                }
                int total = 0;
                for (Drop d : mDrops[i]) {
                    int rindex = route2int.get(d.route.routeId);
                    ++freqArray[rindex];
                    ++total;
                }
                float y = (float)freqArray[0] / total * TimeLineHeight;
                parent.fill(mRoutes.get(0).red, mRoutes.get(0).green, mRoutes.get(0).blue, 100);
                float x = TimeLineLeftX + (float)i * TimeLineWidth / TotalTimeStamps;
                parent.rect(x, TimeLineTopY, StrokeWeight, y);
                y += TimeLineTopY;
                System.out.println("freq " + 0 + " " + freqArray[0]);
                System.out.println("y " + y);
                for (int j=1; j<NumOfRoutes; ++j) {
                    //freqArray[j] += freqArray[j-1];
                    float y1 = (float)freqArray[j] / total * TimeLineHeight;
                    parent.fill(mRoutes.get(j).red, mRoutes.get(j).green, mRoutes.get(j).blue, 100);
                    parent.rect(x, y, StrokeWeight, y1);
                    y += y1;
                    System.out.println("freq " + j + " " + freqArray[j]);
                    System.out.println("y " + y);
                }
            }
            parent.saveFrame("stream.png");
            System.out.println("Done");*/
            
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    
    
    
    @Override
    public void init() {
        parent.size(canvasWidth, canvasHeight);
        parent.frameRate(FrameRate);

        parent.smooth();
        parent.ellipseMode(parent.CENTER);
        parent.textAlign(parent.CENTER);
        
        pimage = parent.loadImage("stream.png");
        
        //Ani.init(parent);

        loadRoutesData();
        loadDropsData();
    }

    @Override
    public void draw() {
        parent.background(255);
        parent.image(pimage, TimeLineLeftX, TimeLineTopY);
        parent.fill(0);
        parent.strokeWeight(2);
        int showTime = mTimer * Interval;
        parent.text((showTime / 3600) + ":" + ((showTime % 3600) / 60), 20, 80);
        mTimer = (mTimer + 1) % TotalTimeStamps;
        parent.noStroke();
        parent.fill(255);
        parent.rect((float)mTimer*TimeLineWidth/TotalTimeStamps+TimeLineLeftX, TimeLineTopY, canvasWidth, canvasHeight);
        if (mDrops[mTimer].isEmpty()) return;
        parent.line(TimeLineLeftX, TimeLineBottomY, TimeLineRightX, TimeLineBottomY);
        parent.noStroke();
        for (Drop d : mDrops[mTimer]) {
            parent.fill(d.red, d.green, d.blue, 100);
            parent.ellipse(d.x, d.y, d.diameter, d.diameter);
        }
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

    @Override
    public void mouseDragged() {
        // TODO Auto-generated method stub
        
    }

}
