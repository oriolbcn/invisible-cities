package edu.invisiblecities.data;

public class Timepoint {
	public Float latitude;
	public Float longitude;
	public Route route;
	public int stop_index;

	public Timepoint(Float lat, Float lon, Route r, int stop_i) {
		this.latitude = lat;
		this.longitude = lon;
		this.route = r;
		this.stop_index = stop_i; // -1 if no stop in this time point
	}
}
