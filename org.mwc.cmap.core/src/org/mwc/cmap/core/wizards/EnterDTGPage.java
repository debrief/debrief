/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.core.wizards;

import java.text.ParseException;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;
import org.mwc.cmap.core.CorePlugin;
import org.osgi.service.prefs.Preferences;

import MWC.GenericData.HiResDate;
import MWC.Utilities.TextFormatting.FullFormatDateTime;

public class EnterDTGPage extends EnterStringPage implements ModifyListener
{
	private static final String DATE = "DATE";

	public EnterDTGPage(final ISelection selection, final HiResDate startDate,
			final String pageTitle, final String pageExplanation, final String fieldExplanation, final String imagePath, final String helpContext)
	{
		super(selection,
				FullFormatDateTime.toString(startDate.getDate().getTime()), pageTitle,
				pageExplanation, fieldExplanation, imagePath, helpContext, true);

		// tell the editor we're listening for modifications
		super.addModifiedListener(this);

		setDefaults();
	}

	private void setDefaults()
	{
		final Preferences prefs = getPrefs();

		if (prefs != null)
		{
			_startName = prefs.get(DATE, _startName);
		}
	}

	/**
	 * sort out our answer
	 * 
	 * @return
	 */
	public HiResDate getDate()
	{
		HiResDate res = null;
		try
		{
			final long time = FullFormatDateTime.fromString(super.getString());
			res = new HiResDate(time);
		}
		catch (final ParseException e)
		{
			CorePlugin.logError(Status.ERROR, "Parsing this text:"
					+ super.getString(), e);
		}
		return res;
	}
	

	@Override
	public void dispose()
	{
		// try to store some defaults
		final Preferences prefs = getPrefs();
		prefs.put(DATE, _myWrapper.getName());

		super.dispose();
	}

	public void modifyText(final ModifyEvent e)
	{
		// ok, check we can parse it
		final Text text = (Text) e.widget;
		final String val = text.getText();

		try
		{
			// do a trial conversion, just to check it's valid
			FullFormatDateTime.fromString(val);
		}
		catch (final ParseException e1)
		{
			// invalid - try to stop it!!!
			super.setErrorMessage("Date not formatted correctly");

		}
	}

}
