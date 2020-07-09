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
/*****************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
package info.limpet.impl;

import info.limpet.IContext;

public class MockContext implements IContext {

	@Override
	public String getCsvFilename() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getInput(final String title, final String description, final String defaultText) {
		return defaultText;
	}

	@Override
	public void log(final Exception e) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void logError(final Status status, final String message, final Exception e) {
		System.err.println("Logging status:" + status + " message:" + message);
		if (e != null) {
			e.printStackTrace();
		}
	}

	@Override
	public void openError(final String title, final String message) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void openInformation(final String title, final String message) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean openQuestion(final String title, final String message) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void openWarning(final String title, final String message) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void placeOnClipboard(final String text) {
		throw new RuntimeException("Not implemented");
	}

}
