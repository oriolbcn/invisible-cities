package edu.invisiblecities.data;

import java.util.LinkedList;
import java.util.List;

public class Trip {
	public Route route;
	public List<String> times;

	public Trip() {
		times = new LinkedList<String>();
	}
}
