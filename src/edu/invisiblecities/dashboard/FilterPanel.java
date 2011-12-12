package edu.invisiblecities.dashboard;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.invisiblecities.data.Model;
import edu.invisiblecities.data.Route;
import edu.invisiblecities.data.Station;

public class FilterPanel extends JPanel implements ChangeListener,
		ActionListener, DoubleSliderAdjustmentListener {

	SliderWithLabel freqsSlider;
	SliderWithLabel delaysSlider;
	SliderWithLabel ridershipSlider;
	JCheckBox routesCBs[];
	JComboBox dayCombo;

	JRadioButton dotsRadio;
	JRadioButton linesRadio;

	Model mod;

	List<FilterListener> listeners;

	public int maxFreq, maxDelay, maxRidership;

	public FilterPanel() {
		this.setLayout(new GridBagLayout());
		loadModel();
		routesCBs = new JCheckBox[8];
		listeners = new LinkedList<FilterListener>();
		createComponents();
		setBorder(BorderFactory.createTitledBorder("Filters:"));
	}

	public void loadModel() {
		mod = Dashboard.mod;

		maxFreq = 0;
		for (Station st : mod.getStations()) {
			for (int i = 0; i < st.frequencies.length; i++) {
				if (st.frequencies[i] > maxFreq)
					maxFreq = st.frequencies[i];
			}
		}

		maxDelay = 0;
		for (Station st : mod.getStations()) {
			for (int i = 0; i < st.delays.length; i++) {
				if (st.delays[i] > maxDelay)
					maxDelay = st.delays[i];
			}
		}

		maxRidership = ICities.maxRidership * ICities.mult;
	}

	public void reloadModel() {
		mod.reloadText();

		maxFreq = 0;
		for (Station st : mod.getStations()) {
			for (int i = 0; i < st.frequencies.length; i++) {
				if (st.frequencies[i] > maxFreq)
					maxFreq = st.frequencies[i];
			}
		}

		maxDelay = 0;
		for (Station st : mod.getStations()) {
			for (int i = 0; i < st.delays.length; i++) {
				if (st.delays[i] > maxDelay)
					maxDelay = st.delays[i];
			}
		}

		freqsSlider.updateMax(maxFreq);
		delaysSlider.updateMax(maxDelay);
	}

	public void createComponents() {

		Color bg = new Color(255, 255, 255);

		// routes
		int numOfRoutes = mod.getRoutes().size();
		for (int i = 0; i < numOfRoutes; i++) {
			Route r = mod.getRoutes().get(i);
			JCheckBox ch = new JCheckBox(r.route_name, true);
			Color color = getColor(r.hex_color);
			ch.setForeground(color);
			ch.addChangeListener(this);
			GridBagConstraints c = createConstraints(
					GridBagConstraints.HORIZONTAL, i / 4, i % 4, -1, -1);
			c.insets = new Insets(0, 0, 0, 20);
			ch.setBackground(bg);
			this.add(ch, c);
			routesCBs[i] = ch;
		}

		// frequencies
		JLabel label1 = new JLabel("Frequency");
		label1.setBackground(bg);
		this.add(label1,
				createConstraints(GridBagConstraints.HORIZONTAL, 2, 0, -1, -1));

		freqsSlider = new SliderWithLabel(0, maxFreq);
		GridBagConstraints c = createConstraints(GridBagConstraints.HORIZONTAL,
				2, 1, -1, -1);
		c.insets = new Insets(0, 0, 0, 0);
		freqsSlider.setBackground(bg);
		this.add(freqsSlider, c);
		// delays
		JLabel label2 = new JLabel("Delay");
		label2.setBackground(bg);
		this.add(label2,
				createConstraints(GridBagConstraints.HORIZONTAL, 2, 2, -1, -1));
		freqsSlider.slider.addAdjustmentListener(this);

		delaysSlider = new SliderWithLabel(0, maxDelay);
		GridBagConstraints c2 = createConstraints(
				GridBagConstraints.HORIZONTAL, 2, 3, -1, -1);
		c2.insets = new Insets(0, 0, 0, 30);
		delaysSlider.setBackground(bg);
		this.add(delaysSlider, c2);
		// ridership ?
		JLabel label3 = new JLabel("Ridership");
		label3.setBackground(bg);
		this.add(label3,
				createConstraints(GridBagConstraints.HORIZONTAL, 3, 0, -1, -1));
		delaysSlider.slider.addAdjustmentListener(this);

		ridershipSlider = new SliderWithLabel(ICities.minRiderhsip
				* ICities.mult, ICities.maxRidership * ICities.mult);
		GridBagConstraints c8 = createConstraints(
				GridBagConstraints.HORIZONTAL, 3, 1, -1, -1);
		c8.insets = new Insets(0, 0, 0, 30);
		ridershipSlider.setBackground(bg);
		this.add(ridershipSlider, c8);
		ridershipSlider.slider.addAdjustmentListener(this);

		// day
		JLabel label5 = new JLabel("Day:");
		label5.setBackground(bg);
		this.add(label5,
				createConstraints(GridBagConstraints.HORIZONTAL, 4, 0, -1, -1));
		dayCombo = new JComboBox(ICities.days);
		dayCombo.setSelectedIndex(0);
		dayCombo.addActionListener(this);
		GridBagConstraints c3 = createConstraints(
				GridBagConstraints.HORIZONTAL, 4, 1, -1, -1);
		c3.insets = new Insets(0, 20, 0, 0);
		this.add(dayCombo, c3);

		JLabel label4 = new JLabel("Show frequency as:");
		GridBagConstraints c5 = createConstraints(
				GridBagConstraints.HORIZONTAL, 4, 2, -1, -1);
		label4.setBackground(bg);
		this.add(label4, c5);

		dotsRadio = new JRadioButton("dots");
		dotsRadio.setSelected(true);
		dotsRadio.addActionListener(this);
		GridBagConstraints c6 = createConstraints(
				GridBagConstraints.HORIZONTAL, 4, 3, -1, -1);
		dotsRadio.setBackground(bg);
		this.add(dotsRadio, c6);

		linesRadio = new JRadioButton("lines");
		linesRadio.setSelected(false);
		linesRadio.addActionListener(this);
		GridBagConstraints c7 = createConstraints(
				GridBagConstraints.HORIZONTAL, 4, 4, -1, -1);
		linesRadio.setBackground(bg);
		this.add(linesRadio, c7);

		ButtonGroup g = new ButtonGroup();
		g.add(dotsRadio);
		g.add(linesRadio);

	}

	public int getMaxFrequency() {
		return (int) freqsSlider.slider.getSelectedMaximum();
	}

	public int getMinFrequency() {
		return (int) freqsSlider.slider.getSelectedMinimum();
	}

	public int getMaxDelay() {
		return (int) delaysSlider.slider.getSelectedMaximum();
	}

	public int getMinDelay() {
		return (int) delaysSlider.slider.getSelectedMinimum();
	}

	public int getMaxRidership() {
		return (int) ridershipSlider.slider.getSelectedMaximum();
	}

	public int getMinRidership() {
		return (int) ridershipSlider.slider.getSelectedMinimum();
	}

	public String getDay() {
		return (String) dayCombo.getSelectedItem();
	}

	public boolean dotsSelected() {
		return dotsRadio.isSelected();
	}

	public boolean linesSelected() {
		return linesRadio.isSelected();
	}

	public boolean[] getSelectedRoutes() {

		boolean[] res = new boolean[8];

		for (int i = 0; i < 8; i++) {
			res[i] = routesCBs[i].isSelected();
		}

		return res;
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

	public Color getColor(String hex) {
		return new Color(Integer.parseInt(hex.substring(0, 2), 16),
				Integer.parseInt(hex.substring(2, 4), 16), Integer.parseInt(
						hex.substring(4, 6), 16));
	}

	public void register(FilterListener fl) {
		listeners.add(fl);
	}

	public void notifyListeners() {
		for (FilterListener fl : listeners) {
			fl.filterChanged();
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		notifyListeners();
	}

	public void actionPerformed(ActionEvent e) {
		String d = (String) dayCombo.getSelectedItem();
		boolean d_changed = false;
		boolean was_playing = false;
		if (!d.equals(mod.day)) {
			mod.day = d;
			reloadModel();
			d_changed = true;
			was_playing = ICities.IsPlaying;
			if (was_playing)
				ICities.IsPlaying = false;
			ICities.timer = 0;
		}
		notifyListeners();
		if (d_changed && was_playing) {
			ICities.IsPlaying = true;
		}
	}

	public void adjustmentValueChanged(DoubleSlider ds) {
		notifyListeners();
	}

	public void updateLabels() {
		freqsSlider.updateLabels();
		delaysSlider.updateLabels();
		ridershipSlider.updateLabels();
	}
}
