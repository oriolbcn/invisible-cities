package edu.invisiblecities.charts;

import processing.core.PApplet;
import edu.invisiblecities.dashboard.Dashboard;
import edu.invisiblecities.data.Constants;
import edu.invisiblecities.data.Model;
import edu.invisiblecities.data.Route;
import edu.invisiblecities.data.Station;

public class LineCharts extends PApplet {

	// Constants
	final int textSize = 12;
	final int titleTextSize = 16;
	final int subtitleTextSize = 14;
	final int nSteps = 8;
	final int sep = 15;
	final int Xsep = 25;
	final int chartHeight = (nSteps + 1) * sep;
	final int chartWidth = (Constants.NUM_TIME_INTERVALS + 1) * Xsep;
	int rY1 = 209;
	int gY1 = 165;
	int bY1 = 0;
	int rY2 = 209;
	int gY2 = 70;
	int bY2 = 0;

	int chartsInRow = 3;
	int chartsInColumn = 4;
	int chartsSep = 100;

	Model mod;

	// Heatmaps
	DoubleAxisLineChartSet chartSet;

	public LineCharts() {
		mod = Dashboard.mod;
	}

	public void setup() {
		size(1900, 1000);

		DoubleAxisLineChart[] charts = new DoubleAxisLineChart[mod
				.getStations().size()];
		int index = 0;
		for (int ri = 0; ri < mod.getRoutes().size(); ri++) {
			Route r = mod.getRoutes().get(ri);
			for (int i = 0; i < r.stations.size(); i++) {
				Station st = r.stations.get(i);
				charts[index] = createChart(st.station_name.substring(1,
						st.station_name.length() - 1), st.route.hex_color,
						st.frequencies, st.delays);
				index++;
			}
		}

		DoubleAxisLineChart[] chartsAggregated = new DoubleAxisLineChart[8];
		int[] sumFreqs = new int[Constants.NUM_TIME_INTERVALS]; // aux var
		int[] sumDelays = new int[Constants.NUM_TIME_INTERVALS]; // aux var
		for (int i = 0; i < mod.getRoutes().size(); i++) {
			Route r = mod.getRoutes().get(i);
			for (int j = 0; j < sumFreqs.length; j++) {
				sumFreqs[j] = 0;
				sumDelays[j] = 0;
			}
			for (int j = 0; j < r.stations.size(); j++) {
				for (int k = 0; k < Constants.NUM_TIME_INTERVALS; k++) {
					sumFreqs[k] += r.stations.get(j).frequencies[k];
					sumDelays[k] += r.stations.get(j).delays[k];
				}
			}
			int[] valuesFreq = new int[Constants.NUM_TIME_INTERVALS];
			int[] valuesDelays = new int[Constants.NUM_TIME_INTERVALS];
			for (int j = 0; j < Constants.NUM_TIME_INTERVALS; j++) {
				valuesFreq[j] = sumFreqs[j] / r.stations.size();
				valuesDelays[j] = sumDelays[j] / r.stations.size();
			}
			chartsAggregated[i] = createChart(r.route_name, r.hex_color,
					valuesFreq, valuesDelays);
		}

		chartSet = new DoubleAxisLineChartSet(40, 80, charts, chartsAggregated,
				"Frequencies", "Delays (in seconds)");
	}

	public DoubleAxisLineChart createChart(String name, String color,
			int[] values1, int[] values2) {
		String labels[] = new String[Constants.NUM_TIME_INTERVALS];
		for (int i = 0; i < Constants.NUM_TIME_INTERVALS; i++) {
			String s;
			if (i >= 7) {
				if (i != 7) {
					s = String.valueOf(5 + i - 12);
				} else {
					s = "12";
				}
				s += "p";
			} else {
				s = String.valueOf(5 + i);
				s += "a";
			}
			labels[i] = s;
		}
		return new DoubleAxisLineChart(labels, values1, values2, name, color);
	}

	public void draw() {
		background(255);
		noStroke();
		chartSet.update();
		chartSet.display();
	}

	public void mousePressed() {
		chartSet.cbox.update();
	}

	class DoubleAxisLineChartSet {

		String var1;
		String var2;

		// GUI
		VScrollbar sbar;
		DoubleAxisLineChart[] charts;
		DoubleAxisLineChart[] chartsAggregated;
		CheckBox cbox;

		int iniX;
		int iniY;
		int rowNum;
		int totalHeight;
		int totalWidth;

		boolean aggregated;

		DoubleAxisLineChartSet(int iniX, int iniY,
				DoubleAxisLineChart[] charts,
				DoubleAxisLineChart[] chartsAggregated, String var1, String var2) {
			this.iniX = iniX;
			this.iniY = iniY;
			this.var1 = var1;
			this.var2 = var2;
			this.charts = charts;
			this.chartsAggregated = chartsAggregated;
			this.rowNum = 0;
			this.aggregated = false;
			this.totalHeight = chartHeight * chartsInColumn + chartsSep
					* (chartsInColumn - 1);
			this.totalWidth = chartWidth * chartsInRow + chartsSep
					* (chartsInRow - 1);

			sbar = new VScrollbar(iniX + totalWidth + chartsSep, iniY, 20,
					totalHeight, 3 * 5 + 1);
			cbox = new CheckBox("Routes", iniX + 40, iniY - 40, 20);
		}

		public void update() {
			aggregated = cbox.checked;
			if (!aggregated) {
				float sbarPos = sbar.getPos();
				int numSteps = ceil(charts.length / chartsInRow)
						- chartsInColumn + 1;
				rowNum = (int) (sbarPos / ((float) (totalHeight - 2) / (float) numSteps));
				sbar.update();
			} else {
				rowNum = 0;
			}
		}

		public void display() {

			DoubleAxisLineChart[] charts = aggregated ? this.chartsAggregated
					: this.charts;
			textSize(titleTextSize);
			float wTitle = textWidth(var1 + " vs. " + var2);
			textAlign(LEFT);
			fill(rY1, gY1, bY1);
			text(var1, iniX + totalWidth / 2 - wTitle / 2, iniY - 40);
			fill(0);
			text(" vs. ", iniX + totalWidth / 2 - wTitle / 2 + textWidth(var1),
					iniY - 40);
			fill(rY2, gY2, bY2);
			text(var2, iniX + totalWidth / 2 - wTitle / 2
					+ textWidth(var1 + " vs. "), iniY - 40);

			textSize(textSize);
			fill(0);

			for (int i = 0; i < chartsInColumn; i++) {
				for (int j = 0; j < chartsInRow; j++) {
					int index = (i + rowNum) * chartsInRow + j;
					if (index < charts.length) {
						charts[index].iniX = iniX + (chartWidth + chartsSep)
								* j;
						charts[index].iniY = iniY + (chartHeight + chartsSep)
								* i;
						charts[index].display();
					}
				}
			}
			if (!aggregated) {
				sbar.display();
			}
			cbox.display();
		}
	}

	// Double Axis Line Chart
	class DoubleAxisLineChart {

		String title;
		String color;

		// Data
		String[] Y1labels;
		String[] Y2labels;
		String[] Xlabels;
		int[] values1;
		int[] values2;

		// Size and position parameters
		int iniX;
		int iniY;
		int maxValue1;
		int maxValue2;
		int step1;
		int step2;

		DoubleAxisLineChart(String[] labels, int[] values1, int[] values2,
				String title, String color) {
			this.values1 = values1;
			this.values2 = values2;
			maxValue1 = getMaxValue(values1);
			maxValue2 = getMaxValue(values2);
			this.title = title;
			this.color = color;
			this.Xlabels = labels;
			int max = maxValue1 + (nSteps - maxValue1 % nSteps) + nSteps;
			step1 = max / nSteps;
			max = maxValue2 + (nSteps - maxValue2 % nSteps) + nSteps;
			step2 = max / nSteps;
			this.Y1labels = getYLabels(maxValue1, step1);
			this.Y2labels = getYLabels(maxValue2, step2);

		}

		public void update() {

		}

		public void display() {

			fill(unhex("FF" + color));
			noStroke();
			rect(iniX + chartWidth / 5, iniY - 30, 3 * chartWidth / 5, 40);
			textSize(subtitleTextSize);
			fill(255);
			textAlign(CENTER);
			text(title, iniX + chartWidth / 2, iniY);

			textSize(textSize);
			fill(0);

			// Y1 axis and line
			stroke(rY1, gY1, bY1);
			fill(rY1, gY1, bY1);
			line(iniX, iniY, iniX, iniY + chartHeight);
			for (int i = 0; i <= nSteps; i++) {
				textAlign(LEFT);
				float w = textWidth(Y1labels[i]);
				text(Y1labels[i], iniX - w - 5, iniY + sep * (i + 1));
			}
			int x = iniX;
			float y = iniY + chartHeight;
			for (int i = 0; i < Constants.NUM_TIME_INTERVALS; i++) {
				int x2 = iniX + Xsep * (i + 1);
				int mod = values1[i] % step1;
				float y2 = iniY
						+ chartHeight
						- ((values1[i] / step1) * sep + ((float) sep / (float) step1)
								* mod);
				if (i != 0)
					line(x, y, x2, y2);
				x = x2;
				y = y2;
			}

			// Y2 axis and line
			fill(rY2, gY2, bY2);
			stroke(rY2, gY2, bY2);
			line(iniX + chartWidth, iniY, iniX + chartWidth, iniY + chartHeight);
			for (int i = 0; i <= nSteps; i++) {
				textAlign(RIGHT);
				float w = textWidth(Y2labels[i]);
				text(Y2labels[i], iniX + chartWidth + w + 5, iniY + sep
						* (i + 1));
			}
			x = iniX;
			y = iniY + chartHeight;
			for (int i = 0; i < Constants.NUM_TIME_INTERVALS; i++) {
				int x2 = iniX + Xsep * (i + 1);
				int mod = values2[i] % step2;
				float y2 = iniY
						+ chartHeight
						- ((values2[i] / step2) * sep + ((float) sep / (float) step2)
								* mod);
				if (i != 0)
					line(x, y, x2, y2);
				x = x2;
				y = y2;
			}

			// X axis
			stroke(0);
			fill(0);
			line(iniX, iniY + chartHeight, iniX + chartWidth, iniY
					+ chartHeight);
			for (int i = 0; i < Constants.NUM_TIME_INTERVALS; i++) {
				textAlign(CENTER);
				text(Xlabels[i], iniX + Xsep * (i + 1), iniY + chartHeight + 15);
			}

		}

		public String[] getYLabels(int maxValue, int step) {
			String[] labels = new String[nSteps + 1];
			int max = maxValue + (nSteps - maxValue % nSteps) + nSteps;
			int val = max;
			for (int i = 0; i <= nSteps; i++) {
				labels[i] = String.valueOf(val);
				val -= step;
			}
			return labels;
		}

		public int getMaxValue(int[] values) {
			int max = 0;
			for (int i = 0; i < values.length; i++) {
				if (values[i] > max)
					max = values[i];
			}

			return max;
		}
	}

	// Vertical Scrollbar
	class VScrollbar {
		int swidth, sheight; // width and height of bar
		int xpos, ypos; // x and y position of bar
		float spos, newspos; // x position of slider
		int sposMin, sposMax; // max and min values of slider
		int loose; // how loose/heavy
		boolean over; // is the mouse over the slider?
		boolean locked;
		float ratio;

		VScrollbar(int xp, int yp, int sw, int sh, int l) {
			swidth = sw;
			sheight = sh;
			int heighttowidth = sh - sw;
			ratio = (float) sh / (float) heighttowidth;
			xpos = xp - swidth / 2;
			ypos = yp;
			spos = ypos;
			newspos = spos;
			sposMin = ypos;
			sposMax = ypos + sheight - swidth;
			loose = l;
		}

		void update() {
			if (over()) {
				over = true;
			} else {
				over = false;
			}
			if (mousePressed && over) {
				locked = true;
			}
			if (!mousePressed) {
				locked = false;
			}
			if (locked) {
				newspos = constrain(mouseY - swidth / 2, sposMin, sposMax);
			}
			if (abs(newspos - spos) > 1) {
				spos = spos + (newspos - spos) / loose;
			}
		}

		int constrain(int val, int minv, int maxv) {
			return min(max(val, minv), maxv);
		}

		boolean over() {
			if (mouseX > xpos && mouseX < xpos + swidth && mouseY > ypos
					&& mouseY < ypos + sheight) {
				return true;
			} else {
				return false;
			}
		}

		void display() {
			fill(127);
			rect(xpos, ypos, swidth, sheight);
			if (over || locked) {
				fill(0, 0, 127);
			} else {
				fill(0, 0, 0);
			}
			rect(xpos, spos, swidth, swidth);
		}

		float getPos() {
			// Convert spos to be values between
			// 0 and the total width of the scrollbar
			return (spos - ypos) * ratio;
		}
	}

	class CheckBox {

		int iniX, iniY;
		int s;
		String label;

		boolean checked = false;

		CheckBox(String l, int iniX, int iniY, int s) {
			this.iniX = iniX;
			this.iniY = iniY;
			this.s = s;
			label = l;
		}

		public void update() {
			if (over())
				checked = !checked;
		}

		boolean over() {
			if (mouseX > iniX && mouseX < iniX + s && mouseY > iniY
					&& mouseY < iniY + s) {
				return true;
			} else {
				return false;
			}
		}

		public void display() {

			fill(0);
			textAlign(LEFT);
			text(label, iniX - textWidth(label) - 5, iniY + s / 2);

			stroke(0);
			noFill();
			rect(iniX, iniY, s, s);

			if (checked) {
				line(iniX, iniY, iniX + s, iniY + s);
				line(iniX + s, iniY, iniX, iniY + s);
			}
			noStroke();
		}
	}
}
