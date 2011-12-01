package edu.invisiblecities.maps;

import processing.core.PApplet;
import edu.invisiblecities.core.BaseInfovis;

abstract public class BaseMap implements BaseInfovis {

    public static PApplet parent = null;
    public static int canvasWidth = 1400;
    public static int canvasHeight = 800;
    public static String canvasType = null;
    
    public BaseMap(PApplet p) {
        parent = p;
    }
    
    @Override
    abstract public void init();

    @Override
    abstract public void draw();

    @Override
    abstract public void mousePressed();

    @Override
    abstract public void mouseReleased();

    @Override
    abstract public void keyPressed();

    @Override
    abstract public void keyReleased();
    
}
