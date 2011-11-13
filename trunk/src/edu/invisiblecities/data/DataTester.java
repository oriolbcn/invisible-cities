package edu.invisiblecities.data;

import java.util.Set;

import processing.core.PApplet;

public class DataTester extends PApplet {

	public void setup() {
		size(900, 900);
		Model mod = new Model();

		Set<Route> routes = mod.getRoutes();
		for (Route r : routes) {
			println(r.route_id + " || " + r.route_name + " GOES " + " from: "
					+ r.stations.get(0).station_name + " to: "
					+ r.stations.get(r.stations.size() - 1).station_name);
			int i = 0;
			for (Station st : r.stations) {
				println("\t" + i + ": " + st.station_id + " || "
						+ st.station_name + " ( " + st.parent.station_id
						+ " || " + st.parent.station_name + " || "
						+ st.parent.lat + " || " + st.parent.lon + " )");
				i++;
			}
		}

	}
}
