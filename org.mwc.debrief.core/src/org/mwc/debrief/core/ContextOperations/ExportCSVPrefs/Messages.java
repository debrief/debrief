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
package org.mwc.debrief.core.ContextOperations.ExportCSVPrefs;

import org.eclipse.osgi.util.NLS;

public final class Messages extends NLS
{

  private static final String BUNDLE_NAME =
      "org.mwc.debrief.core.ContextOperations.ExportCSVPrefs.messages"; //$NON-NLS-1$

  public static String ExportCSVPropertyCellEditor_Select;

  public static String ExportCSVPropertyCellEditor_NotLoaded;

  public static String ExportCSVPropertyDescriptor_InvalidValueType;

  public static String ExportCSVPropertyDescriptor_NotValid;

  public static String ExportCSVLookupPreferencesPage_ErrorOnOpenFileEditor;

  public static String ExportCSVLookupPreferencesPage_FileLabel;

  public static String ExportCSVLookupPreferencesPage_InvalidFileName;

  public static String ExportCSVLookupPreferencesPage_OpenFileLabel;

  public static String ExportCSVLookupPreferencesPage_UpdateNow;

  public static String ExportCSVRegistry_EmptyFile;

  public static String ExportCSVRegistry_ErrorOnReading;

  public static String ExportCSVRegistry_FileNotFound;

  static
  {
    // initialize resource bundle
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }

  private Messages()
  {
  }
}
