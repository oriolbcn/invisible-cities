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

public class Splash extends PApplet {

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
    public static int           TotalTimeStamps = 24 * 3600 / Interval;
    
    @SuppressWarnings("unchecked")
    public static ArrayList<Dot>[] mDots = new ArrayList[TotalTimeStamps];
    public Dot[]                mListHeader;
    public static boolean       IsPlaying = false;
    public static int           mTimer = 0;
    public static Route[]       mRoutes;
    public static int           NumOfDots;
    public static CheckBox[]    checkBoxes;
    public static FilterBar[]   filterBars;
    public static CheckBox[]    footPrintCheckBoxes;
    public static int           selectedFilter;
    public static PImage        clockImg;
    public static final String  Clockimgfilename = "clock.png";
    public static PGraphics     pg;
    
    @Override
    public void setup() {
        size(CanvasWidth, CanvasHeight);
        frameRate(FrameRate);
        Ani.init(this);
        smooth();
        noStroke();
        loadRoutes();
        loadSplash();
        initUI();
        clockImg = loadImage(Clockimgfilename);
        pg = createGraphics(PictureWidth, PictureHeight, P2D);
        pg.beginDraw();
        pg.image(clockImg, 0, 0);
        pg.endDraw();
        //clockImg.loadPixels();
        //drawBackgroundClock();
    }
    public boolean pause = false;
    
    public static final int FilterOffsetTop = 20;
    public static final int FilterOffsetX = 20;
    public static final int FilterSectionHeight = 70;
    public void initUI() {
        checkBoxes = new CheckBox[NumOfRoutes];
        filterBars = new FilterBar[NumOfRoutes];
        footPrintCheckBoxes = new CheckBox[NumOfRoutes];
        for (int i=0; i<NumOfRoutes; ++i) {
            checkBoxes[i] = new CheckBox(i, FilterLeftX + FilterOffsetX, 
                    FilterOffsetTop + i * FilterSectionHeight, true);
            filterBars[i] = new FilterBar(i, FilterLeftX + FilterOffsetX + CheckBoxWidth + 10,
                    FilterOffsetTop + i * FilterSectionHeight,
                    PictureWidth / 4, PictureWidth / 2); // TODO Delay
            footPrintCheckBoxes[i] = new CheckBox(i, FilterLeftX + FilterOffsetX,
                    FilterOffsetTop + i * FilterSectionHeight + CheckBoxHeight + 10, true);
        }
    }
    

    public static int   minute;
    public static int   hour;
    public void drawLayout() {
        fill(0);
        text("FPS " + frameRate, 20, 20);
        text(hour + ":" + minute, 20, 40);
        //text("value " + filterBars[0].value, 20, 60);
        stroke(0);
        line(PictureWidth, 0, PictureWidth, PictureHeight);
        for (int i=0; i<NumOfRoutes; ++i) {
            checkBoxes[i].draw();
            footPrintCheckBoxes[i].draw();
            text("Leave Footprint", footPrintCheckBoxes[i].rx + 10, footPrintCheckBoxes[i].by - 3);
        }
        rectMode(CENTER);
        for (int i=0; i<NumOfRoutes; ++i) {
            filterBars[i].draw();
        }
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
        //PGraphics pg;
        //pg = createGraphics(PictureWidth, PictureHeight, P2D);
        //pg.beginDraw();
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
        //pg.endDraw();
        save("clock.png");
    }
    
    @Override
    public void draw() {
        
        background(255);
        //int g = mTimer;
        //if (mTimer >= HalfTotalTimeStamps) g = TotalTimeStamps - mTimer;
        //int gray = (int)map(g, 0, HalfTotalTimeStamps, 0, 255);
        //tint(gray, 100);
        //text("" + gray, 20, 60);
        image(pg, 0, 0);

        if (IsPlaying) {
            if (mTimer == TotalTimeStamps) {
                IsPlaying = false;
                mTimer = 0;
            }
            if (mDots[mTimer] != null) {
                for (Dot dot : mDots[mTimer]) {
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
            //float theta = map(minute, 0, 60, -HALF_PI, TWO_PI-HALF_PI); 
            //float mx = MinuteRadius * cos(theta) + PictureCenterX;
            //float my = MinuteRadius * sin(theta) + PictureCenterY;
            //stroke(0, 20);
            //strokeWeight(5);
            //line(PictureCenterX, PictureCenterY, mx, my);
            ++mTimer;
        }
        
        noStroke();
        for (int i=0; i<NumOfRoutes; ++i) if (checkBoxes[i].isChecked) {
            fill(mRoutes[i].red, mRoutes[i].green, mRoutes[i].blue);
            Dot pointer = mListHeader[i].nextdot;
            float fvalue = filterBars[i].value;
            while (pointer != null) {
                if (pointer.delay > fvalue) {
                    Dot next = pointer.nextdot;
                    pointer.finish();
                    pointer = next;
                    continue;
                }
                pointer.draw();
                pointer = pointer.nextdot;
            }
        }
        
        drawLayout();
    }
    public static final int HalfTotalTimeStamps = TotalTimeStamps / 2;
    
    
    public boolean isInsidePicture() {
        return mouseX >= 0 && mouseX <= PictureWidth 
                && mouseY >= 0 && mouseY <= PictureHeight;
    }
    
    @Override
    public void mousePressed() {
        if (isInsidePicture()) {
            IsPlaying = !IsPlaying;
        } else {
            for (int i=0; i<NumOfRoutes; ++i) {
                if (checkBoxes[i].isClicked()) return;
                if (footPrintCheckBoxes[i].isClicked()) return;
                if (filterBars[i].isSelected()) {
                    selectedFilter = i;
                    System.out.println("mouse presed " + selectedFilter);
                    return;
                }
            }
            System.out.println("mouser pressed nothing");
            selectedFilter = -1;
        }
    }
    
    @Override
    public void mouseDragged() {
        if (selectedFilter >= 0) {
            FilterBar fb = filterBars[selectedFilter];
            if (mouseX > fb.rx)
                fb.rectx = fb.rx;
            else if (mouseX < fb.lx)
                fb.rectlx = fb.lx;
            else fb.rectx = mouseX;
            fb.value = (float)(fb.rectx - fb.lx) * (fb.rightvalue - fb.leftvalue) / FilterLength + fb.leftvalue;
        }
    }
    
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
            if (footPrintCheckBoxes[rid].isChecked && checkBoxes[rid].isChecked) {
                //clockImg.loadPixels();
                //int[] pix = clockImg.pixels;
                //for (int x=-1; x<2; ++x)
                //    pix[(int)endY * PictureWidth + (int)endX + x] = color;
                //for (int y=-1; y<2; ++y)
                //    pix[((int)endY + y) * PictureWidth + (int)endX] = color;
                //clockImg.updatePixels();
                pg.beginDraw();
                pg.fill(color);
                pg.noStroke();
                pg.smooth();
                pg.ellipse(endX, endY, diameter, diameter);
                pg.endDraw();
            }
            if (predot != null) {
                predot.nextdot = nextdot;
            }
            if (nextdot != null) {
                nextdot.predot = predot;
            }
        }
    }
   
    // UI Components
    public static int CheckBoxWidth = 20;
    public static int CheckBoxHeight = 20;
    public class CheckBox {
        public boolean isChecked;
        public int rid;
        public int lx;
        public int ty;
        public int rx;
        public int by;
        public int red;
        public int green;
        public int blue;
        
        public CheckBox(int rd, int x, int y, boolean checked) {
            rid = rd;
            lx = x;
            ty = y;
            rx = lx + CheckBoxWidth;
            by = ty + CheckBoxHeight;
            red = mRoutes[rd].red;
            green = mRoutes[rd].green;
            blue = mRoutes[rd].blue;
            isChecked = checked;
        }
        public void draw() {
            strokeWeight(1);
            fill(red, green, blue);
            rect(lx, ty, CheckBoxWidth, CheckBoxHeight);
            if (isChecked) {
                stroke(0);
                line(lx, ty, rx, by);
                line(rx, ty, lx, by);
            }
        }
        public boolean isClicked() {
            if (mouseX >= lx && mouseX <= rx && mouseY >= ty && mouseY <= by) {
                isChecked = !isChecked;
                return true;
            } else return false;
        }
    }
    
    public static final int BarThickness = 3;
    public static final int FilterLength = 100;
    public static final int BarRectWidth = 10;
    public static final int BarRectHeight = 20;
    public class FilterBar {
        public int lx;
        public int rx;
        public int y;
        public int rectx; // rectMode(CENTER)
        public int recty;
        public int rectlx;
        public int rectrx;
        public int rectty;
        public int rectby;
        public int rid;
        public int leftvalue;
        public int rightvalue;
        public boolean isSelected;
        public float value;
        public FilterBar(int rd, int x, int _y, int lv, int rv) {
            lx = x;
            y = _y;
            rid = rd;
            leftvalue = lv;
            rightvalue = rv;
            rx = lx + FilterLength;
            rectx = rx;
            recty = y;
            rectlx = rectx - BarRectWidth / 2;
            rectrx = rectx + BarRectWidth / 2;
            rectty = recty - BarRectHeight / 2;
            rectby = recty + BarRectHeight / 2;
            value = rightvalue;
            //isSelected = false;
        }
        public void draw() {
            strokeWeight(5);
            stroke(100);
            line(lx, y, rectx, y);
            strokeWeight(2);
            line(rectx, y, rx, y);
            noFill();
            stroke(0);
            strokeWeight(1);
            rect(rectx, recty, BarRectWidth, BarRectHeight);
            fill(0);
            text("Delay: " + value, lx, y + 20);
        }
        public boolean isSelected() {
            if (mouseX >= rectx - BarRectWidth / 2 && mouseX <= rectx + BarRectWidth / 2 
                    && mouseY >= recty - BarRectHeight / 2 && mouseY <= recty + BarRectHeight / 2) {
                return true;
            } else return false;
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
                float diameter = random(5, 15);
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
