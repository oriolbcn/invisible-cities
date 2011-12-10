package edu.invisiblecities.dashboard;

public class ICities {

	public static Dashboard dashboard;

	public static void main(String[] args) {

		dashboard = new Dashboard();
		dashboard.pack();
		dashboard.setVisible(true);
	}

}
