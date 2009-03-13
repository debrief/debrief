package MWC.GUI.Tools.Chart;

import MWC.GUI.*;
import MWC.GUI.Canvas.MetafileCanvas;
import MWC.GUI.Tools.*;
import MWC.GenericData.WorldLocation;

public class WriteMetafile extends PlainTool
{
	// ///////////////////////////////////////////////////////////
	// member variables
	// //////////////////////////////////////////////////////////
	/** keep a reference to the chart which we are acting upon */
	private PlainChart _theChart;

	static public final String PROP_NAME = "WMF_Directory";

	private boolean _writeToFile = true;

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
	public WriteMetafile(ToolParent theParent, PlainChart theChart, Layers theData)
	{
		super(theParent, "Write MF", "images/write_wmf.gif");
		// remember the chart we are acting upon
		_theChart = theChart;
	}

	public WriteMetafile(ToolParent theParent, PlainChart theChart, boolean writeToFile)
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

			// copy the projection
			mf.setProjection(_theChart.getCanvas().getProjection());

			// start drawing
			mf.startDraw(null);

			// sort out the background colour
			mf.setBackgroundColor(java.awt.Color.white);

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
		catch (java.lang.NoClassDefFoundError e)
		{
			MWC.GUI.Dialogs.DialogFactory.showMessage("Write Operation",
					"Sorry, Microsoft classes not enabled on this installation");
			MWC.Utilities.Errors.Trace.trace(e,
					"Sorry, Microsoft classes not enabled on this installation");
		}
		catch (java.lang.Exception e)
		{
			MWC.Utilities.Errors.Trace.trace(e);
		}

		// end busy
		setBusy(false);
	}

	/**
	 * @param mf
	 */
	protected void paintToMetafile(MetafileCanvas mf)
	{
		MWC.GUI.Canvas.Swing.SwingCanvas sc = (MWC.GUI.Canvas.Swing.SwingCanvas) _theChart
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
	public static void main(String[] args)
	{

		MetafileCanvas mf = new MetafileCanvas("c:\\");

		// copy the projection
		MWC.Algorithms.Projections.FlatProjection fp = new MWC.Algorithms.Projections.FlatProjection();
		fp.setDataArea(new MWC.GenericData.WorldArea(new MWC.GenericData.WorldLocation(0, 0,
				0), new MWC.GenericData.WorldLocation(1, 1, 1)));
		fp.setScreenArea(new java.awt.Dimension(400, 400));
		mf.setProjection(fp);

		// start drawing
		mf.startDraw(null);

		// sort out the background colour
		mf.setBackgroundColor(java.awt.Color.white);

		// sort out the line width
		mf.setLineWidth(1);

		// ask the canvas to paint the image
		// MWC.GUI.Canvas.Swing.SwingCanvas sc =
		// (MWC.GUI.Canvas.Swing.SwingCanvas)_theChart.getCanvas();
		// sc.paintIt(mf);

		mf.setColor(java.awt.Color.green);
		mf.drawLine(350, 50, 200, 200);
		mf.drawLine(252, 50, 200, 230);
		mf.drawLine(54, 250, 200, 250);
		mf.drawLine(56, 50, 200, 270);
		mf.drawText("gere we go", 100, 200);

		java.awt.Font newF = new java.awt.Font("Courier", java.awt.Font.BOLD, 12);

		mf.setColor(java.awt.Color.blue);
		mf.drawText(newF, "and us three", 200, 100);
		mf.setColor(java.awt.Color.blue);
		mf.fillRect(80, 100, 40, 40);

		String theStr = MWC.Utilities.TextFormatting.DebriefFormatLocation
				.toString(new WorldLocation(2.1, 2.2, 2.3));
		mf.drawText(theStr, 40, 20);

		System.out.println("=============== our text");
		String val = MWC.Utilities.TextFormatting.BriefFormatLocation.toStringLat(2.1, false);
		String val2 = MWC.Utilities.TextFormatting.BriefFormatLocation.toStringLong(2.1,
				false);
		mf.drawText(val, 40, 40);
		mf.drawText(val2, 40, 60);
		System.out.println("=================");
		char[] str = val.toCharArray();
		for (int i = 0; i < str.length; i++)
		{
			char c = str[i];
			System.out.println(i + ":" + c);
		}

		str = val2.toCharArray();
		for (int i = 0; i < str.length; i++)
		{
			char c = str[i];
			System.out.println(i + ":" + c);
		}

		// and finish
		mf.endDraw(null);

		System.exit(0);

	}

}
