package edu.invisiblecities.dashboard;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.invisiblecities.IsoMapStage;
import edu.invisiblecities.SideMap;
import edu.invisiblecities.Splash;
import edu.invisiblecities.TopoMapStage;
import edu.invisiblecities.charts.HeatMaps;
import edu.invisiblecities.data.Model;

public class Dashboard extends JFrame implements ChangeListener {

	public static Model mod;

	static FilterPanel filterPanel;
	static Splash splash;
	static IsoMapStage isoMap;
	static TopoMapStage topoMap;
	static JButton playButton;
	static SideMap sideMap;

	static JTabbedPane tabbedPane = new JTabbedPane();
	public int selMap = 0;

	public static List<SelectionListener> selectionListeners;

	public Dashboard() {

		mod = new Model();
		mod.day = "2011-11-26";
		mod.loadText();

		this.getContentPane().setLayout(new GridBagLayout());
		selectionListeners = new LinkedList<SelectionListener>();
		createComponents();
	}

	private void createComponents() {
		Color bg = new Color(255, 255, 255);
		this.getContentPane().setBackground(bg);
		filterPanel = new FilterPanel();
		filterPanel = new FilterPanel();
		GridBagConstraints c1 = new GridBagConstraints();
		c1.gridx = 0;
		c1.gridy = 1;
		c1.gridheight = 2;
		c1.gridwidth = 2;
		filterPanel.setBackground(bg);
		this.getContentPane().add(filterPanel, c1);

		HeatMaps heatMaps = new HeatMaps(20, 10, 25, 670, 600, true);
		GridBagConstraints c2 = new GridBagConstraints();
		c2.gridx = 1;
		c2.gridy = 0;
		c2.gridwidth = 3;
		this.getContentPane().add(heatMaps, c2);
		heatMaps.init();

		topoMap = new TopoMapStage();
		topoMap.setHide(false);
		topoMap.setVisible(true);
		tabbedPane.addTab("TopoMap", topoMap);
		topoMap.init();

		isoMap = new IsoMapStage();
		isoMap.setHide(true);
		isoMap.setVisible(false);
		tabbedPane.addTab("IsoMap", isoMap);
		isoMap.init();

		splash = new Splash();
		splash.setHide(true);
		splash.setVisible(false);
		tabbedPane.addTab("Splash", splash);
		splash.init();

		tabbedPane.addChangeListener(this);

		GridBagConstraints c3 = new GridBagConstraints();
		c3.gridx = 0;
		c3.gridy = 0;
		this.getContentPane().add(tabbedPane, c3);

		final ImageIcon playIcon = new ImageIcon("img/play.png");
		final ImageIcon pauseIcon = new ImageIcon("img/pause.png");
		playButton = new JButton(playIcon);
		playButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ICities.IsPlaying = !ICities.IsPlaying;
				if (!ICities.IsPlaying) {
					playButton.setIcon(playIcon);
				} else {
					playButton.setIcon(pauseIcon);
				}
			}

		});
		GridBagConstraints c4 = new GridBagConstraints();
		c4.gridx = 2;
		c4.gridy = 1;
		// c4.gridwidth = 2;
		c4.ipadx = 60;
		this.getContentPane().add(playButton, c4);

		final JFrame parent = this;
		JButton buttLineCharts = new JButton("Details Charts");
		buttLineCharts.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				LineChartsDialog d = new LineChartsDialog(parent);
				d.setModal(false);
				d.setVisible(true);
			}

		});
		GridBagConstraints c8 = new GridBagConstraints();
		c8.gridx = 2;
		c8.gridy = 2;
		this.getContentPane().add(buttLineCharts, c8);

		sideMap = new SideMap();
		sideMap.init();
		GridBagConstraints c10 = new GridBagConstraints();
		c10.gridx = 3;
		c10.gridy = 1;
		c10.gridheight = 2;
		//this.getContentPane().add(sideMap, c10);

	}

	public static void timeUp() {
		final ImageIcon playIcon = new ImageIcon("img/play.png");
		playButton.setIcon(playIcon);
		splash.resetMap();
		topoMap.resetMap();
	}

	public static void registerAsFilterListener(FilterListener fl) {
		filterPanel.register(fl);
	}

	public static void registerAsSelectionListener(SelectionListener sl) {
		selectionListeners.add(sl);
	}

	public static boolean[] getSelectedRoutes() {
		return filterPanel.getSelectedRoutes();
	}

	public static int getMaxFrequency() {
		return filterPanel.getMaxFrequency();
	}

	public static int getMinFrequency() {
		return filterPanel.getMinFrequency();
	}

	public static int getMaxDelay() {
		return filterPanel.getMaxDelay();
	}

	public static int getMinDelay() {
		return filterPanel.getMinDelay();
	}

	public static int getMaxRidership() {
		return filterPanel.getMaxRidership();
	}

	public static int getMinRidership() {
		return filterPanel.getMinRidership();
	}

	public static String getDay() {
		return filterPanel.getDay();
	}

	public static boolean dotsSelected() {
		return filterPanel.dotsSelected();
	}

	public static boolean linesSelected() {
		return filterPanel.linesSelected();
	}

	// To be called by Topo and Iso maps when a station is selected
	public static void noitifyStationSelection(int stationId, String stationName) {
		for (SelectionListener l : selectionListeners) {
			l.stationSelectionChanged(stationId, stationName);
		}
	}

	public static void noitifyRouteSelection(String routeId, String routeName) {
		for (SelectionListener l : selectionListeners) {
			l.routeSelectionChanged(routeId, routeName);
		}
	}

	public void showMap() {
		boolean t = false;
		boolean i = false;
		boolean s = false;
		if (selMap == ICities.INDEX_MAP_TOPO) {
			t = true;
		} else if (selMap == ICities.INDEX_MAP_ISO) {
			i = true;
		} else if (selMap == ICities.INDEX_MAP_SPLASH) {
			s = true;
		}
		Dashboard.topoMap.setHide(!t);
		Dashboard.topoMap.setVisible(t);
		Dashboard.isoMap.setHide(!i);
		Dashboard.isoMap.setVisible(i);
		Dashboard.splash.setHide(!s);
		Dashboard.splash.setVisible(s);
	}

	public void stateChanged(ChangeEvent arg0) {
		int map = tabbedPane.getSelectedIndex();
		if (map != selMap) {
			selMap = map;
			showMap();
		}
	}
}
