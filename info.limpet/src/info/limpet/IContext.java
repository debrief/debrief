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
package info.limpet;


public interface IContext
{
	enum Status
	{
		INFO, WARNING, ERROR;
	}

	/**
	 * action names
	 */
	String ADD_LAYER_ACTION_NAME = "addLayer";
	String COPY_CSV_TO_CLIPBOARD = "copyScvToClipboard";
	String COPY_CSV_TO_FILE = "copyScvToFile";
	String GENERATE_DATA = "generateData";
	String REFRESH_VIEW = "refreshView";
	
	/** get a string from the user, or null if the user cancelled the operation
	 * 
	 * @param title  shown in the dialog heading
	 * @param description what the text is being input for
	 * @param defaultText the text to pre-populate the input box
	 * @return user-entered string, or null for cancel
	 */
	String getInput(String title, String description, String defaultText);

	/** indicate this warning
	 * 
	 * @param string
	 * @param message
	 * @param e
	 */
	void logError(Status status, String message, Exception e);

	void openWarning(String title, String message);

	void openInformation(String title, String message);

	String getCsvFilename();

	boolean openQuestion(String title, String message);

	void openError(String title, String message);

	void log(Exception e);

	void placeOnClipboard(String text);
}
