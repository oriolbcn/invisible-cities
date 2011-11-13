package edu.invisiblecities.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Trips {
	Set<Trip> trips;
	Model mod;

	public Trips(Model mod) {
		trips = new HashSet<Trip>();
		this.mod = mod;
	}

	public void load() {
		ResultSet rs1 = mod.query("SELECT * FROM trips");

		try {
			while (rs1.next()) {

				Trip t = new Trip();
				trips.add(t);
				String route_id = rs1.getString("route_id");
				Long trip_id = rs1.getLong("trip_id");

				ResultSet rs2 = mod
						.query("SELECT * FROM stop_times WHERE trip_id = "
								+ trip_id + " ORDER BY stop_sequence ASC");

				List<Integer> stop_sequence = new LinkedList<Integer>();
				while (rs2.next()) {
					Time time = rs2.getTime("arrival_time");
					int station_id = rs2.getInt("stop_id");

					stop_sequence.add(new Integer(station_id));
					t.times.add(time.toString());
				}

				Route r = mod.getRoute(stop_sequence, route_id);

				// if it already exists, we clone it with a different
				Route newR = null;
				if (r != null) {
					newR = r;
				} else {
					// if not, the station has to be created
					newR = mod.loadRoute(stop_sequence, route_id);
				}

				t.route = newR;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Set<Trip> getTrips() {
		return trips;
	}
}
