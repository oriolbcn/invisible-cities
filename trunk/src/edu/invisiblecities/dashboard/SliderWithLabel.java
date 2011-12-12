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

		lMin = new JLabel(min + " (" + min + ")");
		GridBagConstraints c1 = new GridBagConstraints();
		c1.gridx = 0;
		c1.gridy = 0;
		this.add(lMin, c1);

		slider = new DoubleSlider(DoubleSlider.HORIZONTAL, min, max);
		GridBagConstraints c2 = new GridBagConstraints();
		c2.gridx = 1;
		c2.gridy = 0;
		c2.ipadx = 120;
		this.add(slider, c2);

		lMax = new JLabel(max + " (" + max + ")");
		GridBagConstraints c3 = new GridBagConstraints();
		c3.gridx = 2;
		c3.gridy = 0;
		this.add(lMax, c3);

	}

	public void updateLabels() {
		lMin.setText(min + " (" + (int) slider.getSelectedMinimum() + ")");
		lMax.setText(max + " (" + (int) slider.getSelectedMaximum() + ")");
	}

	public void updateMax(int val) {

		int selMax = (int) slider.getSelectedMaximum();
		int selMin = (int) slider.getSelectedMinimum();
		slider.setAbsoluteMaximum(val);
		if (val >= selMax) {
			slider.setSelectedMaximum(selMax);
		}
		slider.setSelectedMinimum(selMin);
		max = val;
		updateLabels();
	}

}
