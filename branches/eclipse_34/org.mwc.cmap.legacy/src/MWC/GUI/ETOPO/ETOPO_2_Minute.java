/**
 * Class to plot depths from a 2 minute grid.  The data-file is huge (>100Mb),
 * so don't read it in at once, but we read a byte at a time in
 * RandomAccess mode.
 *
 * User: Ian
 * Date: Nov 12, 2002
 * Time: 11:39:33 AM
 * To change this template use Options | File Templates.
 */
package MWC.GUI.ETOPO;

import java.io.*;

import MWC.GUI.*;
import MWC.GUI.Chart.Painters.SpatialRasterPainter;
import MWC.GUI.Properties.LineWidthPropertyEditor;
import MWC.GenericData.*;

public final class ETOPO_2_Minute extends SpatialRasterPainter
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// //////////////////////////////////
	// member values
	// //////////////////////////////////

	/**
	 * the filename of our data-file
	 */
	private static final String fName = "ETOPO2.raw";

	/**
	 * the file-reader we are using
	 */
	private static java.io.RandomAccessFile ra = null;

	/**
	 * whether to show land
	 */
	private boolean _showLand = true;

	/**
	 * the path to the datafile
	 */
	private final String _thePath;

	/**
	 * flag to ensure we only report missing data on the first occasion
	 */
	private boolean _reportedMissingData = false;

	// //////////////////////////////////
	// constructor
	// //////////////////////////////////
	public ETOPO_2_Minute(final String etopo_path)
	{
		super("2 Minute Topology");

		// store the path to the data file
		_thePath = etopo_path;

		_contourDepths = DEFAULT_CONTOUR_DEPTHS;
	}

	// ////////////////////////////////////////////////
	// static accessor to see if our data-file is there
	// ////////////////////////////////////////////////
	static public boolean dataFileExists(final String etopo_path)
	{
		boolean res = false;

		final String thePath = etopo_path + "/" + fName;

		final File testFile = new File(thePath);

		if (testFile.exists())
			res = true;

		return res;
	}

	// //////////////////////////////////
	// member methods
	// //////////////////////////////////

	/**
	 * override the parent paint method, so we can open/close the datafile
	 * 
	 * @param dest
	 *          where we're painting to
	 */
	public final void paint(final CanvasType dest)
	{
//		long tThen = System.currentTimeMillis();
		
		// start the paint
		openFile();
		
		// hey, it's only worth plotting if we've got some data
		if(!isDataLoaded())
			return;

		if (getVisible())
		{
			// remember width
			final float oldWid = dest.getLineWidth();

			// set our line width
			dest.setLineWidth(this.getLineThickness());

			super.paint(dest);

			// and restore the old one
			dest.setLineWidth(oldWid);
		}

		// end the paint, by closing the file
		// try {
		// ra.close();
		// }
		// catch (IOException e) {
		// e.printStackTrace();
		// }
		
//		long tNow = System.currentTimeMillis();
//		System.out.println("Elapsed time:" + (tNow - tThen));
	}

	// ////////////////////////////////////////////////
	// bathy provider support
	// ////////////////////////////////////////////////

	/**
	 * provide the delta for the data (in degrees)
	 */
	public double getGridDelta()
	{
		return 1d / 30d;
	}

	/**
	 * whether the data has been loaded yet
	 */
	public boolean isDataLoaded()
	{
		// do an open, just to check
		openFile();

		return (ra != null);
	}
	
	

	/** we do want to double-buffer this layer - since it takes "so" long to create
	 * 
	 */
	public boolean isBuffered() {
		return true;
	}

	/**
	 * function to retrieve a data value at the indicated index
	 */
	protected final double contour_valAt(final int i, final int j)
	{

		final double res;
		final int index;
		index = 2 * (j * getHorizontalNumber() + i);
		res = getValueAtIndex(index);
		return res;
	}

	/**
	 * function to retrieve the x-location for a specific array index
	 */
	protected final double contour_xValAt(final int i)
	{
		return getLongitudeFor(i);
	}

	/**
	 * function to retrieve the x-location for a specific array index
	 */
	protected final double contour_yValAt(final int i)
	{
		return getLatitudeFor(i);
	}

	/**
	 * how many horizonal data points are in our data
	 */
	private static int getHorizontalNumber()
	{
		return 10800;
	}
//
//	/**
//	 * how many vertical data points are in our data?
//	 */
//	private static int getVerticalNumber()
//	{
//		return 21600;
//	}

	/**
	 * open our datafile in random access mode
	 */
	private final void openFile()
	{
		if (ra == null)
		{
			String thePath = null;

			// just do a check to see if we have just the file or the whole path
			File testF = new File(_thePath);
			if (testF.isFile())
			{
				thePath = _thePath;
			}
			else if (testF.isDirectory())
			{
				thePath = _thePath + "//" + fName;
			}

			if (thePath != null)
			{
				try
				{
					ra = new RandomAccessFile(thePath, "r");
				} catch (IOException e)
				{
					if (_reportedMissingData)
					{
						MWC.GUI.Dialogs.DialogFactory.showMessage("File missing",
								"2 minute ETOPO data not found at:" + thePath);
						_reportedMissingData = true;
					}
				}
			}
		}
	}

	/* returns a color based on slope and elevation */
	public final int getColor(final int elevation, final double lowerLimit,
			final double upperLimit, final SpatialRasterPainter.ColorConverter converter)
	{
		return getETOPOColor((short) elevation, lowerLimit, upperLimit,
				_showLand, converter);
	}
	

  /* returns a color based on slope and elevation */
  public static int getETOPOColor(short elevation, double lowerLimit, double upperLimit, boolean showLand, final SpatialRasterPainter.ColorConverter converter)
  {
    int res = 0;

    // so, just produce a shade of blue depending on how deep we are.

    // see if we are above or beneath water level
    if((elevation > 0) && (showLand))
    {
      // ABOVE WATER

      // switch to positive
      double val = elevation;
      if(val > upperLimit)
        val = upperLimit;

      double proportion = val / upperLimit;

      double color_val = proportion * 125;

      // limit the colour val to the minimum value
      int green_tone = 255 - (int)color_val;

      // just check we've got a valid colour
      green_tone = Math.min(250, green_tone);

      res = converter.convertColor(88, green_tone, 88);

     // res = new Color(88, green_tone, 88);

    }
    else
    {
      // BELOW WATER

      // switch to positive
      double val = elevation;
      if(val < lowerLimit)
        val = lowerLimit;

      double proportion = val / lowerLimit;

      double color_val = proportion * ETOPOWrapper.BLUE_MULTIPLIER;

      // limit the colour val to the minimum value
      int blue_tone = 255 - (int)color_val;

      // just check we've got a valid colour
      blue_tone = Math.min(250, blue_tone);

      int green =  (int)ETOPOWrapper.GREEN_BASE_VALUE + (int)(blue_tone * ETOPOWrapper.GREEN_MULTIPLIER);

      res = converter.convertColor(ETOPOWrapper.RED_COMPONENT, green, blue_tone);

    }

    return res;
  }
	

	/**
	 * over-rideable method to constrain max value to zero (such as when not
	 * plotting land)
	 * 
	 * @return yes/no
	 */
	protected final boolean zeroIsMax()
	{
		// if we are showing land, then we don't want zero to be the top value
		return !_showLand;
	}

	/**
	 * @param val
	 *          the location to get the depth for
	 * @return the depth (in m)
	 */
	public final int getValueAt(final WorldLocation val)
	{
		final int res;

		// is it valid
		if (!val.isValid())
		{
			res = 0;
		}
		else
		{

			// now get it's index
			final int index = getIndex(val);

			// and get the value itself
			res = getValueAtIndex(index);
		}

		return res;
	}

	private int getValueAtIndex(final int index)
	{
		int res = 0;

		// just check we have the file open
		openFile();

		// just check we have a +ve (valid) index
		if (index >= 0)
		{
			// and retrieve the value
			try
			{
				ra.seek(index);
				res = ra.readShort();

				// rescale as appropriate
				res = rescaleValue(res);
			} catch (IOException e)
			{
				e.printStackTrace(); // To change body of catch statement use Options |
				// File Templates.
			}
		}
		return res;
	}

	/**
	 * rescale the data-set, if necessary
	 * 
	 * @param val
	 *          the depth as read from file
	 * @return the rescaled depth (from feet to metres in this case)
	 */
	private static int rescaleValue(final int val)
	{
		// convert from feet to metres
		// return (int)MWC.Algorithms.Conversions.ft2m(val);
		return val;
	}

	/**
	 * get the index for a particular point
	 * 
	 * @param val
	 *          the location we want the index for
	 * @return the index into the array for this position
	 */
	private int getIndex(final WorldLocation val)
	{
		final int res;

		// and the res
		res = 2 * ((getLatIndex(val) * getHorizontalNumber()) + getLongIndex(val));

		return res;
	}

	/**
	 * get the longitude for the indicated index
	 */
	private static double getLongitudeFor(final int index)
	{
		double res;

		// convert to mins
		res = index * 2;

		// add 1 for luck
		res += 1;

		// convert to degs
		res /= 60;

		// put in hemisphere
		res -= 180;

		return res;
	}

	/**
	 * get the latitude for the indicated index
	 */
	private static double getLatitudeFor(final int index)
	{
		double res;

		res = index;

		// convert to mins
		res *= 2;

		// add 1 for luck
		res += 1;

		// convert to degs
		res /= 60;

		// put in hemisphere
		if (res > 90)
		{
			res = -(res - 90);
		}
		else
		{
			res = 90 - res;
		}

		return res;
	}

	/**
	 * get the lat component of this location
	 */
	protected final int getLatIndex(final WorldLocation val)
	{
		// get the components
		double lat = val.getLat();
		final int lat_index;

		// work out how far down the lat is
		if (lat > 0)
		{
			// convert to down
			lat = 90d - lat;
		}
		else
			lat = 90 + Math.abs(lat);

		// convert to mins
		lat = lat * 60;

		// convert to 2 mins intervals
		lat = lat / 2;

		lat_index = (int) lat;

		return lat_index;
	}

	/**
	 * get the long component of this location
	 */
	protected final int getLongIndex(final WorldLocation val)
	{
		// get the components
		double lon = val.getLong();

		final int long_index;

		// work out how far acrss the lat is
		if (lon < 0)
		{
			lon = 180 + lon;
		}
		else
			lon = 180 + lon;

		// convert to secs
		lon = lon * 60;

		// convert to 2 sec intervals
		lon = lon / 2;

		long_index = (int) lon;

		return long_index;
	}

	/**
	 * accessor for whether to show land
	 */
	public final boolean getShowLand()
	{
		return _showLand;
	}

	/**
	 * setter for whether to show land
	 */
	public final void setShowLand(final boolean val)
	{
		_showLand = val;
	}

	// //////////////////////////////////
	// editor support
	// //////////////////////////////////

	public final Editable.EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new MARTOPOInfo(this);

		return _myEditor;
	}

	public final class MARTOPOInfo extends Editable.EditorType implements
			java.io.Serializable
	{


		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public MARTOPOInfo(final ETOPO_2_Minute data)
		{
			super(data, data.getName(), "");
		}

		public final java.beans.PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final java.beans.PropertyDescriptor[] res = {
						prop("Visible", "whether this layer is visible", VISIBILITY),
						longProp("KeyLocation", "the current location of the color-key", KeyLocationPropertyEditor.class, EditorType.FORMAT),
						prop("Color", "the color of the color-key", EditorType.FORMAT),
						prop("ShowLand", "whether to shade land-data", EditorType.FORMAT),
						longProp("LineThickness", "the thickness to plot the scale border", LineWidthPropertyEditor.class, EditorType.FORMAT),
						prop(
								"BathyRes",
								"the size of the grid at which to plot the shaded bathy (larger blocks gives faster performance)", EditorType.FORMAT),
						prop("BathyVisible", "whether to show the gridded contours", VISIBILITY),
						prop("ContourDepths", "the contour depths to plot", EditorType.FORMAT),
						prop("ContoursVisible", "whether to show the contours", VISIBILITY),
						prop(
								"ContourGridInterval",
								"the interval at which to calculate the contours (larger interval leads to faster performance)", EditorType.FORMAT),
						prop("ContourOptimiseGrid",
								"whether the grid interval should be optimised", EditorType.FORMAT)};
				return res;
			} catch (java.beans.IntrospectionException e)
			{
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class Etopo2Test extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		static public String THE_PATH;

		public Etopo2Test(final String val)
		{
			super(val);

			String pathFromProp = System.getProperty("etopoDir");
			if (pathFromProp == null)
			{
				THE_PATH = "C:\\Program Files\\Debrief 2003\\etopo";
			}
			else
				THE_PATH = pathFromProp;

		}

		public final void testMyParams()
		{
			ETOPO_2_Minute ed = new ETOPO_2_Minute(null);
			ed.setName("blank");
			editableTesterSupport.testParams(ed, this);
			ed = null;
		}

		// check data
		public void testFindData()
		{
			final String thefile = THE_PATH;
			assertTrue("Failed to find the 2 minute dataset:" + thefile,
					ETOPO_2_Minute.dataFileExists(thefile));
		}

		public void testConversions()
		{
			final ETOPO_2_Minute e2m = new ETOPO_2_Minute(THE_PATH);

			WorldLocation loc = new WorldLocation(54, -3, 0);
			int lat = e2m.getLatIndex(loc);
			int lon = e2m.getLongIndex(loc);
			double dLat = ETOPO_2_Minute.getLatitudeFor(lat);
			double dLong = ETOPO_2_Minute.getLongitudeFor(lon);
			WorldLocation loc2 = new WorldLocation(dLat, dLong, 0);

			System.out.println("dist:" + loc2.subtract(loc).getRange());
			assertTrue("points close enough, original: " + loc + " res:" + loc2, loc2
					.subtract(loc).getRange() < 1);

			loc = new WorldLocation(-54, -3, 0);
			lat = e2m.getLatIndex(loc);
			lon = e2m.getLongIndex(loc);
			dLat = ETOPO_2_Minute.getLatitudeFor(lat);
			dLong = ETOPO_2_Minute.getLongitudeFor(lon);
			loc2 = new WorldLocation(dLat, dLong, 0);
			assertTrue("points close enough, original: " + loc + " res:" + loc2, loc2
					.subtract(loc).getRange() < 1);

			loc = new WorldLocation(-54, 3, 0);
			lat = e2m.getLatIndex(loc);
			lon = e2m.getLongIndex(loc);
			dLat = ETOPO_2_Minute.getLatitudeFor(lat);
			dLong = ETOPO_2_Minute.getLongitudeFor(lon);
			loc2 = new WorldLocation(dLat, dLong, 0);
			assertTrue("points close enough, original: " + loc + " res:" + loc2, loc2
					.subtract(loc).getRange() < 1);

			loc = new WorldLocation(54, 3, 0);
			lat = e2m.getLatIndex(loc);
			lon = e2m.getLongIndex(loc);
			dLat = ETOPO_2_Minute.getLatitudeFor(lat);
			dLong = ETOPO_2_Minute.getLongitudeFor(lon);
			loc2 = new WorldLocation(dLat, dLong, 0);
			assertTrue("points close enough, original: " + loc + " res:" + loc2, loc2
					.subtract(loc).getRange() < 1);

			// assertEquals(loc, loc2);
		}
	}

	public static WorldArea theArea = null;
	//
	// public static void main(final String[] args) {
	// System.out.println("testing TOPO_2");
	// final Etopo2Test tm = new Etopo2Test("test");
	// tm.testFindData();
	// tm.ConversionsTest();
	//
	// final JFrame jf = new JFrame("test ETOPO & Contours");
	// jf.setSize(500, 500);
	// final Container cnt = jf.getContentPane();
	// cnt.setLayout(new BorderLayout());
	// final Layers theLayers = new Layers();
	// final SwingChart sc = new SwingChart(theLayers);
	// cnt.add("Center", sc.getPanel());
	// jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//
	// final ZoomIn zi = new ZoomIn(sc, null);
	// zi.execute();
	//
	// final ZoomOut zo = new ZoomOut(null, sc);
	// final JButton jb = new JButton("Zoom out");
	// jb.addActionListener(new java.awt.event.ActionListener()
	// {
	// /**
	// * Invoked when an action occurs.
	// */
	// public void actionPerformed(final ActionEvent e) {
	// zo.execute();
	// }
	// });
	// cnt.add("South", jb);
	//
	// final Layer baseLayer = new BaseLayer();
	// baseLayer.setName("decs");
	// theLayers.addThisLayer(baseLayer);
	// baseLayer.setVisible(true);
	//
	// final CoastPainter cp = new CoastPainter();
	// cp.setColor(Color.gray);
	// cp.setVisible(true);
	// final GridPainter gp = new GridPainter();
	// gp.setColor(Color.darkGray);
	// gp.setPlotLabels(false);
	// gp.setDelta(new WorldDistanceWithUnits(10, WorldDistance.NM));
	// gp.setVisible(true);
	//
	// baseLayer.add(cp);
	// baseLayer.add(gp);
	//
	// // now the ETOPO
	// final ETOPO_2_Minute e2 = new
	// ETOPO_2_Minute("d:\\dev\\debrief\\debrief_out\\etopo");
	// e2.setVisible(true);
	// theLayers.addThisLayer(e2);
	//
	// // create our area
	// final WorldLocation w1 = new WorldLocation(27.3934716,52.4942742, 0);
	// final WorldLocation w2 = new WorldLocation(26.9865997,52.9470112, 0);
	// final WorldArea wa = new WorldArea(w1, w2);
	// theArea = wa;
	// sc.getCanvas().getProjection().setDataArea(wa);
	//
	// sc.repaint();
	//
	// // ok, show it
	// jf.setVisible(true);
	// }

}
