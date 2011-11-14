package edu.invisiblecities.maps;

import processing.core.PApplet;
import edu.invisiblecities.core.BaseInfovis;

abstract public class BaseMap implements BaseInfovis {

    public static PApplet parent = null;
    public static int canvasWidth = 1000;
    public static int canvasHeight = 800;
    
    public BaseMap(PApplet p) {
        parent = p;
    }
    
    @Override
    abstract public void init();

    @Override
    public void draw() {
        parent.text(parent.frameRate, 5, 5);
    }

    @Override
    abstract public void mousePressed();

    @Override
    abstract public void mouseReleased();

    @Override
    abstract public void keyPressed();

    @Override
    abstract public void keyReleased();
    
}
