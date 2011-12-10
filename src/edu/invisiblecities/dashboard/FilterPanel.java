package edu.invisiblecities.dashboard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.invisiblecities.data.Model;
import edu.invisiblecities.data.Route;

public class FilterPanel extends JPanel {

	SliderWithLabel freqsSlider;
	JCheckBox routesCBs[];

	Model mod;

	public FilterPanel() {
		this.setLayout(new GridBagLayout());
		mod = new Model();
		mod.loadTextRoutes();
		mod.loadTextStations();
		createComponents();
		setBorder(BorderFactory.createTitledBorder("Filters:"));

		routesCBs = new JCheckBox[8];
	}

	public void createComponents() {

		// routes
		for (int i = 0; i < 8; i++) {
			Route r = mod.getRoutes().get(i);
			JCheckBox ch = new JCheckBox(r.route_name, true);
			this.add(
					ch,
					createConstraints(GridBagConstraints.HORIZONTAL, 0, i + 1,
							-1, -1));
		}

		// day
		// frequencies
		JLabel label1 = new JLabel("Frequency");
		this.add(label1,
				createConstraints(GridBagConstraints.HORIZONTAL, 1, 0, -1, -1));

		freqsSlider = new SliderWithLabel(0, 20);
		this.add(freqsSlider,
				createConstraints(GridBagConstraints.HORIZONTAL, 1, 1, -1, -1));
		// delays
		// ridership ?
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

}
