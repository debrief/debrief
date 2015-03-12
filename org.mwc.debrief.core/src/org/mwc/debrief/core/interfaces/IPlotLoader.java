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
package org.mwc.debrief.core.interfaces;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IEditorInput;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.interfaces.INamedItem;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.editors.PlotEditor;

/**
 * Interface for classes which are capable of populating a plot from a file
 * 
 * @author ian.mayo
 * 
 */
public interface IPlotLoader extends INamedItem
{
	/**
	 * load the supplied editor input into the plot
	 * 
	 * @param thePlot
	 *          the plot destination
	 * @param inputStream
	 *          the file source
	 * @param fileName
	 *          TODO
	 */
	public void loadFile(final PlotEditor thePlot, final InputStream inputStream,
			final String fileName);

	/**
	 * test whether this loader can load the suppled input source
	 * 
	 * @param fileName
	 *          the input file to check
	 * @return yes/no
	 */
	public boolean canLoad(String fileName);

	/**
	 * utility method to initialise this loader - we need to do this since when we
	 * use these objects as plugins Eclipse has to call the zero-argument
	 * constructor (and we supply the data with these methods)
	 * 
	 * @param name
	 * @param icon
	 * @param fileTypes
	 */
	public void init(String name, String icon, String fileTypes, String regexp);

	abstract public static class BaseLoader implements IPlotLoader
	{
		protected String _myName;
		protected String _icon;
		protected String _fileTypes;
		protected String _regexp;

		public void init(final String name, final String icon,
				final String fileTypes, String regexp)
		{
			_myName = name;
			_icon = icon;
			_fileTypes = fileTypes;
			_regexp = regexp;
		}

		public final String getName()
		{
			return _myName;
		}

		public String getFileName(final IEditorInput input)
		{
			String res = null;

			res = "c:\\boat1.rep";

			return res;
		}

		/**
		 * test whether this loader can load the suppled input source
		 * 
		 * @param fileName
		 *          the input file to check
		 * @return yes/no
		 */
		public boolean canLoad(final String fileName)
		{
			boolean res = false;
			// now pass through our list
			final String[] mySuffixes = _fileTypes.split(";");
			for (int i = 0; i < mySuffixes.length; i++)
			{
				final String mySuffix = mySuffixes[i];
				if (fileName.toUpperCase().endsWith(mySuffix.toUpperCase()))
				{
					res = true;
				}
			}

			// do we have a regexp?
			if (res)
			{
				if (_regexp != null)
				{
					// ok, have a look at the first line

					// hmm, is there anything in the file?
					BufferedReader r = null;
					try
					{
						r = new BufferedReader(new FileReader(fileName));

						// ok, have a look at the first line
						String firstLine = r.readLine();

						// ok, see if it matches our example.
						res = firstLine.matches(_regexp);
						
						// report error
						CorePlugin.logError(Status.INFO, "File didn't match regexp for " + _myName, null);
					}
					catch (FileNotFoundException e)
					{
						CorePlugin.logError(Status.ERROR, "Can't find AIS file", e);
						res = false;
					}
					catch (IOException e)
					{
						CorePlugin.logError(Status.ERROR,
								"Can't read first line of AIS file", e);
						res = false;
					}
					finally
					{
						try
						{
							if (r != null)
								r.close();
						}
						catch (IOException e)
						{
							CorePlugin.logError(Status.ERROR, "Couldn't close file file", e);
						}
					}

				}
			}

			return res;
		}

	}

	public static class DeferredPlotLoader extends BaseLoader
	{
		final IConfigurationElement _config;

		BaseLoader _myLoader = null;

		/**
		 * constructor - stores the information necessary to load the data
		 * 
		 * @param configElement
		 * @param name
		 * @param icon
		 * @param fileTypes
		 * @param regexp
		 */
		public DeferredPlotLoader(final IConfigurationElement configElement,
				final String name, final String icon, final String fileTypes,
				String regexp)
		{
			_config = configElement;
			init(name, icon, fileTypes, regexp);
		}

		public BaseLoader getLoader()
		{
			return _myLoader;
		}

		public void loadFile(final PlotEditor thePlot,
				final InputStream inputStream, final String fileName)
		{
			if (_myLoader == null)
			{
				try
				{
					System.out.println("About to load new loader for:" + getName());

					// and create the loader
					_myLoader = (BaseLoader) _config.createExecutableExtension("class");

					// hey, stick the data in
					_myLoader.init(_myName, _icon, _fileTypes, _regexp);

				}
				catch (final CoreException e)
				{
					DebriefPlugin.logError(Status.ERROR,
							"Failed to create instance of loader:" + _config, e);

				}
			}

			if (_myLoader != null)
			{
				// we either had it already, or we're trying to load it now. go for it
				_myLoader.loadFile(thePlot, inputStream, fileName);
			}
			else
			{
				DebriefPlugin.logError(Status.ERROR,
						"Unable to load file. Loader unavailable for:" + _config, null);

			}

		}

	}
}
