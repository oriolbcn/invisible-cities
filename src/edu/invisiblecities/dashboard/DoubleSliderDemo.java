package edu.invisiblecities.dashboard;

import java.awt.Color;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;

public class DoubleSliderDemo extends JFrame implements
		DoubleSliderAdjustmentListener {
	public void adjustmentValueChanged(DoubleSlider ds) {
		System.out.println("Value changed: " + ds.getSelectedMinimum() + ","
				+ ds.getSelectedMaximum());
	}

	DoubleSliderDemo() {
		DoubleSlider ds = new DoubleSlider();
		Box p = new Box(BoxLayout.X_AXIS);

		p.add(ds);

		ds.setHilitedMinimum(20.0);
		ds.setHilitedMaximum(30.0);
		ds.setHiliteVisible(true);

		ds.setTrackColor(new Color(0.3F, 0.7F, 0.7F));

		ds.addAdjustmentListener(this);

		getContentPane().add(p);

		pack();
		setVisible(true);
	}

	public static void main(String[] args) {
		new DoubleSliderDemo();
	}

}
