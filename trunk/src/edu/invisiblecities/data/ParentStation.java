package edu.invisiblecities.data;

public class ParentStation {
	public int station_id;
	public String station_name;
	public Float lat;
	public Float lon;

	public ParentStation(int station_id, String station_name, Float lat,
			Float lon) {
		this.station_id = station_id;
		this.station_name = station_name;
		this.lat = lat;
		this.lon = lon;
	}
}
