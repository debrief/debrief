package MWC.GUI.S57.features;

import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Iterator;
import java.util.Vector;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GenericData.WorldLocation;

public class LineFeature extends S57Feature
{
	private Color _myColor;
	protected  Vector<Vector<WorldLocation>> _lines = new Vector<Vector<WorldLocation>>(0,1);
	final public static Double DEFAULT_SCALE = 1000000d;	
	
	public LineFeature(String name, Double minScale, Color defaultColor)
	{
		super(name, minScale);
		_myColor = defaultColor;
	}
	
	/** add a new line
	 * 
	 * @param pts
	 */
	public void addLine(Vector<WorldLocation> pts)
	{
		_lines.add(pts);
	}
	
	final EditorType createEditor()
	{
		return new LineFeatureInfo(this, getName());
	}

	public void doPaint(CanvasType dest)
	{
		dest.setColor(_myColor);
		int ctr = 0;
		int xOffset = 0;
		for (Iterator<Vector<WorldLocation>> iterator = _lines.iterator(); iterator.hasNext();)
		{
			Vector<WorldLocation> thisLine = (Vector<WorldLocation>) iterator.next();
			Point last = null;
			Point startPt = null;
			for (Iterator<WorldLocation> iter = thisLine.iterator(); iter.hasNext();)
			{
				WorldLocation loc = (WorldLocation) iter.next();	
				
//				if(ctr > 46)
//				{
//					System.err.println("loc " + ctr + " is:" + loc + " x:" + loc.getLong());
//				}
				
				Point pt =  new Point(dest.toScreen(loc));
				if(startPt == null)
					startPt = new Point(pt);
				if(last != null)
				{
					if(last.equals(pt))
					{
						xOffset += 10;
					}
					dest.drawLine(last.x, last.y, pt.x, pt.y);
			//		dest.drawText(myFont, "" + ctr, last.x + xOffset, last.y + yOffset);
				}			
				ctr++;
				last = pt;			
			}
			// and close it.
			dest.drawLine(last.x, last.y, startPt.x, startPt.y);
		//	dest.setColor(Color.red);
	//		yOffset += 10;
	//		xOffset += 5;
		//	break;
		}
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
	public class LineFeatureInfo extends Editable.EditorType
	{

		public LineFeatureInfo(LineFeature data, String theName)
		{
			super(data, theName, "");
		}

		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				PropertyDescriptor[] res = {
						prop("Color", "the color to plot this feature", SPATIAL)
				};
				return res;
			}
			catch (IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}
	}	
}
