package edu.invisiblecities.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class Routes {
	List<Route> routes;
	Model mod;

	public Routes(Model mod) {
		routes = new LinkedList<Route>();
		this.mod = mod;
	}

	public Route getRoute(List<Integer> stop_sequence, String id) {

		for (Route r : routes) {
			if (r.route_id.equals(id)
					&& r.stations.size() == stop_sequence.size()) {

				boolean found = true;
				for (int i = 0; i < r.stations.size() && !found; i++) {
					if (!stop_sequence.get(i).equals(
							r.stations.get(i).station_id)) {
						found = false;
					}
				}
				if (found)
					return r;
			}
		}
		return null;
	}

	public Route loadRoute(List<Integer> stop_sequence, String id) {

		ResultSet rs1 = mod.query("SELECT * FROM routes WHERE route_id = '"
				+ id + "'");

		Route r = null;
		try {
			rs1.next();

			String route_id = rs1.getString("route_id");
			String route_name = rs1.getString("route_long_name");
			String route_color = "FF" + rs1.getString("route_color");
			r = new Route(route_id, route_name, route_color);
			routes.add(r);

			for (Integer station_id : stop_sequence) {
				// This will iterate over the stations in sequence order
				Station st = mod.getStation(station_id);

				// if it already exists, we clone it with a different
				Station newSt = null;
				if (st != null) {
					if (st.route.route_id.equals(route_id)) {
						newSt = st;
					} else {
						newSt = st.cloneDiffRoute(r);
					}
				} else {
					// if not, the station has to be created
					newSt = mod.loadStation(station_id, r);
				}
				r.stations.add(newSt);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return r;
	}

	public List<Route> getRoutes() {
		return routes;
	}

	public Route getRoute(String r_id) {
		for (Route r : routes) {
			if (r.route_id.equals(r_id))
				return r;
		}
		return null;
	}
}
