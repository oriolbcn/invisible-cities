package edu.invisiblecities.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Set;

public class Model {
	// Connection
	Connection conn = null;

	// Connection parameters
	private static final String HOST = "academic-mysql.cc.gatech.edu";
	private static final String USER = "cs7450_invisible";
	private static final String PASS = "lqDZySqI";
	private static final String DBNAME = "cs7450_invisible";

	// Current day
	public String day = "2011-11-14";

	// Data
	Routes routes;
	Stations stations;
	ParentStations parentStations;
	Trips trips;

	public Model() {
		conn = connect();
		routes = new Routes(this);
		stations = new Stations(this);
		parentStations = new ParentStations(this);
		trips = new Trips(this);
		load();
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

	public void load() {
		trips.load();
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
	public Set<Route> getRoutes() {
		return routes.getRoutes();
	}

	public Set<Station> getStations() {
		return stations.getStations();
	}

	public Set<ParentStation> getParentStations() {
		return parentStations.getParentStations();
	}

	public Set<Trip> getTrips() {
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
}