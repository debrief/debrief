/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/
package org.mwc.debrief.core.contenttype;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.internal.content.TextContentDescriber;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.content.IContentDescription;
import org.mwc.cmap.core.CorePlugin;

import Debrief.ReaderWriter.Nisida.ImportNisida;

/**
 * import format test for SATC export files
 *
 * @author ian
 *
 */
@SuppressWarnings("restriction")
public class NisidaContentDescriber extends TextContentDescriber {

	@Override
	public int describe(final InputStream contents, final IContentDescription description) throws IOException {
		try {
			if(ImportNisida.canLoadThisFile(contents)) {
				return VALID;
			}
		} catch (final Exception e) {
			CorePlugin.logError(IStatus.ERROR, "Nisida content type error", e);
		}
		return super.describe(contents, description);
	}

}
