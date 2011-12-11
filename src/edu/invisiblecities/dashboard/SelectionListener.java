package edu.invisiblecities.dashboard;

public interface SelectionListener {

	public void stationSelectionChanged(int stationId, String stationName);

	public void routeSelectionChanged(String routeId, String routeName);
}
