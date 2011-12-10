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

	public static final String[] days = { "2011-11-26", "2011-11-27",
			"2011-11-28", "2011-11-29", "2011-11-30", "2011-12-01",
			"2011-12-02", "2011-12-03", "2011-12-04", "2011-12-05",
			"2011-12-06", "2011-12-07", "2011-12-08", "2011-12-09",
			"2011-12-10", "2011-12-11", "2011-12-12" };

	public static void main(String[] args) {

		dashboard = new Dashboard();
		dashboard.pack();
		dashboard.setVisible(true);
	}
}
