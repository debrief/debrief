// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: AnalysisView.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.13 $
// $Log: AnalysisView.java,v $
// Revision 1.13  2006/10/03 08:24:05  Ian.Mayo
// Switch to Java 5.
//
// Revision 1.12  2006/08/10 13:58:14  Ian.Mayo
// Double-click edit no longer needs layers
//
// Revision 1.11  2005/09/08 08:57:23  Ian.Mayo
// Refactor name of chart features layer
//
// Revision 1.10  2004/12/02 12:07:08  Ian.Mayo
// No need to output full stack trace when 3d viewer not found
//
// Revision 1.9  2004/11/22 13:40:54  Ian.Mayo
// Replace old variable name used for stepping through enumeration, since it is now part of language (Jdk1.5)
//
// Revision 1.8  2004/10/20 10:18:52  Ian.Mayo
// Move Create Buoy Pattern to Chart Features toolbar
//
// Revision 1.7  2004/10/20 08:36:20  Ian.Mayo
// Update Arc to allow filled plotting and painting in spoke arcs
//
// Revision 1.6  2004/10/19 14:15:56  Ian.Mayo
// Reflect new ArcShape signature
//
// Revision 1.5  2004/10/19 13:50:26  Ian.Mayo
// Add arc plotting
//
// Revision 1.4  2004/10/19 10:12:10  Ian.Mayo
// Add local grid plotter
//
// Revision 1.3  2004/08/09 09:43:21  Ian.Mayo
// Lots of Idea tidying
//
// Revision 1.2  2003/08/12 09:28:32  Ian.Mayo
// Include import of DTF files
//
// Revision 1.1.1.2  2003/07/21 14:47:32  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.8  2003-07-01 16:31:32+01  ian_mayo
// Uncomment debug lines
//
// Revision 1.7  2003-03-31 14:01:35+01  ian_mayo
// Cause plot to redraw after data has been loaded.  We've had to implement it here, since we stopped it happening in the old fireExtended handler for PlainChart
//
// Revision 1.6  2003-03-19 15:38:09+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.5  2002-11-27 15:27:04+00  ian_mayo
// Use new TOPO
//
// Revision 1.4  2002-11-13 13:15:37+00  ian_mayo
// add TOPO palette button
//
// Revision 1.3  2002-10-01 15:38:56+01  ian_mayo
// remove un-necessary abstract method
//
// Revision 1.2  2002-05-28 12:28:06+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:15+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:49+01  ian_mayo
// Initial revision
//
// Revision 1.15  2002-02-26 15:49:23+00  administrator
// Correct title of new polygon
//
// Revision 1.14  2002-02-25 13:21:52+00  administrator
// Add ellipse creator
//
// Revision 1.13  2001-11-14 19:38:53+00  administrator
// Add abillity to drop DSF files onto plot
//
// Revision 1.12  2001-10-03 10:13:46+01  administrator
// Remove Show Layer Manager button
//
// Revision 1.11  2001-08-31 11:12:31+01  administrator
// Change name of Shapes toolbar to Drawing
//
// Revision 1.10  2001-08-31 09:57:21+01  administrator
// just fannying around, really
//
// Revision 1.9  2001-08-24 16:35:34+01  administrator
// Put new data into the existing layers object
//
// Revision 1.8  2001-08-24 12:39:48+01  administrator
// Give the layers its set of editors
//
// Revision 1.7  2001-08-23 13:27:44+01  administrator
// Reflect new signature for PlainCreate class, to allow it to fireExtended()
//
// Revision 1.6  2001-08-21 12:15:25+01  administrator
// improve tidying
//
// Revision 1.5  2001-08-17 08:01:15+01  administrator
// Clear up memory leaks
//
// Revision 1.4  2001-08-06 12:49:11+01  administrator
// Change import to use our threaded importer
//
// Revision 1.3  2001-07-27 17:08:00+01  administrator
// add button to record video to file, and rename "Plot" toolbar to "View"
//
// Revision 1.2  2001-07-23 11:53:22+01  administrator
// don't do a button for a VPFcoastline, since we can't distribute it very well
//
// Revision 1.1  2001-07-17 16:23:13+01  administrator
// add tool to create the old coastline
//
// Revision 1.0  2001-07-17 08:41:36+01  administrator
// Initial revision
//
// Revision 1.13  2001-07-16 15:37:23+01  novatech
// add new method to add VPF data
//
// Revision 1.12  2001-07-16 15:01:44+01  novatech
// make the Session visible to child classes
//
// Revision 1.11  2001-07-09 14:09:13+01  novatech
// Set the StepControl pointer in the Importers, so that it can be set in any narratives
//
// Revision 1.10  2001-06-14 15:35:46+01  novatech
// add new parameter to indicate which tabbed toolbar we want each tool on
//
// Revision 1.9  2001-04-08 10:46:35+01  novatech
// remove WriteVRML function (since we provide 3D views anyway)
//
// Revision 1.8  2001-01-26 11:21:31+00  novatech
// change order of showing buttons
//
// Revision 1.7  2001-01-24 12:12:17+00  novatech
// hide button for write VRML
//
// Revision 1.6  2001-01-19 15:06:49+00  novatech
// adjusted order of tools in toolbar (to move chart decorations up, ad palette items down
//
// Revision 1.5  2001-01-18 13:17:19+00  novatech
// user upper case 'O' instead of lower case for Zoom out accelerator
//
// Revision 1.4  2001-01-17 13:22:31+00  novatech
// reflect name change from Field to Pattern
//
// Revision 1.3  2001-01-08 11:39:25+00  novatech
// now using correct buoy icon
//
// Revision 1.2  2001-01-03 16:03:02+00  novatech
// add new button to allow creation of new BuoyField
//
// Revision 1.1  2001-01-03 13:40:49+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:46:28  ianmayo
// initial import of files
//
// Revision 1.30  2000-11-24 10:52:45+00  ian_mayo
// switch to XML formats, and add LabelCreator button
//
// Revision 1.29  2000-11-17 09:11:12+00  ian_mayo
// acknowledge that we now accept XML files
//
// Revision 1.28  2000-10-12 09:12:16+01  ian_mayo
// part way through clipboard copying
//
// Revision 1.27  2000-10-10 14:08:55+01  ian_mayo
// playing around
//
// Revision 1.26  2000-10-10 13:17:34+01  ian_mayo
// better busy cursors for drag/drop
//
// Revision 1.25  2000-10-10 12:20:56+01  ian_mayo
// provide support for drag & drop
//
// Revision 1.24  2000-08-23 09:36:05+01  ian_mayo
// reflect use of new Debrief-specific VRML writing method
//
// Revision 1.23  2000-08-08 12:37:19+01  ian_mayo
// Don't print stack trace when we find we are missing Java3D files
//
// Revision 1.22  2000-08-07 14:04:58+01  ian_mayo
// removed d-lines
//
// Revision 1.21  2000-08-07 12:23:18+01  ian_mayo
// tidy icon filename
//
// Revision 1.20  2000-06-19 15:06:35+01  ian_mayo
// moved location of View 3d toolbar button
//
// Revision 1.19  2000-06-15 13:46:27+01  ian_mayo
// correct the constructor for the 3d stepper
//
// Revision 1.18  2000-06-08 13:17:52+01  ian_mayo
// experimenting with new button to create 3d view
//
// Revision 1.17  2000-04-26 14:22:46+01  ian_mayo
// tidy up ImportRamgeData button
//
// Revision 1.16  2000-04-19 11:32:40+01  ian_mayo
// implement Close method, clear local storage.  Remove direction buttons, provide "closeGUI" abstract method
//
// Revision 1.15  2000-04-03 14:05:51+01  ian_mayo
// add SaveAs button
//
// Revision 1.14  2000-04-03 10:41:18+01  ian_mayo
// add accessor to get the tote
//
// Revision 1.13  2000-03-27 14:42:08+01  ian_mayo
// don't bother with the directional toolbuttons
//
// Revision 1.12  2000-03-14 15:00:41+00  ian_mayo
// amend order of insertion of ZoomOut method
//
// Revision 1.11  2000-03-14 09:47:44+00  ian_mayo
// assign icons to tool items
//
// Revision 1.10  2000-02-25 09:08:17+00  ian_mayo
// Add "Write VR" toolbar button
//
// Revision 1.9  2000-02-04 15:51:29+00  ian_mayo
// removed test button, and put lat/long cursor position below into status panel
//
// Revision 1.8  2000-01-20 10:10:59+00  ian_mayo
// added cut/copy/paste methods
//
// Revision 1.7  2000-01-18 15:04:59+00  ian_mayo
// changed Decorations to Chart Features
//
// Revision 1.6  2000-01-13 15:32:26+00  ian_mayo
// added canvas editor
//
// Revision 1.5  1999-12-03 14:38:12+00  ian_mayo
// add keyboard shortcuts & mnemonics
//
// Revision 1.4  1999-11-26 15:50:58+00  ian_mayo
// added new Right-click handler
//
// Revision 1.3  1999-11-25 16:54:56+00  ian_mayo
// implementing Swing components
//
// Revision 1.2  1999-11-11 18:23:33+00  ian_mayo
// added shape creation buttons
//
// Revision 1.1  1999-10-12 15:34:16+01  ian_mayo
// Initial revision
//
// Revision 1.9  1999-08-09 13:35:36+01  administrator
// change way 'layers' are passed to handlers (in event, not constructor)
//
// Revision 1.8  1999-08-04 09:45:32+01  administrator
// minor mods, tidying up
//
// Revision 1.7  1999-07-27 12:09:04+01  administrator
// tool locations changed
//
// Revision 1.6  1999-07-27 11:00:39+01  administrator
// implemented new location for chart-related tools
//
// Revision 1.5  1999-07-27 09:27:47+01  administrator
// general improvements
//
// Revision 1.4  1999-07-16 10:01:50+01  administrator
// Nearing end of phase 2
//
// Revision 1.3  1999-07-12 08:09:23+01  administrator
// Property editing added
//
// Revision 1.2  1999-07-08 13:08:45+01  administrator
// <>
//
// Revision 1.1  1999-07-07 11:10:18+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:08+01  sm11td
// Initial revision
//
// Revision 1.3  1999-02-04 08:02:29+00  sm11td
// Plotting to canvas, scaling canvas,
//
// Revision 1.2  1999-02-01 16:08:51+00  sm11td
// creating new sessions & panes, starting import management
//
// Revision 1.1  1999-01-31 13:33:13+00  sm11td
// Initial revision
//

package Debrief.GUI.Views;

import Debrief.GUI.Frames.Session;
import Debrief.GUI.Tote.AnalysisTote;
import Debrief.Tools.Operations.*;
import Debrief.Tools.Palette.CreateLabel;
import Debrief.Tools.Palette.CreateShape;
import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.*;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Shapes.*;
import MWC.GUI.Tools.Chart.*;
import MWC.GUI.Tools.MenuItemInfo;
import MWC.GUI.Tools.Operations.RightClickCutCopyAdaptor;
import MWC.GUI.Tools.Operations.RightClickPasteAdaptor;
import MWC.GUI.Tools.Operations.ShowVideo;
import MWC.GUI.Tools.Palette.*;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;
import MWC.GenericData.WorldVector;

import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

/**
 * a screen layout which represents screen panes necessary for analysis
 */
abstract public class AnalysisView extends PlainView implements
		MWC.GUI.DragDrop.FileDropSupport.FileDropListener
{

	// /////////////////////////////////////////////
	// member variables
	// /////////////////////////////////////////////

	private final Vector<MenuItemInfo> _theTools;
	PlainChart _theChart;
	private AnalysisTote _theTote;
	private Toolbar _theToolbar;
	private PropertiesPanel _theProperties;
	private StatusBar _theStatusBar;
	/**
	 * store the parent session
	 */
	protected Session _theSession;

	/**
	 * support for drag/drop of files onto this view
	 */
	final transient protected MWC.GUI.DragDrop.FileDropSupport _dropSupport = new MWC.GUI.DragDrop.FileDropSupport();

	/**
	 * the cut/copy adaptor. we keep a handle to this so that we can explicitly
	 * clear it when we close - this is to overcome an UndoBuffer memory leak
	 */
	private MWC.GUI.Tools.Operations.RightClickCutCopyAdaptor _rightClicker;

	/**
	 * the tote editor adaptor. we keep a handle to this so that we can explicitly
	 * clear it when we close - this is to overcome an UndoBuffer memory leak
	 */
	private Debrief.GUI.Tote.RightClickEditToteAdaptor _toteAdapter;

	// /////////////////////////////////////////////
	// constructor
	// /////////////////////////////////////////////
	/**
	 * constructor for this class
	 * 
	 * @param theParent
	 *          a parent class which will show busy cursors
	 * @param theSession
	 *          the session which contains the data for this view
	 */
	public AnalysisView(final ToolParent theParent, final Session theSession)
	{
		super("Analysis View", theParent);
		_theTools = new Vector<MenuItemInfo>(0, 1);
		_theSession = theSession;

		/**
		 * note, we don't register an interest with the data for our parent session,
		 * we leave that to the actual panes we contain
		 */
		_dropSupport.setFileDropListener(this, ".REP,.XML,.DSF,.DTF");

	}

	// /////////////////////////////////////////////
	// member functions
	// /////////////////////////////////////////////

	/**
	 * member method to create a toolbar button for this tool, it is intended that
	 * this class be overridden within more able GUI environments (in Swing we
	 * should have a tabbed interface)
	 * 
	 * @param item
	 *          the description of this tool
	 */
	protected void addThisTool(final MenuItemInfo item)
	{
		// see if this is an action button, or it toggles as part of a group
		if (item.getToggleGroup() == null)
		{
			_theToolbar.addTool(item.getTool(), item.getShortCut(), item
					.getMnemonic());
		}
		else
		{
			_theToolbar.addToggleTool(item.getMenuName(), item.getTool(), item
					.getShortCut(), item.getMnemonic());
		}
	}

	/**
	 * <code>buildTheInterface</code> puts the bits together
	 */
	protected final void buildTheInterface()
	{

		// we've had to do this here, so that we know we've foudn the chart
		addTools();

		// retrieve the tools for this interface
		final Enumeration<MenuItemInfo> iter = getTools();

		while (iter.hasMoreElements())
		{
			final MenuItemInfo thisItem = iter.nextElement();

			addThisTool(thisItem);
		}

		// setup our double-click editor
		// and add our dbl click listener
		getChart().addCursorDblClickedListener(new DblClickEdit(getProperties()));

		// create our right click editor, and add it's helpers
		final RightClickEdit rc = new RightClickEdit(getProperties());
		rc
				.addMenuCreator(new MWC.Algorithms.Editors.ProjectionEditPopupMenuAdaptor());
		rc.addMenuCreator(new MWC.GUI.LayerManager.EditLayersPopupMenuAdaptor());
		rc.addMenuCreator(new MWC.GUI.Canvas.EditCanvasPopupMenuAdaptor(getChart()
				.getCanvas()));

		_toteAdapter = new Debrief.GUI.Tote.RightClickEditToteAdaptor(_theTote);
		rc.addPlottableMenuCreator(_toteAdapter, _theProperties);

		// keep local reference to the right-clicker, to overcome a memory leak
		_rightClicker = new RightClickCutCopyAdaptor(_theSession.getClipboard(),
				_theSession.getUndoBuffer());
		rc.addPlottableMenuCreator(_rightClicker, _theProperties);
		rc.addPlottableMenuCreator(new RightClickPasteAdaptor(_theSession
				.getClipboard()), _theProperties);

		// we also want to try to give these properties to the layers object, so
		// that it can be edited
		// properly by the right-clicking in the Layer Manager
		_theSession.getData().setEditor(rc);

		// and add our right-click editor
		getChart().addRightClickListener(rc);

	}

	/**
	 * return the tools we have created
	 */
	private Enumeration<MenuItemInfo> getTools()
	{
		return _theTools.elements();
	}

	/**
	 * build the list of tools necessary for this type of view
	 */
	private void addTools()
	{

		_theTools.addElement(new MenuItemInfo("File", null, "Save",
				new SavePlotXML(_theParent, _theSession), null, ' '));
		_theTools.addElement(new MenuItemInfo("File", null, "Save As",
				new SavePlotAsXML(_theParent, _theSession), null, ' '));
		_theTools.addElement(new MenuItemInfo("File", null, "Save WMF",
				new WriteMetafile(_theParent, _theChart, _theSession.getData()), null,
				' '));
		// _theTools.addElement(new MenuItemInfo(null, "Copy to clipboard", new
		// WriteClipboard(_theParent, _theChart, _theSession.getData()), null,
		// ' '));
		_theTools.addElement(new MenuItemInfo("File", null, "Import",
				new ImportData2(_theParent, null, _theSession), null, ' '));
		_theTools.addElement(new MenuItemInfo("File", null, "Import Range",
				new ImportRangeData(_theParent, _theProperties, _theSession.getData()),
				null, 'G'));

		// NOTE: wrap this next creator, in case we haven't got the right files
		// avaialble
		try
		{
			_theTools.addElement(new MenuItemInfo("File", null, "View in 3d",
					new View3dPlot(_theParent, _theProperties, _theSession.getData(),
							getTote().getStepper()), null, ' '));
		}
		catch (java.lang.NoClassDefFoundError e)
		{
			System.err.println("3D Viewer not provided, classes not found");
		}

		// NOTE: wrap this next creator, in case we haven't got the right files
		// avaialble
		try
		{
			_theTools.addElement(new MenuItemInfo("File", null, "Record to video",
					new ShowVideo(_theParent, _theProperties, _theChart.getPanel()),
					null, ' '));
		}
		catch (java.lang.NoClassDefFoundError e)
		{
			System.err.println("Record to video not provided, JMF classes not found");
			// e.printStackTrace();
		}

		_theTools.addElement(new MenuItemInfo("View", null, "Repaint", new Repaint(
				_theParent, _theChart), null, 'R'));

		// ////////////////////////
		// second row
		// ////////////////////////

		// _theTools.addElement(new MenuItemInfo(null, "Print Chart", new
		// PrintChart(_theParent, _theChart), null, 'f'));
		_theTools.addElement(new MenuItemInfo("View", null, "Fit", new FitToWin(
				_theParent, _theChart), null, 'f'));
		_theTools.addElement(new MenuItemInfo("View", "Drag", "Pan", new Pan(
				_theChart, _theParent, null), null, 'P'));
		_theTools.addElement(new MenuItemInfo("View", "Drag", "Rng Brg",
				new RangeBearing(_theChart, _theParent, getStatusBar()), null, 'B'));
		_theTools.addElement(new MenuItemInfo("View", "Drag", "Zoom", new ZoomIn(
				_theChart, _theParent), null, 'I'));
		_theTools.addElement(new MenuItemInfo("View", null, "Zoom Out",
				new ZoomOut(_theParent, _theChart), new java.awt.MenuShortcut(
						java.awt.event.KeyEvent.VK_SUBTRACT), 'O'));

		// //////////////////////////////////////////////////////////
		// now the decorations
		// //////////////////////////////////////////////////////////
		// find the decorations layer
		final Layer decs = _theSession.getData().findLayer(Layers.CHART_FEATURES);
		_theTools.addElement(new MenuItemInfo(Layers.CHART_FEATURES, null,
				"Create Scale", new CreateScale(_theParent, _theProperties, decs,
						_theSession.getData(), _theChart), null, ' '));

		_theTools.addElement(new MenuItemInfo(Layers.CHART_FEATURES, null,
				"Create Grid", new CreateGrid(_theParent, _theProperties, decs,
						_theSession.getData(), _theChart), null, ' '));
		_theTools.addElement(new MenuItemInfo(Layers.CHART_FEATURES, null,
				"Create Local Grid", new CreateLocalGrid(_theParent, _theProperties,
						decs, _theSession.getData(), _theChart), null, ' '));

		_theTools.addElement(new MenuItemInfo(Layers.CHART_FEATURES, null,
				"Create Coast", new CreateCoast(_theParent, _theProperties, decs,
						_theSession.getData(), _theChart), null, ' '));
		// let's not read in the VPF reference layer, since we can't get OpenMap
		// code to read
		// it in from the jar file.
		/*
		 * _theTools.addElement(new MenuItemInfo(Layers.CHART_FEATURES, null,
		 * "Create VPF Coast", new CreateVPFCoast(_theParent, _theProperties, decs,
		 * _theChart),null, ' ' ));
		 */
		_theTools.addElement(new MenuItemInfo(Layers.CHART_FEATURES, null,
				"Create VPF Layers", new CreateVPFLayers(_theParent, _theProperties,
						decs, _theSession.getData(), _theChart), null, ' '));
		_theTools.addElement(new MenuItemInfo(Layers.CHART_FEATURES, null,
				"Create ETOPO Bathy", new CreateTOPO(_theParent, _theProperties,
						_theSession.getData(), _theChart), null, ' '));
		_theTools.addElement(new MenuItemInfo(Layers.CHART_FEATURES, null,
				"Create Buoy Pattern",
				new Debrief.Tools.Palette.BuoyPatterns.CreateBuoyPattern(_theParent,
						_theProperties, _theSession.getData(), _theChart, "Buoy Pattern",
						"images/buoy.gif"), null, ' '));
		// //////////////////////////////////////////////////////////
		// now the shape creators
		// //////////////////////////////////////////////////////////

		_theTools.addElement(new MenuItemInfo("Drawing", null, "Create Label",
				new CreateLabel(_theParent, _theProperties, _theSession.getData(),
						_theChart, "Label", "images/label.gif"), null, ' '));
		_theTools.addElement(new MenuItemInfo("Drawing", null, "Create Ellipse",
				new CreateShape(_theParent, _theProperties, _theSession.getData(),
						_theChart, "Ellipse", "images/ellipse.gif")
				{
					protected ShapeWrapper getShape(final WorldLocation centre)
					{
						return new ShapeWrapper("new ellipse", new EllipseShape(centre, 0,
								new WorldDistance(0, WorldDistance.DEGS), new WorldDistance(0,
										WorldDistance.DEGS)), java.awt.Color.red, null);
					}
				}, null, ' '));
		// rectangle
		_theTools.addElement(new MenuItemInfo("Drawing", null, "Create Rectangle",
				new CreateShape(_theParent, _theProperties, _theSession.getData(),
						_theChart, "Rectangle", "images/rectangle.gif")
				{
					protected ShapeWrapper getShape(final WorldLocation centre)
					{
						return new ShapeWrapper("new rectangle", new RectangleShape(centre,
								centre.add(new WorldVector(MWC.Algorithms.Conversions
										.Degs2Rads(45), 0.05, 0))), java.awt.Color.red, null);
					}
				}, null, ' '));

		// arc
		_theTools.addElement(new MenuItemInfo("Drawing", null, "Create arc",
				new CreateShape(_theParent, _theProperties, _theSession.getData(),
						_theChart, "Arc", "images/arc.gif")
				{
					protected ShapeWrapper getShape(final WorldLocation centre)
					{
						return new ShapeWrapper("new arc", new ArcShape(centre,
								new WorldDistance(4000, WorldDistance.YARDS), 135, 90, true,
								false), java.awt.Color.red, null);
					}
				}, null, ' ')); // circle
		_theTools.addElement(new MenuItemInfo("Drawing", null, "Create circle",
				new CreateShape(_theParent, _theProperties, _theSession.getData(),
						_theChart, "Circle", "images/circle.gif")
				{
					protected ShapeWrapper getShape(final WorldLocation centre)
					{
						return new ShapeWrapper("new circle",
								new CircleShape(centre, 4000), java.awt.Color.red, null);
					}
				}, null, ' '));
		// line
		_theTools.addElement(new MenuItemInfo("Drawing", null, "Create line",
				new CreateShape(_theParent, _theProperties, _theSession.getData(),
						_theChart, "Line", "images/line.gif")
				{
					protected ShapeWrapper getShape(final WorldLocation centre)
					{
						return new ShapeWrapper("new line", new LineShape(centre, centre
								.add(new WorldVector(
										MWC.Algorithms.Conversions.Degs2Rads(45.0), 0.05, 0))),
								java.awt.Color.red, null);
					}
				}, null, ' '));

		// line
		_theTools.addElement(new MenuItemInfo("Drawing", null, "Create polygon",
				new CreateShape(_theParent, _theProperties, _theSession.getData(),
						_theChart, "Polygon", "images/polygon.gif")
				{
					protected ShapeWrapper getShape(final WorldLocation centre)
					{
						return new ShapeWrapper("new polygon", new PolygonShape(
								new WorldPath(new WorldLocation[]
								{ centre })), java.awt.Color.red, null);
					}
				}, null, ' '));

	}

	protected final void setChart(final PlainChart theChart)
	{
		_theChart = theChart;
	}

	protected final void setTote(final AnalysisTote theTote)
	{
		_theTote = theTote;
	}

	protected final void setToolbar(final Toolbar theToolbar)
	{
		_theToolbar = theToolbar;
	}

	public final AnalysisTote getTote()
	{
		return _theTote;
	}

	protected final void setProperties(final PropertiesPanel theProperties)
	{
		_theProperties = theProperties;
	}

	protected final void setStatusBar(final StatusBar theBar)
	{
		_theStatusBar = theBar;
	}

	private StatusBar getStatusBar()
	{
		return _theStatusBar;
	}

	private PropertiesPanel getProperties()
	{
		return _theProperties;
	}

	/**
	 * data has been modified, update panes as necesary
	 */
	public final void update()
	{
		// check they are valid
		if (_theChart != null)
			_theChart.update();
		if (_theTote != null)
			_theTote.update();

	}

	/**
	 * data has been modified, update panes as necesary
	 */
	public final void rescale()
	{
		// check they are valid
		if (_theChart != null)
			_theChart.rescale();
	}

	public final PlainChart getChart()
	{
		return _theChart;
	}

	/**
	 * get ready to close, set all local references to null, to assist garbage
	 * collection
	 */
	public void close()
	{
		// we'll also try to remove all of the tools
		final Enumeration<MenuItemInfo> iter = _theTools.elements();
		while (iter.hasMoreElements())
		{
			final MenuItemInfo mn = iter.nextElement();
			mn.close();
		}

		// clear the dangling reference to the undo buffer
		_rightClicker.closeMe();
		_toteAdapter = null;

		// clear the file drop listener
		if (_dropSupport != null)
			_dropSupport.removeComponent(getChart().getPanel());

		_dropSupport.removeFileDropListener(this);

		// now remove references to the tools themselves
		_theTools.removeAllElements();

		_theChart.close();
		_theChart = null;

		if (_theTote != null)
		{
			_theTote.closeMe();
			_theTote = null;
		}
		if (_theToolbar != null)
			_theToolbar.close();
		_theToolbar = null;
		_theProperties = null;
		_theStatusBar = null;

		_theSession = null;

	}

	protected final void finalize()
	{
		try
		{
			super.finalize();
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}

	/**
	 * process this list of file
	 * 
	 * @param files
	 *          the list of files
	 */
	public final void FilesReceived(final java.util.Vector<File> files)
	{
		// get our layers object
		// Layers newLayers = new Layers();
		final Layers newLayers = _theSession.getData();

		_theParent.setCursor(java.awt.Cursor.WAIT_CURSOR);

		// set the pointer to the step control which we use when creating narrative
		// objects
		Debrief.ReaderWriter.XML.Tactical.NarrativeHandler.setStepper(this
				.getTote().getStepper());

		java.io.File[] theFiles = new java.io.File[]
		{ null };
		theFiles = (java.io.File[]) files.toArray(theFiles);

		// ok, go for it!
		final MWC.Utilities.ReaderWriter.ImportManager.BaseImportCaller caller = new MWC.Utilities.ReaderWriter.ImportManager.BaseImportCaller(
				theFiles, newLayers)
		{
			// handle the completion of each file
			public void fileFinished(final java.io.File fName, final Layers newData)
			{
			}

			// handle completion of the full import process
			public void allFilesFinished(final java.io.File[] fNames,
					final Layers newData)
			{
				// _theSession.getData().addThis(newData);

				_theSession.getData().fireExtended();

				// clear the pointer to the step control which we use when creating
				// narrative objects
				Debrief.ReaderWriter.XML.Tactical.NarrativeHandler.setStepper(null);

				// and get the plot to redraw

				// create a deferred event, to run immediately all of this madness is
				// over.
				// with these events inlined, the plot itself wasn't actaully getting.
				// Putting
				// them into invokeLater triggers the refresh after the current
				// processing is complete
				javax.swing.SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						_theChart.rescale();
						_theChart.update();
					}
				});

				// and clear the busy flag
				_theParent.restoreCursor();
			}
		};

		// ok, get going!
		caller.start();

	}

}
