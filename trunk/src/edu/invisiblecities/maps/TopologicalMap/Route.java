package edu.invisiblecities.maps.TopologicalMap;

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
