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
 * content.
 * <P>
 * <b>WARNING:</B> The FindFilter inner class for this object does not implement
 * an efficient strng search algorithm. Efficiency was traded for code
 * simplicity.
 */
class FindByContent extends JPanel implements FindFilterFactory {

	/**
	 * Implements a simple content filter.
	 */
	class ContentFilter implements FindFilter {
		/**
		 * Thrown when a LocatorStream object finds a byte array.
		 */
		class LocatedException extends IOException {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			public LocatedException(final long location) {
				super(String.valueOf(location));
			}

			public LocatedException(final String msg) {
				super(msg);
			}
		}

		/**
		 * Locate an array of bytes on the output stream. Throws a LocatedStream
		 * exception for every occurrence of the byte array.
		 */
		class LocatorStream extends OutputStream {
			/**
			 * Thrown when the bytes written match the byte pattern.
			 */
			class MatchMadeException extends IOException {
				/**
				 *
				 */
				private static final long serialVersionUID = 1L;

				public MatchMadeException(final long mark) {
					super(String.valueOf(mark));
				}

				public MatchMadeException(final String msg) {
					super(msg);
				}
			}

			/**
			 * Accept "output" as long as it matches a specified array of bytes. Throw a
			 * MatchMadeException when the bytes written equals the match array. Throw an
			 * IOException when a byte does not match. Ignore everything after a match is
			 * made.
			 */
			class MatchStream extends OutputStream {
				protected long mark1 = -1;
				protected int pos = 0;
				protected byte[] match = null;
				protected boolean matchMade = false;

				MatchStream(final byte[] b, final long m) {
					mark1 = m;
					match = b;
				}

				public long getMark() {
					return mark1;
				}

				@Override
				public void write(final int b) throws IOException {
					if (matchMade)
						return;
					if (match == null)
						throw new IOException("NULL match array");

					if (match.length == 0)
						throw new IOException("Empty match array");

					if (pos >= match.length)
						throw new IOException("No match");

					if (b != match[pos])
						throw new IOException("No match");

					pos++;
					if (pos >= match.length) {
						matchMade = true;
						throw new MatchMadeException(mark1);
					}
				}
			}

			protected byte[] locate = null;

			protected Vector<MatchStream> matchMakers = new Vector<MatchStream>();

			protected long mark = 0;

			LocatorStream(final byte[] b) {
				locate = b;
			}

			@Override
			public void write(final int b) throws IOException {
				if (locate == null)
					throw new IOException("NULL locator array");
				if (locate.length == 0)
					throw new IOException("Empty locator array");

				long foundAt = -1;

				for (int i = matchMakers.size() - 1; i >= 0; i--) {
					final MatchStream m = matchMakers.elementAt(i);
					try {
						m.write(b);
					} catch (final MatchMadeException e) {
						foundAt = m.getMark();
						matchMakers.removeElementAt(i);
					} catch (final IOException e) {
						// Match not made. Remove current matchMaker stream.
						matchMakers.removeElementAt(i);
					}
				}

				if (b == locate[0]) {
					final MatchStream m = new MatchStream(locate, mark);
					m.write(b); // This will be accepted
					matchMakers.addElement(m);
				}
				mark++;

				if (foundAt >= 0) {
					throw new LocatedException(foundAt);
				}
			}
		}

		protected String content = null;

		protected boolean ignoreCase = true;

		ContentFilter(final String s, final boolean ignore) {
			content = s;
			ignoreCase = ignore;
		}

		@Override
		public boolean accept(final File f, final FindProgressCallback callback) {
			if (f == null)
				return false;
			if (f.isDirectory())
				return false;
			if ((content == null) || (content.length() == 0))
				return true;

			boolean result = false;
			BufferedInputStream in = null;
			try {
				final long fileLength = f.length();
				in = new BufferedInputStream(new FileInputStream(f));
				byte[] contentBytes = null;
				if (ignoreCase)
					contentBytes = content.toLowerCase().getBytes();
				else
					contentBytes = content.getBytes();
				final LocatorStream locator = new LocatorStream(contentBytes);
				long counter = 0;
				int callbackCounter = 20; // Only call back every 20 bytes
				int c = -1;
				while ((c = in.read()) != -1) {
					counter++;
					int matchChar = c;
					if (ignoreCase)
						matchChar = Character.toLowerCase((char) c);
					locator.write(matchChar);

					// This search could be time consuming, especially since
					// this algorithm is not exactly the most efficient.
					// Report progress to search monitor and abort
					// if method returns false.
					if (callback != null) {
						if (--callbackCounter <= 0) {
							if (!callback.reportProgress(this, f, counter, fileLength)) {
								locator.close();
								return false;
							}
							callbackCounter = 20;
						}
					}
				}

				locator.close();
			} catch (final LocatedException e) {
				result = true;
			} catch (final Throwable e) {
				// can't read the file: it isn't a match
			}

			try {
				if (in != null)
					in.close();
			} catch (final IOException e) {
				// nothing more to do if closing fails
			}
			return result;
		}

	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Find for the first occurrence of the text in this field.
	 */
	protected JTextField contentField = null;

	protected JCheckBox ignoreCaseCheck = null;

	/**
	 * Constructs a user interface and a FindFilterFactory for searching files
	 * containing specified text.
	 */
	FindByContent() {
		super();
		setLayout(new BorderLayout());

		final JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

		// Name
		final JLabel l = new JLabel("File contains...", SwingConstants.LEFT);
		l.setForeground(Color.black);
		l.setFont(new Font("Helvetica", Font.PLAIN, 10));
		p.add(l);

		contentField = new JTextField();
		contentField.setForeground(Color.black);
		contentField.setFont(new Font("Helvetica", Font.PLAIN, 10));
		p.add(contentField);

		// ignore case
		ignoreCaseCheck = new JCheckBox("ignore case", true);
		ignoreCaseCheck.setForeground(Color.black);
		ignoreCaseCheck.setFont(new Font("Helvetica", Font.PLAIN, 9));
		p.add(ignoreCaseCheck);

		add(p, BorderLayout.NORTH);
	}

	@Override
	public FindFilter createFindFilter() {
		return new ContentFilter(contentField.getText(), ignoreCaseCheck.isSelected());
	}

}
