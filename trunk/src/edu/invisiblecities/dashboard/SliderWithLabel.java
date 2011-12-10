package edu.invisiblecities.dashboard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class SliderWithLabel extends JPanel {

	DoubleSlider slider;
	JLabel lMin;
	JLabel lMax;

	int min;
	int max;

	public SliderWithLabel(int min, int max) {

		this.min = min;
		this.max = max;
		setLayout(new GridBagLayout());

		lMin = new JLabel(min + "");
		GridBagConstraints c1 = new GridBagConstraints();
		c1.gridx = 0;
		c1.gridy = 0;
		add(lMin, c1);

		slider = new DoubleSlider(DoubleSlider.HORIZONTAL, min, max);
		GridBagConstraints c2 = new GridBagConstraints();
		c2.gridx = 1;
		c2.gridy = 0;
		c2.ipadx = 120;
		add(slider, c2);

		lMax = new JLabel(max + "");
		GridBagConstraints c3 = new GridBagConstraints();
		c3.gridx = 2;
		c3.gridy = 0;
		add(lMax, c3);

	}

}
