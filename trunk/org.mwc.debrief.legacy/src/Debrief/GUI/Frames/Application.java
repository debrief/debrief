// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: Application.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.9 $
// $Log: Application.java,v $
// Revision 1.9  2005/09/08 11:00:26  Ian.Mayo
// Allow provision for over-riding the tool-parent in the Application, so we can redirect calls to our CMAP prefs store
//
// Revision 1.8  2005/09/08 08:36:13  Ian.Mayo
// Add error logging
//
// Revision 1.7  2005/05/31 13:23:06  Ian.Mayo
// Only fire "app props must be initialised" once
//
// Revision 1.6  2004/12/06 09:07:27  Ian.Mayo
// Reflect changed signature of XML Reader Writer
//
// Revision 1.5  2004/11/22 13:40:47  Ian.Mayo
// Replace old variable name used for stepping through enumeration, since it is now part of language (Jdk1.5)
//
// Revision 1.4  2004/08/09 08:56:55  Ian.Mayo
// Add default plot command to File menu
//
// Revision 1.3  2004/07/23 09:19:38  Ian.Mayo
// IntelliJ tidying
//
// Revision 1.2  2003/08/12 09:28:24  Ian.Mayo
// Include import of DTF files
//
// Revision 1.1.1.2  2003/07/21 14:47:04  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.10  2003-07-03 14:26:14+01  ian_mayo
// New application name
//
// Revision 1.9  2003-03-19 15:38:08+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.8  2003-03-12 13:39:38+00  ian_mayo
// Switch to new version (2003)
//
// Revision 1.7  2003-02-25 14:36:40+00  ian_mayo
// Change version to D2003 Beta
//
// Revision 1.6  2002-10-01 15:39:49+01  ian_mayo
// remove unused variables
//
// Revision 1.5  2002-07-23 08:48:04+01  ian_mayo
// No longer a beta
//
// Revision 1.4  2002-05-29 10:06:05+01  ian_mayo
// Update year
//
// Revision 1.3  2002-05-28 12:27:49+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:21+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-05-08 14:39:20+01  ian_mayo
// Improve the cursor feedback when opening files from the MRU
//
// Revision 1.1  2002-04-23 12:30:11+01  ian_mayo
// Initial revision
//
// Revision 1.15  2002-03-13 08:58:21+00  administrator
// Handle occasion when singleton not initialised (during JUnit testing)
//
// Revision 1.14  2002-01-22 15:26:36+00  administrator
// Refactor session name generation into its own method
//
// Revision 1.13  2002-01-22 09:11:09+00  administrator
// Use Trace instead of d-line for file-type not handled
//
// Revision 1.12  2001-11-14 19:38:08+00  administrator
// switch to new XML(SAX2) reader structure
//
// Revision 1.11  2001-10-29 11:28:51+00  administrator
// Remove BETA status
//
// Revision 1.10  2001-10-08 17:12:38+01  administrator
// Comment out cut/copy/paste buttons
//
// Revision 1.9  2001-08-24 09:54:53+01  administrator
// Remove year 2000 text strings
//
// Revision 1.8  2001-08-21 15:18:45+01  administrator
// Allow use of dsf suffix, and ignore case of filenames
//
// Revision 1.7  2001-08-17 07:59:42+01  administrator
// Clear up memory leaks
//
// Revision 1.6  2001-08-14 14:05:02+01  administrator
// reset it to BETA status
//
// Revision 1.5  2001-08-06 12:50:35+01  administrator
// Switch to using our threaded importer
//
// Revision 1.4  2001-07-30 15:43:53+01  administrator
// Take the application name from our local property
//
// Revision 1.3  2001-07-30 15:40:02+01  administrator
// Denote as BETA software
//
// Revision 1.2  2001-07-20 10:36:09+01  administrator
// Add getName method to automate setting the name in the application frame
//
// Revision 1.1  2001-07-19 08:47:28+01  administrator
// add method to find properties by pattern
//
// Revision 1.0  2001-07-17 08:41:43+01  administrator
// Initial revision
//
// Revision 1.4  2001-07-09 14:08:05+01  novatech
// After we have read in data, pass through layers updating the StepControl in any Narratives
//
// Revision 1.3  2001-06-14 15:35:12+01  novatech
// add new tool parameter to show which tabbed toolbar we want the information on
//
// Revision 1.2  2001-01-17 09:48:23+00  novatech
// add auto-filled javadoc comments
//
// Revision 1.1  2001-01-03 13:40:56+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:44:56  ianmayo
// initial import of files
//
// Revision 1.17  2000-12-01 10:20:48+00  ian_mayo
// removed duff code (old MRU)
//
// Revision 1.16  2000-11-24 10:54:52+00  ian_mayo
// switch to XML and tidy up in general
//
// Revision 1.15  2000-11-17 09:11:54+00  ian_mayo
// include XML support
//
// Revision 1.14  2000-10-10 13:17:35+01  ian_mayo
// better busy cursors for drag/drop
//
// Revision 1.13  2000-10-10 12:21:15+01  ian_mayo
// provide support for drag & drop
//
// Revision 1.12  2000-10-09 13:37:49+01  ian_mayo
// Switch stackTrace to go to file
//
// Revision 1.11  2000-09-27 15:38:30+01  ian_mayo
// provide correct support for canClose() methods
//
// Revision 1.10  2000-09-21 12:23:56+01  ian_mayo
// create header line for top of Debrief properties file
//
// Revision 1.9  2000-08-30 14:50:14+01  ian_mayo
// implementing MRU
//
// Revision 1.8  2000-04-19 11:36:22+01  ian_mayo
// remove ImportReplay button
//
// Revision 1.7  2000-02-25 09:07:27+00  ian_mayo
// Pass ReplayImporter to ImportManager
//
// Revision 1.6  2000-02-22 13:44:26+00  ian_mayo
// Put Replay importer object into ImportManager
//
// Revision 1.5  2000-02-15 15:51:54+00  ian_mayo
// don't create unique clipboards
//
// Revision 1.4  2000-01-20 10:07:08+00  ian_mayo
// added application-wide clipboard
//
// Revision 1.3  1999-12-03 14:38:12+00  ian_mayo
// add keyboard shortcuts & mnemonics
//
// Revision 1.2  1999-11-25 16:55:09+00  ian_mayo
// added shortcut for exit
//
// Revision 1.1  1999-10-12 15:34:26+01  ian_mayo
// Initial revision
//
// Revision 1.4  1999-08-09 13:36:11+01  administrator
// tidy code formatting (Code companion rules)
//
// Revision 1.3  1999-07-27 09:27:46+01  administrator
// general improvements
//
// Revision 1.2  1999-07-16 10:01:51+01  administrator
// Nearing end of phase 2
//
// Revision 1.1  1999-07-07 11:10:20+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:09+01  sm11td
// Initial revision
//
// Revision 1.2  1999-06-16 15:34:43+01  sm11td
// <>
//
// Revision 1.1  1999-06-16 15:34:00+01  sm11td
// Initial revision
//
// Revision 1.4  1999-02-04 08:02:23+00  sm11td
// Plotting to canvas, scaling canvas,
//
// Revision 1.3  1999-02-01 16:08:46+00  sm11td
// creating new sessions & panes, starting import management
//
// Revision 1.2  1999-02-01 14:24:59+00  sm11td
// Skeleton there, opening new sessions, window management.
//
// Revision 1.1  1999-01-31 13:32:57+00  sm11td
// Initial revision
//

package Debrief.GUI.Frames;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import Debrief.Tools.Operations.*;
import MWC.GUI.*;
import MWC.GUI.DragDrop.FileDropSupport;
import MWC.GUI.Tools.*;
import MWC.Utilities.Errors.Trace;

public abstract class Application implements ToolParent, ActionListener,
		FileDropSupport.FileDropListener
{

	// ///////////////////////////////////////////////////////////
	// member variables
	// //////////////////////////////////////////////////////////

	/**
	 * the filename for the help for this application
	 */
	private final String HELP_FILENAME = "d2003.chm";

	/**
	 * store a list of tools we use
	 */
	private final Vector _theTools;

	private final Vector _theMenus;

	private final java.util.Vector _theSessions;

	private Toolbar _theToolbar;

	private final Clipboard _theClipboard;

	private int _sessionCounter;

	protected MWC.GUI.Dialogs.MruMenuManager _mru;

	protected MWC.GUI.Dialogs.ApplicationProperties _appProps;

	private static ToolParent _substituteParent = null;

	/**
	 * keep track of whether we have provided our "app props missing" report. we
	 * only need to report it once.
	 */
	private static boolean PROPS_WARNING_ISSUED = false;

	private static Application _singleton; // local copy of ourselves

	/**
	 * the name of this application
	 */
	private final String _myName = "Debrief 2003";

	/**
	 * handle drag and drop for this item
	 */
	protected final FileDropSupport _dropSupport = new FileDropSupport();

	// ///////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////

	public Application()
	{
		_theSessions = new Vector(0, 1);
		_theTools = new Vector(0, 1);
		_theMenus = new Vector(0, 1);
		_sessionCounter = 0;

		// create unique name for clipboard
		_theClipboard = new Clipboard("Debrief");

		// set the importer in the libary
		MWC.Utilities.ReaderWriter.ImportManager
				.addImporter(new Debrief.ReaderWriter.Replay.ImportReplay());

		// add the XML importer
		MWC.Utilities.ReaderWriter.ImportManager
				.addImporter(new Debrief.ReaderWriter.XML.DebriefXMLReaderWriter(this));

		try
		{
			final String header = System.getProperty("line.separator")
					+ "#Debrief 2003 Properties File";
			_appProps = new MWC.GUI.Dialogs.ApplicationProperties("d2ksettings.prp",
					header);
		}
		catch (IOException e)
		{
			MWC.Utilities.Errors.Trace.trace(e);
		}

		_singleton = this;

		// create drag/drop support for this class, so that the user can
		// drop .rep files onto the plto
		_dropSupport.setFileDropListener(this, " .REP, .XML, .DSF, .DTF");

	}

	protected final void completeInitialisation()
	{
		if (_mru != null)
			_mru.addActionListener(this);
	}

	// ///////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////

	/**
	 * allow us to override the toolparent responsibilities of the application
	 * (particularly in CMAP);
	 */
	public static void initialise(ToolParent newParent)
	{
		_substituteParent = newParent;
	}

	protected final String getName()
	{
		return _myName;
	}

	public final Clipboard getClipboard()
	{
		return _theClipboard;
	}

	/**
	 * add the specified session to the current 'workspace'
	 * 
	 * @param theSession
	 *          autofilled
	 */
	public void newSession(final Session theSession)
	{
		// store this session
		_theSessions.addElement(theSession);
		// check if the session needs naming
		if (theSession.getName() == null)
		{
			theSession.setName(getNewSessionName());
		}
	}

	/**
	 * get a name for a new session
	 */
	protected final String getNewSessionName()
	{
		return "Session " + _sessionCounter++;
	}

	/**
	 * create a new session, BUT DON'T add it to the current application
	 */
	public abstract Session createSession();

	/**
	 * add the set of tools we normally use for an application (menu and top
	 * toolbar)
	 */
	private void addTools()
	{
		_theMenus.addElement(new MenuItemInfo("File", null, "New", new NewSession(
				this, false), new MenuShortcut(KeyEvent.VK_N), ' '));
		_theMenus.addElement(new MenuItemInfo("File", null, "New (default plot)",
				new NewSession(this, true), new MenuShortcut(KeyEvent.VK_D), ' '));

		_theMenus.addElement(new MenuItemInfo("File", null, null, null, null, ' '));
		_theMenus
				.addElement(new MenuItemInfo("File", null, "Open Plot",
						new OpenPlotXML(this, null, this), new MenuShortcut(KeyEvent.VK_O),
						' '));

		_theMenus.addElement(new MenuItemInfo("File", null, null, null, null, ' '));
		_theMenus
				.addElement(new MenuItemInfo("File", null, "Import Replay",
						new ImportData2(this, this, null), new MenuShortcut(KeyEvent.VK_M),
						' '));

		_theMenus.addElement(new MenuItemInfo("File", null, null, null, null, ' '));
		// this gap is for our MRU items
		_theMenus.addElement(new MenuItemInfo("File", null, null, null, null, ' '));
		_theMenus.addElement(new MenuItemInfo("File", null, "Close Session",
				new CloseSession(this), new MenuShortcut(KeyEvent.VK_F4), ' '));

		_theMenus.addElement(new MenuItemInfo("File", null, "Exit",
				new ExitApplication(this), new MenuShortcut(KeyEvent.VK_X), ' '));

		_theMenus.addElement(new MenuItemInfo("Edit", null, "Undo", new Undo(this),
				new MenuShortcut(KeyEvent.VK_Z), ' '));
		_theMenus.addElement(new MenuItemInfo("Edit", null, "Redo", new Redo(this),
				new MenuShortcut(KeyEvent.VK_Y), ' '));
		/*
		 * hey, cut/copy/paste don't do anything! _theMenus.addElement(new
		 * MenuItemInfo("Edit", null, "Cut", null, null, ' '));
		 * _theMenus.addElement(new MenuItemInfo("Edit", null, "Copy", null, null, '
		 * ')); _theMenus.addElement(new MenuItemInfo("Edit", null, "Paste", null,
		 * null, ' '));
		 */

		_theTools.addElement(new MenuItemInfo("File", null, "New", new NewSession(
				this), null, ' '));
		_theTools.addElement(new MenuItemInfo("File", null, "Open",
				new OpenPlotXML(this, null, this), null, ' '));
		_theTools.addElement(new MenuItemInfo("File", null, "Close Session",
				new CloseSession(this), null, ' '));
		_theTools.addElement(new MenuItemInfo("File", null, "Exit",
				new ExitApplication(this), new MenuShortcut(KeyEvent.VK_X), ' '));

	}

	/**
	 * do both of the GUI actions
	 */
	protected final void buildTheInterface()
	{
		fillToolbar();
		setupMenu();
	}

	/**
	 * show the contents page from help
	 */
	protected final void helpContents()
	{
		try
		{
			final Process p = Runtime.getRuntime().exec("hh " + HELP_FILENAME);

			// insert check to make sure help process started.
			// - really though, this is just to stop the Eclipse warning over the p
			// not being called locally
			if (p == null)
			{
				Trace.trace("Faled to start help process");
			}
		}
		catch (Exception e)
		{
			MWC.Utilities.Errors.Trace.trace(e);
		}
	}

	/**
	 * return the session currently at the top of the stack
	 * 
	 * @return the current session
	 */
	public abstract Session getCurrentSession();

	/**
	 * @param theName
	 *          autofilled
	 */
	protected final Session getSessionNamed(final String theName)
	{
		Session res = null;
		final Enumeration iter = _theSessions.elements();
		while (iter.hasMoreElements())
		{
			final Session thisSess = (Session) iter.nextElement();
			if (thisSess.getName().equals(theName))
			{
				res = thisSess;
				break;
			}
		}
		return res;
	}

	/**
	 * @param theBar
	 *          autofilled
	 */
	protected final void setToolbar(final Toolbar theBar)
	{
		// store this locally
		_theToolbar = theBar;

	}

	/**
	 * go through the list of tool buttons we have defined, and create the GUI
	 * element for each
	 */
	private void fillToolbar()
	{

		// add our normal 'default' toolset
		addTools();

		// now add the tools to the toolbar
		final Enumeration iter = _theTools.elements();

		while (iter.hasMoreElements())
		{
			final MenuItemInfo thisItem = (MenuItemInfo) iter.nextElement();

			_theToolbar.addTool(thisItem.getTool(), thisItem.getShortCut(), thisItem
					.getMnemonic());
		}
	}

	/**
	 * go through the list of menu elements we have defined, and create the GUI
	 * element for each
	 */
	private void setupMenu()
	{
		final Enumeration iter = _theMenus.elements();

		while (iter.hasMoreElements())
		{
			final MenuItemInfo thisItem = (MenuItemInfo) iter.nextElement();
			// see if it is a menu item or a separator
			if (thisItem.getMenuItemName() == null)
			{
				addMenuSeparator(thisItem.getMenuName());
			}
			else
			{
				addMenuItem(thisItem.getMenuName(), thisItem.getMenuItemName(),
						thisItem.getTool(), thisItem.getShortCut());
			}
		}
	}

	/**
	 * set the title bar text to parameter
	 * 
	 * @param theStr
	 *          to assign to title bar of frame
	 * @param theStr
	 *          autofilled
	 */
	protected abstract void setTitleName(final String theStr);

	/**
	 * create a new menu item
	 * 
	 * @param theMenu
	 *          the name of the menu to add the item to
	 * @param theLabel
	 *          the text to use as the menu item
	 * @param theTool
	 *          callback function for menu item to trigger
	 * @param theShortCut
	 *          shortcut to be applied to the menu item
	 */
	protected abstract void addMenuItem(final String theMenu,
			final String theLabel, final Tool theTool, final MenuShortcut theShortCut);

	/**
	 * create a menu separator
	 * 
	 * @param theMenu
	 *          the menu to insert the separator into
	 */
	protected abstract void addMenuSeparator(final String theMenu);

	/**
	 * exit the application
	 */
	public final void exit()
	{
		// save the MRU
		_mru.storeMruItems();
		try
		{
			_appProps.storeProperties();
		}
		catch (IOException e)
		{
			MWC.Utilities.Errors.Trace.trace(e);
		}

		// clear the file drop listener
		_dropSupport.removeFileDropListener(this);

		System.exit(0);
	}

	/**
	 * bring the specified session to the front of the others
	 * 
	 * @param theSession
	 *          autofilled
	 */
	protected abstract void showSession(Session theSession);

	/**
	 * try close the specified session
	 * 
	 * @param theSession
	 *          autofilled
	 */
	public final boolean closeSession(Session theSession)
	{

		// check the session is valid
		if (theSession != null)
		{

			boolean canClose = true;
			canClose = theSession.close();

			if (canClose)
			{

				// remove the GUI from our desktop
				closeSessionGUI(theSession);

				theSession.closeGUI();

				// and close the object itself
				// remove from Sessions list
				_theSessions.removeElement(theSession);

				theSession = null;

				// now show another session, if we have one
				if (_theSessions.size() > 0)
				{
					showSession((Session) _theSessions.elementAt(_theSessions.size() - 1));
				}
				else
				{
					// we haven't got any more sessions, so just display the default bar
					setTitleName("");
				}

				// also perform the garbage collection
				System.runFinalization();
				System.gc();

			}
		}
		return true;

	}

	/**
	 * close the specified session
	 * 
	 * @param theSession
	 *          the Session to close
	 */
	protected abstract void closeSessionGUI(final Session theSession);

	/**
	 * change the default cursor
	 * 
	 * @param theCursor
	 *          autofilled
	 */
	public abstract void setCursor(final int theCursor);

	/**
	 * restore the previous cursor
	 */
	public abstract void restoreCursor();

	/**
	 * @param theAction
	 *          autofilled
	 */
	public final void addActionToBuffer(final Action theAction)
	{
		// check we have a valid session running
		final Session se = getCurrentSession();
		if (se != null)
			se.getUndoBuffer().add(theAction);
	}

	/**
	 * @param theFile
	 *          the file to open
	 */
	public final void openFile(final File theFile)
	{
		try
		{

			// indicate that we are busy
			this.setCursor(Cursor.WAIT_CURSOR);

			Layers newLayers = null;

			// wrap the single file into a list
			final File[] fList = new File[] { theFile };

			final String suff = suffixOf(theFile.getName());
			if (suff.equalsIgnoreCase(".DPL"))
			{
				MWC.GUI.Dialogs.DialogFactory.showMessage("Open File",
						"Sorry DPL file format no longer supported");
			}
			else
			{
				if ((suff.equalsIgnoreCase(".REP")) || (suff.equalsIgnoreCase(".DSF"))
						|| (suff.equalsIgnoreCase(".DTF")))
				{
					newLayers = new Layers();

					MWC.Utilities.ReaderWriter.ImportManager.BaseImportCaller caller = new MWC.Utilities.ReaderWriter.ImportManager.BaseImportCaller(
							fList, newLayers)
					{
						// handle the completion of each file
						public void fileFinished(final File fName, final Layers newData)
						{
							addToMru(fName.getPath());
						}

						// handle completion of the full import process
						public void allFilesFinished(final File[] fNames,
								final Layers newData)
						{
							// create a new session, to hold this track
							newSession(null);
							final Session sess = getCurrentSession();

							// did it work?
							if (sess != null)
							{
								sess.getData().addThis(newData);
								sess.getData().fireExtended();
							}

							// finally configure the narratives
							configureNarratives();

							// put the cursor back to normal
							restoreCursor();

						}
					};

					caller.start();
					caller = null;

					// forget the reference
					newLayers = null;

				}
				else if (suff.equalsIgnoreCase(".XML"))
				{

					MWC.Utilities.ReaderWriter.ImportManager.BaseImportCaller caller = new MWC.Utilities.ReaderWriter.ImportManager.BaseImportCaller(
							fList, null)
					{
						// handle the completion of each file
						public void fileFinished(final File fName, final Layers newData)
						{
							addToMru(fName.getPath());
						}

						// handle completion of the full import process
						public void allFilesFinished(final File[] fNames,
								final Layers newData)
						{
							configureNarratives();

							// put the cursor back to normal
							restoreCursor();

						}
					};

					caller.start();

					caller = null;

				}
				else
				{
					MWC.Utilities.Errors.Trace
							.trace("This file type not handled:" + suff);
				}
			}
		}
		finally
		{
		}
	}

	/**
	 * helper method which gets called after we've read in a data file
	 */
	private void configureNarratives()
	{
		// the narratives need to know where the time stepper is, so inform them if
		// we have any
		// try to set the pointer to the TimeStepper in the narratives, if there are
		// any
		final Session newSession = this.getCurrentSession();
		if (newSession != null)
		{
			final Layers theData = newSession.getData();
			final Debrief.GUI.Views.PlainView pv = newSession.getCurrentView();
			if (pv instanceof Debrief.GUI.Views.AnalysisView)
			{
				final Debrief.GUI.Views.AnalysisView av = (Debrief.GUI.Views.AnalysisView) pv;
				final Debrief.GUI.Tote.StepControl stepper = av.getTote().getStepper();
				final int len = theData.size();
				for (int i = 0; i < len; i++)
				{
					final Layer ly = theData.elementAt(i);
					if (ly instanceof Debrief.Wrappers.NarrativeWrapper)
					{
						final Debrief.Wrappers.NarrativeWrapper nw = (Debrief.Wrappers.NarrativeWrapper) ly;
						nw.setStepper(stepper);
					} // whether this is a narrative
				} // through the layers
			} // whether this is an analysis view
		} // if we managed to create a session
	}

	/**
	 * @param fName
	 *          autofilled
	 */
	public static void addToMru(final String fName)
	{
		if (_singleton != null)
			_singleton._mru.addMruItem(fName);
	}

	/**
	 * handle events fired from the MRU menu
	 * 
	 * @param p1
	 *          autofilled
	 */
	public final void actionPerformed(final java.awt.event.ActionEvent p1)
	{
		// get the filename
		final String fName = p1.getActionCommand();

		final java.io.File theFile = new java.io.File(fName);
		openFile(theFile);
		/*
		 * // sort out the suffix of the file String theSuffix=null; int pos =
		 * fName.lastIndexOf("."); theSuffix = fName.substring(pos, fName.length());
		 * if(theSuffix.equalsIgnoreCase (".xml")) { final Action op = new
		 * OpenPlot.OpenPlotAction(fName, this); op.execute(); }
		 */

	}

	/**
	 * get the value of this named property
	 * 
	 * @param name
	 *          autofilled
	 */
	public final String getProperty(final String name)
	{
		String res = null;
		if(_substituteParent != null)
			 res = _substituteParent.getProperty(name);
		else
			res = _appProps.getProperty(name);
			
		return res;
	}

	/**
	 * get the values of any properties like this pattern
	 */
	public static java.util.Map getPropertiesLikeThis(final String pattern)
	{
		Map res = null;
		if (_substituteParent != null)
		{
			res = _substituteParent.getPropertiesLike(pattern);
		}
		else
		{
			final Application app = _singleton;
			final MWC.GUI.Dialogs.ApplicationProperties apps = app._appProps;
			res = apps.getPropertiesLike(pattern);
		}
		return res;
	}

	/**
	 * get the values of any properties like this pattern
	 */
	public final java.util.Map getPropertiesLike(final String pattern)
	{
		final Map res;
		if(_substituteParent != null)
			res = _substituteParent.getPropertiesLike(pattern);
		else
			res = _appProps .getPropertiesLike(pattern);
			
		return res;
	}

	/**
	 * STATIC METHOD for getting a propertu
	 * 
	 * @param name
	 *          autofilled
	 */
	public static String getThisProperty(final String name)
	{
		String res = null;
		if(_substituteParent != null)
		{
			res = _substituteParent.getProperty(name);
		}
		else
		{
			final Application app = _singleton;
			if (app == null)
			{
				if (!PROPS_WARNING_ISSUED)
				{
					System.out.println("APPLICATION PROPERTIES NOT INITIALISED!");
					PROPS_WARNING_ISSUED = true;
				}
				return null;
			}
			final MWC.GUI.Dialogs.ApplicationProperties apps = app._appProps;
			res = apps.getProperty(name);
			
		}
		return res;
	}

	/**
	 * store this value in the named property
	 * 
	 * @param name
	 *          autofilled
	 * @param value
	 *          autofilled
	 */
	public final void setProperty(final String name, final String value)
	{
		_appProps.setProperty(name, value);
	}

	/**
	 * @param files
	 *          autofilled
	 */
	public final void FilesReceived(final java.util.Vector files)
	{
		setCursor(Cursor.WAIT_CURSOR);
		try
		{
			final Enumeration iter = files.elements();
			while (iter.hasMoreElements())
			{
				final java.io.File file = (java.io.File) iter.nextElement();
				openFile(file);
			}

		}
		catch (Exception e)
		{
			MWC.Utilities.Errors.Trace.trace(e);
		}

		restoreCursor();
	}

	/**
	 * @param filename
	 *          autofilled
	 */
	private static String suffixOf(final String filename)
	{
		String theSuffix = null;
		final int pos = filename.lastIndexOf(".");
		theSuffix = filename.substring(pos, filename.length());
		return theSuffix.toUpperCase();
	}

	/**
	 * @param status
	 * @param text
	 * @param e
	 */
	public void logError(int status, String text, Exception e)
	{
		System.out.println("Error:" + text);
	}

}
