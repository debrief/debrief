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
 * A wrapper for JFileChooser with accessories for directory history and file
 * preview. (E)nhancedFilechooser.
 *
 * @author Klaus Berg
 */

public class EFileChooser extends JFileChooser {

	/**
	 * This inner class is used to set the UI to MyComboBoxUI Unlike JButton,
	 * JComboBox has no public setUI() method, so we have to go this way using the
	 * protected setUI() method of JComponent.
	 */
	@SuppressWarnings("rawtypes")
	class MyComboBox extends JComboBox {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unchecked")
		public MyComboBox(final Vector<String> items) {
			super(items);
			setUI(new MyComboBoxUI());
		}
	}

	/**
	 * Modified UI for JComboBox.
	 */
	class MyComboBoxUI extends BasicComboBoxUI {

		class MyComboPopup extends BasicComboPopup {

			class MyInvocationMouseHandler extends MouseAdapter {

				@Override
				public void mousePressed(final MouseEvent e) {
					final boolean isLeft = SwingUtilities.isLeftMouseButton(e);

					// @@IM - ignore it, if it is the right mouse button!
					// this did only work for right-mouse buttons!
					if (!isLeft)
						return;

					if (!comboBox_.isEnabled())
						return;

					delegateFocus(e);
					togglePopup();
				}

				@Override
				public void mouseReleased(final MouseEvent e) {
					final int oldSelectedIndex = comboBox_.getSelectedIndex();
					final Component source = (Component) e.getSource();
					final Dimension size = source.getSize();
					final Rectangle bounds = new Rectangle(0, 0, size.width - 1, size.height - 1);
					if (!bounds.contains(e.getPoint())) {
						final MouseEvent newEvent = convertMouseEvent(e);
						final Point location = newEvent.getPoint();
						final Rectangle r = new Rectangle();
						list_.computeVisibleRect(r);
						if (r.contains(location)) {
							updateListBoxSelectionForEvent(newEvent, false);
							final int index = list_.getSelectedIndex();
							comboBox_.setSelectedIndex(index);
							if (index == oldSelectedIndex) {
								comboItemListener.selectNewDirectory();
							}
						}
						hide();
					}
					hasEntered_ = false;
					stopAutoScrolling();
				}

			}

			/**
			 * This listener hides the popup when the mouse is released in the list.
			 */
			protected class MyListMouseHandler extends MouseAdapter {

				@Override
				public void mouseReleased(final MouseEvent e) {
					comboItemListener.selectNewDirectory();
					comboBox_.setSelectedIndex(list_.getSelectedIndex());
					hide();
				}
			}

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("rawtypes")
			protected JList list_;

			@SuppressWarnings("rawtypes")
			protected JComboBox comboBox_;

			protected boolean hasEntered_;

			@SuppressWarnings("rawtypes")
			MyComboPopup(final JComboBox combo) {
				super(combo);
				list_ = list;
				this.comboBox_ = super.comboBox;
				hasEntered_ = hasEntered;
			}

			@Override
			protected MouseEvent convertMouseEvent(final MouseEvent e) {
				return super.convertMouseEvent(e);
			}

			@Override
			protected MouseListener createListMouseListener() {
				return new MyListMouseHandler();
			}

			/**
			 * Creates the mouse listener that is returned by ComboPopup.getMouseListener().
			 * Returns an instance of MyComboPopup$MyInvocationMouseHandler.
			 */
			@Override
			protected MouseListener createMouseListener() {
				return new MyInvocationMouseHandler();
			}

			@Override
			protected void delegateFocus(final MouseEvent e) {
				super.delegateFocus(e);
			}

			@Override
			public void hide() {
				super.hide();
			}

			@Override
			protected void stopAutoScrolling() {
				super.stopAutoScrolling();
			}

			@Override
			protected void togglePopup() {
				super.togglePopup();
			}

			@Override
			protected void updateListBoxSelectionForEvent(final MouseEvent anEvent, final boolean shouldScroll) {
				super.updateListBoxSelectionForEvent(anEvent, shouldScroll);
			}

		}

		MyComboBoxUI() {
			super();
		}

		/**
		 * Creates an implementation of the ComboPopup interface. Returns an instance of
		 * MyComboPopup.
		 */
		@Override
		protected ComboPopup createPopup() {
			return new MyComboPopup(comboBox);
		}

	}

	private final class PreviewAndHistoryPanel extends JPanel {

		/**
		 * Display a tooltip for the cell if needed.
		 * <p/>
		 * Note : When JComboBox is located near the border of a Frame, the tooltip
		 * doesn't display outside the frame due to current Swing limitations.
		 */
		class ComboBoxRendererWithTooltips extends BasicComboBoxRenderer {

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			@SuppressWarnings("rawtypes")
			public Component getListCellRendererComponent(final JList list, final Object value, final int index,
					final boolean isSelected, final boolean cellHasFocus) {
				setFont(list.getFont());
				setText((value == null) ? "" : value.toString());
				if (isSelected) {
					setBackground(list.getSelectionBackground());
					setForeground(list.getSelectionForeground());
					if (index >= 0) {
						if (this.getPreferredSize().width > combo.getSize().width) {
							list.setToolTipText(comboModel.elementAt(index));
						} else {
							list.setToolTipText(null);
						}
					}
				} else {
					setBackground(list.getBackground());
					setForeground(list.getForeground());
				}
				return this;
			}
		}

		/**
		 * We use an ItemListener imstead of an ActionListener because an action event
		 * is also generated if the DEL or SHIFT+DEL button is pressed in order to
		 * delete an item resp. all items.
		 */
		protected final class ComboItemListener implements ItemListener {
			String dir;

			@Override
			public void itemStateChanged(final ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					selectNewDirectory();
				}
			}

			void selectNewDirectory() {
				dir = (String) combo.getSelectedItem();
				EFileChooser.this.setCurrentDirectory(new File(dir));
				final JLabel label = new JLabel(dir);
				label.setFont(combo.getFont());
				if (label.getPreferredSize().width > combo.getSize().width) {
					combo.setToolTipText(dir);
				} else {
					combo.setToolTipText(null);
				}
			}
		}

		private final class DeleteKeyListener implements ActionListener {
			String action_;

			DeleteKeyListener(final String action) {
				action_ = action;
			}

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (action_.equals("ONE")) {
					combo.removeItemAt(combo.getSelectedIndex());
				} else {
					combo.removeAllItems();
				}
			}
		}

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unchecked")
		PreviewAndHistoryPanel() {
			setPreferredSize(new Dimension(250, 250));
			setBorder(BorderFactory.createEtchedBorder());
			setLayout(new BorderLayout());

			combo = new MyComboBox(comboModel);
			comboItemListener = new ComboItemListener();
			combo.addItemListener(comboItemListener);
			combo.registerKeyboardAction(new DeleteKeyListener("ONE"),
					KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false), JComponent.WHEN_IN_FOCUSED_WINDOW);
			combo.registerKeyboardAction(new DeleteKeyListener("ALL"),
					KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.SHIFT_MASK, false),
					JComponent.WHEN_IN_FOCUSED_WINDOW);
			combo.setRenderer(new ComboBoxRendererWithTooltips());
			add(combo, BorderLayout.NORTH);

			previewer = new TextPreviewer();
			add(previewer, BorderLayout.CENTER);
		}

	}

	class TextPreviewer extends JComponent {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
		private final JTextArea textArea = new JTextArea();
		private final JScrollPane scroller = new JScrollPane(textArea);
		private final char[] buff = new char[500];
		private Color bg;

		TextPreviewer() {

			try {
				textArea.setEditable(false);
				if ((bg = UIManager.getColor("TextArea.background")) != null)
					textArea.setBackground(bg);
				else
					textArea.setBackground(Color.white);
				setBorder(BorderFactory.createEtchedBorder());
				setLayout(new BorderLayout());
				add(scroller, BorderLayout.CENTER);
			} catch (final NullPointerException np) {
				// layout can throw exceptions sometimes: ignore
			}
		}

		public void clear() {
			textArea.setText("");
		}

		private String contentsOfFile(final File file) {
			if (file == null) {
				return "";
			}
			if (file.getName().equals("")) {
				return "";
			}
			String s = new String();
			FileReader reader = null;
			try {
				reader = new FileReader(file);
				final int nch = reader.read(buff, 0, buff.length);
				if (nch != -1) {
					s = new String(buff, 0, nch);
				}
			} catch (final IOException iox) {
				s = "";
			}
			try {
				if (reader != null)
					reader.close();
			} catch (final Exception ex) {
				// ignore
			}
			return s;
		}

		public void initTextArea(final File file) {
			textArea.setText(contentsOfFile(file));
			textArea.setCaretPosition(0);
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	static Vector<String> comboModel;
	private static FileInputStream fis;
	private static ObjectInputStream ois;
	private static String dirFile;

	public static void main(final String[] argv) {
		final EFileChooser fileChooser = new EFileChooser("DEMO");

		final JFrame frame = new JFrame("EFileChooser Demo");
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final java.awt.event.WindowEvent A) {
				EFileChooser.saveDirectoryEntries();
				frame.setVisible(false);
				frame.dispose();
				System.exit(0);
			}
		});
		final Container c = frame.getContentPane();
		final JPanel buttonP = new JPanel();
		final JButton openB = new JButton("Open File");
		final JTextField textField = new JTextField(20);
		textField.setEditable(false);
		openB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
				final int result = fileChooser.showOpenDialog(frame);
				if (result == JFileChooser.APPROVE_OPTION) {
					final File f = fileChooser.getSelectedFile();
					if (f == null) {
						return;
					}
					if (f.getName().equals("")) {
						return;
					}
					textField.setText(f.getAbsolutePath());
				}
			}
		});
		final JButton exitB = new JButton("Exit Demo");
		exitB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				EFileChooser.saveDirectoryEntries();
				frame.setVisible(false);
				frame.dispose();
				System.exit(0);
			}
		});
		buttonP.add(openB);
		buttonP.add(exitB);
		c.add(buttonP, BorderLayout.NORTH);
		c.add(textField, BorderLayout.SOUTH);
		c.add(new JLabel("Exercising EFileChooser", SwingConstants.CENTER), BorderLayout.CENTER);
		frame.pack();
		frame.setLocation(300, 300);
		frame.setSize(400, 200);
		frame.setVisible(true);
	}

	// --- Helper classes:

	// --- PreviewAndHistoryPanel ----------------------------------------------

	public static void saveDirectoryEntries() {
		try {
			final FileOutputStream fos = new FileOutputStream(dirFile);
			final ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(comboModel);
			oos.flush();
			oos.close();
			fos.close();
		} catch (final Exception e) {
			System.err.println("Trouble saving EFileChooser directories: " + e);
			MWC.Utilities.Errors.Trace.trace(e);
		}

	}

	// --- TextPreviewer -------------------------------------------------------

	TextPreviewer previewer;

	// -------------------------------------------------------------------------

	// CONSTRUCTORS //

	private PreviewAndHistoryPanel previewAndHistoryPanel;

	FindAccessory findPanel;

	MyComboBox combo;

	PreviewAndHistoryPanel.ComboItemListener comboItemListener;

	public EFileChooser() {
		this("APP");
	}

	// --- Demo app ------------------------------------------------------------

	@SuppressWarnings("unchecked")
	public EFileChooser(final String applicationName) {
		// look for existing directory history from last session
		dirFile = System.getProperty("user.home") + System.getProperty("file.separator") + applicationName
				+ "_DIRECTORY_HISTORY.cfg";
		if (new File(dirFile).exists()) {
			try {
				fis = new FileInputStream(dirFile);
				ois = new ObjectInputStream(fis);
				comboModel = (Vector<String>) (ois.readObject());
				ois.close();
				fis.close();
			} catch (final Exception e) {
				System.err.println("Trouble reading EFileChooser directories: " + e);
				MWC.Utilities.Errors.Trace.trace(e);
			}
		} else {
			comboModel = new Vector<String>(10); // we expect about 10 directory entries
		}

		setMultiSelectionEnabled(false);
		setPreferredSize(new Dimension(500, 350));
		previewAndHistoryPanel = new PreviewAndHistoryPanel();
		findPanel = new FindAccessory(this, null);
		final JPanel choicePanel = new JPanel(new BorderLayout());
		final JTabbedPane choicePane = new JTabbedPane();
		choicePane.addTab("Navigation", previewAndHistoryPanel);
		choicePane.addTab("Find Files", findPanel);
		choicePanel.add(choicePane, BorderLayout.CENTER);
		setAccessory(choicePanel);
		addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent e) {
				if (e.getPropertyName().equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
					findPanel.updateFindDirectory();
					previewer.clear();
					final File dir = (File) e.getNewValue();
					if (dir == null) {
						return;
					}
					if (dir.getName().equals("")) {
						return;
					}
					final String pathname = dir.getAbsolutePath();
					int i;
					boolean found = false;
					for (i = 0; i < comboModel.size(); i++) {
						final String dirname = comboModel.elementAt(i);
						if (dirname.equals(pathname)) {
							found = true;
							break;
						}
					}
					if (found) {
						combo.setSelectedIndex(i);
					}
				}
				if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
					final File f = (File) e.getNewValue();
					showFileContents(f);
					insertDirectory(f);
				}
			}
		});
	}

	// -------------------------------------------------------------------------

	void insertDirectory(final File file) {
		if (file == null) {
			return;
		}
		if (file.getName().equals("")) {
			return;
		}
		final String pathname = file.getAbsolutePath();
		final int pos = pathname.lastIndexOf(System.getProperty("file.separator"));
		final String dir = pathname.substring(0, pos);
		if (comboModel.contains(dir)) {
			return;
		} else {
			comboModel.addElement(dir);
			Collections.sort(comboModel);
			combo.revalidate();
			combo.setSelectedItem(dir);
		}
	}

	protected void showFileContents(final File f) {
		previewer.initTextArea(f);
	}

}
