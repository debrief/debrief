/**
 * 
 */
package MWC.GUI.S57.features;

import MWC.GUI.*;
import MWC.GenericData.*;

public abstract class S57Feature implements Plottable
{

	private boolean _visible = true;

	protected EditorType _myEditor = null;

	final private String _myName;

	final private Double _minScale;

	public S57Feature(String name, Double minScale)
	{
		_myName = name;
		_minScale = minScale;
	}

	public WorldArea getBounds()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * actually do the paint operation
	 * 
	 * @param dest
	 */
	abstract void doPaint(CanvasType dest);

	/**
	 * @param dest
	 */
	final public void paint(CanvasType dest)
	{
		// are we visible
		if (_visible)
		{
			boolean inScale = true;

			if (_minScale != null)
			{
				// sort out the scale
//				double worldVal = dest.getProjection().getVisibleDataArea().getFlatEarthWidth();
//				double worldMetres = worldVal * 60 * 60 * 30;
//				double scrMetres = 0.3;
//				double theScale = worldMetres / scrMetres;
				
//				if (theScale > _minScale.doubleValue())
//				{
//					inScale = false;
//				}
				
			}
			if (inScale)
				doPaint(dest);
		}
	}

	public boolean getVisible()
	{
		return _visible;
	}

	public double rangeFrom(WorldLocation other)
	{
		return 0;
	}

	public void setVisible(boolean val)
	{
		_visible = val;
	}

	public EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = createEditor();
		return _myEditor;
	}

	abstract EditorType createEditor();

	public String getName()
	{
		return _myName;
	}
	
	public String toString()
	{
		return getName();
	}

	public boolean hasEditor()
	{
		return true;
	}

	public int compareTo(Plottable arg0)
	{
		return 0;
	}
}