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

package MWC.GUI.Tools.Swing;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.Timer;
import javax.swing.event.EventListenerList;

/**
 * repeat button code, taken from KIWI library
 *
 *
 * The author may be contacted at:
 *
 * frenzy@ix.netcom.com
 */

public class RepeaterButton extends JButton {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** The default initial delay. */
	public static final int DEFAULT_INITIAL_DELAY = 200;

	private Timer timer;

	/**
	 * Construct a new <code>RepeaterButton</code> with the specified icon.
	 *
	 * @param icon The icon for the button.
	 */

	public RepeaterButton(final Icon icon) {
		super(icon);
		_init();
	}

	/**
	 * Construct a new <code>RepeaterButton</code> with the specified text.
	 *
	 * @param text The text for the button.
	 */

	public RepeaterButton(final String text) {
		super(text);
		_init();
	}

	/**
	 * Construct a new <code>RepeaterButton</code> with the specified text and icon.
	 *
	 * @param text The text for the button.
	 * @param icon The icon for the button.
	 */

	public RepeaterButton(final String text, final Icon icon) {
		super(text, icon);
		_init();
	}

	/**
	 * copy of the code used for creating buttons elsewhere in Debrief code, which
	 * loads an icon from the app's resources
	 */
	public RepeaterButton(final String name, final String theIcon) {
		// load the icon first
		final java.lang.ClassLoader loader = getClass().getClassLoader();
		java.net.URL myURL = null;
		if (loader != null) {
			myURL = loader.getResource(theIcon);
			if (myURL != null)
				setIcon(new ImageIcon(myURL));
		}

		super.setName(name);

		// see if we failed to find icon
		if (myURL == null)
			setText(name);

		setBorderPainted(false);
		setToolTipText(name);
		setMargin(new Insets(0, 0, 0, 0));
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(final MouseEvent e) {
				setBorderPainted(true);
			}

			@Override
			public void mouseExited(final MouseEvent e) {
				setBorderPainted(false);
			}
		});

		_init();
	}

	private void _init() {
		timer = new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
			}
		}) {
			/**
				 *
				 */
			private static final long serialVersionUID = 1L;

			/**
			 * Notify all listeners that have registered interest for notification on this
			 * event type. The event instance is lazily created using the parameters passed
			 * into the fire method.
			 *
			 * @see EventListenerList
			 */
			@Override
			protected void fireActionPerformed(final ActionEvent e) {
				final ActionEvent e2 = new ActionEvent(RepeaterButton.this, e.getID(), e.getActionCommand());
				super.fireActionPerformed(e2);
			}
		};

		timer.setInitialDelay(DEFAULT_INITIAL_DELAY);
	}

	/**
	 * Add an <code>ActionListener</code> to this component's list of listeners.
	 *
	 * @param listener The listener to add.
	 */

	@Override
	public void addActionListener(final ActionListener listener) {
		super.addActionListener(listener);
		timer.addActionListener(listener);
	}

	/**
	 * Get the initial delay.
	 *
	 * @return The current initial delay, in milliseconds.
	 */

	public int getInitialDelay() {
		return (timer.getInitialDelay());
	}

	/**
	 * Get the repeat delay.
	 *
	 * @return The current repeat delay, in milliseconds.
	 */

	public int getRepeatDelay() {
		return timer.getDelay();
	}

	/** Process mouse events. */

	@Override
	protected void processMouseEvent(final java.awt.event.MouseEvent evt) {
		final int id = evt.getID();

		switch (id) {
		case java.awt.event.MouseEvent.MOUSE_CLICKED:
			super.processMouseEvent(evt);
			break;
		case java.awt.event.MouseEvent.MOUSE_PRESSED:
			model.setArmed(true);
			model.setPressed(true);
			repaint();
			timer.start();
			break;

		case java.awt.event.MouseEvent.MOUSE_RELEASED:
			model.setPressed(false);
			repaint();
			timer.stop();
			break;

		default:
			super.processMouseEvent(evt);
			break;
		}

	}

	/**
	 * Remove an <code>ActionListener</code> from this component's list of
	 * listeners.
	 *
	 * @param listener The listener to remove.
	 */

	@Override
	public void removeActionListener(final ActionListener listener) {
		super.removeActionListener(listener);
		timer.removeActionListener(listener);
	}

	/**
	 * Set the initial delay on this button.
	 *
	 * @param msec The number of milliseconds to wait after the initial mouse press
	 *             to begin generating action events.
	 */

	public void setInitialDelay(final int msec) {
		timer.setInitialDelay(msec);
	}

	/**
	 * Set the repeat delay.
	 *
	 * @param msec The number of milliseconds to wait between the firing of
	 *             successive action events.
	 */

	public void setRepeatDelay(final int msec) {
		timer.setDelay(msec);
	}
}
