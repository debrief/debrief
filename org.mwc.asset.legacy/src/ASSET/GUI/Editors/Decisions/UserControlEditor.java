
package ASSET.GUI.Editors.Decisions;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ASSET.Models.Decision.UserControl;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

public class UserControlEditor extends MWC.GUI.Properties.Swing.SwingCustomEditor
		implements java.beans.PropertyChangeListener {

	// ////////////////////////////////////////////////////////////////////
	// GUI components
	// ////////////////////////////////////////////////////////////////////

	abstract private class DemandedSliders extends JPanel {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
		/**
		 * the demanded vlaue
		 */
		JSlider _demanded;
		/**
		 * the current value
		 */
		private JSlider _current;
		/**
		 * label for the demanded value
		 */
		private JLabel _demLabel;
		/**
		 * label for the current value
		 */
		private JLabel _curLabel;

		/**
		 * a formatter, to format the text label
		 */
		private final java.text.DecimalFormat _df = new java.text.DecimalFormat("000");

		// ///////////////////////////////////////////////
		// constructor for this item
		// ///////////////////////////////////////////////

		/**
		 * constructor for a pair of sliders
		 *
		 * @param min         the slider's max value
		 * @param max         the slider's min value
		 * @param current     the slider's current value
		 * @param orientation whether the slider is horiz or vertical
		 * @param title       the title for the value being edited
		 */
		public DemandedSliders(final int min, final int max, final int current, final int orientation,
				final String title) {
			buildGUI(orientation, title, min, max, current);
		}

		private void buildGUI(final int orientation, final String title, final int min, final int max,
				final int current) {
			this.setLayout(new BorderLayout(0, 1));
			this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

			final JPanel demPanel = new JPanel();
			demPanel.setLayout(new BorderLayout());
			_demLabel = new JLabel();
			_demanded = new JSlider(orientation);
			_demanded.setMinimum(min);
			_demanded.setMaximum(max);
			setDemanded(current);
			_demanded.setToolTipText("Demanded " + title);
			_demanded.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(final ChangeEvent e) {
					if (!_demanded.getValueIsAdjusting())
						demandedChanged(_demanded.getValue());
				}
			});

			demPanel.add("West", _demanded);
			demPanel.add("Center", _demLabel);

			_curLabel = new JLabel();
			_current = new JSlider(orientation);
			_current.setMinimum(min);
			_current.setMaximum(max);
			setCurrent(current);
			_current.setToolTipText("Current " + title);
			_current.setEnabled(false);

			final JPanel curPanel = new JPanel();
			curPanel.setLayout(new BorderLayout());
			curPanel.add("West", _current);
			curPanel.add("Center", _curLabel);

			this.add("North", demPanel);
			this.add("Center", new JLabel(title, SwingConstants.CENTER));
			this.add("South", curPanel);
		}

		void demandedChanged(final int val) {
			_demLabel.setText(_df.format(val));

			// first update the GUI
			_demanded.setValue(val);

			// now update the listener
			newDemanded(val);
		}

		abstract void newDemanded(int val);

		public void setCurrent(final int val) {
			_current.setValue(val);
			_curLabel.setText(_df.format(val));
		}

		public void setDemanded(final int val) {
			_demanded.setValue(val);
			_demLabel.setText(_df.format(val));
		}

	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static void main(final String[] args) {
		final UserControl control = new UserControl(270, new WorldSpeed(12, WorldSpeed.Kts),
				new WorldDistance(40, WorldDistance.METRES));
		final UserControlEditor uc = new UserControlEditor();
		uc.setValue(control);
		final JFrame jf = new JFrame("jere");
		jf.setSize(200, 430);
		jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		jf.getContentPane().setLayout(new BorderLayout());
		jf.getContentPane().add("Center", uc);
		jf.doLayout();
		jf.setVisible(true);

	}

	private DemandedSliders _course;
	private DemandedSliders _speed;

	// ////////////////////////////////////////////////////////////////////
	// drag and drop components
	// ////////////////////////////////////////////////////////////////////

	private DemandedSliders _depth;

	JCheckBox _isActive;

	ASSET.Models.Decision.UserControl _userControl;

	public UserControlEditor() {
	}

	/**
	 * prepare the form
	 */
	public void buildGUI() {
		this.setLayout(new BorderLayout());
		final JPanel hori = new JPanel();
		hori.setLayout(new GridLayout(0, 1));

		_course = new DemandedSliders(0, 360, (int) _userControl.getCourse(), SwingConstants.HORIZONTAL, "Course") {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			void newDemanded(final int val) {
				_userControl.setCourse(val);
			}
		};
		_speed = new DemandedSliders(0, 40, (int) _userControl.getSpeed().getValueIn(WorldSpeed.Kts),
				SwingConstants.HORIZONTAL, "Speed") {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			void newDemanded(final int val) {
				_userControl.setSpeed(new WorldSpeed(val, WorldSpeed.Kts));
			}
		};
		_depth = new DemandedSliders(0, 300, (int) _userControl.getDepth().getValueIn(WorldDistance.METRES),
				SwingConstants.HORIZONTAL, "Depth") {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			void newDemanded(final int val) {
				_userControl.setDepth(new WorldDistance(val, WorldDistance.METRES));
			}
		};

		hori.add(_course);
		hori.add(_speed);
		hori.add(_depth);

		this.add("Center", hori);

		_isActive = new JCheckBox();
		_isActive.setText("User in control");
		_isActive.setSelected(_userControl.isActive());
		_isActive.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent e) {
				_userControl.setActive(_isActive.isSelected());
			}
		});

		this.add("South", _isActive);

	}

	/**
	 * ok, the user control we watch has changed, update our current values
	 *
	 * @param pe the event triggering the update
	 */
	@Override
	public void propertyChange(final java.beans.PropertyChangeEvent pe) {
		final String type = pe.getPropertyName();
		if (type == ASSET.Models.Decision.UserControl.UPDATED) {
			_course.setCurrent((int) _userControl.getCourse());
			_speed.setCurrent((int) _userControl.getSpeed().getValueIn(WorldSpeed.Kts));
			_depth.setCurrent((int) _userControl.getDepth().getValueIn(WorldDistance.METRES));
		}
	}

	/**
	 * store the new object
	 *
	 * @param value
	 */
	@Override
	public void setObject(final Object value) {
		setValue(value);
	}

	// //////////////////////////////////////////////////
	// embedded class to provide current/demanded sliders
	// //////////////////////////////////////////////////

	/**
	 * store the new value
	 *
	 * @param value
	 */
	private void setValue(final Object value) {
		//
		if (value instanceof ASSET.Models.Decision.UserControl) {
			_userControl = (ASSET.Models.Decision.UserControl) value;

			_userControl.addListener(ASSET.Models.Decision.UserControl.UPDATED, this);

			buildGUI();
		}
	}

	/**
	 * yes, we do support a custom editor
	 *
	 * @return
	 */
	public boolean supportsCustomEditor() {
		return true;
	}

}