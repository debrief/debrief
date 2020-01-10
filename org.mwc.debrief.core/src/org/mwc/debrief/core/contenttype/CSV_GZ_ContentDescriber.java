package org.mwc.debrief.core.contenttype;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.internal.content.TextContentDescriber;
import org.eclipse.core.runtime.content.IContentDescription;

/** import format test for SATC export files
 * 
 * @author ian
 *
 */
@SuppressWarnings("restriction")
public class CSV_GZ_ContentDescriber extends TextContentDescriber
{

	@Override
	public int describe(InputStream contents, IContentDescription description)
			throws IOException
	{
	  return VALID;
	}

}
