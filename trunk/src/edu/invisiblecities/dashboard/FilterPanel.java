package edu.invisiblecities.dashboard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class FilterPanel extends JPanel {

	JCheckBox freqsCheck;
	DoubleSlider freqsSlider;

	public FilterPanel() {
		this.setLayout(new GridBagLayout());
		createComponents();
	}

	public void createComponents() {

		// day
		// frequencies
		freqsCheck = new JCheckBox("Frequency", true);
		this.add(freqsCheck,
				createConstraints(GridBagConstraints.HORIZONTAL, 0, 0, -1, -1));

		freqsSlider = new DoubleSlider(DoubleSlider.HORIZONTAL, 0, 20);
		this.add(freqsSlider,
				createConstraints(GridBagConstraints.HORIZONTAL, 0, 1, 120, -1));
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
