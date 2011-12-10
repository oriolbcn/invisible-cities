package edu.invisiblecities;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import de.looksgood.ani.Ani;
import de.looksgood.ani.easing.Easing;
import edu.invisiblecities.IsoMapStage.Route;
import edu.invisiblecities.dashboard.Dashboard;
import edu.invisiblecities.dashboard.FilterListener;
import edu.invisiblecities.dashboard.ICities;

public class Splash extends PApplet implements FilterListener {

    public static final int     CanvasWidth = 600;
    public static final int     CanvasHeight = 600;
    public static final int     PictureWidth = CanvasHeight;
    public static final int     PictureHeight = CanvasHeight;
    public static final int     PictureCenterX = PictureWidth / 2;
    public static final int     PictureCenterY = PictureHeight / 2;
    public static final int     FilterLeftX = PictureWidth;
    public static final int     FilterWidth = CanvasWidth - FilterLeftX;
    public static final int     FilterHeight = CanvasHeight;
    public static final Easing  Easing = Ani.QUINT_OUT;
    public static final int     Interval = 30;
    public static final int     TotalTimeStamps = 24 * 3600 / Interval;
    
    @SuppressWarnings("unchecked")
    public static ArrayList<Dot>[] mDots = new ArrayList[TotalTimeStamps];
    public Dot[]                mListHeader;
    public static boolean       IsPlaying = false;
    public static Route[]       mRoutes;
    public static int           NumOfDots;
    public static int           selectedFilter;
    public static PImage        clockImg;
    public static final String  Clockimgfilename = "clock.png";
    public static PGraphics     pg;
    public static int           mTimer = 0;
    
    
    @Override
    public void setup() {
        size(CanvasWidth, CanvasHeight);
        frameRate(FrameRate);
        Ani.init(this);
        smooth();
        noStroke();
        loadRoutes();
        DisplayRoutes = Dashboard.getSelectedRoutes();
        loadSplash();
        initUI();
        clockImg = loadImage(Clockimgfilename);
        pg = createGraphics(PictureWidth, PictureHeight, P2D);
        pg.beginDraw();
        pg.image(clockImg, 0, 0);
        pg.endDraw();
        Dashboard.registerAsFilterListener(this);
    }
    public boolean pause = false;
    
    public static final int FilterOffsetTop = 20;
    public static final int FilterOffsetX = 20;
    public static final int FilterSectionHeight = 70;
    public void initUI() {
    }
    

    public static int   minute;
    public static int   hour;
    public void drawLayout() {
        fill(0);
        text("FPS " + frameRate, 20, 20);
        text(hour + ":" + minute, 20, 40);
        stroke(0);
        line(PictureWidth, 0, PictureWidth, PictureHeight);
        rectMode(CENTER);
        rectMode(CORNER);
    }
    
    public static final int ClockDiameter = PictureWidth - 10;
    public static final int ClockRadius = ClockDiameter / 2;
    public static final int ClockTwelveX = PictureCenterX;
    public static final int ClockTwelveY = PictureCenterY - ClockRadius + 20;
    public static final int ClockThreeX  = PictureCenterX + ClockRadius - 10;
    public static final int ClockThreeY  = PictureCenterY;
    public static final int ClockSixX    = PictureCenterX;
    public static final int ClockSixY    = PictureCenterY + ClockRadius - 20;
    public static final int ClockNineX   = 20;
    public static final int ClockNineY   = PictureCenterY;
    public static final int HourRadius   = ClockRadius - 140;
    public static final int MinuteRadius = ClockRadius - 50;
    public void drawBackgroundClock() {
        PFont pf = loadFont("Apple-Chancery-28.vlw");
        textFont(pf, 28);
        background(255);
        noFill();
        smooth();
        stroke(0);
        strokeWeight(2);
        ellipse(PictureCenterX, PictureCenterY, ClockDiameter, ClockDiameter);
        textAlign(CENTER);
        text("12", ClockTwelveX, ClockTwelveY);
        text("3", ClockThreeX, ClockThreeY);
        text("6", ClockSixX, ClockSixY);
        text("9", ClockNineX, ClockNineY);
        textAlign(LEFT);
        save("clock.png");
    }
    
    public static boolean[] DisplayRoutes;
    
    public static boolean isDisplayed = true;
    
    public static void setHide(boolean hide) {
        isDisplayed = !hide;
    }
        
    @Override
    public void draw() {
        if (isDisplayed) {
            background(255);
            image(pg, 0, 0);
            text(mTimer, 20, 80);
        }
        if (ICities.IsPlaying) {
            if (mTimer == TotalTimeStamps) {
                mTimer = 0;
            }
            if (mDots[mTimer] != null) {
                for (Dot dot : mDots[mTimer]) if (DisplayRoutes[dot.rid]) {
                    dot.x = PictureCenterX;
                    dot.y = PictureCenterY;
                    int rid = dot.rid;
                    Dot oriNext = mListHeader[rid].nextdot;
                    dot.nextdot = oriNext;
                    dot.predot = mListHeader[rid];
                    mListHeader[rid].nextdot = dot;
                    if (oriNext != null) oriNext.predot = dot;
                    dot.setAni();
                }
            }
            int showTime = mTimer * Interval;
            hour = showTime / 3600;
            minute = (showTime % 3600) / 60;
            ++mTimer;
        }
        if (isDisplayed) {
            noStroke();
            for (int i=0; i<NumOfRoutes; ++i) {
                fill(mRoutes[i].red, mRoutes[i].green, mRoutes[i].blue);
                Dot pointer = mListHeader[i].nextdot;
                float fvalue = 0.f;
                while (pointer != null) {
                    /*if (pointer.delay > fvalue) {
                        Dot next = pointer.nextdot;
                        pointer.finish();
                        pointer = next;
                        continue;
                    }*/
                    pointer.draw();
                    pointer = pointer.nextdot;
                }
            }
            drawLayout();
        }   
    }
    public static final int HalfTotalTimeStamps = TotalTimeStamps / 2;
    
    @Override
    public void mousePressed() {
    }
    
    @Override
    public void mouseDragged() {
    }
    
    public void filterChanged() {
        DisplayRoutes = Dashboard.getSelectedRoutes();
        for (int i=0; i<NumOfRoutes; ++i)
            System.out.println(mRoutes[i].name + " " + DisplayRoutes[i]);
        // dashboard.getSelectedRoutes();
        // dashboard.getMaxFrequency();
        // dashboard.getMinFrequency();
        // dashboard.getMaxDelay();
        // dashboard.getMinDelay();
        // dashboard.getMaxRidership();
        // dashboard.getMinRidership();
    }
    
////////////////// Inner Class /////////////////////////////////////////////////
    
    public class Dot {
        public float x;
        public float y;
        public float diameter;
        public int red;
        public int green;
        public int blue;
        public int alpha;
        public float endX;
        public float endY;
        public Dot predot;
        public Dot nextdot;
        public float duration;
        public Route route;
        //public boolean alive;
        public String tripid;
        public int mTime;
        public int index;
        public int rid;
        public Ani myAni;
        public float delay;
        public int color;
        
        public Dot(int rd, float _x, float _y, float ex, float ey, 
                float d, int r, int g, int b, String tid, float de, float dia) {
            x = _x;
            y = _y;
            endX = ex;
            endY = ey;
            duration = d;
            red = r;
            green = g;
            blue = b;
            alpha = 100;
            predot = null;
            nextdot = null;
            tripid = tid;
            rid = rd;
            route = mRoutes[rid];
            delay = de;
            color = color(red, green, blue, 30);
            diameter = dia;
        }
        
        public Dot() {
            predot = nextdot = null;
        }
        
        public void setAni() {
            Ani.to(this, duration, "x", endX, Easing, "onEnd:finish");
            Ani.to(this, duration, "y", endY, Easing);
        }
        
        public void draw() {
            //fill(red, green, blue);
            ellipse(x, y, diameter, diameter);
            //stroke(red, green, blue, 10);
        }
        
        public void finish() {
            pg.beginDraw();
            pg.fill(color);
            pg.noStroke();
            pg.smooth();
            pg.ellipse(endX, endY, diameter, diameter);
            pg.endDraw();
            if (predot != null) {
                predot.nextdot = nextdot;
            }
            if (nextdot != null) {
                nextdot.predot = predot;
            }
        }
    }
    
    
    
////////////////////////////////////////////////////////////////////////////////
    public static FileInputStream ifstream;
    public static DataInputStream in;
    public static BufferedReader br;
    public static int NumOfRoutes;
    public static HashMap<String, Integer> route2Int;
    public static final String  Routeinfofilename = "bin/routes.csv";
    public static final String  Splashfilename = "bin/splash.csv";
    public static final int FrameRate = 30;
    public static final int ClockInnerDiameter = 100;
    
    
    public void loadSplash() {
        try {
            ifstream = new FileInputStream(Splashfilename);
            in = new DataInputStream(ifstream);
            br = new BufferedReader(new InputStreamReader(in));
            String line;
            int slot = 3600/Interval;
            int totalTrips = 0;
            int maxTrips = 0;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(";");
                int rid = findRoute(split[0]);
                int starttime = Integer.parseInt(split[2]);
                int endtime = Integer.parseInt(split[3]);
                int duration = endtime - starttime;
                float theta = map((starttime/Interval)%slot, 0, slot, -HALF_PI, TWO_PI-HALF_PI);
                
                // Since delay for each trip is not available, change this to start hour
                //float delay = ClockDiameter * random(0.3f, 1.0f) / 2;
                float hour = (starttime / 3600.f) / 24 * ((ClockDiameter - ClockInnerDiameter)/ 2) + ClockInnerDiameter / 2;
                float x = hour * cos(theta) + PictureCenterX;
                float y = hour * sin(theta) + PictureCenterY;
                // TODO Ridership
                float diameter = random(ICities.minRiderhsip, ICities.maxRidership);
                Dot dot = new Dot(rid, PictureCenterX, PictureCenterY, x, y, (float)duration/Interval/FrameRate, 
                                mRoutes[rid].red, mRoutes[rid].green, mRoutes[rid].blue, split[1], hour, diameter);
                
                int itime = starttime / Interval;
                if (mDots[itime] == null) mDots[itime] = new ArrayList<Dot>();
                mDots[itime].add(dot);
                dot.mTime = itime;
                dot.index = mDots[itime].size() - 1;
                ++totalTrips;
                if (mDots[itime].size() > maxTrips) 
                    maxTrips = mDots[itime].size();
            }

            System.out.println("Load splash done: Total trips " + totalTrips + " max Trips " + maxTrips);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    
    public static int findRoute(String rid) {
        for (int i=0; i<NumOfRoutes; ++i) {
            if (mRoutes[i].id.equalsIgnoreCase(rid)) {
                return i;
            }
        }
        return -1;
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
                route2Int.put(r.id, new Integer(alr.size()));
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
            br.close();
            in.close();
            ifstream.close();
            mListHeader = new Dot[NumOfRoutes];
            for (int i=0; i<NumOfRoutes; ++i) {
                mListHeader[i] = new Dot();
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    
    private static final long serialVersionUID = 18932839819L;

}
