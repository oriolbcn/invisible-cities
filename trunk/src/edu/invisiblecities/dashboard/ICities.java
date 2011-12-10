package edu.invisiblecities.dashboard;

public class ICities {

	public static Dashboard dashboard;
	public static int timer = 0;
	public static boolean IsPlaying = true;

	public static final int frameRate = 30;
	public static final int Interval = 30;
	public static final int TotalTimeStamps = 24 * 3600 / Interval;
	public static final int minRiderhsip = 5;
	public static final int maxRidership = 15;
	public static final int mult = 50;

	public static void main(String[] args) {

		dashboard = new Dashboard();
		dashboard.pack();
		dashboard.setVisible(true);
	}
}
