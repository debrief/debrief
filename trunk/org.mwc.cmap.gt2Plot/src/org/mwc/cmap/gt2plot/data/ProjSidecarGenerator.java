package org.mwc.cmap.gt2plot.data;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class ProjSidecarGenerator
{

	public String inFolder;

	public String pRegex = null;

	public String pCode;

	public String outCurrentfile = null;

	public List<File> filesList = null;

	public List<String> pathsList = null;

	private int fileIndex = 0;

	private String prjWkt;

	public void process() throws FactoryException, IOException 
	{
		if (pCode != null)
		{
			CoordinateReferenceSystem crs = CRS.decode(pCode);
			prjWkt = crs.toWKT();
		}

		if (filesList == null)
		{
			filesList = new ArrayList<File>();
			pathsList = new ArrayList<String>();

			new FileTraversal()
			{
				public void onFile(final File f)
				{
					if (f.getName().endsWith("tif")) { //$NON-NLS-1$//$NON-NLS-2$
						filesList.add(f);
						pathsList.add(f.getAbsolutePath());
					}
				}
			}.traverse(new File(inFolder));

			if (prjWkt != null)
			{
				for (File file : filesList)
				{
					String nameWithoutExtention = FileUtilities
							.getNameWithoutExtention(file);
					File prjFile = new File(file.getParentFile(), nameWithoutExtention
							+ ".prj"); //$NON-NLS-1$
					if (!prjFile.exists())
					{
						// create it
						FileUtilities.writeFile(prjWkt, prjFile);
					}
				}
			}

		}

		outCurrentfile = filesList.get(fileIndex).getAbsolutePath();

		fileIndex++;

	}

	/**
	 * Utility to add to all found files in a given folder the prj file following
	 * the supplied epsg.
	 * 
	 * @param folder
	 *          the folder to browse.
	 * @param epsg
	 *          the epsg from which to take the prj.
	 * @throws IOException 
	 * @throws FactoryException 
	 */
	public static void addPrj(String folder, String epsg) throws FactoryException, IOException
	{
		ProjSidecarGenerator fiter = new ProjSidecarGenerator();
		fiter.inFolder = folder;
		fiter.pCode = epsg;
		fiter.process();
	}

	public static class FileTraversal
	{
		public final void traverse(final File f) throws IOException
		{
			if (f.isDirectory())
			{
				onDirectory(f);
				final File[] childs = f.listFiles();
				for (File child : childs)
				{
					traverse(child);
				}
				return;
			}
			onFile(f);
		}

		public void onDirectory(final File d)
		{
		}

		public void onFile(final File f)
		{
		}
	}
}