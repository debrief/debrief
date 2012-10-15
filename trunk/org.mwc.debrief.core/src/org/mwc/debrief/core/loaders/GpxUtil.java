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
import org.eclipse.jface.dialogs.MessageDialog;
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
			DirectoryDialog dlg = new DirectoryDialog(Display.getDefault().getActiveShell());

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
		catch (SAXException e)
		{
			throw new IllegalStateException("Unable to load GPX schema. Cannot perform validation of imported documents", e);
		}
	}

	public static Source getDocumentSource(InputStream gpxStream) throws JDOMException, IOException
	{
		SAXBuilder builder = new SAXBuilder();
		Document document = builder.build(gpxStream);
		JDOMSource in = new JDOMSource(document);
		return in;
	}

	public static boolean isGpx10(Source source)
	{
		Document document = ((JDOMSource) source).getDocument();
		if ("1.0".equals(document.getRootElement().getAttributeValue("version")))
		{
			return true;
		}
		return false;
	}

	public static boolean isValid(Source source, boolean isGpx10) throws SAXException, IOException
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
		catch (SAXException ex)
		{
			CorePlugin.logError(Status.ERROR, "GPX file trying to import is not valid because " + ex.getMessage(), ex);
			errorDialog("Load GPS File", "GPX failed validation. Reason: " + ex.getMessage());
		}
		return false;
	}

	public static String collectDirecotryPath()
	{
		DirectoryCollector collector = new DirectoryCollector();
		Display.getDefault().syncExec(collector);

		return collector.getSelectedFolder();
	}

	public static void infoDialog(final String title, final String msg)
	{
		Display.getDefault().syncExec(new Runnable()
		{
			@Override
			public void run()
			{
				MessageDialog.openInformation(Display.getDefault().getActiveShell(), title, msg);
			}
		});
	}

	public static void errorDialog(final String title, final String msg)
	{
		Display.getDefault().syncExec(new Runnable()
		{
			@Override
			public void run()
			{
				MessageDialog.openError(Display.getDefault().getActiveShell(), title, msg);
			}
		});
	}

	public static void warningDialog(final String title, final String msg)
	{
		Display.getDefault().syncExec(new Runnable()
		{
			@Override
			public void run()
			{
				MessageDialog.openWarning(Display.getDefault().getActiveShell(), title, msg);
			}
		});
	}

	public static boolean isValid(File f) throws SAXException, IOException
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
			errorDialog("Load GPS File", "GPX failed validation. Reason: " + ex.getMessage());
		}
		return false;
	}
}