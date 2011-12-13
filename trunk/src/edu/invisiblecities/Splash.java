package edu.invisiblecities;

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
    public static final int     FrameRate = 30;
    public static final int     ClockInnerDiameter = 100;
    
    
    @SuppressWarnings("unchecked")
    public static ArrayList<Dot>[] mDots = new ArrayList[TotalTimeStamps];
    public Dot[]                mListHeader;
    public static boolean       IsPlaying = false;
    public static Route[]       mRoutes;
    public static int           NumOfDots;
    public static int           selectedFilter;
    public static PImage        clockImg;
    public static final String  Clockimgfilename = "clock.png";
    public static PGraphics[]     pg;
    public static int           mTimer = 0;
    
    
    public Splash() {
        loadRoutes();
        loadSplash();
    }
    
    @Override
    public void setup() {
        size(CanvasWidth, CanvasHeight);
        Ani.init(this);
        frameRate(FrameRate);
        
        smooth();
        noStroke();
        
        MaxRidership = Dashboard.getMaxRidership() / ICities.mult;
        MinRidership = Dashboard.getMinRidership() / ICities.mult;
        DisplayRoutes = Dashboard.getSelectedRoutes();
        //DisplayRoutes = new boolean[NumOfRoutes];
        //for (int i=0; i<NumOfRoutes; ++i) DisplayRoutes[i] = true;
        
        initUI();
        clockImg = loadImage(Clockimgfilename);
        pg = new PGraphics[NumOfRoutes];
        for (int i=0; i<NumOfRoutes; ++i) {
            pg[i] = createGraphics(PictureWidth, PictureHeight, P2D);
        }
        Dashboard.registerAsFilterListener(this);
        //drawBackgroundClock();
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
        text("Clicktime " + (ClickedTime / Interval), 20, 60);
        stroke(0);
        line(PictureWidth, 0, PictureWidth, PictureHeight);
    }
    
    public static final int ClockDiameter = PictureWidth - 10;
    public static final int ClockRadius = ClockDiameter / 2;
    public void drawBackgroundClock() {
        PFont pf = loadFont("Apple-Chancery-28.vlw");
        textFont(pf, 28);
        background(255);
        noFill();
        smooth();
        stroke(0);
        strokeWeight(2);
        ellipse(PictureCenterX, PictureCenterY, ClockDiameter, ClockDiameter);
        fill(0, 100);
        textAlign(CENTER);
        int t;
        for (int i=0; i<12; ++i) {
            if (i == 0) t = 12;
            else t = i;
            float theta = map(i, 0, 12, -HALF_PI, TWO_PI-HALF_PI);
            float x = (ClockRadius - 15) * cos(theta) + PictureCenterX;
            float y = (ClockRadius - 15) * sin(theta) + PictureCenterY + 5;
            text("" + t, x, y);
        }
        noFill();
        stroke(0, 100);
        int slice = (ClockDiameter - ClockInnerDiameter) / 2 / 12;
        textSize(15);
        for (int i=0; i<12; ++i) {
            int diameter = 2 * slice * i + ClockInnerDiameter;
            ellipse(PictureCenterX, PictureCenterY, diameter, diameter);
            text("" + (24 - i * 2), PictureCenterX + diameter / 2, PictureCenterY);
        }
        textAlign(LEFT);
        save("clock.png");

    }
    
    public static boolean[] DisplayRoutes;
    
    public static boolean isDisplayed = true;
    
    public static void setHide(boolean hide) {
        isDisplayed = !hide;
    }
    
    public void resetMap() {
        mTimer = 0;
        INC = 1;
        pg = new PGraphics[NumOfRoutes];
        for (int i=0; i<NumOfRoutes; ++i) {
            pg[i] = createGraphics(PictureWidth, PictureHeight, P2D);
        }
    }
    
    public static int INC = 1;
    
    @Override
    public void draw() {
        if (isDisplayed) {
            background(255);
            image(clockImg, 0, 0);
            for (int i=0; i<NumOfRoutes; ++i) if (DisplayRoutes[i])
                image(pg[i], 0, 0);
        }
        if (ICities.IsPlaying) {
            if (mTimer == TotalTimeStamps) {
                INC = 0;
            }
            else if (mDots[mTimer] != null) {
                for (Dot dot : mDots[mTimer]) 
                    if (dot.diameter >= MinRidership 
                        && dot.diameter <= MaxRidership) {
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
            mTimer += INC;
        }
        if (isDisplayed) {
            noStroke();
            for (int i=0; i<NumOfRoutes; ++i) if (DisplayRoutes[i]) {
                fill(mRoutes[i].red, mRoutes[i].green, mRoutes[i].blue);
                Dot pointer = mListHeader[i].nextdot;
                while (pointer != null) {
                    pointer.draw();
                    pointer = pointer.nextdot;
                }
            }
            //drawLayout();
        }   
    }
    
    public static int ClickedTime = -1;
    public static final int HalfTotalTimeStamps = TotalTimeStamps / 2;
    private static final int ClockDistance = ClockDiameter - ClockInnerDiameter;
    @Override
    public void mouseReleased() {
        float rad = sqrt(sq(mouseX - PictureCenterX) + sq(mouseY - PictureCenterY));
        ClickedTime = (int)((ClockDiameter - rad * 2.f) * 24) * 3600 / ClockDistance;
    }
    
    @Override
    public void mousePressed() {
    }
    
    @Override
    public void mouseDragged() {
    }
    
    public static int MinRidership;
    public static int MaxRidership;
    
    public void filterChanged() {
        DisplayRoutes = Dashboard.getSelectedRoutes();
        MaxRidership = Dashboard.getMaxRidership() / ICities.mult;
        MinRidership = Dashboard.getMinRidership() / ICities.mult;
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
            color = color(red, green, blue);
            diameter = dia;
        }
        
        public Dot() {
            predot = nextdot = null;
        }
        
        public void setAni() {
            try {
                Ani.to(this, 
                    duration, 
                    "x", 
                    endX, 
                    Easing, 
                    "onEnd:finish");
                Ani.to(this, 
                    duration, 
                    "y", 
                    endY, 
                    Easing);
            } catch (Exception e) {}
        }
        
        public void draw() {
            //fill(red, green, blue);
            ellipse(x, y, diameter, diameter);
            //stroke(red, green, blue, 10);
        }
        
        public void finish() {
            pg[rid].beginDraw();
            pg[rid].fill(color);
            pg[rid].noStroke();
            //pg[rid].smooth();
            pg[rid].ellipse(endX, endY, diameter, diameter);
            pg[rid].endDraw();
            if (predot != null) {
                predot.nextdot = nextdot;
            }
            if (nextdot != null) {
                nextdot.predot = predot;
            }
        }
    }
    
////////////////////////////////////////////////////////////////////////////////
  
    public static int NumOfRoutes;
    public static HashMap<String, Integer> route2Int;
    public static final String  Routeinfofilename = "routes.csv";
    public static final String  Splashfilename = "splash.csv";
    
    public void loadSplash() {
        try {
            String[] lines = loadStrings(Splashfilename);
            int slot = 3600/Interval;
            int totalTrips = 0;
            int maxTrips = 0;
            int linelength = lines.length;
           for (int kk=0; kk<linelength; ++kk) {
                String[] split = lines[kk].split(";");
                int rid = findRoute(split[0]);
                int starttime = Integer.parseInt(split[2]);
                int endtime = Integer.parseInt(split[3]);
                int duration = endtime - starttime;
                float theta = map((starttime/Interval)%slot, 0, slot, -HALF_PI, TWO_PI-HALF_PI);
                
                // Since delay for each trip is not available, change this to start hour
                //float delay = ClockDiameter * random(0.3f, 1.0f) / 2;
                float hour = (ClockDiameter - (endtime / 3600.f) * (ClockDiameter - ClockInnerDiameter) / 24) / 2;
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
            String[] lines = loadStrings(Routeinfofilename);
            int linelength = lines.length;
            for (int kk=0; kk<linelength; ++kk) {
                String[] split = lines[kk].split(";");
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
