package edu.invisiblecities.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ParentStations {

	Set<ParentStation> stations;
	Model mod;

	public ParentStations(Model mod) {
		stations = new HashSet<ParentStation>();
		this.mod = mod;
	}

	public ParentStation getParentStation(int id) {
		for (ParentStation st : stations) {
			if (st.station_id == id)
				return st;
		}
		return null;
	}

	public ParentStation loadParentStation(int station_id) {

		ResultSet rs = mod
				.query("SELECT stop_id,stop_name,stop_lat,stop_lon FROM stops WHERE stop_id = "
						+ station_id);
		ParentStation st = null;
		try {
			rs.next();
			String station_name = rs.getString("stop_name");
			Float station_lat = rs.getFloat("stop_lat");
			Float station_lon = rs.getFloat("stop_lon");
			st = new ParentStation(station_id, station_name, station_lat,
					station_lon);
			stations.add(st);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return st;

	}

	public Set<ParentStation> getParentStations() {
		return stations;
	}

}
