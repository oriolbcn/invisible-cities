package edu.invisiblecities.dashboard;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;

public class HeatMapsFrame extends JFrame {

	public HeatMapsFrame() {
		super("Swing Slider Example");
		setLayout(new BorderLayout());
		// EmbeddedWithSlider embed = new EmbeddedWithSlider(); // Applet
		// add(embed, BorderLayout.CENTER);
		// embed.init();
		Box box = Box.createHorizontalBox();
		JLabel label = new JLabel("Hue:");
		box.add(label);
		JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 360, 0);
		// slider.addChangeListener(embed);
		box.add(slider);
		box.setBorder(new EmptyBorder(5, 10, 5, 10));
		add(box, BorderLayout.SOUTH);
		pack();
		setVisible(true);
		setLocation(100, 100);
	}
}