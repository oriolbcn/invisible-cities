package edu.invisiblecities.data;

import java.util.Set;

import processing.core.PApplet;

public class DataTester extends PApplet {

	public void setup() {
		size(900, 900);
		Model mod = new Model();

		/*
		 * Set<Route> routes = mod.getRoutes(); for (Route r : routes) {
		 * println(r.route_id + " || " + r.route_name + " GOES " + " from: " +
		 * r.stations.get(0).station_name + " to: " +
		 * r.stations.get(r.stations.size() - 1).station_name); int i = 0; for
		 * (Station st : r.stations) { println("\t" + i + ": " + st.station_id +
		 * " || " + st.station_name + " ( " + st.parent.station_id + " || " +
		 * st.parent.station_name + " || " + st.parent.lat + " || " +
		 * st.parent.lon + " )"); i++; } }
		 */

		/*
		 * Set<Station> stations = mod.getStations(); for (Station st :
		 * stations) { println(st.station_id + " || " + st.station_name);
		 * println("\t frequencies: "); for (int i = 0; i <
		 * Constants.NUM_TIME_INTERVALS; i++) { print(st.frequencies[i] +
		 * " || "); } println();
		 * 
		 * println("\t delays: "); for (int i = 0; i <
		 * Constants.NUM_TIME_INTERVALS; i++) { print(st.delays[i] + " || "); }
		 * println();
		 * 
		 * println("\t riderhips: " + st.ridership); }
		 */

		Set<Trip> trips = mod.getTrips();

		int to_show = 10;
		int i = 0;
		for (Trip t : trips) {
			if (i == to_show) {

				int i_station = 0;
				Float lat = null;
				Float lon = null;
				for (int j = 0; j < t.times.size(); j++) {
					lat = t.latitudes.get(j);
					lon = t.longitudes.get(j);
					if (lat.equals(t.route.stations.get(i_station).parent.lat)) {
						println(t.route.stations.get(i_station).station_name);
						i_station++;
					}
					println(t.times.get(j) + " || " + lat + " || " + lon);
				}
			}
			i++;
		}
	}
}
