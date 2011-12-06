package edu.invisiblecities.dashboard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;

public class Dashboard extends JFrame {

	FilterPanel filterPanel;

	public Dashboard() {
		this.getContentPane().setLayout(new GridBagLayout());
		createComponents();
	}

	private void createComponents() {

		this.getContentPane().setLayout(new GridBagLayout());

		GridBagConstraints c1 = new GridBagConstraints();
		c1.gridx = 0;
		c1.gridy = 0;
		filterPanel = new FilterPanel();
		this.getContentPane().add(filterPanel, c1);
	}
}
