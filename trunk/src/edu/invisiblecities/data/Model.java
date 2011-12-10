package edu.invisiblecities.data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Model {
	// Connection
	Connection conn = null;

	// Connection parameters
	private static final String HOST = "academic-mysql.cc.gatech.edu";
	private static final String USER = "cs7450_invisible";
	private static final String PASS = "lqDZySqI";
	private static final String DBNAME = "cs7450_invisible";

	// Current day
	public String day; // YYYY-MM-DD
	// Number of trips to show
	public int limit; // 0 for no limit

	// Data
	private Routes routes;
	private Stations stations;
	private ParentStations parentStations;
	private Trips trips;
	public List<Timepoint> timepoints[];

	@SuppressWarnings("unchecked")
	public Model() {

		routes = new Routes(this);
		stations = new Stations(this);
		parentStations = new ParentStations(this);
		trips = new Trips(this);
		timepoints = (List<Timepoint>[]) new List[Constants.NUM_TIMEPOINTS];
		for (int i = 0; i < Constants.NUM_TIMEPOINTS; i++) {
			timepoints[i] = new LinkedList<Timepoint>();
		}
		this.limit = Constants.limit;
		this.day = Constants.day;

	}

	public void load() {
		conn = connect();
		__load();
	}

	public Connection connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		try {
			String url = "jdbc:mysql://" + HOST + "/" + DBNAME;
			return DriverManager.getConnection(url, USER, PASS);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public ResultSet query(String query) {
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void __load() {
		trips.load();
		loadTimepoints();
	}

	private void loadTimepoints() {

		List<Trip> trips = getTrips();
		for (Trip t : trips) {
			int i_station = 0;
			// Classify the timepoints of the trip
			for (int i = 0; i < t.times.size(); i++) {
				Float lat = t.latitudes.get(i);
				Float lon = t.longitudes.get(i);
				Route r = t.route;
				int stop_index = -1;
				if (lat.equals(r.stations.get(i_station).parent.lat)) {
					stop_index = i_station;
					i_station++;
				}
				timepoints[getPointIndex(t.times.get(i))].add(new Timepoint(
						lat, lon, r, stop_index));
			}
		}
	}

	@SuppressWarnings("deprecation")
	private int getPointIndex(Time t) {
		return (t.getHours() - 5) * 120 + t.getMinutes() * 2
				+ (t.getSeconds() == 30 ? 1 : 0);

	}

	public Station loadStation(int station_id, Route r) {
		return stations.loadStation(station_id, r);
	}

	public ParentStation loadParentStation(int station_id) {
		return parentStations.loadParentStation(station_id);
	}

	public Route loadRoute(List<Integer> stop_sequence, String id) {
		return routes.loadRoute(stop_sequence, id);
	}

	// Get Data
	public List<Route> getRoutes() {
		return routes.getRoutes();
	}

	public List<Station> getStations() {
		return stations.getStations();
	}

	public List<ParentStation> getParentStations() {
		return parentStations.getParentStations();
	}

	public List<Trip> getTrips() {
		return trips.getTrips();
	}

	public Station getStation(int id) {
		return stations.getStation(id);
	}

	public ParentStation getParentStation(int id) {
		return parentStations.getParentStation(id);
	}

	public Route getRoute(List<Integer> stop_sequence, String id) {
		return routes.getRoute(stop_sequence, id);
	}

	public void loadTextRoutes() {
		String dir = Constants.dirProcessing;

		try {
			ArrayList<String> lines = new ArrayList<String>();
			Scanner scanner;
			scanner = new Scanner(new FileInputStream(dir + "routes.csv"));

			while (scanner.hasNextLine()) {
				lines.add(scanner.nextLine());
			}
			scanner.close();

			for (int i = 0; i < lines.size(); i++) {
				String values[] = lines.get(i).split(";");
				Route r = new Route("0", values[2], values[5]);
				getRoutes().add(r);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void loadTextStations() {
		String dir = Constants.dirProcessing;

		try {
			ArrayList<String> lines = new ArrayList<String>();
			Scanner scanner;

			scanner = new Scanner(new FileInputStream(dir + "stations.txt"));

			while (scanner.hasNextLine()) {
				lines.add(scanner.nextLine());
			}
			scanner.close();

			for (int i = 0; i < lines.size(); i++) {
				String values[] = lines.get(i).split(",");
				boolean contains = false;
				for (Station st : getStations()) {
					if (st.station_name.equals(values[0]))
						contains = true;
				}
				if (!contains) {
					Station st = new Station(0, values[0], new Route("0",
							"name", values[1]));
					for (int j = 0; j < Constants.NUM_TIME_INTERVALS; j++) {
						st.frequencies[j] = Integer.parseInt(values[2 + j]
								.trim());
					}
					for (int j = 0; j < Constants.NUM_TIME_INTERVALS; j++) {
						st.delays[j] = Integer.parseInt(values[2
								+ Constants.NUM_TIME_INTERVALS + j].trim());
					}
					getStations().add(st);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}