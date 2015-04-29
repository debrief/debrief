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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
	 *          file suffix that must match
	 * @param firstLine
	 *           String that the first line of text must start with
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
	public void init(String name, String icon, String fileTypes, String firstLine);

	abstract public static class BaseLoader implements IPlotLoader
	{
		protected String _myName;
		protected String _icon;
		protected String _fileTypes;
		protected String _firstLine;

		public void init(final String name, final String icon,
				final String fileTypes, final String firstLine)
		{
			_myName = name;
			_icon = icon;
			_fileTypes = fileTypes;
			_firstLine = firstLine;
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
			
			// if the suffix matches, see if the first line matches
			if(res)
			{
				if(_firstLine != null)
				{
					// ok, check the first line

					BufferedReader r = null;
					FileInputStream fis = null;
					try
					{
						fis  = new FileInputStream(fileName);
						r = new BufferedReader(new InputStreamReader(fis));
						String firstLine = r.readLine();
						if (firstLine != null && firstLine.contains(_firstLine))
						{
							res = true;
						}
						else
						{
							// just double-check that it's invalid
							res = false;
						}
					}
					catch (Exception e)
					{
						CorePlugin.logError(Status.ERROR, "Trouble whilst verifying first line of content", e);
					}
					finally
					{
						try
						{
							if (r != null)
								r.close();
							if(fis != null)
								fis.close();
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
		 * @param firstLine 
		 * @param regexp
		 */
		public DeferredPlotLoader(final IConfigurationElement configElement,
				final String name, final String icon, final String fileTypes, String firstLine)
		{
			_config = configElement;
			init(name, icon, fileTypes, firstLine);
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
					_myLoader.init(_myName, _icon, _fileTypes, _firstLine);

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
