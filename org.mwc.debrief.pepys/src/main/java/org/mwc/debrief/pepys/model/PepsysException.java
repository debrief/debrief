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

package org.mwc.debrief.pepys.model;

public class PepsysException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = -1990428413735762653L;

	private final String title;
	private final String message;

	public PepsysException(final String title, final String message) {
		super();
		this.title = title;
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public String getTitle() {
		return title;
	}

}
