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
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

import edu.invisiblecities.IsoMapStage;
import edu.invisiblecities.Splash;
import edu.invisiblecities.TopoMapStage;
import edu.invisiblecities.charts.HeatMaps;
import edu.invisiblecities.data.Model;

public class Dashboard extends JFrame implements ActionListener {

	public static Model mod;

	static FilterPanel filterPanel;
	static Splash splash;
	static IsoMapStage isoMap;
	static TopoMapStage topoMap;
	static JButton playButton;
	static JButton pauseButton;
	static JTabbedPane tabbedPane = new JTabbedPane();

	JComboBox mapCombo;
	public int selMap = 0;

	List<SelectionListener> selectionListeners;

	public Dashboard() {

		mod = new Model();
		mod.loadText();

		this.getContentPane().setLayout(new GridBagLayout());
		selectionListeners = new LinkedList<SelectionListener>();
		createComponents();
	}

	private void createComponents() {
		Color bg = new Color(255, 255, 255);
		this.getContentPane().setBackground(bg);
		filterPanel = new FilterPanel();
		GridBagConstraints c1 = new GridBagConstraints();
		c1.gridx = 0;
		c1.gridy = 1;
		filterPanel.setBackground(bg);
		this.getContentPane().add(filterPanel, c1);

		HeatMaps heatMaps = new HeatMaps(20, 10, 25, 600, 600, true);
		GridBagConstraints c2 = new GridBagConstraints();
		c2.gridx = 1;
		c2.gridy = 0;
		c2.gridwidth = 2;
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
		
        GridBagConstraints c3 = new GridBagConstraints();
        c3.gridx = 0;
        c3.gridy = 0;
        this.getContentPane().add(tabbedPane);
		
		// maps combo
		JLabel label1 = new JLabel("Map Vis:");
		GridBagConstraints c7 = new GridBagConstraints();
		c7.gridx = 1;
		c7.gridy = 1;
		this.getContentPane().add(label1, c7);

		mapCombo = new JComboBox(ICities.maps);
		mapCombo.setSelectedIndex(ICities.INDEX_MAP_TOPO);
		mapCombo.addActionListener(this);
		GridBagConstraints c6 = new GridBagConstraints();
		c6.gridx = 2;
		c6.gridy = 1;
		this.getContentPane().add(mapCombo, c6);

		ImageIcon playIcon = new ImageIcon("img/play.png");
		ImageIcon pauseIcon = new ImageIcon("img/pause.png");
		playButton = new JButton(playIcon);
		playButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ICities.IsPlaying = true;
			}

		});
		GridBagConstraints c4 = new GridBagConstraints();
		c4.gridx = 3;
		c4.gridy = 1;
		this.getContentPane().add(playButton, c4);

		pauseButton = new JButton(pauseIcon);
		pauseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ICities.IsPlaying = false;
			}

		});
		GridBagConstraints c5 = new GridBagConstraints();
		c5.gridx = 4;
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

	// To be called by Topo and Iso maps when a station is selected
	public void noitifyStationSelection(int stationId, String stationName) {
		for (SelectionListener l : selectionListeners) {
			l.stationSelectionChanged(stationId, stationName);
		}
	}

	public void noitifyRouteSelection(String routeId, String routeName) {
		for (SelectionListener l : selectionListeners) {
			l.routeSelectionChanged(routeId, routeName);
		}
	}

	public void actionPerformed(ActionEvent e) {
		int map = mapCombo.getSelectedIndex();
		if (map != selMap) {
			selMap = map;
			showMap();
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
		tabbedPane.setSelectedIndex(selMap);
		System.out.println(selMap);
		Dashboard.topoMap.setHide(!t);
		Dashboard.topoMap.setVisible(t);
		Dashboard.isoMap.setHide(!i);
		Dashboard.isoMap.setVisible(i);
		Dashboard.splash.setHide(!s);
		Dashboard.splash.setVisible(s);
	}
}
