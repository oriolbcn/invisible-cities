package edu.invisiblecities.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class Stations {
	Set<Station> stations;
	Model mod;

	public Stations(Model mod) {
		stations = new HashSet<Station>();
		this.mod = mod;
	}

	public Station getStation(int id) {
		for (Station st : stations) {
			if (st.station_id == id)
				return st;
		}
		return null;
	}

	public Station loadStation(int station_id, Route r) {

		ResultSet rs = mod
				.query("SELECT stop_id,stop_name,parent_station FROM stops WHERE stop_id = "
						+ station_id);

		Station st = null;
		try {
			rs.next();
			String station_name = rs.getString("stop_name");
			int parent_station_id = rs.getInt("parent_station");
			st = new Station(station_id, station_name, r);
			stations.add(st);

			ParentStation parentSt = mod.getParentStation(parent_station_id);

			ParentStation newParentSt = null;
			if (parentSt != null) {
				newParentSt = parentSt;
			} else {
				newParentSt = mod.loadParentStation(parent_station_id);
			}
			st.parent = newParentSt;

			// TODO: fill frequencies, delays and ridership
			ResultSet rs2 = mod
					.query("SELECT * FROM stop_times WHERE stop_id = "
							+ station_id
							+ " AND route_id = '"
							+ r.route_id
							+ "' AND arrival_time > '05:00:00' ORDER BY arrival_time ASC");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return st;
	}

	public Set<Station> getStations() {
		return stations;
	}
}
