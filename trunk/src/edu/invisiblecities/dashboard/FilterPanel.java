package edu.invisiblecities.dashboard;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.invisiblecities.data.Model;
import edu.invisiblecities.data.Route;

public class FilterPanel extends JPanel implements ChangeListener {

	SliderWithLabel freqsSlider;
	SliderWithLabel delaysSlider;
	SliderWithLabel ridershipSlider;
	JCheckBox routesCBs[];

	Model mod;

	List<FilterListener> listeners;

	public FilterPanel() {
		this.setLayout(new GridBagLayout());
		mod = new Model();
		mod.loadTextRoutes();
		mod.loadTextStations();
		routesCBs = new JCheckBox[8];
		listeners = new LinkedList<FilterListener>();
		createComponents();
		setBorder(BorderFactory.createTitledBorder("Filters:"));
	}

	public void createComponents() {

		// routes
		for (int i = 0; i < 8; i++) {
			Route r = mod.getRoutes().get(i);
			JCheckBox ch = new JCheckBox(r.route_name, true);
			Color color = getColor(r.hex_color);
			ch.setForeground(color);
			ch.addChangeListener(this);
			this.add(
					ch,
					createConstraints(GridBagConstraints.HORIZONTAL, 0, i + 1,
							-1, -1));
			routesCBs[i] = ch;
		}

		// frequencies
		JLabel label1 = new JLabel("Frequency");
		this.add(label1,
				createConstraints(GridBagConstraints.HORIZONTAL, 1, 0, -1, -1));

		freqsSlider = new SliderWithLabel(0, 20);
		this.add(freqsSlider,
				createConstraints(GridBagConstraints.HORIZONTAL, 1, 1, -1, -1));
		// delays
		JLabel label2 = new JLabel("Delay");
		this.add(label2,
				createConstraints(GridBagConstraints.HORIZONTAL, 1, 2, -1, -1));

		delaysSlider = new SliderWithLabel(0, 20);
		this.add(delaysSlider,
				createConstraints(GridBagConstraints.HORIZONTAL, 1, 3, -1, -1));
		// ridership ?
		JLabel label3 = new JLabel("Ridership");
		this.add(label3,
				createConstraints(GridBagConstraints.HORIZONTAL, 1, 4, -1, -1));

		ridershipSlider = new SliderWithLabel(0, 20);
		this.add(ridershipSlider,
				createConstraints(GridBagConstraints.HORIZONTAL, 1, 5, -1, -1));

		// day
		// play and stop
		// lines or dots
	}

	// getMaxFreq
	// getMinFreq

	public boolean[] getSelectedRoutes() {

		boolean[] res = new boolean[8];

		for (int i = 0; i < 8; i++) {
			res[i] = routesCBs[i].isSelected();
		}

		return res;
	}

	public GridBagConstraints createConstraints(int fill, int gridx, int gridy,
			int ipadx, int ipady) {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = fill;
		c.gridx = gridx;
		c.gridy = gridy;
		if (ipadx != -1) {
			c.ipadx = ipadx;
		}
		if (ipady != -1) {
			c.ipady = ipady;
		}
		return c;
	}

	public Color getColor(String hex) {
		return new Color(Integer.parseInt(hex.substring(0, 2), 16),
				Integer.parseInt(hex.substring(2, 4), 16), Integer.parseInt(
						hex.substring(4, 6), 16));
	}

	public void register(FilterListener fl) {
		listeners.add(fl);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		for (FilterListener fl : listeners) {
			fl.filterChanged();
		}
	}
}
