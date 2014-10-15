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
package org.mwc.cmap.core.property_support.lengtheditor;

import org.eclipse.osgi.util.NLS;

public final class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.mwc.cmap.core.property_support.lengtheditor.messages"; //$NON-NLS-1$

	public static String LengthPropertyCellEditor_Select;

	public static String LengthPropertyCellEditor_NotLoaded;

	public static String LengthPropertyDescriptor_InvalidValueType;

	public static String LengthPropertyDescriptor_NotValid;

	public static String LengthsLookupPreferencesPage_ErrorOnOpenFileEditor;

	public static String LengthsLookupPreferencesPage_FileLabel;

	public static String LengthsLookupPreferencesPage_InvalidFileName;

	public static String LengthsLookupPreferencesPage_OpenFileLabel;

	public static String LengthsLookupPreferencesPage_UpdateNow;

	public static String LengthsRegistry_EmptyFile;

	public static String LengthsRegistry_ErrorOnReading;

	public static String LengthsRegistry_FileNotFound;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
