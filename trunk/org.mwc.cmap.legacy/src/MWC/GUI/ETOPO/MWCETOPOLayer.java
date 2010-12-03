/* **********************************************************************
 * 
 *    Use, duplication, or disclosure by the Government is subject to
 * 	     restricted rights as set forth in the DFARS.
 *  
 * 			   BBNT Solutions LLC
 * 			      A Part of  
 * 			         GTE      
 * 			  10 Moulton Street
 * 			 Cambridge, MA 02138
 * 			    (617) 873-3000
 *  
 * 	  Copyright 1998, 2000 by BBNT Solutions LLC,
 * 		A part of GTE, all rights reserved.
 *  
 * **********************************************************************
 * 
 * $Source: i:/mwc/coag/asset/cvsroot/util/MWC/GUI/ETOPO/MWCETOPOLayer.java,v $
 * $Revision: 1.2 $
 * $Date: 2004/05/25 15:25:43 $
 * $Author: Ian.Mayo $
 * 
 * **********************************************************************
 */

package MWC.GUI.ETOPO;

/*  Java Core  */
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.Layer;
import com.bbn.openmap.event.InfoDisplayEvent;
import com.bbn.openmap.event.LayerStatusEvent;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.io.BinaryBufferedFile;
import com.bbn.openmap.io.FormatException;
import com.bbn.openmap.layer.util.LayerUtils;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMRaster;
import com.bbn.openmap.proj.CADRG;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.util.Debug;
import com.bbn.openmap.util.PaletteHelper;
import com.bbn.openmap.util.SwingWorker;

/**
 * MWCETOPOLayer extends Layer to provide rendering of the ETOPO5 world
 * elevation data set. The ETOPO5 data consists of terrain altitude and ocean
 * depth measurements at 5 minute intervals for the entire globe. Rendering is
 * allowed in any projection that implements the inverse(int,int) method. Two
 * types of rendering are provided: grayscale slope-shaded and colored
 * slope-shaded.
 * <p>
 * The distribution consists of the following:
 * <ul>
 * <li>1. MWCETOPOLayer.java</li>
 * <li>2. ETOPO5 (5 minute spacing data set, 4320x2160 shorts, ~18MB)</li>
 * <li>3. ETOPO10 (10 minute spacing data set, sampled from ETOPO5, ~4.6MB)</li>
 * <li>4. ETOPO15 (15 minute spacing data set, sampled from ETOPO5, ~2MB)</li>
 * <li>5. MWCETOPOLayer.properties (example properties for openmap.properties)</li>
 * </ul>
 * <p>
 * The sampled ETOPO data sets are provided to speed up the loading of data to
 * compute the slope shading. The algorithm inverse projects the x/y screen
 * coords (for the entire projection screen space) to get the corresponding
 * lat/lon coords then samples the database to get altitude/depth and slope
 * values. While this method is slower than the forward projection method, it
 * does provide a more attractive screen presentation and will support all
 * projections (not just the equidistant cylindrical). A palette provides the
 * ability to choose between the 5,10, or 15 minute resolutions, as well as
 * color or grayscale selection, transparency, and slope contrast.
 * <p>
 * The MWCETOPOLayer also relies on properties to set its variables, such as the
 * etopo frame paths (there can be several at a time), the opaqueness of the
 * frame images, number of colors to use, and some other display variables. The
 * MWCETOPOLayer properties look something like this:
 * <P>
 * 
 * #------------------------------<BR>
 * # Properties for MWCETOPOLayer<BR>
 * #------------------------------<BR>
 * # This property should reflect the paths to the etopo directory<BR>
 * etopo.path=c:/openmap/share<BR>
 * <BR>
 * # Number between 0-255: 0 is transparent, 255 is opaque<BR>
 * etopo.opaque=255<BR>
 * <BR>
 * # Number of colors to use on the maps - 16, 32, 216<BR>
 * etopo.number.colors=216<BR>
 * <BR>
 * # Type of display for the data<BR>
 * # 0 = grayscale slope shading<BR>
 * # 1 = colored slope shading<BR>
 * etopo.view.type=1<BR>
 * <BR>
 * # Contrast setting, 1-5<BR>
 * etopo.contrast=3<BR>
 * <BR>
 * # lat/lon spacing in minutes<BR>
 * # must be 5, 10, or 15<BR>
 * etopo.minute.spacing=10<BR>
 * <BR>
 * #-------------------------------------<BR>
 * # End of properties for MWCETOPOLayer<BR>
 * #-------------------------------------<BR>
 * 
 * */
public class MWCETOPOLayer extends Layer implements ActionListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Gray scale slope shading, sun from the Northwest. */
	public static final int SLOPESHADING = 0;

	/**
	 * Colorized slope shading. Color basnds are based on elevation, and are
	 * accented by shaded indications.
	 */
	public static final int COLOREDSHADING = 1;

	/** Default contrast setting for slope shading. */
	public static final int DEFAULT_SLOPE_ADJUST = 3;

	/** Default minute spacing */
	public static final int DEFAULT_MINUTE_SPACING = 10;

	/** for colorizing */
	public final static int DEFAULT_OPAQUENESS = 255;

	/** The graphics list used for display. */
	protected OMGraphicList omGraphics;

	/** Projection that gets set on a projection event. */
	Projection projection;

	/**
	 * Set when the projection has changed while a swing worker is gathering
	 * graphics, and we want him to stop early.
	 */
	protected boolean cancelled = false;

	/**
	 * The paths to the ETOPO directory, telling where the data is.
	 */
	protected String path;

	/** The etopo elevation data */
	protected short[] dataBuffer = null;
	protected int bufferWidth;
	protected int bufferHeight;

	/** The current resolution (in minutes) */
	protected int minuteSpacing;
	//
	// /** ETOPO elevation files */
	// protected final static String[] etopoFileNames =
	// {"/ETOPO2", "/ETOPO5", "/ETOPO10", "/ETOPO15"}; //ep-g
	//
	// /** dimensions of the ETOPO files (don't mess with these!) */
	// protected final static int[] etopoWidths = {10800, 4320, 2160, 1440};//ep-g
	// protected final static int[] etopoHeights = {5400, 2160, 1080, 720}; //ep-g
	//
	// /** Spacings (in meters) between adjacent lon points at the
	// * equater. The values here were aesthetically defined (they are
	// * not the actual spacings) */
	// protected final static double[] etopoSpacings =
	// {1800., 3500., 7000., 10500.}; //ep-g

	/** ETOPO elevation files */
	protected final static String[] etopoFileNames =
	{ "/ETOPO5", "/ETOPO10", "/ETOPO15" }; // ep-g

	/** dimensions of the ETOPO files (don't mess with these!) */
	protected final static int[] etopoWidths =
	{ 4320, 2160, 1440 };// ep-g
	protected final static int[] etopoHeights =
	{ 2160, 1080, 720 }; // ep-g

	/**
	 * Spacings (in meters) between adjacent lon points at the equater. The values
	 * here were aesthetically defined (they are not the actual spacings)
	 */
	protected final static double[] etopoSpacings =
	{ 3500., 7000., 10500. }; // ep-g

	/**
	 * The display type for the etopo images. Slope shading is grayscale terrain
	 * modeling with highlights and shading, with the 'sun' being in the
	 * NorthWest. Colored Elevation shading is the same thing, except colors are
	 * added to indicate the elevation. Band shading colors the pixels according
	 * to a range of elevations.
	 */
	protected int viewType;

	/** The elevation range to use for each color in band shading. */
	protected int bandHeight;

	/** A contrast adjustment, for slope shading (1-5). */
	protected int slopeAdjust;

	/** transparency control */
	protected int opaqueness;

	/** property suffixes */
	public static final String ETOPOPathProperty = ".path";
	public static final String OpaquenessProperty = ".opaque";
	public static final String ETOPOViewTypeProperty = ".view.type";
	public static final String ETOPOSlopeAdjustProperty = ".contrast";
	public static final String ETOPOMinuteSpacingProperty = ".minute.spacing";

	/**
	 * Holds the slope values, updated when the resolution changes or the slope
	 * adjustment (contrast) is changed. Slope values are scaled between -127 to
	 * 127.
	 */
	protected byte[] slopeMap = null;

	/** elevation bands */
	protected static final int[] elevLimit =
	{ -11000, -9000, -7000, -5000, -3000, -1500, 0, 250, 500, 750, 1000, 2000,
			3500, 5000 };
	/** number of elevation bands */
	protected static final int elevLimitCnt = 14;

	/** elevation band colors (one for each elevation band) */
	protected static final int[] redElev =
	{ 0, 0, 4, 20, 124, 130, 135, 117, 252, 253, 229, 244, 252, 132 };
	protected static final int[] greenElev =
	{ 2, 12, 51, 159, 235, 255, 235, 255, 236, 162, 115, 50, 20, 132 };
	protected static final int[] blueElev =
	{ 76, 145, 242, 249, 252, 255, 110, 58, 29, 35, 5, 14, 46, 132 };

	/** for slope shading colors, indexed by elevation band then slope */
	protected static Color[][] slopeColors = null;

	/* flag to recompute slope map */
	protected boolean slopeReset = true;

	/* flag to load new elevation file */
	protected boolean spacingReset = true;

	/* returns the color lookup index based on elevation */
	protected int getElevIndex(short el)
	{
		for (int i = 0; i < elevLimitCnt - 1; i++)
			if (el < elevLimit[i + 1])
				return i;
		return elevLimitCnt - 1;
	}

	/* returns a color based on slope and elevation */
	protected Color getColor(short elevation, byte slopeVal)
	{
		// build first time
		if (slopeColors == null)
		{

			// allocate storage for elevation bands, 8 slope bands
			slopeColors = new Color[elevLimitCnt][8];

			// process each elevation band
			for (int i = 0; i < elevLimitCnt; i++)
			{

				// get base color (0 slope color)
				Color base = new Color(redElev[i], greenElev[i], blueElev[i]);

				// call the "brighter" method on the base color for
				// positive slope
				for (int j = 4; j < 8; j++)
				{

					// set
					if (j == 4)
						slopeColors[i][j] = base;
					else
						slopeColors[i][j] = slopeColors[i][j - 1].brighter();

				}

				// call the "darker" method on the base color for
				// negative slopes
				for (int k = 3; k >= 0; k--)
				{

					// set
					slopeColors[i][k] = slopeColors[i][k + 1].darker();

				}

			}
		}

		// get the elevation band index
		int elIdx = getElevIndex(elevation);

		// compute slope idx
		int slopeIdx = ((int) slopeVal + 127) / 32;

		// return color
		Color norm = slopeColors[elIdx][slopeIdx];

		// set alpha
		return new Color(norm.getRed(), norm.getGreen(), norm.getBlue(), opaqueness);

	}

	class ETOPOWorker extends SwingWorker
	{

		/** Constructor used to create a worker thread. */
		public ETOPOWorker()
		{
			super();
		}

		/**
		 * Compute the value to be returned by the <code>get</code> method.
		 */
		public Object construct()
		{
			Debug.message("etopo", getName() + "|ETOPOWorker.construct()");
			fireStatusUpdate(LayerStatusEvent.START_WORKING);
			try
			{
				return prepare();
			}
			catch (OutOfMemoryError e)
			{
				String msg = getName() + "|MWCETOPOLayer.ETOPOWorker.construct(): " + e;
				Debug.error(msg);
				fireRequestMessage(new InfoDisplayEvent(this, msg));
				fireStatusUpdate(LayerStatusEvent.FINISH_WORKING);
				return null;
			}
		}

		/**
		 * Called on the event dispatching thread (not on the worker thread) after
		 * the <code>construct</code> method has returned.
		 */
		public void finished()
		{
			workerComplete(this);
			fireStatusUpdate(LayerStatusEvent.FINISH_WORKING);
		}
	}

	/** The thread worker used to create the ETOPO images. */
	ETOPOWorker currentWorker;

	/**
	 * The default constructor for the Layer. All of the attributes are set to
	 * their default values.
	 */
	public MWCETOPOLayer()
	{
		this(null);
	}

	/**
	 * The default constructor for the Layer. All of the attributes are set to
	 * their default values.
	 * 
	 * @param pathToETOPODir
	 *          path to the directory holding the ETOPO data
	 */
	public MWCETOPOLayer(String pathToETOPODir)
	{
		setDefaultValues();
		path = pathToETOPODir;
	}

	public void setPath(String pathToETOPODir)
	{
		path = pathToETOPODir;
	}

	protected void setDefaultValues()
	{
		// defaults
		path = null;
		dataBuffer = null;
		opaqueness = DEFAULT_OPAQUENESS;
		slopeAdjust = DEFAULT_SLOPE_ADJUST;
		viewType = COLOREDSHADING;
		minuteSpacing = DEFAULT_MINUTE_SPACING;
	}

	/**
	 * Sets the current graphics list to the given list.
	 * 
	 * @param aList
	 *          a list of OMGraphics
	 */
	public synchronized void setGraphicList(OMGraphicList aList)
	{
		omGraphics = aList;
	}

	/**
	 * Retrieves the current graphics list.
	 */
	public synchronized OMGraphicList getGraphicList()
	{
		return omGraphics;
	}

	/**
	 * Set all the ETOPO properties from a properties object.
	 */
	public void setProperties(String prefix, java.util.Properties properties)
	{

		super.setProperties(prefix, properties);

		path = properties.getProperty(prefix + ETOPOPathProperty);

		opaqueness = LayerUtils.intFromProperties(properties, prefix
				+ OpaquenessProperty, DEFAULT_OPAQUENESS);

		viewType = LayerUtils.intFromProperties(properties, prefix
				+ ETOPOViewTypeProperty, COLOREDSHADING);

		slopeAdjust = LayerUtils.intFromProperties(properties, prefix
				+ ETOPOSlopeAdjustProperty, DEFAULT_SLOPE_ADJUST);

		minuteSpacing = LayerUtils.intFromProperties(properties, prefix
				+ ETOPOMinuteSpacingProperty, DEFAULT_MINUTE_SPACING);

	}

	/**
	 * Called when the layer is no longer part of the map. In this case, we should
	 * disconnect from the server if we have a link.
	 */
	public void removed(java.awt.Container cont)
	{
	}

	/**
	 * Used to set the cancelled flag in the layer. The swing worker checks this
	 * once in a while to see if the projection has changed since it started
	 * working. If this is set to true, the swing worker quits when it is safe.
	 */
	public synchronized void setCancelled(boolean set)
	{
		cancelled = set;
	}

	/** Check to see if the cancelled flag has been set. */
	public synchronized boolean isCancelled()
	{
		return cancelled;
	}

	/**
	 * Implementing the ProjectionPainter interface.
	 */
	public synchronized void renderDataForProjection(Projection proj,
			java.awt.Graphics g)
	{
		if (proj == null)
		{
			Debug.error("MWCETOPOLayer.renderDataForProjection: null projection!");
			return;
		}
		else if (!proj.equals(projection))
		{
			projection = proj.makeClone();
			setGraphicList(prepare());
		}
		paint(g);
	}

	/**
	 * From the ProjectionListener interface.
	 */
	public void projectionChanged(ProjectionEvent e)
	{
		Debug.message("basic", getName() + "|MWCETOPOLayer.projectionChanged()");

		if (projection != null)
		{
			if (projection.equals(e.getProjection()))
			// Nothing to do, already have it and have acted on it...
			{
				repaint();
				return;
			}
		}
		setGraphicList(null);

		projection = (Projection) e.getProjection().makeClone();
		doPrepare();
	}

	/**
	 * The ETOPOWorker calls this method on the layer when it is done working. If
	 * the calling worker is not the same as the "current" worker, then a new
	 * worker is created.
	 * 
	 * @param worker
	 *          the worker that has the graphics.
	 * */
	protected synchronized void workerComplete(ETOPOWorker worker)
	{
		if (!isCancelled())
		{
			currentWorker = null;
			setGraphicList((OMGraphicList) worker.get());
			repaint();
		}
		else
		{
			setCancelled(false);
			currentWorker = new ETOPOWorker();
			currentWorker.execute();
		}
	}

	/**
	 * Builds the slope index map. This method is called when the ETOPO resolution
	 * changes and when the slope contrast changes. The slope of the terrain is
	 * cliped; slopes are between the range of +/- 45 deg. The calculated slope
	 * value is then linearly scaled to the range +/- 127.
	 */
	protected void buildSlopeMap()
	{
		// this should never happen, but...
		if (dataBuffer == null)
			return;

		// get resolution index
		int resIdx = minuteSpacing / 5 - 1;
		if (resIdx < 0)
			resIdx = 0;
		else if (resIdx > 2)
			resIdx = 2;

		// Set deltaX constant. The deltaX is actually is smaller at latitude
		// extremes, but
		double deltaX = etopoSpacings[resIdx];

		// allocate storage for slope map
		slopeMap = new byte[bufferWidth * bufferHeight];

		// process dataBuffer to create slope
		for (int y = 0; y < bufferHeight; y++)
		{

			// compute the lattitude of this
			double lat = 90. - 180. * (double) y / (double) bufferHeight;

			// get cosine of the latitude. This is used because the
			// spacing between minutes gets smaller in high latitude
			// extremes.
			double coslat = Math.cos(Math.toRadians(lat));

			// for scaling the slope
			double slopeScaler = (double) slopeAdjust * coslat / deltaX;

			// indexcies
			int idx0 = y * bufferWidth;

			// do each row
			for (int x = 0; x < bufferWidth; x++)
			{

				// indexcies
				int idx1 = idx0 + x;
				int idx2 = idx1 + bufferWidth;

				// special case at end
				if (y == bufferHeight - 1)
					idx2 = idx1;

				// get altitudes
				double d1 = (double) dataBuffer[idx1];
				double d2 = (double) dataBuffer[idx2];

				// compute (lookup) slope
				double slope = slopeScaler * (d2 - d1);

				// clip
				if (slope > 0.99)
					slope = 0.99;
				else if (slope < -0.99)
					slope = -0.99;

				// scale
				int islope = (int) (slope * 127.);

				// store
				slopeMap[idx1] = (byte) islope;

			}
		}
	}

	/**
	 * Loads the database from the appropriate file based on the current
	 * resolution. The data files are in INTEL format (must call
	 * BinaryBufferedFile.byteOrder(true)).
	 */
	protected void loadBuffer()
	{

		// get the resolution index
		int resIdx = minuteSpacing / 5 - 1;
		if (resIdx < 0)
			resIdx = 0;
		else if (resIdx > 2)
			resIdx = 2;

		// build file name
		String fileName = new String(path + etopoFileNames[resIdx]);

		try
		{

			// open file
			File file = new File(fileName);

			// treat as buffered binary
			BinaryBufferedFile binFile = new BinaryBufferedFile(file);
			binFile.byteOrder(true);

			// set width/height
			bufferWidth = etopoWidths[resIdx];
			bufferHeight = etopoHeights[resIdx];

			// allocate storage
			dataBuffer = new short[(bufferWidth + 1) * bufferHeight];

			// read data
			int i = 0;
			for (i = 0; i < bufferWidth * bufferHeight; i++)
			{
				dataBuffer[i] = binFile.readShort();
			}

			// done
			binFile.close();

			// don't know why I have to do this, but...
			bufferWidth = bufferWidth + 1;

		}
		catch (FileNotFoundException e)
		{
			MWC.Utilities.Errors.Trace.trace(e, "MWCETOPOLayer loadBuffer(): file "
					+ fileName + " not found");
		}
		catch (IOException e)
		{
			MWC.Utilities.Errors.Trace.trace(e,
					"MWCETOPOLayer loadBuffer(): File IO Error!\n" + e.toString());
		}
		catch (FormatException e)
		{
			MWC.Utilities.Errors.Trace.trace(e,
					"MWCETOPOLayer loadBuffer(): Format exception!\n" + e.toString());
		}

	}

	/*
	 * Builds the raster image that has the dimensions of the current projection.
	 * The alogorithm is is follows: <P> <pre> allocate storage the size of the
	 * projection (use ints for RGBA)
	 * 
	 * for each screen point
	 * 
	 * inverse project screen point to get lat/lon (world coords) get altitude
	 * and/or slope at the world coord compute (lookup) color at the world coord
	 * set color value into screen coord location
	 * 
	 * end
	 * 
	 * create OMRaster from the int array data. </pre>
	 * 
	 * The code contains a HACK (primarily for the Orthographic projection) since
	 * * x/y values which would return an "Outer Space" value actually return
	 * lat/lon values for the center of the projection (see
	 * Orthographic.inverse(...)). This resulted in the "Outer Space" being
	 * painted the color of whatever the center lat/lon was. The HACK turns any
	 * center lat/lon value into black. Of course, this causes valid center
	 * lat/lon values to be painted black, but the trade off is worth it visually.
	 * The appropriate method may be to have Projection.inverse and its variants
	 * raise an exception for "Outer Space" values.
	 */
	protected OMRaster buildRaster()
	{
		// initialize the return
		OMRaster ret = null;

		// work with the slopeMap
		if (slopeMap != null)
		{

			// compute our deltas
			int width = projection.getWidth();
			int height = projection.getHeight();

			// create int array to hold colors
			int[] colors = new int[width * height];

			// compute scalers for lat/lon indicies
			float scy = (float) bufferHeight / 180F;
			float scx = (float) bufferWidth / 360F;

			// starting and ending indices
			int sx = 0, sy = 0, ex = width, ey = height;

			// handle CADRG
			if (projection instanceof CADRG)
			{

				// get corners
				LatLonPoint ul = projection.getUpperLeft();
				LatLonPoint lr = projection.getLowerRight();

				// set start/end indicies
				Point ulp = projection.forward(ul);
				Point lrp = projection.forward(lr);
				sx = (int) ulp.getX();
				ex = (int) lrp.getX();
				sy = (int) ulp.getY();
				ey = (int) lrp.getY();

			}

			// get the center lat/lon (used by the HACK, see above in method
			// description)
			LatLonPoint center = projection.getCenter();
			LatLonPoint llp = new LatLonPoint();
			// build array
			for (int y = sy; y < ey; y++)
			{

				// process each column
				for (int x = sx; x < ex; x++)
				{

					// inverse project x,y to lon,lat
					projection.inverse(x, y, llp);

					// get point values
					float lat = llp.getLatitude();
					float lon = llp.getLongitude();

					// check
					if (lon < 0.)
						lon += 360.;

					// find indicies
					int lat_idx = (int) ((90. - lat) * scy);
					int lon_idx = (int) (lon * scx);

					// offset
					int ofs = lon_idx + lat_idx * bufferWidth;

					// make a color
					int idx = 0;
					int gray = 0;
					try
					{

						// get elevation
						short el = dataBuffer[ofs];

						// slope
						byte sl = slopeMap[ofs];

						// our index
						idx = y * width + x;

						// create a color
						Color pix = null;
						if (viewType == SLOPESHADING)
						{
							// HACK (see method description above)
							if ((llp.getLatitude() == center.getLatitude())
									&& (llp.getLongitude() == center.getLongitude()))
								gray = 0;
							else
								gray = 127 + sl;
							pix = new Color(gray, gray, gray, opaqueness);
						}
						else if (viewType == COLOREDSHADING)
						{
							// HACK (see method description above)
							if ((llp.getLatitude() == center.getLatitude())
									&& (llp.getLongitude() == center.getLongitude()))
								pix = new Color(0, 0, 0, opaqueness);
							else
								pix = getColor(el, sl);
						}

						// set
						if (pix != null)
							colors[idx] = pix.getRGB();

					}

					// tried to set a bad color level
					catch (IllegalArgumentException e)
					{
						Debug.error(e.toString() + ":" + gray);
					}

					// bad index
					catch (ArrayIndexOutOfBoundsException e)
					{
						Debug.error(e.toString() + ":" + idx);
					}
				}
			}

			// create the raster
			ret = new OMRaster(0, 0, width, height, colors);

		}

		// return or raster
		return ret;

	}

	public void doPrepare()
	{
		// If there isn't a worker thread working on this already,
		// create a thread that will do the real work. If there is
		// a thread working on this, then set the cancelled flag
		// in the layer.
		if (currentWorker == null)
		{
			fireStatusUpdate(LayerStatusEvent.START_WORKING);
			currentWorker = new ETOPOWorker();
			currentWorker.execute();
		}
		else
			setCancelled(true);
	}

	/**
	 * Prepares the graphics for the layer. This is where the getRectangle()
	 * method call is made on the etopo.
	 * <p>
	 * Occasionally it is necessary to abort a prepare call. When this happens,
	 * the map will set the cancel bit in the LayerThread, (the thread that is
	 * running the prepare). If this Layer needs to do any cleanups during the
	 * abort, it should do so, but return out of the prepare asap.
	 */
	public OMGraphicList prepare()
	{

		if (isCancelled())
		{
			Debug.message("etopo", getName() + "|MWCETOPOLayer.prepare(): aborted.");
			return null;
		}

		if (projection == null)
		{
			Debug
					.error("ETOPO Layer needs to be added to the MapBean before it can draw images!");
			return new OMGraphicList();
		}

		// load the buffer
		if (dataBuffer == null || spacingReset)
		{
			loadBuffer();
			spacingReset = false;
			slopeReset = true;
		}

		// re-do the slope map
		if (slopeReset)
		{
			buildSlopeMap();
			slopeReset = false;
		}

		Debug.message("basic", getName() + "|MWCETOPOLayer.prepare(): doing it");

		// Setting the OMGraphicsList for this layer. Remember, the
		// OMGraphicList is made up of OMGraphics, which are generated
		// (projected) when the graphics are added to the list. So,
		// after this call, the list is ready for painting.

		// call getRectangle();
		if (Debug.debugging("etopo"))
		{
			Debug.output(getName() + "|MWCETOPOLayer.prepare(): "
					+ "calling getRectangle " + " with projection: " + projection
					+ " ul = " + projection.getUpperLeft() + " lr = "
					+ projection.getLowerRight());
		}

		// build graphics list
		OMGraphicList omGraphicList = new OMGraphicList();
		omGraphicList.addOMGraphic(buildRaster());

		// ///////////////////
		// safe quit
		int size = 0;
		if (omGraphicList != null)
		{
			size = omGraphicList.size();
			Debug.message("basic", getName()
					+ "|MWCETOPOLayer.prepare(): finished with " + size + " graphics");
		}

		// Don't forget to project them. Since they are only being
		// recalled if the projection hase changed, then we need to
		// force a reprojection of all of them because the screen
		// position has changed.
		omGraphicList.project(projection, true);
		return omGraphicList;
	}

	/**
	 * Paints the layer.
	 * 
	 * @param g
	 *          the Graphics context for painting
	 * 
	 */
	public void paint(java.awt.Graphics g)
	{
		Debug.message("etopo", getName() + "|MWCETOPOLayer.paint()");

		OMGraphicList tmpGraphics = getGraphicList();

		if (tmpGraphics != null)
		{
			tmpGraphics.render(g);
		}

	}

	// ----------------------------------------------------------------------
	// GUI
	// ----------------------------------------------------------------------

	/** The user interface palette for the ETOPO layer. */
	protected Box palette = null;

	/** Creates the interface palette. */
	public java.awt.Component getGUI()
	{

		if (palette == null)
		{
			if (Debug.debugging("etopo"))
				Debug.output("MWCETOPOLayer: creating ETOPO Palette.");

			palette = Box.createVerticalBox();
			Box subbox0 = Box.createHorizontalBox();
			Box subbox1 = Box.createHorizontalBox();
			Box subbox2 = Box.createVerticalBox();
			Box subbox3 = Box.createHorizontalBox();

			// The ETOPO resolution selector
			JPanel resPanel = PaletteHelper.createPaletteJPanel("Lat/Lon Spacing");
			String[] resStrings =
			{ "5 Minute", "10 Minute", "15 Minute" };

			JComboBox resList = new JComboBox(resStrings);
			resList.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					JComboBox jcb = (JComboBox) e.getSource();
					int newRes = jcb.getSelectedIndex();
					int curRes = minuteSpacing / 5 - 1;
					if (curRes != newRes)
						spacingReset = true;
					switch (newRes)
					{
					case 0:
						minuteSpacing = 5;
						break;
					case 1:
						minuteSpacing = 10;
						break;
					case 2:
						minuteSpacing = 15;
						break;
					}

				}
			});

			resList.setSelectedIndex(minuteSpacing / 5 - 1);
			resPanel.add(resList);

			// The ETOPO view selector
			JPanel viewPanel = PaletteHelper.createPaletteJPanel("View Type");
			String[] viewStrings =
			{ "Grayscale Shading", "Color Shading" };

			JComboBox viewList = new JComboBox(viewStrings);
			viewList.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					JComboBox jcb = (JComboBox) e.getSource();
					int newView = jcb.getSelectedIndex();
					if (newView != viewType)
						slopeReset = true;
					switch (newView)
					{
					case 0:
						viewType = SLOPESHADING;
						break;
					case 1:
						viewType = COLOREDSHADING;
						break;
					}

				}
			});

			viewList.setSelectedIndex(viewType);
			viewPanel.add(viewList);

			// The ETOPO Contrast Adjuster
			JPanel contrastPanel = PaletteHelper
					.createPaletteJPanel("Contrast Adjustment");
			JSlider contrastSlide = new JSlider(JSlider.HORIZONTAL, 1/* min */,
					5/* max */, 3/* inital */);
			java.util.Hashtable<Integer, JLabel> dict = new java.util.Hashtable<Integer, JLabel>();
			dict.put(new Integer(1), new JLabel("min"));
			dict.put(new Integer(5), new JLabel("max"));
			contrastSlide.setLabelTable(dict);
			contrastSlide.setPaintLabels(true);
			contrastSlide.setMajorTickSpacing(1);
			contrastSlide.setPaintTicks(true);
			contrastSlide.addChangeListener(new ChangeListener()
			{
				public void stateChanged(ChangeEvent ce)
				{
					JSlider slider = (JSlider) ce.getSource();
					if (slider.getValueIsAdjusting())
					{
						Debug.output("MWCETOPOLayer - Contrast Slider value = "
								+ slider.getValue());
						slopeAdjust = slider.getValue();
					}
				}
			});
			contrastPanel.add(contrastSlide);

			// The ETOPO Opaqueness
			JPanel opaquenessPanel = PaletteHelper.createPaletteJPanel("Opaqueness");
			JSlider opaquenessSlide = new JSlider(JSlider.HORIZONTAL, 0/* min */,
					255/* max */, opaqueness/* inital */);
			opaquenessSlide.addChangeListener(new ChangeListener()
			{
				public void stateChanged(ChangeEvent ce)
				{
					JSlider slider = (JSlider) ce.getSource();
					if (slider.getValueIsAdjusting())
					{
						fireRequestInfoLine("MWCETOPOLayer - Opaqueness Slider value = "
								+ slider.getValue());
						opaqueness = slider.getValue();
					}
				}
			});

			opaquenessPanel.add(opaquenessSlide);

			JButton redraw = new JButton("Redraw ETOPO Layer");
			redraw.addActionListener(this);
			redraw.setActionCommand(RedrawCommand);

			subbox0.add(resPanel);
			palette.add(subbox0);
			subbox1.add(viewPanel);
			palette.add(subbox1);
			subbox2.add(contrastPanel);
			subbox2.add(opaquenessPanel);
			palette.add(subbox2);
			subbox3.add(redraw);
			palette.add(subbox3);
		}

		return palette;
	}

	public final static String RedrawCommand = "redrawCmd";

	// ----------------------------------------------------------------------
	// ActionListener interface implementation
	// ----------------------------------------------------------------------

	/**
	 * Used just for the redraw button.
	 */
	public void actionPerformed(ActionEvent e)
	{
		// super.actionPerformed(e);
		if (e.getActionCommand() == RedrawCommand)
		{
			doPrepare();
		}
	}

}
