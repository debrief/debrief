package org.mwc.cmap.core.property_support.lengtheditor;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "com.pml.lengtheditor.messages"; //$NON-NLS-1$

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
