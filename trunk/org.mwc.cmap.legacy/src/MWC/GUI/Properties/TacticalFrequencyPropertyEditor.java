package MWC.GUI.Properties;

import java.beans.PropertyEditorSupport;

/**
 * class to provide list of time frequencies related to typical analysis periods
 */
public class TacticalFrequencyPropertyEditor extends PropertyEditorSupport
{

	private static final long _60_MINS = 60 * 60 * 1000l;

	private static final long _30_MINS = 30 * 60 * 1000l;

	private static final long _10_MINS = 10 * 60 * 1000l;

	/**
	 * the currently selected frequency (in millis)
	 */
	protected Long _myFreq;

	/**
	 * the list of tags shown in the drop-down list
	 */
	private String _stringTags[] =
	{ "1 Min", "10 Mins", "30 Mins", "1 Hour", "6 Hours"};

	/**
	 * the values to use for the tags in the list
	 */
	private long _freqs[] =
	{ 1 * 60 * 1000l, _10_MINS, _30_MINS, _60_MINS, 6 * _60_MINS};

	public String[] getTags()
	{
		return _stringTags;
	}

	protected long[] getFreqs()
	{
		return _freqs;
	}

	public Object getValue()
	{
		return new Long(_myFreq);
	}

	public void setValue(Object p1)
	{
		if (p1 instanceof Long)
		{
			_myFreq = (Long) p1;
		}
		else if (p1 instanceof String)
		{
			String val = (String) p1;
			setAsText(val);
		}
		else if (p1 == null)
		{
			_myFreq = getFreqs()[0];
		}
	}

	public void setAsText(String val)
	{
		long[] freqs = getFreqs();
		String[] tags = getTags();
		for (int i = 0; i < tags.length; i++)
		{
			String thisS = tags[i];
			if (thisS.equals(val))
			{
				_myFreq = freqs[i];
				break;
			}
		}

	}

	public String getAsText()
	{
		String res = null;

		// check we have a freq
		if (_myFreq == null)
			return res;

		long[] freqs = getFreqs();
		String[] tags = getTags();
		long current = _myFreq;
		for (int i = 0; i < freqs.length; i++)
		{
			long v = freqs[i];
			if (v == current)
			{
				res = tags[i];
				break;
			}

		}
		return res;
	}
}
