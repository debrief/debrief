/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.Utilities.ReaderWriter.json;

/*
 * Copyright 2006-2007 Sxip Identity Corporation
 */

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Date;

/**
 * A date formatter based on the Internet Date/Time format. This is defined in section 5.6 of RFC 3339.
 *
 * <p>A date formatted in this way looks like:<br />
 * <b><code>2005-05-15T17:11:51Z</code></b>
 *
 * @see <a href="http://www.ietf.org/rfc/rfc3339.txt">RFC 3339: section 5.6</a>
 * @author Marius Scurtescu, Johnny Bufu
 */
public class InternetDateFormat extends SimpleDateFormat
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		public static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final TimeZone GMT_TIME_ZONE = TimeZone.getTimeZone("GMT");

    public InternetDateFormat()
    {
        super(PATTERN, Locale.US);
        setTimeZone(GMT_TIME_ZONE);
    }

    public Date parse(final String source) throws ParseException
    {
        final Date date = super.parse(source.toUpperCase());
        return date;
    }
}