package edu.invisiblecities.data;

public class Station {
	// Basic Attributes
	public int station_id;
	public String station_name;
	public Route route;

	public ParentStation parent;

	// Other Attributes. This is for a given day!
	public int[] frequencies; // in number of trips
	public int[] delays; // in number of seconds
	public int ridership;

	public Station(int station_id, String station_name, Route route) {
		this.station_id = station_id;
		this.station_name = station_name;
		this.route = route;

		frequencies = new int[Constants.NUM_TIME_INTERVALS];
		delays = new int[Constants.NUM_TIME_INTERVALS];

		for (int i = 0; i < Constants.NUM_TIME_INTERVALS; i++) {
			frequencies[i] = 0;
			delays[i] = 0;
		}
	}

	public Station cloneDiffRoute(Route r) {
		Station st = new Station(station_id, station_name, r);
		st.parent = parent;
		st.frequencies = frequencies.clone();
		st.delays = delays.clone();
		st.ridership = ridership;

		return st;
	}
}
