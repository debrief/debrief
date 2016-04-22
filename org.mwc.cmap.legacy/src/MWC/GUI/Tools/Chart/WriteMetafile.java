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
package MWC.GUI.Tools.Chart;

import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.ToolParent;
import MWC.GUI.Canvas.MetafileCanvas;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.PlainTool;
import MWC.GenericData.WorldLocation;

public class WriteMetafile extends PlainTool
{
	// ///////////////////////////////////////////////////////////
	// member variables
	// //////////////////////////////////////////////////////////
	/** keep a reference to the chart which we are acting upon */
	private PlainChart _theChart;

	static public final String PROP_NAME = "WMF_Directory";
	
	private String _errorMessage;

	private boolean _writeToFile = true;

	private boolean _writable;

	// ///////////////////////////////////////////////////////
	// constructor
	// ///////////////////////////////////////////////////////

	/**
	 * constructor, stores information ready for when the button finally gets
	 * pressed
	 * 
	 * @param theParent
	 *          the parent application, so we can set cursors
	 * @param theChart
	 *          the chart we are to resize
	 * @param theData
	 *          the data we are plotting
	 */
	public WriteMetafile(final ToolParent theParent, final PlainChart theChart, final Layers theData)
	{
		super(theParent, "Write MF", "images/write_wmf.gif");
		// remember the chart we are acting upon
		_theChart = theChart;
	}

	public WriteMetafile(final ToolParent theParent, final PlainChart theChart, final boolean writeToFile)
	{
		this(theParent, theChart, null);
		_writeToFile = writeToFile;
	}

	// ///////////////////////////////////////////////////////
	// member functions
	// ///////////////////////////////////////////////////////
	public Action getData()
	{
		// don't bother, since we can do it in our execute method
		return null;
	}

	public void execute()
	{

		// start busy
		setBusy(true);

		try
		{
			// create our output metafile
			final MetafileCanvas mf;
			
			// do we just want to create it in the temp directory?
			if (_writeToFile)
			{
				mf = new MetafileCanvas(getParent().getProperty(PROP_NAME));
			}
			else
			{
				mf = new MetafileCanvas();
			}

			_writable = true;
			if (!mf.isWritable())
			{
				_writable = false;
				_errorMessage = "Cannot write to '" + mf.getOutputFileName() + "'.";
				return;
			}
			// copy the projection
			mf.setProjection(_theChart.getCanvas().getProjection());

			// start drawing
			mf.startDraw(null);

			// sort out the background colour
			mf.setBackgroundColor(DebriefColors.WHITE);

			// sort out the line width
			mf.setLineWidth(_theChart.getCanvas().getLineWidth());

			// ask the canvas to paint the image
			paintToMetafile(mf);

			/*
			 * mf.setColor(java.awt.Color.green); mf.drawLine(350, 50, 200, 200);
			 * mf.drawLine(252, 50, 200, 230); mf.drawLine(54, 250, 200, 250);
			 * mf.drawLine(56, 50, 200, 270); mf.drawText("gere we go", 100, 200);
			 * java.awt.Font newF = new java.awt.Font("Courier", java.awt.Font.BOLD ,
			 * 12); mf.setColor(java.awt.Color.blue); int ht =
			 * mf.getStringHeight(newF); int wid = mf.getStringWidth(newF, "and us
			 * to"); mf.drawText(newF, "and us three", 200, 100);
			 * mf.setColor(java.awt.Color.blue); mf.fillRect(80, 100, 40, 40);
			 */

			// and finish
			mf.endDraw(null);

		}
		catch (final java.lang.NoClassDefFoundError e)
		{
			MWC.GUI.Dialogs.DialogFactory.showMessage("Write Operation",
					"Sorry, Microsoft classes not enabled on this installation");
			MWC.Utilities.Errors.Trace.trace(e,
					"Sorry, Microsoft classes not enabled on this installation");
		}
		catch (final java.lang.Exception e)
		{
			MWC.Utilities.Errors.Trace.trace(e);
		}

		// end busy
		setBusy(false);
	}

	/**
	 * @param mf
	 */
	protected void paintToMetafile(final MetafileCanvas mf)
	{
		final MWC.GUI.Canvas.Swing.SwingCanvas sc = (MWC.GUI.Canvas.Swing.SwingCanvas) _theChart
				.getCanvas();
		sc.paintIt(mf);
	}

	/**
	 * provide method to close (remove all references, help garbage collector)
	 */
	public void close()
	{
		// clear parent
		super.close();

		// now local members
		_theChart = null;
	}

	/**
	 * test we can output a metafile
	 * 
	 * @param args
	 */
	public static void main(final String[] args)
	{

		final MetafileCanvas mf = new MetafileCanvas("c:\\");

		// copy the projection
		final MWC.Algorithms.Projections.FlatProjection fp = new MWC.Algorithms.Projections.FlatProjection();
		fp.setDataArea(new MWC.GenericData.WorldArea(new MWC.GenericData.WorldLocation(0, 0,
				0), new MWC.GenericData.WorldLocation(1, 1, 1)));
		fp.setScreenArea(new java.awt.Dimension(400, 400));
		mf.setProjection(fp);

		// start drawing
		mf.startDraw(null);

		// sort out the background colour
		mf.setBackgroundColor(DebriefColors.WHITE);

		// sort out the line width
		mf.setLineWidth(1);

		// ask the canvas to paint the image
		// MWC.GUI.Canvas.Swing.SwingCanvas sc =
		// (MWC.GUI.Canvas.Swing.SwingCanvas)_theChart.getCanvas();
		// sc.paintIt(mf);

		mf.setColor(DebriefColors.GREEN);
		mf.drawLine(350, 50, 200, 200);
		mf.drawLine(252, 50, 200, 230);
		mf.drawLine(54, 250, 200, 250);
		mf.drawLine(56, 50, 200, 270);
		mf.drawText("gere we go", 100, 200);

		final java.awt.Font newF = new java.awt.Font("Courier", java.awt.Font.BOLD, 12);

		mf.setColor(DebriefColors.BLUE);
		mf.drawText(newF, "and us three", 200, 100);
		mf.setColor(DebriefColors.BLUE);
		mf.fillRect(80, 100, 40, 40);

		final String theStr = MWC.Utilities.TextFormatting.DebriefFormatLocation
				.toString(new WorldLocation(2.1, 2.2, 2.3));
		mf.drawText(theStr, 40, 20);

		System.out.println("=============== our text");
		final String val = MWC.Utilities.TextFormatting.BriefFormatLocation.toStringLat(2.1, false);
		final String val2 = MWC.Utilities.TextFormatting.BriefFormatLocation.toStringLong(2.1,
				false);
		mf.drawText(val, 40, 40);
		mf.drawText(val2, 40, 60);
		System.out.println("=================");
		char[] str = val.toCharArray();
		for (int i = 0; i < str.length; i++)
		{
			final char c = str[i];
			System.out.println(i + ":" + c);
		}

		str = val2.toCharArray();
		for (int i = 0; i < str.length; i++)
		{
			final char c = str[i];
			System.out.println(i + ":" + c);
		}

		// and finish
		mf.endDraw(null);

		System.exit(0);

	}

	public boolean isWritable()
	{
		return _writable;
	}
	
	public String getErrorMessage()
	{
		return _errorMessage;
	}


}
