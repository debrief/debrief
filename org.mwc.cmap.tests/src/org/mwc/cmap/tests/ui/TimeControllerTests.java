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
package org.mwc.cmap.tests.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.mwc.cmap.TimeController.controls.DTGBiSlider;
import org.mwc.cmap.TimeController.views.TimeController;
import org.mwc.cmap.tests.Activator;
import org.osgi.framework.Bundle;

public class TimeControllerTests extends TestCase
{

	private static final String VIEW_ID = "org.mwc.cmap.TimeController.views.TimeController";
	private TimeController _myController;
	private IProject project;
	
	public TimeControllerTests(final String testName)
	{
		super(testName);
	}
	


  /**
   * Perform pre-test initialization
   *
   * @throws Exception
   *
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception {
     super.setUp();

     // initialize the test fixture for each test that is run
     project = createSampleProject();

     openSampleFile(project);
     _myController =
        (TimeController) PlatformUI
           .getWorkbench()
           .getActiveWorkbenchWindow()
           .getActivePage()
           .showView(VIEW_ID);

     // Delay for 3 seconds so that 
     // the favorites view can be seen
     waitForJobs();
     //delay(3000);
  }

  /**
   * Perform post-test clean up
   *
   * @throws Exception
   *
   * @see TestCase#tearDown()
   */
	protected void tearDown() throws Exception
	{
		super.tearDown();

		// Dispose of the test fixture
		waitForJobs();
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage();
		page.closeAllEditors(false);
		waitForJobs();
		page.hideView(_myController);
		try
		{
			project.delete(true, true, null);
		} catch (Exception e)
		{
			Activator.log(e);
		}
	}

  /**
   * Run the view test
   */
  public void testView() {
  	
  	final DTGBiSlider periodSlider = _myController.getPeriodSlider();
  	
  	// did we find it?
  	assertNotNull("haven't found the slider", periodSlider);
  	
  	// ok, what else can we do?
  	final Scale timeSlider = _myController.getTimeSlider();

  	// did we find it?
  	assertNotNull("haven't found the time slider", timeSlider);
  	
  	// hey, are we looking at any data?
  	_myController.doTests();
  
  }

  /**
   * Process UI input but do not return for the specified time interval.
   * 
   * @param waitTimeMillis the number of milliseconds 
   */
  protected static void delay(final long waitTimeMillis) {
     final Display display = Display.getCurrent();

     // If this is the user interface thread, then process input
     if (display != null) {
        final long endTimeMillis =
           System.currentTimeMillis() + waitTimeMillis;
        while (System.currentTimeMillis()
           < endTimeMillis) {
           if (!display.readAndDispatch())
              display.sleep();
        }
        display.update();
     }

     // Otherwise perform a simple sleep
     else {
        try {
           Thread.sleep(waitTimeMillis);
        }
        catch (final InterruptedException e) {
           // ignored
        }
     }
  }
	
  /**
   * Wait until all background tasks are complete
   */
	public void waitForJobs()
	{
		waitForJobs(60 * 60 * 1000);
	}

	public static void waitForJobs(long maxIdle)
	{
		long start = System.currentTimeMillis();
		while (!Job.getJobManager().isIdle())
		{
			delay(1000);
			if ((System.currentTimeMillis() - start) > maxIdle)
			{
				Job[] jobs = Job.getJobManager().find(null);
				StringBuffer buffer = new StringBuffer();
				for (Job job : jobs)
				{
					if (job.getThread() != null)
					{
						buffer.append(job.getName()).append(" (").append(job.getClass())
								.append(")\n");
					}
				}
				if (buffer.length() > 0)
					throw new RuntimeException("Invalid jobs found:" + buffer.toString()); //$NON-NLS-1$
			}
		}
	}

	public static boolean extract(File file, File destination,
			IProgressMonitor monitor)
	{
		ZipFile zipFile = null;
		destination.mkdirs();
		try
		{
			zipFile = new ZipFile(file);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements())
			{
				if (monitor.isCanceled())
				{
					return false;
				}
				ZipEntry entry = (ZipEntry) entries.nextElement();
				if (entry.isDirectory())
				{
					monitor.setTaskName("Extracting " + entry.getName());
					File dir = new File(destination, entry.getName());
					dir.mkdirs();
					continue;
				}
				monitor.setTaskName("Extracting " + entry.getName());
				File entryFile = new File(destination, entry.getName());
				entryFile.getParentFile().mkdirs();
				InputStream input = null;
				OutputStream output = null;
				try
				{
					input = zipFile.getInputStream(entry);
					output = new FileOutputStream(entryFile);
					copyFile(input, output);
				} finally
				{
					if (input != null)
					{
						try
						{
							input.close();
						} catch (Exception e)
						{
						}
					}
					if (output != null)
					{
						try
						{
							output.close();
						} catch (Exception e)
						{
						}
					}
				}
			}
		} catch (IOException e)
		{
			Activator.log(e);
			return false;
		} finally
		{
			if (zipFile != null)
			{
				try
				{
					zipFile.close();
				} catch (IOException e)
				{
					// ignore
				}
			}
		}
		return true;
	}

	public static void copyFile(InputStream in, OutputStream out)
			throws IOException
	{
		byte[] buffer = new byte[16 * 1024];
		int len;
		while ((len = in.read(buffer)) >= 0)
		{
			out.write(buffer, 0, len);
		}
	}  	

	private void openSampleFile(IProject project) throws PartInitException
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage();
		IFile sample = project.getFile("/sample.xml");
		assertTrue(sample.exists());
		IDE.openEditor(page, sample);
	}

	private IProject createSampleProject() throws IOException, CoreException
	{
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		String projectName = "cmapProject";
		IProjectDescription description = workspace
				.newProjectDescription(projectName);
		Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
		String zipLocation = FileLocator.resolve(
				bundle.getEntry("projects/cmapProject.zip")).getFile();
		File file = new File(zipLocation);
		String dest = Platform.getConfigurationLocation().getURL().getFile();
		File destination = new File(dest);
		extract(file, destination, new NullProgressMonitor());
		description.setName(projectName);
		description.setLocation(new Path(dest).append(projectName));
		IProject project = workspace.getRoot().getProject(projectName);
		project.create(description, null);
		project.open(null);
		return project;
	}

}
