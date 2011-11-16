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
		ResultSet rs1 = mod
				.query("SELECT * FROM stop_times_routes ORDER BY trip_id ASC, stop_sequence ASC");
		try {
			while (rs1.next()) {

				Trip t = new Trip();
				trips.add(t);
				String route_id = rs1.getString("route_id");
				Long current_trip_id = rs1.getLong("trip_id");

				Long trip_id = current_trip_id;
				List<Integer> stop_sequence = new LinkedList<Integer>();
				while (current_trip_id.equals(trip_id)) {

					Time time = rs1.getTime("arrival_time");
					int station_id = rs1.getInt("stop_id");

					stop_sequence.add(new Integer(station_id));
					t.times.add(time);

					rs1.next();
					trip_id = rs1.getLong("trip_id");
				}
				rs1.previous();

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
				int i_station = 0;
				for (int i = 0; i < t.times.size() - 1; i++) {

					Time time = t.times.get(i);
					Float lat = t.route.stations.get(i_station).parent.lat;
					Float lon = t.route.stations.get(i_station).parent.lon;
					t.latitudes.add(i, lat);
					t.longitudes.add(i, lon);

					Time next_time = t.times.get(i + 1);

					List<Time> new_times = new LinkedList<Time>();

					Time new_time = add30secs(time);
					while (!new_time.equals(next_time)) {
						new_times.add(new_time);
						new_time = add30secs(new_time);
					}

					for (int j = 0; j < new_times.size(); j++) {
						i++;
						Time aux = new_times.get(j);
						t.times.add(i, aux);
						t.latitudes.add(i, lat);
						t.longitudes.add(i, lon);
					}
					i_station++;
				}
				t.latitudes.add(t.route.stations.get(i_station).parent.lat);
				t.longitudes.add(t.route.stations.get(i_station).parent.lon);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Set<Trip> getTrips() {
		return trips;
	}

	@SuppressWarnings("deprecation")
	public Time add30secs(Time time) {
		Time new_time = null;

		if (time.getSeconds() < 30) {
			new_time = new Time(time.getHours(), time.getMinutes(),
					time.getSeconds() + 30);
		} else {
			if (time.getMinutes() < 59) {
				new_time = new Time(time.getHours(), time.getMinutes() + 1,
						30 - time.getSeconds());
			} else {
				new_time = new Time(time.getHours() + 1, 0,
						30 - time.getSeconds());
			}
		}
		return new_time;
	}
}
