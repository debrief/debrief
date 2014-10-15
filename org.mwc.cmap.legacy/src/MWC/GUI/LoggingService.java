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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GUI;

/**
 * logging service that accomodates a surrogate - so the fancy RCP gui can
 * register as a logger with the legacy ASSET code
 * 
 * @author ian
 * 
 */
public class LoggingService implements ErrorLogger
{

	static ErrorLogger _substituteParent;
	static LoggingService _singleton;

	public static void initialise(final ErrorLogger logger)
	{
		_substituteParent = logger;
	}

	@Override
	public void logError(final int status, final String text, final Exception e)
	{
		if (_substituteParent != null)
			_substituteParent.logError(status, text, e);
		else
		{
			System.err.println("Error:" + text);
			if (e != null)
				e.printStackTrace();
		}

	}

	public static LoggingService INSTANCE()
	{
		if (_singleton == null)
			_singleton = new LoggingService();

		return _singleton;
	}

}
