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
package org.mwc.debrief.core.loaders;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.transform.JDOMSource;
import org.mwc.cmap.core.CorePlugin;
import org.xml.sax.SAXException;

public final class GpxUtil
{
	private static final SchemaFactory FACTORY = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	private static Schema GPX_1_0_SCHEMA;
	private static Schema GPX_1_1_SCHEMA;

	private static final class DirectoryCollector implements Runnable
	{
		private String selectedFolder = null;

		@Override
		public void run()
		{
			final DirectoryDialog dlg = new DirectoryDialog(Display.getDefault().getActiveShell());

			// Change the title bar text
			dlg.setText("Export to GPS");

			// Customizable message displayed in the dialog
			dlg.setMessage("Select a directory to save the exported GPS file");

			// Calling open() will open and run the dialog.
			// It will return the selected directory, or
			// null if user cancels
			selectedFolder = dlg.open();
		}

		public String getSelectedFolder()
		{
			return selectedFolder;
		}
	}

	static
	{
		try
		{
			GPX_1_0_SCHEMA = FACTORY.newSchema(new StreamSource(GpxUtil.class.getResourceAsStream("gpx_1.0.xsd")));
			GPX_1_1_SCHEMA = FACTORY.newSchema(new StreamSource(GpxUtil.class.getResourceAsStream("gpx_1.1.xsd")));
		}
		catch (final SAXException e)
		{
			throw new IllegalStateException("Unable to load GPX schema. Cannot perform validation of imported documents", e);
		}
	}

	public static Source getDocumentSource(final InputStream gpxStream) throws JDOMException, IOException
	{
		final SAXBuilder builder = new SAXBuilder();
		final Document document = builder.build(gpxStream);
		final JDOMSource in = new JDOMSource(document);
		return in;
	}

	public static boolean isGpx10(final Source source)
	{
		final Document document = ((JDOMSource) source).getDocument();
		if ("1.0".equals(document.getRootElement().getAttributeValue("version")))
		{
			return true;
		}
		return false;
	}

	public static boolean isValid(final Source source, final boolean isGpx10) throws SAXException, IOException
	{
		Validator validator;

		if (isGpx10)
		{
			if (GPX_1_0_SCHEMA == null)
			{
				throw new IllegalStateException("Unable to load GPX 1.0 schema. Cannot perform validation of imported documents");
			}
			validator = GPX_1_0_SCHEMA.newValidator();
		}
		else
		{
			if (GPX_1_1_SCHEMA == null)
			{
				throw new IllegalStateException("Unable to load GPX 1.1 schema. Cannot perform validation of imported documents");
			}
			validator = GPX_1_1_SCHEMA.newValidator();
		}

		try
		{
			validator.validate(source);
			return true;
		}
		catch (final SAXException ex)
		{
			CorePlugin.logError(Status.ERROR, "GPX file trying to import is not valid because " + ex.getMessage(), ex);
			CorePlugin.errorDialog("Load GPS File", "GPX failed validation. Reason: " + ex.getMessage());
		}
		return false;
	}

	public static String collectDirecotryPath()
	{
		final DirectoryCollector collector = new DirectoryCollector();
		Display.getDefault().syncExec(collector);

		return collector.getSelectedFolder();
	}
		
	public static boolean isValid(final File f) throws SAXException, IOException
	{
		Validator validator;

		if (GPX_1_0_SCHEMA == null)
		{
			throw new IllegalStateException("Unable to load GPX 1.0 schema. Cannot perform validation of imported documents");
		}
		validator = GPX_1_0_SCHEMA.newValidator();

		try
		{
			validator.validate(new StreamSource(f));
			return true;
		}
		catch (final SAXException ex)
		{
			CorePlugin.logError(Status.ERROR, "GPX failed validation. Reason: " + ex.getMessage(), ex);
			CorePlugin.errorDialog("Load GPS File", "GPX failed validation. Reason: " + ex.getMessage());
		}
		return false;
	}
}