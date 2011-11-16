package edu.invisiblecities.data;

import java.sql.Time;
import java.util.LinkedList;
import java.util.List;

public class Trip {
	public Route route;
	public List<Time> times;
	public List<Float> latitudes;
	public List<Float> longitudes;

	public Trip() {
		times = new LinkedList<Time>();
		latitudes = new LinkedList<Float>();
		longitudes = new LinkedList<Float>();
	}
}
