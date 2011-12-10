package edu.invisiblecities.dashboard;

public class ICities {

	public static Dashboard dashboard;
	public static int timer = 0;
	public static boolean IsPlaying = true;

	public static final int Interval = 30;
	public static final int TotalTimeStamps = 24 * 3600 / Interval;

	public static void main(String[] args) {

		dashboard = new Dashboard();
		dashboard.pack();
		dashboard.setVisible(true);
	}

}
