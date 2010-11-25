package org.mwc.cmap.core.wizards;

import java.text.ParseException;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;
import org.mwc.cmap.core.CorePlugin;

import MWC.GenericData.HiResDate;
import MWC.Utilities.TextFormatting.FullFormatDateTime;

public class EnterDTGPage extends EnterStringPage implements ModifyListener
{
	public EnterDTGPage(ISelection selection, HiResDate startDate,
			String pageTitle, String pageExplanation, String fieldExplanation, String imagePath, String helpContext)
	{
		super(selection,
				FullFormatDateTime.toString(startDate.getDate().getTime()), pageTitle,
				pageExplanation, fieldExplanation, imagePath, helpContext);

		// tell the editor we're listening for modifications
		super.addModifiedListener(this);

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
			long time = FullFormatDateTime.fromString(super.getString());
			res = new HiResDate(time);
		}
		catch (ParseException e)
		{
			CorePlugin.logError(Status.ERROR, "Parsing this text:"
					+ super.getString(), e);
		}
		return res;
	}

	@Override
	public void modifyText(ModifyEvent e)
	{
		// ok, check we can parse it
		Text text = (Text) e.widget;
		String val = text.getText();

		try
		{
			// do a trial conversion, just to check it's valid
			FullFormatDateTime.fromString(val);
		}
		catch (ParseException e1)
		{
			// invalid - try to stop it!!!
			super.setErrorMessage("Date not formatted correctly");

		}
	}

}
