package edu.invisiblecities;

import java.util.ArrayList;
import java.util.HashMap;

import processing.core.PApplet;
import processing.core.PImage;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import edu.invisiblecities.IsoMapStage.Route;
import edu.invisiblecities.dashboard.Dashboard;
import edu.invisiblecities.dashboard.FilterListener;
import edu.invisiblecities.dashboard.ICities;
import edu.invisiblecities.dashboard.SelectionListener;

public class TopoMapStage extends PApplet implements FilterListener,
		SelectionListener {

	public static final int CanvasWidth = 600;
	public static final int CanvasHeight = 600;
	public static final int MapLeftX = 0;
	public static final int MapTopY = 0;
	public static final int MapWidth = 600;
	public static final int MapHeight = CanvasHeight;
	public static final float MapCenterLat = 41.895156f;
	public static final float MapCenterLon = -87.69999f;
	public static final int PanelLeftX = MapLeftX + MapWidth;
	public static final int PanelTopY = 0;
	public static final int PanelWidth = CanvasWidth - MapWidth;
	public static final int PanelHeight = CanvasHeight;
	public static final int Interval = 30;
	public static final int TotalTimeStamps = 24 * 3600 / Interval;
	public static final int FrameRate = 30;
	public static final int ProgressBarLeft = 20;
	public static final int ProgressBarY = CanvasHeight - 50;
	public static final int CheckBoxOffsetX = 5;
	public static final int CheckBoxOffsetTop = 5;
	public static final String Mapfilename = "map.png";

	public static de.fhpotsdam.unfolding.Map map;
	public static Stop[] mStops;
	public static Route[] mRoutes;
	@SuppressWarnings("unchecked")
	public static ArrayList<Trip>[] mTrips = new ArrayList[TotalTimeStamps];
	public static int NumOfRoutes;
	public static int NumOfStops;
	public static Trip[] mListHeader;
	public static boolean FilterSelected = false;
	public static PImage MapImage;
	public static boolean[] DisplayRoutes;
	public static int mTimer = 0;

	public static final String API_KEY = "d3e0942376a3438b8d5fce7378307b58";
	public static final int OpenMapID = 44094;
	public static final int ZoomLevel = 11;
	public static Path[] mPaths;

	public void initUI() {
		TripsCounter = new int[NumOfRoutes];
	}

	public TopoMapStage() {

		loadRoutes();
		loadTrip2Route();

		System.out.println("TopoMap construction done");
	}

	@Override
	public void setup() {
		size(CanvasWidth, CanvasHeight);
		smooth();
		frameRate(FrameRate);
		map = new de.fhpotsdam.unfolding.Map(this, MapLeftX, MapTopY, MapWidth,
				MapHeight, new OpenStreetMap.CloudmadeProvider(API_KEY,
						OpenMapID));
		map.zoomAndPanTo(new Location(MapCenterLat, MapCenterLon), ZoomLevel);
		mPaths = new Path[NumOfRoutes];
		loadStop();
		loadTrip();
		loadStopDelays();
		DisplayRoutes = Dashboard.getSelectedRoutes();
		// DisplayRoutes = new boolean[NumOfRoutes];
		// for (int i=0; i<NumOfRoutes; ++i) {
		// DisplayRoutes[i] = true;
		// }
		MapImage = loadImage(Mapfilename);
		initUI();
		Dashboard.registerAsFilterListener(this);
		Dashboard.registerAsSelectionListener(this);
		System.out.println("TopoMap setup done");
	}

	public void stationSelectionChanged(int stationId, String stationName) {
		for (int i = 0; i < NumOfStops; ++i) {
			if (mStops[i].name.equals(stationName)) {
				StopClicked = mStops[i].stopId;
				return;
			}
		}
		StopClicked = -1;
	}

	public void routeSelectionChanged(String routeId, String routeName) {
		// Do nothing
	}

	public static HashMap<Integer, Float> PathWeight = new HashMap();

	public static void addTrains() {
		// PathWeight.clear();
		if (mTrips[mTimer] != null) {
			for (Trip trip : mTrips[mTimer]) {
				int rid = trip.rid;
				Trip oriNext = mListHeader[rid].nexttrip;
				trip.nexttrip = oriNext;
				trip.pretrip = mListHeader[rid];
				mListHeader[rid].nexttrip = trip;
				if (oriNext != null)
					oriNext.pretrip = trip;
			}
		}
		int showTime = mTimer * Interval;
		hour = showTime / 3600;
		minute = (showTime % 3600) / 60;
		++mTimer;
	}

	public void drawStops() {
		noStroke();
		rectMode(CENTER);
		for (int i = 0; i < NumOfStops; ++i) {
			mStops[i].draw();
		}
		rectMode(CORNER);
	}

	public static int hour, minute;
	public static boolean isDisplayed = true;

	public static void setHide(boolean hide) {
		isDisplayed = !hide;
	}

	public static void increaseStepCount() {
		for (int i = 0; i < NumOfRoutes; ++i) {
			Trip pointer = mListHeader[i].nexttrip;
			while (pointer != null) {
				++pointer.stepCount;
				pointer = pointer.nexttrip;
			}
		}
	}

	@Override
	public void draw() {
		if (ICities.IsPlaying) {
			if (mTimer >= TotalTimeStamps) {
				mTimer = 0;
				for (int i = 0; i < NumOfRoutes; ++i) {
					mListHeader[i] = new Trip();
				}
			}
			if (isDisplayed) {
				background(255);
				// background(0);
				image(MapImage, 0, 0);
			}
			addTrains();

			if (isDisplayed) {
				drawStops();
				drawTrains();
				if (StopClicked >= 0)
					drawClickedStop();
				drawLayout();
			}
			increaseStepCount();
		} else {
			if (isDisplayed) {
				background(255);
				// background(0);
				image(MapImage, 0, 0);
				drawStops();
				drawTrains();
				if (StopClicked >= 0)
					drawClickedStop();
			}
		}
	}

	public static final int PopupWindowWidth = 200;
	public static final int PopupWindowHeight = 120;

	public void drawClickedStop() {
		Stop stop = mStops[StopClicked];
		stop.drawSelected();
		float popupLeft = stop.screenX;
		float stopy = stop.screenY;
		float popupTop = stopy - PopupWindowHeight;
		if (popupTop <= 0)
			popupTop = stopy;
		// Draw shadow
		noStroke();
		fill(100, 100);
		rect(popupLeft + 5, popupTop + 5, PopupWindowWidth, PopupWindowHeight);
		stroke(stop.color);
		fill(255, 100);
		rect(popupLeft, popupTop, PopupWindowWidth, PopupWindowHeight);
		fill(0);
		text("Name: " + stop.name, popupLeft + 5, popupTop + 20);
		text("Delay: " + stop.delay[mTimer / 120], popupLeft + 5, popupTop + 40);
	}

	public void drawLayout() {
		stroke(0);
		fill(0);
		text("Time: " + hour + ":" + minute, 20, 20);
		text("FPS: " + frameRate, 20, 40);
		text("mTimer: " + mTimer, 20, 60);
	}

	public static final int TripCounterSize = 200;
	public static int[] TripsCounter;
	public static boolean DrawDots = true;

	public void drawTrains() {
		for (int i = 0; i < NumOfRoutes; ++i) {
			TripsCounter[i] = 0;
			if (DisplayRoutes[i]) {
				Trip pointer = mListHeader[i].nexttrip;
				while (pointer != null) {
					++TripsCounter[i];
					pointer = pointer.nexttrip;
				}
				int color = color(mRoutes[i].red, mRoutes[i].green,
						mRoutes[i].blue);
				fill(color);
				stroke(color, 50);
				pointer = mListHeader[i].nexttrip;
				float strokeweight = TripsCounter[i] / 30.f;
				if (DrawDots) {
					while (pointer != null) {
						pointer.draw();
						pointer = pointer.nexttrip;
					}
				} else {
					strokeWeight(strokeweight);
					mPaths[i].draw();
				}
			}
		}
		strokeWeight(1);
	}

	@Override
	public void mousePressed() {
		for (int i = 0; i < NumOfStops; ++i) {
			if (mStops[i].isClicked()) {
				Dashboard.noitifyStationSelection(mStops[i].ori_stop_id,
						mStops[i].name);
				return;
			}
		}
		StopClicked = -1;
	}

	@Override
	public void keyPressed() {
	}

	@Override
	public void mouseDragged() {
	}

	public void filterChanged() {
		DisplayRoutes = Dashboard.getSelectedRoutes();
		DrawDots = Dashboard.dotsSelected();
		// dashboard.getMaxFrequency();
		// dashboard.getMinFrequency();
		// dashboard.getMaxDelay();
		// dashboard.getMinDelay();
		// dashboard.getMaxRidership();
		// dashboard.getMinRidership();
	}

	// //////////// Inner Classes
	// ///////////////////////////////////////////////////

	public static final float StopRadius = 8;
	public static final float StopDiameter = 16;
	public static int StopClicked = -1;
	public static final int HoursPerDay = 24;
	public static final int StopDelayIndex = 3600 / Interval;

	public class Path {
		public ArrayList<float[][]> paths;
		public int rid;

		public Path(int rd, float[][] p) {
			rid = rd;
			paths = new ArrayList<float[][]>();
			paths.add(p);
		}

		public void insert(float[][] p) {
			for (float[][] path : paths) {
				int length = path.length;
				if (length != p.length)
					continue;
				for (int j = 0; j < length; ++j)
					if (p[j][0] == path[j][0] && p[j][1] == path[j][1]) {
						return;
					}
			}
			paths.add(p);
		}

		public void draw() {
			for (float[][] path : paths) {
				int length = path.length;
				for (int j = 1; j < length; ++j)
					line(path[j - 1][0], path[j - 1][1], path[j][0], path[j][1]);
			}
		}
	}

	public class Stop {
		public float screenX;
		public float screenY;
		public float[] diameter;
		public String name;
		public int stopId;
		public int ori_stop_id; // the number starts with '3'
		public Route route;
		public int color;
		public int acolor;
		public int[] delay;

		public Stop(int ori_si, int si, float x, float y, String n) {
			ori_stop_id = ori_si;
			stopId = si;
			float[] xy = map.getScreenPositionFromLocation(new Location(x, y));
			screenX = xy[0];
			screenY = xy[1];
			name = n;
		}

		public void draw() {
			fill(color, 50);
			int index = mTimer / 120;
			ellipse(screenX, screenY, 5, 5);
			rect(screenX, screenY, diameter[index], diameter[index]);
		}

		public void drawSelected() {
			fill(color);
			ellipse(screenX, screenY, StopDiameter, StopDiameter);
		}

		public boolean isClicked() {
			// AABB
			if (mouseX >= screenX - StopRadius
					&& mouseX <= screenX + StopRadius
					&& mouseY >= screenY - StopRadius
					&& mouseY <= screenY + StopRadius) {
				StopClicked = stopId;
				return true;
			} else
				return false;
		}
	}

	public class Trip {
		public float[] screenX;
		public float[] screenY;
		public int stepLength;
		public int stepCount;
		public int color;
		public float[] diameter;
		public Trip pretrip;
		public Trip nexttrip;
		public int rid;
		public String tripid;
		public int length;
		public int starttime;
		public int endtime;

		public Trip(String tid, int rd, float[] ax, float[] ay, int col,
				float[] dia, int st) {
			tripid = tid;
			rid = rd;
			color = col;
			screenX = ax;
			screenY = ay;
			stepLength = ax.length;
			stepCount = 0;
			diameter = dia;
			starttime = st;
			endtime = st + stepLength;
		}

		public Trip() {
			pretrip = nexttrip = null;
		}

		public void draw() {
			if (stepCount >= stepLength) {
				if (pretrip != null) {
					pretrip.nexttrip = nexttrip;
				}
				if (nexttrip != null) {
					nexttrip.pretrip = pretrip;
				}
				nexttrip = pretrip = null;
				return;
			}
			ellipse(screenX[stepCount], screenY[stepCount],
					diameter[stepCount], diameter[stepCount]);
		}
	}

	// //////////////////////////////////////////////////////////////////////////////

	/*************** Load Data ***************/

	public static int findRoute(String rid) {
		for (int i = 0; i < NumOfRoutes; ++i) {
			if (mRoutes[i].id.equalsIgnoreCase(rid)) {
				return i;
			}
		}
		return -1;
	}

	public static final String Routeinfofilename = "routes.csv";

	public void loadRoutes() {
		try {
			ArrayList<Route> alr = new ArrayList<Route>();
			String[] lines = loadStrings(Routeinfofilename);
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
			}
			NumOfRoutes = mRoutes.length;
			mListHeader = new Trip[NumOfRoutes];
			for (int i = 0; i < NumOfRoutes; ++i) {
				mListHeader[i] = new Trip();
			}
			System.out
					.println("Load routes done\nTotal Routes: " + NumOfRoutes);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	public static final String Triproutefilename = "routetrip.csv";
	public static HashMap<String, String> trip2route;

	public void loadTrip2Route() {
		try {
			trip2route = new HashMap<String, String>();
			String[] lines = loadStrings(Triproutefilename);
			int total = 0;
			int linelength = lines.length;
			for (int i = 0; i < linelength; ++i) {
				String[] split = lines[i].split(";");
				trip2route.put(split[1], split[0]);
				++total;
			}
			System.out.println("Trip2Route done " + total);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	public static final float RidershipUpper = 12;
	public static final float RidershipLower = 4;
	public static final String Tripstoplatlon = "tripstoplatlontime.csv";

	public void loadTrip() {
		try {
			String[] lines = loadStrings(Tripstoplatlon);
			int total = 1;
			int linelength = lines.length;
			for (int kk = 0; kk < linelength;) {
				if (lines[kk] == null)
					break;
				String[] split = lines[kk].split(";"); // 0:trip 1:time 2:lat
														// 3:lon
														// 4:stopid
				String tripid = split[0];
				int rid = findRoute(trip2route.get(tripid));
				int color = this.color(mRoutes[rid].red, mRoutes[rid].green,
						mRoutes[rid].blue);
				int scolor = this.color(mRoutes[rid].red, mRoutes[rid].green,
						mRoutes[rid].blue, 70);
				int starttime = Integer.parseInt(split[1]);
				int mtimer = starttime / Interval;
				float[] xy = map
						.getScreenPositionFromLocation(new Location(Float
								.parseFloat(split[2]), Float
								.parseFloat(split[3])));
				ArrayList<Float> ax = new ArrayList<Float>(); // x
				ax.add(new Float(xy[0]));
				ArrayList<Float> ay = new ArrayList<Float>(); // y
				ay.add(new Float(xy[1]));
				ArrayList<Float> ad = new ArrayList<Float>(); // diameter
				float diameter = random(RidershipLower, RidershipUpper); // TODO
																			// Ridership
				ad.add(new Float(diameter));
				ArrayList<Float> astopx = new ArrayList<Float>();
				ArrayList<Float> astopy = new ArrayList<Float>();
				astopx.add(new Float(xy[0]));
				astopy.add(new Float(xy[1]));
				int stopid = stop2int.get(split[4]).intValue();
				mStops[stopid].acolor = scolor;
				mStops[stopid].color = color;
				float prex = xy[0];
				float prey = xy[1];
				int pret = starttime;
				++kk;
				while (kk < linelength) {
					String[] cursplit = lines[kk].split(";");
					if (!cursplit[0].equals(tripid))
						break;
					xy = map.getScreenPositionFromLocation(new Location(Float
							.parseFloat(cursplit[2]), Float
							.parseFloat(cursplit[3])));
					float curx = xy[0];
					float cury = xy[1];
					astopx.add(new Float(curx));
					astopy.add(new Float(cury));
					int curt = Integer.parseInt(cursplit[1]);
					int nitv = (curt - pret) / Interval;
					float xitv = (curx - prex) / nitv;
					float yitv = (cury - prey) / nitv;
					stopid = stop2int.get(cursplit[4]).intValue();
					mStops[stopid].acolor = scolor;
					mStops[stopid].color = color;
					for (int k = 1; k < nitv; ++k) {
						ax.add(new Float(prex + xitv * k));
						ay.add(new Float(prey + yitv * k));
						ad.add(new Float(diameter));
					}
					ax.add(new Float(curx));
					ay.add(new Float(cury));
					ad.add(new Float(diameter));
					pret = curt;
					prex = curx;
					prey = cury;
					diameter = random(RidershipLower, RidershipUpper); // TODO
																		// Ridership
					++total;
					++kk;
				}
				int stepcnt = ax.size();
				float[] sx = new float[stepcnt];
				float[] sy = new float[stepcnt];
				float[] sd = new float[stepcnt];
				for (int i = 0; i < stepcnt; ++i) {
					sx[i] = Float.valueOf(ax.get(i));
					sy[i] = Float.valueOf(ay.get(i));
					sd[i] = Float.valueOf(ad.get(i));
				}
				Trip trip = new Trip(tripid, rid, sx, sy, color, sd, mtimer);
				int stopsize = astopx.size();
				float[][] stops = new float[stopsize][2];
				for (int i = 0; i < stopsize; ++i) {
					stops[i][0] = astopx.get(i);
					stops[i][1] = astopy.get(i);
				}
				if (mPaths[rid] == null)
					mPaths[rid] = new Path(rid, stops);
				else
					mPaths[rid].insert(stops);
				if (mTrips[mtimer] == null) {
					mTrips[mtimer] = new ArrayList<Trip>();
				}
				mTrips[mtimer].add(trip);
			}
			System.out.println("Load Trip done " + total);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	public static HashMap<String, String> stop2station;
	public static final String Stopstationfilename = "stopstation.csv";

	public void loadStop2Station() {
		try {
			String[] lines = loadStrings(Stopstationfilename);
			int linelength = lines.length;
			int total = 0;
			stop2station = new HashMap<String, String>();
			for (int i = 0; i < linelength; ++i) {
				String[] split = lines[i].split(";");
				stop2station.put(split[0], split[1]);
				++total;
			}
			System.out.println("Load Stop2station done " + total);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	public static final String stopsfilename = "stopsInfo.csv";
	public static HashMap<String, Integer> stop2int;

	public void loadStop() {
		try {
			stop2int = new HashMap<String, Integer>();
			ArrayList<String> lines = new ArrayList<String>();
			String[] line = loadStrings(stopsfilename);
			int linelength = line.length;
			for (int i = 0; i < linelength; ++i) {
				lines.add(line[i]);
			}
			NumOfStops = lines.size();
			mStops = new Stop[NumOfStops];

			for (int i = 0; i < NumOfStops; ++i) {
				String[] split = lines.get(i).split(";");
				stop2int.put(split[0], new Integer(i));
				String name = split[1].substring(1, split[1].length() - 1);
				float x = Float.parseFloat(split[2]);
				float y = Float.parseFloat(split[3]);
				mStops[i] = new Stop(Integer.parseInt(split[0]), i, x, y, name);
			}
			System.out.println("Total stops " + mStops.length);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	public static final String stopdelayfilename = "stationsdelay.csv";

	public void loadStopDelays() {
		try {
			String[] lines = loadStrings(stopdelayfilename);
			int linelength = lines.length;
			for (int kk = 0; kk < linelength; ++kk) {
				String[] split = lines[kk].split(";");
				int stopid = stop2int.get(split[0]).intValue();
				int size = split.length - 1;
				float[] diameters = new float[HoursPerDay + 1];
				int[] delay = new int[HoursPerDay + 1];
				for (int i = 1; i < size; ++i) {
					int del = Integer.parseInt(split[i]);
					diameters[i + 4] = del * 10.f / 150;
					delay[i + 4] = del;
				}
				diameters[HoursPerDay] = diameters[0];
				mStops[stopid].diameter = diameters;
				mStops[stopid].delay = delay;
			}
			System.out.println("Stop delays done");
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	private static final long serialVersionUID = 3201839212322830L;
}
