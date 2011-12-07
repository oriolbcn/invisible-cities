package edu.invisiblecities.charts;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import processing.core.PApplet;
import edu.invisiblecities.data.Constants;
import edu.invisiblecities.data.Model;
import edu.invisiblecities.data.Route;
import edu.invisiblecities.data.Station;

public class HeatMaps extends PApplet {

	private static final long serialVersionUID = 1L;

	// Constants
	final int rectHeight = 20;
	final int textSize = 12;
	final int titleTextSize = 16;
	final int legendRowHeight = 20;
	final int legendHeight = legendRowHeight * 8;
	final int legendWidth = 100;
	final int tooltipHeight = 15;

	int rectWidth; // 40
	int nRects; // 20
	int nRectsExpanded; // 45
	int chartHeight;
	int chartHeightExpanded;
	int chartWidth;

	int w;
	int h;
	Model mod;

	// Heatmaps
	Heatmap hm1, hm2;

	public HeatMaps(int rectWidth, int nRects, int nRectsExpanded, int width,
			int height) {
		this.rectWidth = rectWidth;
		this.nRects = nRects;
		this.nRectsExpanded = nRectsExpanded;

		h = height;
		w = width;

		chartHeight = rectHeight * nRects;
		chartHeightExpanded = rectHeight * nRectsExpanded;
		chartWidth = Constants.NUM_TIME_INTERVALS * rectWidth;
	}

	public void setup() {
		size(w, h);
		mod = new Model();

		String dir = Constants.dirProcessing;

		String lines[] = loadStrings(dir + "stations.txt");
		for (int i = 0; i < lines.length; i++) {
			String values[] = split(lines[i], ',');
			boolean contains = false;
			for (Station st : mod.getStations()) {
				if (st.station_name.equals(values[0]))
					contains = true;
			}
			if (!contains) {
				Station st = new Station(0, values[0], new Route("0", "name",
						values[1]));
				for (int j = 0; j < Constants.NUM_TIME_INTERVALS; j++) {
					st.frequencies[j] = Integer.parseInt(values[2 + j].trim());
				}
				for (int j = 0; j < Constants.NUM_TIME_INTERVALS; j++) {
					st.delays[j] = Integer.parseInt(values[2
							+ Constants.NUM_TIME_INTERVALS + j].trim());
				}
				mod.getStations().add(st);
			}
		}
		Collections.sort(mod.getStations(), new Comparator<Station>() {
			public int compare(Station o1, Station o2) {
				return o1.route.hex_color.compareTo(o2.route.hex_color);
			}
		});

		String routesLines[] = loadStrings(dir + "routes.txt");
		for (int i = 0; i < routesLines.length; i++) {
			String values[] = split(routesLines[i], ',');
			Route r = new Route("0", values[0], values[1]);
			for (int j = 0; j < Constants.NUM_TIME_INTERVALS; j++) {
				r.frequencies[j] = Integer.parseInt(values[2 + j].trim());
			}
			for (int j = 0; j < Constants.NUM_TIME_INTERVALS; j++) {
				r.delays[j] = Integer.parseInt(values[2
						+ Constants.NUM_TIME_INTERVALS + j].trim());
			}
			mod.getRoutes().add(r);
		}
		Collections.sort(mod.getRoutes(), new Comparator<Route>() {
			public int compare(Route o1, Route o2) {
				return o1.hex_color.compareTo(o2.hex_color);
			}
		});

		hm1 = new Heatmap(140, 80, createRows(true, false),
				createAggregatedRows(true, false), "Frequencies", 140, 80,
				"trips");

		hm2 = new Heatmap(140, 160 + chartHeight + 40, createRows(false, true),
				createAggregatedRows(false, true), "Delays (in seconds)", 140,
				80, "seconds");
	}

	public HeatMapRow[] createRows(boolean freq, boolean del) {

		HeatMapRow[] rows = new HeatMapRow[mod.getStations().size()];
		for (int i = 0; i < mod.getStations().size(); i++) {
			Station st = mod.getStations().get(i);
			HeatMapRow row = new HeatMapRow(st.station_name.substring(1,
					st.station_name.length() - 1),
					st.route.hex_color.substring(2));
			row.values = new int[Constants.NUM_TIME_INTERVALS];
			for (int j = 0; j < st.frequencies.length; j++) {
				int val;
				if (freq)
					val = st.frequencies[j];
				else if (del)
					val = st.delays[j];
				else
					val = 0;
				row.values[j] = val;
			}
			rows[i] = row;
		}

		return rows;

	}

	public HeatMapRow[] createAggregatedRows(boolean freq, boolean del) {

		HeatMapRow[] rows = new HeatMapRow[8];
		int[] sum = new int[Constants.NUM_TIME_INTERVALS]; // aux var
		int[] n = new int[Constants.NUM_TIME_INTERVALS]; // aux var

		Route r = mod.getRoutes().get(0);
		int index = 0;
		for (int i = 0; i < mod.getRoutes().size();) {
			for (int j = 0; j < sum.length; j++) {
				sum[j] = 0;
				n[j] = 0;
			}
			String rName = r.route_name;
			String rColor = r.hex_color;
			while (i < mod.getRoutes().size() && r.route_name.equals(rName)) {
				for (int j = 0; j < Constants.NUM_TIME_INTERVALS; j++) {
					int val;
					if (freq)
						val = r.frequencies[j];
					else if (del)
						val = r.delays[j];
					else
						val = 0;
					sum[j] += val;
					n[j]++;
				}
				i++;
				if (i < mod.getRoutes().size())
					r = mod.getRoutes().get(i);
			}
			HeatMapRow row = new HeatMapRow(rName, rColor.substring(2));
			row.values = new int[Constants.NUM_TIME_INTERVALS];
			for (int j = 0; j < sum.length; j++) {
				row.values[j] = sum[j] / n[j];
			}
			rows[index] = row;
			index++;
		}

		return rows;

	}

	public void draw() {
		background(255);
		noStroke();

		if (hm1.visible) {
			hm1.update();
			hm1.display();
		}
		if (hm2.visible) {
			hm2.update();
			hm2.display();
		}

	}

	public void mousePressed() {
		if (hm1.visible) {
			hm1.cbox.update();
			hm1.expandButton.update();
		}
		if (hm2.visible) {
			hm2.cbox.update();
			hm2.expandButton.update();
		}
	}

	// Heatmap
	class Heatmap {

		String title;
		String unit;

		// GUI
		VScrollbar sbar;
		VScrollbar sbarExpanded;
		CheckBox cbox;
		Button expandButton;

		// Data
		HeatMapRow[] rows;
		HeatMapRow[] rowsAggregated;
		Map<String, Integer> maxColor;
		Map<String, Integer> maxColorAggregated;

		// Size and position parameters
		int iniX;
		int iniXCollapsed;
		int iniXExpanded;
		int iniLegendX;
		int iniY;
		int iniYCollapsed;
		int iniYExpanded;
		int iniLegendY;
		int shownRects;
		int h;
		int maxValue;
		int maxValueAggregated;
		boolean visible;
		boolean expanded;
		int tooltipValue;

		// Rows to show
		int rowNum;
		boolean aggregated;

		Heatmap(int iniX, int iniY, HeatMapRow[] rows,
				HeatMapRow[] rowsAggregated, String title, int iniXExpanded,
				int iniYExpanded, String unit) {
			this.iniX = iniX;
			this.iniY = iniY;
			this.iniXExpanded = iniXExpanded;
			this.iniYExpanded = iniYExpanded;
			this.iniXCollapsed = iniX;
			this.iniYCollapsed = iniY;
			this.shownRects = nRects;
			this.h = chartHeight;
			this.rows = rows;
			this.rowsAggregated = rowsAggregated;
			maxValue = getMaxValue(rows);
			maxValueAggregated = getMaxValue(rowsAggregated);
			this.rowNum = 0;
			this.title = title;
			this.unit = unit;
			this.iniLegendX = iniX + chartWidth + 40;
			this.iniLegendY = iniY + chartHeight / 2 - legendHeight / 2;
			tooltipValue = -1;
			aggregated = true;
			visible = true;
			expanded = false;

			maxColor = new HashMap<String, Integer>();
			maxColorAggregated = new HashMap<String, Integer>();
			calcMaxColors(maxColor, rows);
			calcMaxColors(maxColorAggregated, rowsAggregated);

			sbar = new VScrollbar(iniX + Constants.NUM_TIME_INTERVALS
					* (rectWidth + 1), iniY, 20, chartHeight, 3 * 5 + 1);
			sbarExpanded = new VScrollbar(iniXExpanded
					+ Constants.NUM_TIME_INTERVALS * (rectWidth + 1),
					iniYExpanded, 20, chartHeightExpanded, 3 * 5 + 1);
			cbox = new CheckBox("Routes", iniX - 40, iniY - 40, 20);
			expandButton = new Button("Expand", "Collapse", iniX + chartWidth
					- 50, iniY - 60, 30);
		}

		public void update() {
			aggregated = cbox.checked;
			expanded = expandButton.checked;
			if (expanded) {
				iniX = iniXExpanded;
				iniY = iniYExpanded;
				shownRects = nRectsExpanded;
				h = chartHeightExpanded;
				iniLegendY = iniY + chartHeightExpanded / 2 - legendHeight / 2;
				cbox.iniX = iniXExpanded - 40;
				cbox.iniY = iniYExpanded - 40;
				expandButton.iniX = iniXExpanded + chartWidth - 50;
				expandButton.iniY = iniYExpanded - 60;
			} else {
				iniX = iniXCollapsed;
				iniY = iniYCollapsed;
				shownRects = nRects;
				h = chartHeight;
				iniLegendY = iniY + chartHeight / 2 - legendHeight / 2;
				cbox.iniX = iniX - 40;
				cbox.iniY = iniY - 40;
				expandButton.iniX = iniX + chartWidth - 50;
				expandButton.iniY = iniY - 60;
			}
			if (!aggregated) {
				if (expanded) {
					float sbarPos = sbarExpanded.getPos();
					int numSteps = rows.length - nRectsExpanded;
					rowNum = (int) (sbarPos / ((float) (chartHeightExpanded - 2) / (float) numSteps));
					sbarExpanded.update();
				} else {
					float sbarPos = sbar.getPos();
					int numSteps = rows.length - nRects;
					rowNum = (int) (sbarPos / ((float) (chartHeight - 2) / (float) numSteps));
					sbar.update();
				}
				updateTooltipValue();
			} else {
				rowNum = 0;
				updateTooltipValueAggregated();
			}
		}

		public void updateTooltipValue() {
			if (mouseX > iniX && mouseX < iniX + chartWidth && mouseY > iniY
					&& mouseY < iniY + h) {
				int x = (mouseX - iniX) / rectWidth;
				int y = (mouseY - iniY) / rectHeight;
				tooltipValue = rows[y + rowNum].values[x];
			} else {
				tooltipValue = -1;
			}
		}

		public void updateTooltipValueAggregated() {
			if (mouseX > iniX && mouseX < iniX + chartWidth && mouseY > iniY
					&& mouseY < iniY + rectHeight * 8) {
				int x = (mouseX - iniX) / rectWidth;
				int y = (mouseY - iniY) / rectHeight;
				tooltipValue = rowsAggregated[y].values[x];
			} else {
				tooltipValue = -1;
			}

		}

		public void display() {

			textSize(titleTextSize);
			float wTitle = textWidth(title);
			fill(0);
			text(title, iniX + chartWidth / 2 - wTitle / 2, iniY - 40);

			textSize(textSize);
			fill(0);
			// Write time labels
			for (int i = 0; i < Constants.NUM_TIME_INTERVALS; i += 2) {
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
				textAlign(LEFT);
				text(s, iniX + i * rectWidth, iniY - rectHeight, rectWidth * 2,
						rectHeight);
			}

			List<String> colors = new LinkedList<String>();
			int nRectsAux = aggregated ? 8 : shownRects;
			for (int i = 0; i < nRectsAux; i++) {
				HeatMapRow row;
				if (aggregated) {
					row = rowsAggregated[i + rowNum];
				} else {
					row = rows[i + rowNum];
				}

				// Write station name
				textAlign(LEFT);
				fill(0);
				float w = textWidth(row.label);
				text(row.label, iniX - w, iniY + rectHeight * (i + 1));

				colorMode(HSB, 360, 100, 100);
				// Write values
				for (int j = 0; j < row.values.length; j++) {
					if (!colors.contains(row.color)) {
						colors.add(row.color);
					}
					fill(getHue(row.color),
							100,
							((float) row.values[j] / (float) (aggregated ? maxValueAggregated
									: maxValue)) * 100);
					rect(iniX + j * rectWidth, iniY + rectHeight * i,
							rectWidth, rectHeight);
				}
				colorMode(RGB, 255, 255, 255);

			}

			// Legend
			stroke(0);
			noFill();
			rect(iniLegendX, iniLegendY, legendWidth, legendHeight);
			noStroke();

			int k = 0;
			for (String c : colors) {
				fill(0);
				text("0", iniLegendX + 5, iniLegendY + rectHeight * k + 15);
				for (int b = 0; b <= 50; b++) {
					colorMode(HSB, 360, 100, 100);
					stroke(getHue(c), 100, b * 2);
					line(iniLegendX + 15 + b, iniLegendY + rectHeight * k + 5,
							iniLegendX + 15 + b, iniLegendY + rectHeight * k
									+ 15);
					noStroke();
					colorMode(RGB, 255, 255, 255);
				}
				fill(0);
				int max = aggregated ? maxColorAggregated.get(c) : maxColor
						.get(c);
				text(max, iniLegendX + 70, iniLegendY + rectHeight * k + 15);
				k++;
			}

			if (!aggregated)
				if (expanded)
					sbarExpanded.display();
				else
					sbar.display();
			cbox.display();
			expandButton.display();

			// Tooltip
			if (tooltipValue != -1) {
				String t = tooltipValue + " " + unit;
				float w = textWidth(t);
				stroke(0);
				fill(200);
				rect(mouseX, mouseY + 20, w + 20, tooltipHeight);
				noStroke();
				fill(0);
				text(t, mouseX + 10, mouseY + 32);
			}

		}

		public void setVisibility(boolean someExpanded) {
			if ((someExpanded && expandButton.checked) || !someExpanded) {
				visible = true;
			} else {
				visible = false;
			}
		}

		public int getMaxValue(HeatMapRow[] rows) {
			int max = 0;
			for (int i = 0; i < rows.length; i++) {
				for (int j = 0; j < rows[i].values.length; j++) {
					if (rows[i].values[j] > max) {
						max = rows[i].values[j];
					}
				}
			}

			return max;
		}

		public void calcMaxColors(Map<String, Integer> maxColor,
				HeatMapRow[] rows) {
			for (int i = 0; i < rows.length;) {

				int max = 0;
				String color = rows[i].color;
				while (i < rows.length && rows[i].color.equals(color)) {
					for (int j = 0; j < rows[i].values.length; j++) {
						if (rows[i].values[j] > max) {
							max = rows[i].values[j];
						}
					}
					i++;
				}
				maxColor.put(color, max);
			}
		}

		public int getHue(String hex) {
			int rRGB = Integer.parseInt(hex.substring(0, 2), 16);
			int gRGB = Integer.parseInt(hex.substring(2, 4), 16);
			int bRGB = Integer.parseInt(hex.substring(4, 6), 16);

			float r = ((float) rRGB) / 255;
			float g = ((float) gRGB) / 255;
			float b = ((float) bRGB) / 255;
			float max = Math.max(r, Math.max(g, b));
			float min = Math.min(r, Math.min(g, b));
			float diff = max - min;

			float h;
			if (min == max) {
				h = 0;
			} else if (r == max) {
				if (g < b) {
					h = ((60 * ((g - b) / diff)) + 360) % 360;
				} else {
					h = (60 * ((g - b) / diff));
				}
			} else if (g == max) {
				h = (60 * ((b - r) / diff)) + 120;
			} else {
				h = (60 * ((r - g) / diff)) + 240;
			}

			return (int) h;
		}
	}

	class HeatMapRow {
		String label;
		String color;
		int[] values;

		public HeatMapRow(String l, String c) {
			label = l;
			color = c;
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

	class Button {
		int iniX, iniY;
		int h, w;
		String label1;
		String label2;

		boolean checked;

		Button(String l1, String l2, int iniX, int iniY, int h) {
			this.iniX = iniX;
			this.iniY = iniY;
			this.h = h;
			label1 = l1;
			label2 = l2;
			this.w = (int) max(textWidth(label1), textWidth(label2)) + 20;
			checked = false;
		}

		public void update() {
			if (over()) {
				checked = !checked;
				hm1.setVisibility(checked);
				hm2.setVisibility(checked);
			}
		}

		boolean over() {
			if (mouseX > iniX && mouseX < iniX + w && mouseY > iniY
					&& mouseY < iniY + h) {
				return true;
			} else {
				return false;
			}
		}

		public void display() {

			stroke(0);
			fill(190);
			rect(iniX, iniY, w, h);
			noStroke();

			String t = checked ? label2 : label1;
			fill(0);
			text(t, iniX + 10, iniY + 20);
		}
	}
}
