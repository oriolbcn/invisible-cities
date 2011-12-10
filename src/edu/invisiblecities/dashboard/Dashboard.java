package edu.invisiblecities.dashboard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

import edu.invisiblecities.IsoMapStage;
import edu.invisiblecities.Splash;
import edu.invisiblecities.TopoMapStage;
import edu.invisiblecities.charts.HeatMaps;

public class Dashboard extends JFrame {

	static FilterPanel filterPanel;
	static Splash splash;
	static IsoMapStage isoMap;
	static TopoMapStage topoMap;
	static JButton playButton;
	static JButton pauseButton;

	// List<SelectionListener>

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

		HeatMaps heatMaps = new HeatMaps(20, 10, 25, 700, 600, true);
		GridBagConstraints c2 = new GridBagConstraints();
		c2.gridx = 1;
		c2.gridy = 0;
		this.getContentPane().add(heatMaps, c2);
		heatMaps.init();

		splash = new Splash();
		GridBagConstraints c3 = new GridBagConstraints();
		c3.gridx = 0;
		c3.gridy = 0;
		this.getContentPane().add(splash, c3);
		splash.init();
		splash.setHide(true);
		splash.setVisible(false);

		isoMap = new IsoMapStage();
		this.getContentPane().add(isoMap, c3);
		isoMap.init();
		isoMap.setHide(false);
		isoMap.setVisible(true);

		topoMap = new TopoMapStage();
		this.getContentPane().add(topoMap, c3);
		// topoMap.init();
		// topoMap.setHide(false);
		// topoMap.setVisible(true);

		// TODO: play and stop
		ImageIcon playIcon = new ImageIcon("img/play.png");
		ImageIcon pauseIcon = new ImageIcon("img/pause.png");
		playButton = new JButton(playIcon);
		GridBagConstraints c4 = new GridBagConstraints();
		c4.gridx = 1;
		c4.gridy = 1;
		this.getContentPane().add(playButton, c4);

		pauseButton = new JButton(pauseIcon);
		GridBagConstraints c5 = new GridBagConstraints();
		c5.gridx = 2;
		c5.gridy = 1;
		this.getContentPane().add(pauseButton, c5);

	}

	public static void registerAsFilterListener(FilterListener fl) {
		filterPanel.register(fl);
	}

	public static boolean[] getSelectedRoutes() {
		return filterPanel.getSelectedRoutes();
	}

	public int getMaxFrequency() {
		return filterPanel.getMaxFrequency();
	}

	public int getMinFrequency() {
		return filterPanel.getMinFrequency();
	}

	public int getMaxDelay() {
		return filterPanel.getMaxDelay();
	}

	public int getMinDelay() {
		return filterPanel.getMinDelay();
	}

	public int getMaxRidership() {
		return filterPanel.getMaxRidership();
	}

	public int getMinRidership() {
		return filterPanel.getMinRidership();
	}

	public void noitifySelection(String stationId) {
		// notifiy listeners
	}
}
