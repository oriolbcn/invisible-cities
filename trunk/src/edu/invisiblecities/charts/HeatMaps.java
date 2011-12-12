package edu.invisiblecities.charts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import processing.core.PApplet;
import edu.invisiblecities.dashboard.Dashboard;
import edu.invisiblecities.dashboard.FilterListener;
import edu.invisiblecities.dashboard.ICities;
import edu.invisiblecities.dashboard.SelectionListener;
import edu.invisiblecities.data.Constants;
import edu.invisiblecities.data.Model;
import edu.invisiblecities.data.Route;
import edu.invisiblecities.data.Station;

public class HeatMaps extends PApplet implements FilterListener,
		SelectionListener {

	private static final long serialVersionUID = 1L;

	// Constants
	final int rectHeight = 20;
	final int textSize = 12;
	final int titleTextSize = 16;
	final int legendRowHeight = 20;
	final int legendHeight = legendRowHeight * 8;
	final int legendWidth = 100;
	final int tooltipHeight = 15;
	final int timerStart = 5 * 3600 / ICities.Interval;

	int rectWidth; // 40
	int nRects; // 20
	int nRectsExpanded; // 45
	int chartHeight;
	int chartHeightExpanded;
	int chartWidth;
	boolean timeLine;

	int w;
	int h;
	Model mod;

	// Heatmaps
	Heatmap hm1, hm2;

	// Filters
	boolean[] selectedRoutes;
	String day;

	// Selection
	String selectedStation;

	public HeatMaps() {

		this.rectWidth = 40;
		this.nRects = 20;
		this.nRectsExpanded = 45;
		this.timeLine = true;

		h = 1000;
		w = 1100;

		chartHeight = rectHeight * nRects;
		chartHeightExpanded = rectHeight * nRectsExpanded;
		chartWidth = Constants.NUM_TIME_INTERVALS * rectWidth;

		mod = Dashboard.mod;

	}

	public void stationSelectionChanged(int stationId, String stationName) {
		hm1.selectStation(stationName);
		hm2.selectStation(stationName);
	}

	public void routeSelectionChanged(String routeId, String routeName) {
		// Do nothing...
	}

	public HeatMaps(int rectWidth, int nRects, int nRectsExpanded, int width,
			int height, boolean timeLine) {
		this.rectWidth = rectWidth;
		this.nRects = nRects;
		this.nRectsExpanded = nRectsExpanded;
		this.timeLine = timeLine;

		h = height;
		w = width;

		chartHeight = rectHeight * nRects;
		chartHeightExpanded = rectHeight * nRectsExpanded;
		chartWidth = Constants.NUM_TIME_INTERVALS * rectWidth;
		selectedRoutes = new boolean[8];
		for (int i = 0; i < selectedRoutes.length; i++) {
			selectedRoutes[i] = true;
		}

		mod = Dashboard.mod;
		day = mod.day;
	}

	public void setup() {
		size(700, 600);
		frameRate(ICities.frameRate);

		hm1 = new Heatmap(140, 80, createRows(true, false),
				createAggregatedRows(true, false), "Frequencies", 140, 80,
				"trips");

		hm2 = new Heatmap(140, 160 + chartHeight, createRows(false, true),
				createAggregatedRows(false, true), "Delays (in seconds)", 140,
				80, "seconds");

		Dashboard.registerAsFilterListener(this);
		Dashboard.registerAsSelectionListener(this);
	}

	public void filterChanged() {
		String d = Dashboard.getDay();
		if (!d.equals(day)) {
			day = d;
			hm1.rows = createRows(true, false);
			hm1.rowsAggregated = createAggregatedRows(true, false);
			hm1.modelUpdated();

			hm2.rows = createRows(false, true);
			hm2.rowsAggregated = createAggregatedRows(false, true);
			hm2.modelUpdated();
		} else {
			selectedRoutes = Dashboard.getSelectedRoutes();
			int minFreq = Dashboard.getMinFrequency();
			int maxFreq = Dashboard.getMaxFrequency();
			int minDelay = Dashboard.getMinDelay();
			int maxDelay = Dashboard.getMaxDelay();
			hm1.updateVisibleRows(minFreq, maxFreq);
			hm2.updateVisibleRows(minDelay, maxDelay);
		}
	}

	public HeatMapRow[] createRows(boolean freq, boolean del) {

		HeatMapRow[] rows = new HeatMapRow[mod.getStations().size()];
		int index = 0;
		for (int ri = 0; ri < mod.getRoutes().size(); ri++) {
			Route r = mod.getRoutes().get(ri);
			println(r.route_id);
			for (int i = 0; i < r.stations.size(); i++) {
				Station st = r.stations.get(i);
				HeatMapRow row = new HeatMapRow(st.station_name.substring(1,
						st.station_name.length() - 1), st.route.hex_color, ri);
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
				rows[index] = row;
				index++;
			}
		}
		return rows;

	}

	public HeatMapRow[] createAggregatedRows(boolean freq, boolean del) {

		HeatMapRow[] rows = new HeatMapRow[mod.getRoutes().size()];
		int[] sum = new int[Constants.NUM_TIME_INTERVALS];

		for (int i = 0; i < mod.getRoutes().size(); i++) {
			Route r = mod.getRoutes().get(i);
			for (int j = 0; j < sum.length; j++) {
				sum[j] = 0;
			}
			for (int j = 0; j < r.stations.size(); j++) {
				for (int k = 0; k < Constants.NUM_TIME_INTERVALS; k++) {
					int val;
					if (freq)
						val = r.stations.get(j).frequencies[k];
					else if (del)
						val = r.stations.get(j).delays[k];
					else
						val = 0;
					sum[k] += val;
				}
			}
			HeatMapRow row = new HeatMapRow(r.route_name, r.hex_color, i);
			row.values = new int[Constants.NUM_TIME_INTERVALS];
			for (int j = 0; j < sum.length; j++) {
				row.values[j] = sum[j] / r.stations.size();
			}
			rows[i] = row;
		}

		return rows;

	}

	public void draw() {
		background(255);
		noStroke();

		String ap = "am";
		int hourinterval = 3600 / ICities.Interval;
		int hour = ICities.timer / hourinterval;
		int mins = (ICities.timer - hour * hourinterval)
				/ (60 / ICities.Interval);
		if (hour == 0)
			hour = 12;
		else if (hour >= 12) {
			ap = "pm";
			if (hour > 12)
				hour -= 12;
		}
		String shour = hour < 10 ? "0" + hour : hour + "";
		String smins = mins < 10 ? "0" + mins : mins + "";
		textSize(16);
		text("Time: " + shour + ":" + smins + ap, 20, 20);
		if (hm1.visible) {
			hm1.update();
			hm1.display();
		}
		if (hm2.visible) {
			hm2.update();
			hm2.display();
		}

		if (ICities.IsPlaying) {
			++ICities.timer;
			if (ICities.timer == ICities.TotalTimeStamps) {
				ICities.timer = 0;
				ICities.IsPlaying = false;
			}
		}

	}

	public void mousePressed() {
		if (hm1.visible) {
			hm1.cbox.update();
			hm1.expandButton.update();
			stationSelected(hm1.getSelection());

		}
		if (hm2.visible) {
			hm2.cbox.update();
			hm2.expandButton.update();
			stationSelected(hm2.getSelection());
		}
	}

	public void stationSelected(String sel) {
		if (sel != null) {
			if (!sel.equals(selectedStation)) {
				selectedStation = sel;
				Dashboard.noitifyStationSelection(-1, sel);
			}
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
		HeatMapRow[] visibleRows;
		HeatMapRow[] visibleRowsAggregated;
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
		int currMax;
		int currMin;

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
			currMax = maxValue;
			currMin = 0;
			this.rowNum = 0;
			this.title = title;
			this.unit = unit;
			this.iniLegendX = iniX + chartWidth + 40;
			this.iniLegendY = iniY + chartHeight / 2 - legendHeight / 2;
			tooltipValue = -1;
			aggregated = true;
			visible = true;
			expanded = false;
			this.visibleRows = rows;
			this.visibleRowsAggregated = rowsAggregated;
			selectedStation = null;

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
				if (visibleRows.length > shownRects) {
					if (expanded) {
						float sbarPos = sbarExpanded.getPos();
						int numSteps = visibleRows.length - nRectsExpanded;
						rowNum = (int) (sbarPos / ((float) (chartHeightExpanded - 2) / (float) numSteps));
						sbarExpanded.update();
					} else {
						float sbarPos = sbar.getPos();
						int numSteps = visibleRows.length - nRects;
						rowNum = (int) (sbarPos / ((float) (chartHeight - 2) / (float) numSteps));
						sbar.update();
					}
				} else {
					rowNum = 0;
				}
				updateTooltipValue();
			} else {
				rowNum = 0;
				updateTooltipValueAggregated();
			}
		}

		public void updateTooltipValue() {
			int currH = visibleRows.length < shownRects ? rectHeight
					* visibleRows.length : h;
			if (mouseX > iniX && mouseX < iniX + chartWidth && mouseY > iniY
					&& mouseY < iniY + currH) {
				int x = (mouseX - iniX) / rectWidth;
				int y = (mouseY - iniY) / rectHeight;
				tooltipValue = visibleRows[y + rowNum].values[x];
			} else {
				tooltipValue = -1;
			}
		}

		public void updateTooltipValueAggregated() {
			if (mouseX > iniX
					&& mouseX < iniX + chartWidth
					&& mouseY > iniY
					&& mouseY < iniY + rectHeight
							* visibleRowsAggregated.length) {
				int x = (mouseX - iniX) / rectWidth;
				int y = (mouseY - iniY) / rectHeight;
				tooltipValue = visibleRowsAggregated[y].values[x];
			} else {
				tooltipValue = -1;
			}

		}

		public String getSelection() {
			if (aggregated) {
				return null;
			} else {
				int currH = visibleRows.length < shownRects ? rectHeight
						* visibleRows.length : h;
				if (mouseX > iniX && mouseX < iniX + chartWidth
						&& mouseY > iniY && mouseY < iniY + currH) {
					int y = (mouseY - iniY) / rectHeight;
					return visibleRows[y + rowNum].label;
				} else {
					return null;
				}
			}
		}

		public void updateVisibleRows(int minVal, int maxVal) {
			ArrayList<HeatMapRow> ro = new ArrayList<HeatMapRow>();
			ArrayList<HeatMapRow> roAggr = new ArrayList<HeatMapRow>();
			for (int i = 0; i < rows.length; i++) {
				HeatMapRow r = rows[i];
				boolean v = selectedRoutes[r.r_index];
				if (v) {
					v = false;
					for (int j = 0; j < r.values.length; j++) {
						if (r.values[j] >= minVal && r.values[j] <= maxVal) {
							v = true;
							break;
						}
					}
				}
				r.visible = v;
				if (r.visible)
					ro.add(r);
			}
			for (int i = 0; i < rowsAggregated.length; i++) {
				HeatMapRow r = rowsAggregated[i];
				boolean v = selectedRoutes[r.r_index];
				if (v) {
					v = false;
					for (int j = 0; j < r.values.length; j++) {
						if (r.values[j] >= minVal && r.values[j] <= maxVal) {
							v = true;
							break;
						}
					}
				}
				r.visible = v;
				if (r.visible)
					roAggr.add(r);
			}

			visibleRows = new HeatMapRow[ro.size()];
			visibleRowsAggregated = new HeatMapRow[roAggr.size()];
			ro.toArray(visibleRows);
			roAggr.toArray(visibleRowsAggregated);

			calcMaxColors(maxColor, visibleRows);
			calcMaxColors(maxColorAggregated, visibleRowsAggregated);

			currMax = maxVal;
			currMin = minVal;
		}

		public void selectStation(String stationName) {
			selectedStation = stationName;
			if (!aggregated && visibleRows.length > shownRects) {
				if (expanded) {
					int rn = getRowNum(stationName);
					if (rn != -1) {
						int numSteps = visibleRows.length - nRectsExpanded;
						rowNum = rn;
						if (rowNum > numSteps)
							rowNum = numSteps;
						int sbarPos = (int) (((float) (float) (chartHeightExpanded - 2) / (float) numSteps) * rowNum);
						sbarExpanded.move(sbarPos);
					}
				} else {
					int rn = getRowNum(stationName);
					if (rn != -1) {
						int numSteps = visibleRows.length - nRects;
						rowNum = rn;
						if (rowNum > numSteps)
							rowNum = numSteps;
						int sbarPos = (int) ((float) ((float) (chartHeight - 2) / (float) numSteps) * rowNum);
						sbar.move(sbarPos);
					}
				}
			}
		}

		public int getRowNum(String station_name) {
			for (int i = 0; i < visibleRows.length; i++) {
				if (visibleRows[i].label.equals(station_name)) {
					return i;
				}
			}
			return -1;
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
			for (int i = 0; i < nRectsAux
					&& (i + rowNum) < (aggregated ? visibleRowsAggregated.length
							: visibleRows.length);) {
				HeatMapRow row;
				if (aggregated) {
					row = visibleRowsAggregated[i + rowNum];
				} else {
					row = visibleRows[i + rowNum];
				}

				// Write station/route name
				float w = textWidth(row.label);

				if (row.label.equals(selectedStation)) {
					fill(127, 50);
					rect(iniX - w - 5, iniY + rectHeight * i + 6, w + 5,
							rectHeight);
				}
				textAlign(LEFT);
				fill(0);

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
				i++;

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

			if (!aggregated && visibleRows.length > shownRects)
				if (expanded)
					sbarExpanded.display();
				else
					sbar.display();
			cbox.display();
			expandButton.display();

			// TimeLine
			if (timeLine && ICities.timer > timerStart) {
				stroke(255);
				strokeWeight(4);
				float x = (ICities.timer - timerStart) * chartWidth
						/ (ICities.TotalTimeStamps * 19 / 24) + iniX;
				line(x, iniY, x, iniY
						+ (expanded ? chartHeightExpanded : chartHeight));
				strokeWeight(1);
				noStroke();
			}

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

		public void modelUpdated() {
			updateVisibleRows(currMin, currMax);
			maxValue = getMaxValue(rows);
			maxValueAggregated = getMaxValue(rowsAggregated);
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
		int r_index;
		boolean visible;

		public HeatMapRow(String l, String c, int ri) {
			label = l;
			color = c;
			r_index = ri;
			visible = true;
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

		public void move(int pos) {
			spos = pos;
			spos = ((float) pos + ypos * ratio) / ratio;
			newspos = spos;
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
