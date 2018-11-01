/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package MWC.GUI;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.util.Vector;

import javax.swing.JOptionPane;

/**
 * interface for object that can show a message to the user
 * 
 * @author Administrator
 * 
 */
public interface MessageProvider
{

	/** core implementation, used by anybody...
	 * 
	 * @author Administrator
	 *
	 */
	public static class Base
	{
		private static MessageProvider Provider;

		public static void setProvider(final MessageProvider provider)
		{
			Provider = provider;
		}
		public static void show(final String title, final String message, final int status)
		{
		  if(Provider!=null) {
		    Provider.show(title, message, status);
		  }
		  else {
		    final Frame tmp = new Frame();
	      // and put the frame in the centre of the screen
	      final Dimension sz = Toolkit.getDefaultToolkit().getScreenSize();
	      tmp.setLocation(sz.width / 2, sz.height / 2);
	      JOptionPane.showMessageDialog(tmp, message, title,
	          JOptionPane.INFORMATION_MESSAGE);
		  }
		}
	}

	/**
	 * instance we can use to test message provider is working correctly
	 * 
	 * @author Administrator
	 * 
	 */
	public static class TestableMessageProvider implements MessageProvider
	{
		public Vector<String> _titles;
		public Vector<String> _messages;
		public Vector<Integer> _statuses;

		public void show(final String title, final String message, final int status)
		{
			if (_titles == null)
			{
				_titles = new Vector<String>();
				_messages = new Vector<String>();
				_statuses = new Vector<Integer>();
			}

			_titles.add(title);
			_messages.add(message);
			_statuses.add(status);
		}

	}

	/**
	 * Status severity constant (value 0) indicating this status represents the
	 * nominal case. This constant is also used as the status code representing
	 * the nominal case.
	 */
	public static final int OK = 0;

	/**
	 * Status type severity (bit mask, value 1) indicating this status is
	 * informational only.
	 */
	public static final int INFO = 0x01;

	/**
	 * Status type severity (bit mask, value 2) indicating this status represents
	 * a warning.
	 */
	public static final int WARNING = 0x02;

	/**
	 * Status type severity (bit mask, value 4) indicating this status represents
	 * an error.
	 */
	public static final int ERROR = 0x04;

	/**
	 * 
	 * @param title
	 * @param message
	 * @param status
	 */
	public void show(final String title, final String message, int status);
}
