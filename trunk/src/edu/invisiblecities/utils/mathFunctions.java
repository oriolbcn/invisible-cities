package edu.invisiblecities.utils;

public class mathFunctions {
    
    public static float getDistance(float x1, float y1, float x2, float y2) {
        return (float)java.lang.Math
                .sqrt((double)(x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

}
