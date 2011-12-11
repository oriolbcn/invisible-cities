package edu.invisiblecities.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import edu.invisiblecities.dashboard.ICities;

public class SnapshotMaker {

	public static void main(String[] args) {

		for (String day : ICities.days) {

			Model mod = new Model();
			mod.day = day;
			mod.load();
			Writer out = null;

			String dir = Constants.dir;

			try {
				(new File(dir + mod.day)).mkdirs(); // Create Directory

				// Stations
				out = openWriter(
						dir + mod.day + System.getProperty("file.separator"),
						"stations_freqs_delays.csv");

				List<Station> stations = mod.getStations();
				List<String> added = new LinkedList<String>();
				for (Station s : stations) {
					if (!added.contains(s.station_name)) {
						out.write(s.station_id + "," + s.station_name + ","
								+ s.route.route_id + ",");
						added.add(s.station_name);
						for (int i = 0; i < s.frequencies.length; i++) {
							out.write(s.frequencies[i] + " , ");
						}
						for (int i = 0; i < s.delays.length; i++) {
							if (i == s.delays.length - 1) {
								out.write(String.valueOf(s.delays[i]));
							} else {
								out.write(s.delays[i] + " , ");
							}
						}
						out.write(System.getProperties().getProperty(
								"line.separator"));
					}
				}
				out.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static Writer openWriter(String dir, String fileName) {
		Writer out = null;
		try {
			out = new OutputStreamWriter(new FileOutputStream(dir + fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return out;

	}

}
