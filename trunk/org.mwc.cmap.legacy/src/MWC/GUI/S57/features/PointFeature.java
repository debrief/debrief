/**
 * 
 */
package MWC.GUI.S57.features;

import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Vector;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GenericData.WorldLocation;

public class PointFeature extends S57Feature
{
	private Vector<WorldLocation> _pts;
	private Color _myColor = Color.DARK_GRAY;

	final public static Double DEFAULT_SCALE = 100000d;
	PointPainter _myPainter;
	
	public PointFeature(String name, Double minScale,
			Color defaultColor, PointPainter painter)
	{
		super(name, minScale);
		_myColor = defaultColor;
		_myPainter = painter;
		_pts = new Vector<WorldLocation>(0,1);
	}
	
	/**
	 * @return the _myColor
	 */
	public final Color getColor()
	{
		return _myColor;
	}
	/**
	 * @param color the _myColor to set
	 */
	public final void setColor(Color color)
	{
		_myColor = color;
	}

	EditorType createEditor()
	{
		return new PointInfo(this, getName());
	}

	public void doPaint(CanvasType dest)
	{
		dest.setColor(_myColor);		
		for (Iterator<WorldLocation> iter = _pts.iterator(); iter.hasNext();)
		{
			WorldLocation loc = (WorldLocation) iter.next();
			Point pt =  dest.toScreen(loc);
			if(_myPainter != null)
				_myPainter.paintSymbol(dest, loc, pt);
		}
	}
	
	public void paintSymbol(Point pt)
	{
		// don't bother - let it get overridden
	}
	
	public void add(Vector<WorldLocation> theList)
	{
		_pts.addAll(theList);
	}
	
	
	public class PointInfo extends Editable.EditorType
	{

		public PointInfo(PointFeature data, String theName)
		{
			super(data, theName, "");
		}

		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				PropertyDescriptor[] res = {
						prop("Color", "color to plot the points", FORMAT)
				};
				return res;
			}
			catch (IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}
	}	
	
	
	public static interface PointPainter
	{
		public void paintSymbol(CanvasType dest, WorldLocation loc, Point pt);
	}	
	
	public static class DepthPainter implements PointPainter
	{
		NumberFormat _nf = new DecimalFormat("0.0");
		public void paintSymbol(CanvasType dest, WorldLocation loc, Point pt)
		{
			dest.drawText(_nf.format(loc.getDepth()), pt.x, pt.y);
		}
	}
	
	public static class LabelPainter implements PointPainter
	{
		private final String _myLabel;
		private boolean _showMarker = true;
		
		public LabelPainter(String label)
		{
			_myLabel = label;
		}
		
		public LabelPainter(String label, boolean showMarker)
		{
			this(label);
			_showMarker = showMarker;
		}
		
		public void paintSymbol(CanvasType dest, WorldLocation loc, Point pt)
		{
			if(_showMarker)
			{
				dest.drawRect(pt.x - 2, pt.y - 2, 5,5);
				dest.drawText(_myLabel, pt.x, pt.y - 5);
			}
			else
			dest.drawText(_myLabel, pt.x, pt.y);			
		}
	}
}