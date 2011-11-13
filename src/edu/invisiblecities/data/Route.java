package edu.invisiblecities.data;

import java.util.LinkedList;
import java.util.List;

public class Route {
	public String route_id;
	public String route_name;
	public String hex_color; // use unhex(hex_color) to obtain int equivalent

	// List stations (in order!)
	public List<Station> stations;

	public Route(String route_id, String route_name, String hex_color) {
		this.route_id = route_id;
		this.route_name = route_name;
		this.hex_color = hex_color;

		stations = new LinkedList<Station>();
	}

	/*
	 * public Route clone() { return new Route(route_id, route_name, hex_color);
	 * }
	 */
}
