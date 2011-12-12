package edu.invisiblecities;

import java.util.ArrayList;
import java.util.HashMap;

import processing.core.PApplet;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import edu.invisiblecities.IsoMapStage.Route;
import edu.invisiblecities.dashboard.Dashboard;
import edu.invisiblecities.dashboard.SelectionListener;

public class SideMap extends PApplet implements SelectionListener {

	public static final int CanvasWidth = 200;
	public static final int CanvasHeight = 200;
	public static final String Stationinfofilename = "stationrelationship.csv";
	public static final String API_KEY = "d3e0942376a3438b8d5fce7378307b58";
	public static final int OpenMapID = 44094;
	public static final int ZoomLevel = 10;
	public static final int StationDiameter = 6;
	public static final int StationRadius = StationDiameter / 2;
	public static final int RadarDiameterMax = 20;
	public static final int FrameRate = 20;

	public static de.fhpotsdam.unfolding.Map map;
	public static int radarDiameter = 0;
	public static Station[] mStations;
	public static Route[] mRoutes;
	public static int NumOfRoutes;
	public static int NumOfStations;
	public static int SelectedNode = -1;

	public SideMap() {
		loadRoutes();
		loadStations();
	}

	public void setup() {
		// size(CanvasWidth, CanvasHeight, GLConstants.GLGRAPHICS);
		size(CanvasWidth, CanvasHeight);
		map = new de.fhpotsdam.unfolding.Map(this, 0, 0, CanvasWidth,
				CanvasHeight, new OpenStreetMap.CloudmadeProvider(API_KEY,
						OpenMapID));
		// MapUtils.createDefaultEventDispatcher(this, map);
		smooth();
		noStroke();
		updateLocations(0);
		frameRate(FrameRate);
		Dashboard.registerAsSelectionListener(this);
	}

	public int getSelectionByName(String stationName) {
		for (int i = 0; i < NumOfStations; ++i)
			if (mStations[i] != null) {
				if (mStations[i].name.equals(stationName))
					return i;
			}
		return -1;
	}

	@Override
	public void stationSelectionChanged(int stationId, String stationName) {
		SelectedNode = getSelectionByName(stationName);
		if (SelectedNode != -1) {
			updateLocations(SelectedNode);
		}
	}

	@Override
	public void routeSelectionChanged(String routeId, String routeName) {
		// TODO Auto-generated method stub

	}

	public static Location loc = new Location(0, 0);

	public void updateLocations(int selected) {
		System.out.println("Selected Node " + selected + " lat "
				+ mStations[selected].lat + " lon " + mStations[selected].lon);
		loc.setLat(mStations[selected].lat);
		loc.setLon(mStations[selected].lon);
		map.zoomAndPanTo(loc, ZoomLevel);
		for (int i = 0; i < NumOfStations; ++i) {
			Station sta = mStations[i];
			loc.setLat(sta.lat);
			loc.setLon(sta.lon);
			float[] xy = map.getScreenPositionFromLocation(loc);
			sta.screenX = xy[0];
			sta.screenY = xy[1];
		}
	}

	public void draw() {
		background(255);
		map.draw();
		for (int i = 0; i < NumOfStations; ++i)
			mStations[i].draw();
		if (SelectedNode >= 0) {
			drawRadar();
		}
		radarDiameter = (radarDiameter + 1) % RadarDiameterMax;
	}

	public void drawRadar() {
		Station sta = mStations[SelectedNode];
		fill(sta.color, 100);
		ellipse(sta.screenX, sta.screenY, radarDiameter, radarDiameter);
	}

	public int getSelection() {
		for (int i = 0; i < NumOfStations; ++i)
			if (mStations[i] != null) {
				if (mStations[i].isInside())
					return i;
			}
		return -1;
	}

	public void mousePressed() {

	}

	@Override
	public void mouseReleased() {
		int id = getSelection();
		System.out.println("Node Selected " + id);
		if (id >= 0) {
			SelectedNode = id;
			updateLocations(SelectedNode);
			Dashboard.noitifyStationSelection(-1, mStations[SelectedNode].name);
			radarDiameter = 0;
		}
	}

	// //////////////////// Inner Classes
	// ///////////////////////////////////////////
	public class Station {
		public float lat;
		public float lon;
		public float screenX;
		public float screenY;
		public int color;
		public String name;
		public int rid;
		public int id;

		public Station(int id, float la, float lo, int col, String name) {
			this.id = id;
			lat = la;
			lon = lo;
			color = col;
			this.name = name;
		}

		public void draw() {
			fill(color);
			ellipse(screenX, screenY, StationDiameter, StationDiameter);
		}

		public boolean isInside() {
			if (screenX + StationRadius >= mouseX
					&& screenX - StationRadius <= mouseX
					&& screenY + StationRadius >= mouseY
					&& screenY - StationRadius <= mouseY)
				return true;
			return false;
		}
	}

	// //////////////////////////////////////////////////////////////////////////////
	// load data
	private static HashMap<String, Integer> route2Int;

	public static int findRoute(String rid) {
		for (int i = 0; i < NumOfRoutes; ++i) {
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
			String[] lines = loadStrings(IsoMapStage.Routeinfofilename);
			int linelength = lines.length;
			for (int i = 0; i < linelength; ++i) {
				String[] split = lines[i].split(";");
				Route r = new Route(split[0], split[2].substring(1), split[4],
						split[5]);
				alr.add(r);
			}
			int size = alr.size();
			// mRoutes = (Route[]) alr.toArray(); //??
			mRoutes = new Route[size];
			for (int i = 0; i < size; ++i) {
				Route r = alr.get(i);
				mRoutes[i] = new Route(r.id, r.name, r.url, r.red, r.green,
						r.blue);
				route2Int.put(r.id, new Integer(i));
			}
			NumOfRoutes = mRoutes.length;
			System.out
					.println("Load routes done\nTotal Routes: " + NumOfRoutes);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	public void loadStations() {
		try {
			String[] lines = loadStrings(Stationinfofilename);
			NumOfStations = lines.length / 2;
			mStations = new Station[NumOfStations];
			for (int kk = 0; kk < NumOfStations; ++kk) {
				String[] split = lines[kk * 2].split(";"); // 0: station id; 1:
															// route_id 2: name
															// 3:
															// lat 4: lon
				int stationId = kk;// Integer.parseInt(split[0]);
				if (mStations[stationId] != null)
					throw new Exception();
				String routeId = split[1];
				String name = split[2].substring(1, split[2].length() - 1); // remove
																			// the
																			// "s
				float lat = Float.parseFloat(split[3]);
				float lon = Float.parseFloat(split[4]);
				// Skip the second line
				int rid = findRoute(routeId);
				Route r = mRoutes[rid];
				int color = color(mRoutes[rid].red, mRoutes[rid].green,
						mRoutes[rid].blue);
				mStations[stationId] = new Station(stationId, lat, lon, color,
						name);
			}
			System.out.println("Total Stations: " + NumOfStations);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	private static final long serialVersionUID = 1L;

}
