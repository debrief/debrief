package org.mwc.debrief.core.contenttype;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.internal.content.TextContentDescriber;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentDescription;
import org.mwc.cmap.core.CorePlugin;

/** import format test for SATC export files
 * 
 * @author ian
 *
 */
@SuppressWarnings("restriction")
public class NMEAContentDescriber extends TextContentDescriber
{

	@Override
	public int describe(InputStream contents, IContentDescription description)
			throws IOException
	{
		BufferedReader r = null;
		try
		{
			r = new BufferedReader(new InputStreamReader(contents));
			String firstLine = r.readLine();
			if (firstLine != null && firstLine.contains("$POSL"))
			{
				return VALID;
			}
		}
		catch (Exception e)
		{
			CorePlugin.logError(Status.ERROR, "NMEA content type error", e);
		}
		finally
		{
			try
			{
				if (r != null)
					r.close();
			}
			catch (IOException e)
			{
				CorePlugin.logError(Status.ERROR, "Couldn't close file", e);
			}
		}
		return super.describe(contents, description);
	}

}
