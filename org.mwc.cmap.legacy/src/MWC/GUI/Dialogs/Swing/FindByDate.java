/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package MWC.GUI.Dialogs.Swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import MWC.Utilities.TextFormatting.GMTDateFormat;

/**
 * Implements a user interface and generates FindFilter for selecting files by
 * date.
 */
class FindByDate extends JPanel implements FindFilterFactory {
	/**
	 * Filter object for selecting files by the date range specified by the UI.
	 */
	class DateFilter implements FindFilter {
		protected long startTime = -1;
		protected long endTime = -1;

		DateFilter(final long from, final long to) {
			startTime = from;
			endTime = to;
		}

		@Override
		public boolean accept(final File f, final FindProgressCallback callback) {
			if (f == null)
				return false;

			final long t = f.lastModified();

			if (startTime >= 0) {
				if (t < startTime)
					return false;
			}
			if (endTime >= 0) {
				if (t > endTime)
					return false;
			}

			return true;
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public static String THE_BIG_BANG = "The Big Bang";
	public static String THE_BIG_CRUNCH = "The Big Crunch";
	public static String YESTERDAY = "Yesterday";
	public static String TODAY = "Today";

	public static String NOW = "Now";
	public static String MODIFIED_LABEL = "Modified";
	public static String FORMAT_LABEL = "mm/dd/yyyy";
	public static String FROM_DATE_LABEL = "between start of";

	public static String TO_DATE_LABEL = "and end of";
	@SuppressWarnings("rawtypes")
	protected JComboBox fromDateField = null;

	@SuppressWarnings("rawtypes")
	protected JComboBox toDateField = null;
	protected String[] fromDateItems = { THE_BIG_BANG, YESTERDAY, TODAY };

	protected String[] toDateItems = { THE_BIG_CRUNCH, TODAY, NOW, YESTERDAY };

	@SuppressWarnings({ "unchecked", "rawtypes" })
	FindByDate() {
		super();
		setLayout(new BorderLayout());

		final Font font = new Font("Helvetica", Font.PLAIN, 10);

		// Grid Layout
		final JPanel p = new JPanel();
		p.setLayout(new GridLayout(0, 2, 2, 2));

		// Date selection criteria
		final JLabel modified = new JLabel(MODIFIED_LABEL, SwingConstants.LEFT);
		modified.setFont(font);
		modified.setForeground(Color.black);
		p.add(modified);

		// format note
		final JLabel format = new JLabel(FORMAT_LABEL, SwingConstants.LEFT);
		format.setFont(font);
		format.setForeground(Color.black);
		p.add(format);

		// between
		final JLabel betweenLabel = new JLabel(FROM_DATE_LABEL, SwingConstants.RIGHT);
		betweenLabel.setFont(font);
		betweenLabel.setForeground(Color.black);
		p.add(betweenLabel);

		// from date
		// fromDateField = new JTextField(8);
		fromDateField = new JComboBox(fromDateItems);
		fromDateField.setFont(font);
		fromDateField.setEditable(true);
		p.add(fromDateField);

		// and
		final JLabel andLabel = new JLabel(TO_DATE_LABEL, SwingConstants.RIGHT);
		andLabel.setFont(font);
		andLabel.setForeground(Color.black);
		p.add(andLabel);

		// toDateField = new JTextField(8);
		toDateField = new JComboBox(toDateItems);
		toDateField.setFont(font);
		toDateField.setEditable(true);
		p.add(toDateField);

		add(p, BorderLayout.NORTH);
	}

	/**
	 * Generate a search filter object based on the setting of this UI component.
	 *
	 * @return a FindFilter object that implements the selection criteria
	 */
	@Override
	public FindFilter createFindFilter() {
		long from = -1;
		long to = -1;

		from = startDateToTime((String) fromDateField.getSelectedItem());
		to = endDateToTime((String) toDateField.getSelectedItem());

		return new DateFilter(from, to);
	}

	/**
	 * Convenience method for converting the end date text to milliseconds since
	 * January 1, 1970. The end time is the end of the specified day.
	 *
	 * @return milliseconds since January 1, 1970
	 */
	protected long endDateToTime(final String s) {
		if (s == null)
			return -1;
		final DateFormat dateFormatter = new GMTDateFormat("MM/dd/yyyy");

		long time = -1;
		Date d = dateFormatter.parse(s, new ParsePosition(0));
		if (d == null) {
			if (s.equalsIgnoreCase(TODAY)) {
				final String today = dateFormatter.format(new Date());
				d = dateFormatter.parse(today, new ParsePosition(0));
				if (d != null)
					time = d.getTime() + (24L * 3600L * 1000L);
			} else if (s.equalsIgnoreCase(YESTERDAY)) {
				final String yesterday = dateFormatter.format(new Date(new Date().getTime() - 24 * 60 * 60 * 1000));
				d = dateFormatter.parse(yesterday, new ParsePosition(0));
				if (d != null)
					time = d.getTime() + (24L * 3600L * 1000L);
			} else if (s.equalsIgnoreCase(NOW)) {
				d = new Date();
				if (d != null)
					time = d.getTime();
			} else if (s.equalsIgnoreCase(THE_BIG_CRUNCH)) {
				time = Long.MAX_VALUE;
			}
		} else {
			// Valid date. Now add 24 hours to make sure that the
			// date is inclusive
			time = d.getTime() + (24L * 3600L * 1000L);
		}

		return time;
	}

	/**
	 * Convenience method for converting the start date text to milliseconds since
	 * January 1, 1970.
	 *
	 * @return milliseconds since January 1, 1970
	 */
	protected long startDateToTime(final String s) {
		if (s == null)
			return -1;
		final DateFormat formatter = new GMTDateFormat("MM/dd/yyyy");
		Date d = formatter.parse(s, new ParsePosition(0));
		if (d == null) {
			if (s.equalsIgnoreCase(TODAY)) {
				final String today = formatter.format(new Date());
				d = formatter.parse(today, new ParsePosition(0));
			} else if (s.equalsIgnoreCase(YESTERDAY)) {
				final String yesterday = formatter.format(new Date(new Date().getTime() - 24 * 60 * 60 * 1000));
				d = formatter.parse(yesterday, new ParsePosition(0));
			} else if (s.equalsIgnoreCase(THE_BIG_BANG)) {
				return 0; // Not exactly the beginning of time, but
				// close enough for computer work
			}
		}
		if (d != null)
			return d.getTime();
		return -1;
	}

}
