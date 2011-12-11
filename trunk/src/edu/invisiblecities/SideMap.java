package edu.invisiblecities;

import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import edu.invisiblecities.IsoMapStage.Station;
import processing.core.PApplet;

public class SideMap extends PApplet {
    
    private static final long serialVersionUID = 1L;
    public static final int CanvasWidth = 300;
    public static final int CanvasHeight = 200;
    
    /*
    @Override
    public void setup() {
        map = new de.fhpotsdam.unfolding.Map(this, MapLeftX, 0, MapWidth,
            MapHeight, new OpenStreetMap.CloudmadeProvider(API_KEY, OpenMapID));
    }
    
    public static int radarDiameter = 0;
    public static de.fhpotsdam.unfolding.Map map;
    
    public static final int MapLeftX = PictureWidth;
    public static final int MapWidth = CanvasWidth - PictureWidth;
    public static final int MapHeight = CanvasHeight / 2;
    public static final int MapBottomY = MapHeight;
    public static final int MapRightX = MapLeftX + MapWidth;
    
    public static final int SideTableLeftX = MapLeftX;
    public static final int SideTableTopY = MapHeight;
    public static final int SideTableWidth = MapWidth;
    public static final int SideTableHeight = MapHeight;
    
    public static final String API_KEY = "d3e0942376a3438b8d5fce7378307b58";
    public static final int OpenMapID = 40077;
    public static final int ZoomLevel = 10;
    public static final int SideMapDiameter = 6;
    public static final int SideMapRadius = SideMapDiameter / 2;
    public static Location loc = new Location(0, 0);
    @Override
    public void draw() {
        map.draw();
        Station sta = mStations[SelectedNode];
        // parent.noFill();
        noStroke();
        // parent.strokeWeight(RadarStrokeWeight);
        loc.setLat(sta.lat);
        loc.setLon(sta.lon);
        map.zoomAndPanTo(loc, ZoomLevel);
        float[] xy = map.getScreenPositionFromLocation(loc);
        sta.screenX = xy[0];
        sta.screenY = xy[1];
        radarDiameter = (radarDiameter + 1) % RadarDiameterMax;
        fill(sta.fred, sta.fgreen, sta.fblue, 100);
        ellipse(sta.screenX, sta.screenY, radarDiameter, radarDiameter);
        for (int i = 0; i < NumOfStations; ++i)
            if (mStations[i] != null) {
                sta = mStations[i];
                loc.setLat(sta.lat);
                loc.setLon(sta.lon);
                xy = map.getScreenPositionFromLocation(loc);
                sta.screenX = xy[0];
                sta.screenY = xy[1];
                if (sta.isInsideSideMap()) {
                    if (sta.isHover) {
                        fill(sta.fred, sta.fgreen, sta.fblue);
                        ellipse(sta.screenX, sta.screenY, SideMapDiameter + 4,
                                SideMapDiameter + 4);
                    } else {
                        fill(sta.fred, sta.fgreen, sta.fblue, 100);
                        ellipse(sta.screenX, sta.screenY, SideMapDiameter,
                                SideMapDiameter);
                    }
                }
            }
        }
    }
    
    public static final int StationOffsetY = 20;
    public static final int StationNameOffsetX = SideTableLeftX + 20;
    public static final int StationNameOffsetY = SideTableTopY + StationOffsetY;
    public static final int StationCapacityOffsetX = StationNameOffsetX;
    public static final int StationCapacityOffsetY = StationNameOffsetY
            + StationOffsetY;

    public void drawSideTable() {
        fill(255);
        rect(SideTableLeftX, SideTableTopY, SideTableWidth, SideTableHeight);
        fill(0);
        Station sta = mStations[SelectedNode];
        // text(sta.name + " " + sta.diameter, StationNameOffsetX,
        // StationNameOffsetY);
        if (hoverId >= 0) {
            int len = sta.sssp[hoverId].length;
            for (int i = 0; i < len; ++i) {
                Station ssp = mStations[sta.sssp[hoverId][i]];
                text(ssp.name + " " + ssp.diameter, StationNameOffsetX,
                        StationNameOffsetY + i * StationOffsetY);
            }
        }
    }
    
    
    public void drawLayoutAndText() {
        fill(0);
        text("Mouse x: " + mouseX + " y: " + mouseY, 20, 20);
        text("Scale " + FixedScale, 20, 40);
        text("FPS: " + frameRate, 20, 60);
        stroke(0);
        strokeWeight(0);
        line(PictureWidth, 0, PictureWidth, PictureHeight);
        line(MapLeftX, MapBottomY, MapRightX, MapBottomY);
    }
    
    
    
    
    station:
                if (screenX + SideMapRadius >= sx && screenX - SideMapRadius <= sx
                    && screenY + SideMapRadius >= sy
                    && screenY - SideMapRadius <= sy)
                return true;
    */
}
