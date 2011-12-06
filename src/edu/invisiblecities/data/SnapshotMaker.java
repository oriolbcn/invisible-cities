package edu.invisiblecities.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

public class SnapshotMaker {

	static Model mod;

	public static void main(String[] args) {

		mod = new Model();
		mod.load();
		Writer out = null;

		String dir = Constants.dir;

		try {
			(new File(dir)).mkdirs(); // Create Directory

			// Stations
			out = openWriter(dir, "stations.txt");

			List<Station> stations = mod.getStations();
			for (Station s : stations) {
				out.write(s.station_name + "," + s.route.hex_color + ",");
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
				out.write(System.getProperties().getProperty("line.separator"));
			}
			out.close();

			// Routes
			out = openWriter(dir, "routes.txt");

			List<Route> routes = mod.getRoutes();
			for (Route r : routes) {
				out.write(r.route_name + "," + r.hex_color + ",");
				int[] freqs = r.getFrequencies();
				for (int i = 0; i < freqs.length; i++) {
					out.write(freqs[i] + " , ");
				}
				int[] delays = r.getDelays();
				for (int i = 0; i < delays.length; i++) {
					if (i == delays.length - 1) {
						out.write(String.valueOf(delays[i]));
					} else {
						out.write(delays[i] + " , ");
					}
				}
				out.write(System.getProperties().getProperty("line.separator"));
			}

			out.close();

		} catch (Exception e) {
			e.printStackTrace();
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
