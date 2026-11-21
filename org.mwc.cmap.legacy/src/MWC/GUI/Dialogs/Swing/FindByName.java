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
 * Implements user interface and generates FindFilter for selecting files by
 * name.
 */
class FindByName extends JPanel implements FindFilterFactory {
	/**
	 * Filter object for selecting files by name.
	 */
	class NameFilter implements FindFilter {
		protected String match = null;
		protected int howToMatch = -1;
		protected boolean ignoreCase = true;

		NameFilter(final String name, final int how, final boolean ignore) {
			match = name;
			howToMatch = how;
			ignoreCase = ignore;
		}

		@Override
		public boolean accept(final File f, final FindProgressCallback callback) {
			if (f == null)
				return false;

			if ((match == null) || (match.length() == 0))
				return true;
			if (howToMatch < 0)
				return true;

			final String filename = f.getName();

			if (howToMatch == NAME_CONTAINS_INDEX) {
				if (ignoreCase) {
					return filename.toLowerCase().indexOf(match.toLowerCase()) >= 0;
				} else {
					return filename.indexOf(match) >= 0;
				}
			} else if (howToMatch == NAME_IS_INDEX) {
				if (ignoreCase) {
					return filename.equalsIgnoreCase(match);
				} else {
					return filename.equals(match);
				}
			} else if (howToMatch == NAME_STARTS_WITH_INDEX) {
				if (ignoreCase) {
					return filename.toLowerCase().startsWith(match.toLowerCase());
				} else {
					return filename.startsWith(match);
				}
			} else if (howToMatch == NAME_ENDS_WITH_INDEX) {
				if (ignoreCase) {
					return filename.toLowerCase().endsWith(match.toLowerCase());
				} else {
					return filename.endsWith(match);
				}
			}

			return true;
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	protected String NAME_CONTAINS = "contains";
	protected String NAME_IS = "is";
	protected String NAME_STARTS_WITH = "starts with";
	protected String NAME_ENDS_WITH = "ends with";
	protected int NAME_CONTAINS_INDEX = 0;
	protected int NAME_IS_INDEX = 1;
	protected int NAME_STARTS_WITH_INDEX = 2;
	protected int NAME_ENDS_WITH_INDEX = 3;

	protected String[] criteria = { NAME_CONTAINS, NAME_IS, NAME_STARTS_WITH, NAME_ENDS_WITH };
	protected JTextField nameField = null;
	@SuppressWarnings("rawtypes")
	protected JComboBox combo = null;

	protected JCheckBox ignoreCaseCheck = null;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	FindByName() {
		super();
		setLayout(new BorderLayout());

		// Grid Layout
		final JPanel p = new JPanel();
		p.setLayout(new GridLayout(0, 2, 2, 2));

		// Name
		combo = new JComboBox(criteria);
		combo.setFont(new Font("Helvetica", Font.PLAIN, 10));
		combo.setPreferredSize(combo.getPreferredSize());
		p.add(combo);

		nameField = new JTextField(12);
		nameField.setFont(new Font("Helvetica", Font.PLAIN, 10));
		p.add(nameField);

		// ignore case
		p.add(new JLabel("", SwingConstants.RIGHT));

		ignoreCaseCheck = new JCheckBox("ignore case", true);
		ignoreCaseCheck.setForeground(Color.black);
		ignoreCaseCheck.setFont(new Font("Helvetica", Font.PLAIN, 10));
		p.add(ignoreCaseCheck);

		add(p, BorderLayout.NORTH);
	}

	@Override
	public FindFilter createFindFilter() {
		return new NameFilter(nameField.getText(), combo.getSelectedIndex(), ignoreCaseCheck.isSelected());
	}

}
