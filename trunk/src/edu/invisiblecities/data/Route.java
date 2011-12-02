package edu.invisiblecities.data;

import java.util.LinkedList;
import java.util.List;

public class Route {
	public String route_id;
	public String route_name;
	public String hex_color; // use unhex(hex_color) to obtain int equivalent

	// List stations (in order!)
	public List<Station> stations;

	// For Testing
	public int frequencies[];
	public int delays[];

	public Route(String route_id, String route_name, String hex_color) {
		this.route_id = route_id;
		this.route_name = route_name;
		this.hex_color = hex_color;

		stations = new LinkedList<Station>();

		// For Testing
		frequencies = new int[Constants.NUM_TIME_INTERVALS];
		delays = new int[Constants.NUM_TIME_INTERVALS];
	}

	public int[] getFrequencies() {
		int[] res = new int[Constants.NUM_TIME_INTERVALS];

		for (int i = 0; i < Constants.NUM_TIME_INTERVALS; i++) {
			int sum = 0, n = 0;
			for (Station st : stations) {
				sum += st.frequencies[i];
				n++;
			}
			res[i] = sum / n;
		}

		return res;
	}

	public int[] getDelays() {
		int[] res = new int[Constants.NUM_TIME_INTERVALS];

		for (int i = 0; i < Constants.NUM_TIME_INTERVALS; i++) {
			int sum = 0, n = 0;
			for (Station st : stations) {
				sum += st.delays[i];
				n++;
			}
			res[i] = sum / n;
		}

		return res;
	}

	/*
	 * public Route clone() { return new Route(route_id, route_name, hex_color);
	 * }
	 */
}
