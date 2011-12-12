package edu.invisiblecities;

import java.util.ArrayList;

import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import edu.invisiblecities.IsoMapStage.Route;
import edu.invisiblecities.IsoMapStage.Station;
import processing.core.PApplet;

public class SideMap extends PApplet {

	private static final long serialVersionUID = 1L;
	public static final int CanvasWidth = 300;
	public static final int CanvasHeight = 200;
	public static final int ParentCanvasHeight = 600;
	public static final int PictureWidth = ParentCanvasHeight;

	public static final String Stationinfofilename = "stationrelationship.csv";
	public static Station[] mStations;
	public static int NumOfStations = 170; // Hard code for this case

	public SideMap() {
		loadStations();
	}

    public void setup() {
    	size(CanvasWidth, CanvasHeight);
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

	/*
	 * station: if (screenX + SideMapRadius >= sx && screenX - SideMapRadius <=
	 * sx && screenY + SideMapRadius >= sy && screenY - SideMapRadius <= sy)
	 * return true;
	 */

	// load data
	public void loadStations() {
		try {
			mStations = new Station[NumOfStations];
			String[] lines = loadStrings(Stationinfofilename);
			int total = lines.length;
			for (int kk = 0; kk < total; ++kk) {
				String[] split = lines[kk].split(";"); // 0: station id; 1:
														// route_id 2: name 3:
														// lat 4: lon
				int stationId = Integer.parseInt(split[0]);
				if (mStations[stationId] != null)
					throw new Exception();
				String routeId = split[1];
				String name = split[2].substring(1, split[2].length() - 1);
				float lat = Float.parseFloat(split[3]);
				float lon = Float.parseFloat(split[4]);
				// Read second line
				++kk;
				split = lines[kk].split(";");
				int len = split.length;
				ArrayList<String> al = new ArrayList<String>(len);
				for (int i = 0; i < len; i += 2) {
					if (split[i] == null)
						break;
					al.add("" + split[i] + " " + split[i + 1]);
				}
				int size = al.size();
				int rid = findRoute(routeId);
				Route r = mRoutes[rid];
				mStations[stationId] = new Station(r, name, size, r.red,
						r.green, r.blue, rid, lat, lon);
				int cnt = 0;
				for (String s : al) {
					String[] twoparts = s.split(" ");
					int stid = Integer.parseInt(twoparts[0]);
					int dist = Integer.parseInt(twoparts[1]);
					mStations[stationId].connected[cnt] = stid;
					mStations[stationId].distance[cnt] = dist;
					++cnt;
				}
			}
			System.out.println("Total Stations: " + total);

			// Load bfs distance
			MaxDistance = new int[NumOfStations];
			lines = loadStrings(Bfsinfofilename);
			total = lines.length;
			for (int kk = 0; kk < total; ++kk) {
				int stationid = Integer.parseInt(lines[kk]);
				++kk;
				String[] split = lines[kk].split(";");
				int len = split.length;
				for (int i = 0; i < len; i += 2) {
					if (split[i] == null)
						break;
					int sid = Integer.parseInt(split[i]);
					int acc = Integer.parseInt(split[i + 1]);
					mStations[stationid].bfsDistance[sid] = acc;
					if (MaxDistance[stationid] < acc)
						MaxDistance[stationid] = acc;
				}
			}
			System.out.println("BFS distance done");

			// Load SSSP
			lines = loadStrings(SSSPfilename);
			total = lines.length;
			for (int kk = 0; kk < total;) {
				int sid = Integer.parseInt(lines[kk]);
				++kk;
				while (kk < total) {
					String[] split = lines[kk].split(";");
					int len = split.length;
					if (len < 2)
						break;
					int toid = Integer.parseInt(split[0]); // Get the
															// destination
					if (len == 2 && split[1].equals("-1")) {
						mStations[sid].sssp[toid] = new int[1];
						mStations[sid].sssp[toid][0] = sid;
						++kk;
						continue;
					}
					mStations[sid].sssp[toid] = new int[len];
					// Since the path in csv is in reversed order
					for (int i = len - 1; i >= 0; --i) {
						mStations[sid].sssp[toid][len - 1 - i] = Integer
								.parseInt(split[i]);
					}
					++kk;
				}
			}

			System.out.println("SSSP done");

			initAngleRange();
			// Load bfs positions
			for (int i = 0; i < NumOfStations; ++i)
				if (mStations[i] != null) {
					Station sta = mStations[i];
					for (int j = 0; j < NumOfStations; ++j)
						if (mStations[j] != null) {
							Station sj = mStations[j];
							float theta = random(AngleRanges[sj.rid][0],
									AngleRanges[sj.rid][1]);
							sta.bfsPosition[j][0] = sta.bfsDistance[j]
									* cos(theta);
							sta.bfsPosition[j][1] = sta.bfsDistance[j]
									* sin(theta);
						}
				}
			System.out.println("BFS positions done");

		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}
}
