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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.invisiblecities.data.Model;
import edu.invisiblecities.data.Route;
import edu.invisiblecities.data.Station;

public class FilterPanel extends JPanel implements ChangeListener,
		ActionListener {

	SliderWithLabel freqsSlider;
	SliderWithLabel delaysSlider;
	SliderWithLabel ridershipSlider;
	JCheckBox routesCBs[];
	JComboBox dayCombo;

	Model mod;

	List<FilterListener> listeners;

	int maxFreq, maxDelay, maxRidership;

	public FilterPanel() {
		this.setLayout(new GridBagLayout());
		loadModel();
		routesCBs = new JCheckBox[8];
		listeners = new LinkedList<FilterListener>();
		createComponents();
		setBorder(BorderFactory.createTitledBorder("Filters:"));
	}

	public void loadModel() {
		mod = new Model();
		mod.loadTextRoutes();
		mod.loadTextStations();

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

	public void createComponents() {

		// routes
		for (int i = 0; i < 8; i++) {
			Route r = mod.getRoutes().get(i);
			JCheckBox ch = new JCheckBox(r.route_name, true);
			Color color = getColor(r.hex_color);
			ch.setForeground(color);
			ch.addChangeListener(this);
			GridBagConstraints c = createConstraints(
					GridBagConstraints.HORIZONTAL, 0, i + 1, -1, -1);
			c.insets = new Insets(0, 0, 0, 30);
			this.add(ch, c);
			routesCBs[i] = ch;
		}

		// TODO: Add labels with current value!
		// frequencies
		JLabel label1 = new JLabel("Frequency");
		this.add(label1,
				createConstraints(GridBagConstraints.HORIZONTAL, 1, 0, -1, -1));

		freqsSlider = new SliderWithLabel(0, maxFreq);
		GridBagConstraints c = createConstraints(GridBagConstraints.HORIZONTAL,
				1, 1, -1, -1);
		c.insets = new Insets(0, 0, 20, 0);
		this.add(freqsSlider, c);
		// delays
		JLabel label2 = new JLabel("Delay");
		this.add(label2,
				createConstraints(GridBagConstraints.HORIZONTAL, 1, 2, -1, -1));

		delaysSlider = new SliderWithLabel(0, maxDelay);
		GridBagConstraints c2 = createConstraints(
				GridBagConstraints.HORIZONTAL, 1, 3, -1, -1);
		c2.insets = new Insets(0, 0, 20, 0);
		this.add(delaysSlider, c2);
		// ridership ?
		JLabel label3 = new JLabel("Ridership");
		this.add(label3,
				createConstraints(GridBagConstraints.HORIZONTAL, 1, 4, -1, -1));

		ridershipSlider = new SliderWithLabel(ICities.minRiderhsip
				* ICities.mult, ICities.maxRidership * ICities.mult);
		this.add(ridershipSlider,
				createConstraints(GridBagConstraints.HORIZONTAL, 1, 5, -1, -1));

		// day
		dayCombo = new JComboBox(ICities.days);
		dayCombo.setSelectedIndex(2);
		dayCombo.addActionListener(this);
		GridBagConstraints c3 = createConstraints(
				GridBagConstraints.HORIZONTAL, 2, 0, -1, -1);
		c3.insets = new Insets(0, 20, 0, 0);
		c3.gridheight = 5;
		this.add(dayCombo, c3);
		// play and stop
		// lines or dots
	}

	public int getMaxFrequency() {
		return maxFreq;
	}

	public int getMinFrequency() {
		return 0;
	}

	public int getMaxDelay() {
		return maxDelay;
	}

	public int getMinDelay() {
		return 0;
	}

	public int getMaxRidership() {
		return ICities.maxRidership * ICities.mult;
	}

	public int getMinRidership() {
		return ICities.minRiderhsip * ICities.mult;
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
		notifyListeners();
	}
}
