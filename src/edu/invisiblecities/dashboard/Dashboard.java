package edu.invisiblecities.dashboard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;

import edu.invisiblecities.Splash;
import edu.invisiblecities.TopoMapStage;
import edu.invisiblecities.charts.HeatMaps;

public class Dashboard extends JFrame {

	static FilterPanel filterPanel;

	public Dashboard() {
		this.getContentPane().setLayout(new GridBagLayout());
		createComponents();
	}

	private void createComponents() {

		// this.getContentPane().setLayout(new GridBagLayout());

		filterPanel = new FilterPanel();
		GridBagConstraints c1 = new GridBagConstraints();
		c1.gridx = 0;
		c1.gridy = 1;
		this.getContentPane().add(filterPanel, c1);

		HeatMaps heatMaps = new HeatMaps(20, 10, 25, 800, 650, true);
		GridBagConstraints c2 = new GridBagConstraints();
		c2.gridx = 1;
		c2.gridy = 0;
		this.getContentPane().add(heatMaps, c2);
		heatMaps.init();

		Splash splash = new Splash();
		GridBagConstraints c3 = new GridBagConstraints();
		c3.gridx = 0;
		c3.gridy = 0;
		this.getContentPane().add(splash, c3);
		splash.init();
	}

	public static void registerAsFilterListener(FilterListener fl) {
		filterPanel.register(fl);
	}

	public static boolean[] getSelectedRoutes() {
		return filterPanel.getSelectedRoutes();
	}
}
