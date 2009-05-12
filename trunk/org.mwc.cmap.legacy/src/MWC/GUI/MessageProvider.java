package MWC.GUI;

import java.util.Vector;

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
		public static MessageProvider Provider;

		public static void setProvider(MessageProvider provider)
		{
			Provider = provider;
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
