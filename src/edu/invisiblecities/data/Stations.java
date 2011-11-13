package edu.invisiblecities.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
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

	@SuppressWarnings("deprecation")
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

			// TODO: ridership
			// frequencies
			ResultSet rs2 = mod
					.query("SELECT arrival_time FROM stop_times_routes WHERE stop_id = "
							+ station_id
							+ " AND route_id = '"
							+ r.route_id
							+ "' AND arrival_time > '05:00:00' ORDER BY arrival_time ASC");

			int current_limit_hour = 6;
			int current_i = 0;
			while (rs2.next()) {
				Time time = rs2.getTime("arrival_time");
				if (time.getHours() > current_limit_hour) {
					int aux = time.getHours();
					current_i = current_i + (aux - current_limit_hour);
					current_limit_hour = aux;
				}
				st.frequencies[current_i]++;
			}

			// delays
			ResultSet rs3 = mod
					.query("SELECT arrival_time, predicted_time FROM predictions WHERE stop_id = "
							+ station_id
							+ " AND line_id = '"
							+ r.route_id
							+ "' AND day = '"
							+ mod.day
							+ "' AND arrival_time > '05:00:00' ORDER BY arrival_time ASC");

			current_limit_hour = 6;
			current_i = 0;
			int current_num = 0;
			while (rs3.next()) {
				Time arr_time = rs3.getTime("arrival_time");
				Time pred_time = rs3.getTime("predicted_time");
				if (arr_time.getHours() > current_limit_hour) {
					int aux = arr_time.getHours();
					current_i = current_i + (aux - current_limit_hour);
					current_limit_hour = aux;
					current_num = 0;
				}
				current_num++;
				st.delays[current_i] = (st.delays[current_i]
						* (current_num - 1) + getDiffernceSeconds(pred_time,
							arr_time)) / current_num;
			}

			// riderhsip
			ResultSet rs4 = mod
					.query("SELECT rides FROM ridership WHERE station_id = "
							+ st.parent.station_id + " AND date = '" + mod.day
							+ "'");
			rs4.next();
			int rides = rs4.getInt("rides");
			st.ridership = rides;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return st;
	}

	public Set<Station> getStations() {
		return stations;
	}

	public int getDiffernceSeconds(Time t1, Time t2) {
		return (int) ((t1.getTime() - t2.getTime()) / 1000);
	}
}
