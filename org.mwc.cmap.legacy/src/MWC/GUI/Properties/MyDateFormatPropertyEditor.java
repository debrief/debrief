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
package MWC.GUI.Properties;
public class MyDateFormatPropertyEditor extends
			MWC.GUI.Properties.DateFormatPropertyEditor
	{
		static private final String NULL_VALUE = "N/A";

		static final String[] stringTags =
		{ NULL_VALUE, "mm:ss.SSS", "HHmm.ss", "HHmm", "ddHHmm", "ddHHmm.ss",
				"yy/MM/dd HH:mm", };

		public final String[] getTags()
		{
			return stringTags;
		}

		public void setAsText(final String val)
		{
			_myFormat = getMyIndexOf(val);
		}

		private int getMyIndexOf(final String val)
		{
			int res = INVALID_INDEX;

			// cycle through the tags until we get a matching one
			for (int i = 0; i < getTags().length; i++)
			{
				final String thisTag = getTags()[i];
				if (thisTag.equals(val))
				{
					res = i;
					break;
				}

			}
			return res;
		}

	}
