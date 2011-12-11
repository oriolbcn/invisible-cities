package edu.invisiblecities.dashboard;

import javax.swing.JDialog;
import javax.swing.JFrame;

import edu.invisiblecities.charts.LineCharts;
import edu.invisiblecities.data.Model;

public class LineChartsDialog extends JDialog {

	public static Model mod;

	public LineChartsDialog(JFrame parent) {
		super(parent, "Details Charts", true);
		mod = Dashboard.mod;
		setSize(1900, 1000);
		createComponents();
	}

	public void createComponents() {
		LineCharts lineCharts = new LineCharts();
		this.getContentPane().add(lineCharts);
		lineCharts.init();
	}
}
