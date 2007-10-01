package MWC.GUI.S57;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: S57Layer.java,v $
// @author $Author: ian.mayo $
// @version $Revision: 1.6 $
// $Log: S57Layer.java,v $
// Revision 1.6  2007/05/11 10:52:10  ian.mayo
// Add rudimentary S57 import support
//
// Revision 1.5  2007/05/08 08:07:42  ian.mayo
// Lots of refactoring.  Plot lots of new feature types
//
// Revision 1.4  2007/05/04 08:30:16  ian.mayo
// Changes from home
//
// Revision 1.3  2007/05/01 15:17:11  ian.mayo
// Have series of points, called it a layer...
//
// Revision 1.2  2007/04/27 16:06:35  ian.mayo
// Getting closer
//
// Revision 1.1  2007/04/27 13:45:42  ian.mayo
// Getting closer.  Importing data
//
// Revision 1.1  2007/04/27 09:20:28  ian.mayo
// initial import

import java.awt.*;
import java.beans.*;
import java.io.*;
import java.util.Enumeration;

import MWC.GUI.*;
import MWC.GUI.Plottables.IteratorWrapper;
import MWC.GUI.S57.features.S57Feature;
import MWC.GenericData.WorldArea;

public class S57Layer implements Plottable, Serializable, Layer
{
	// ///////////////////////////////////////////////////////////
	// member variables
	// //////////////////////////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * whether this layer is visible
	 */
	protected boolean _isOn;

	/**
	 * our editor
	 */
	transient protected Editable.EditorType _myEditor;

	private String _myName = "S57";

	private S57Database _myDatabase;

	private File _sourceFile = null;

	// ///////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////
	public S57Layer()
	{
		// make it visible to start with
		setVisible(true);
	}

	protected void load(String name)
	{
		_myDatabase = new S57Database();
		_myDatabase.loadDatabase(name, false);
	}

	// ///////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////

	public void setVisible(boolean val)
	{
		_isOn = val;
	}

	public boolean getVisible()
	{
		return _isOn;
	}

	public void paint(CanvasType g)
	{

		// check we are visible
		if (!_isOn)
			return;

		if (_myDatabase == null)
		{
			// do we have a name yet
			if (_sourceFile != null)
			{
				// ok, does it exist?
				if (_sourceFile.exists())
				{
					load(_sourceFile.getAbsolutePath());
					// _myDatabase = new S57Database();
					// _myDatabase.loadDatabase("d://dev/AU411141.000", true);
					// _myDatabase.loadDatabase(_sourceFile, true);
				}
			}
		}

		// just double-check things went ok
		if (_myDatabase == null)
		{
			System.err.println("Not painting S57, data not present");
			return;

		}

		g.setColor(Color.orange);
		// g.setColor(Color.cyan);
		// g.drawLine(222, 133, 244, 355);

		Enumeration<Editable> theFeatures = elements();
		while (theFeatures.hasMoreElements())
		{
			S57Feature nextF = (S57Feature) theFeatures.nextElement();
			nextF.paint(g);
		}

		// give us the area aswell
		WorldArea area = _myDatabase.getArea();
		Point p1 = new Point(g.toScreen(area.getTopLeft()));
		Point p2 = new Point(g.toScreen(area.getBottomRight()));
		g.setColor(Color.white);
		g.drawRect(p1.x, p1.y, p2.x - p1.x, p2.y - p1.y);
	}

	public MWC.GenericData.WorldArea getBounds()
	{
		// doesn't return a sensible size
		return null;
	}

	public double rangeFrom(MWC.GenericData.WorldLocation other)
	{
		// doesn't return a sensible distance;
		return INVALID_RANGE;
	}

	/**
	 * return this item as a string
	 */
	public String toString()
	{
		return getName();
	}

	public String getName()
	{
		return _myName;
	}

	public boolean hasEditor()
	{
		return true;
	}

	public Editable.EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new S57PainterInfo(this);

		return _myEditor;
	}

	public int compareTo(Object arg0)
	{
		Plottable other = (Plottable) arg0;
		String myName = this.getName() + this.hashCode();
		String hisName = other.getName() + arg0.hashCode();
		return myName.compareTo(hisName);
	}

	// ///////////////////////////////////////////////////////////
	// info class
	// //////////////////////////////////////////////////////////
	public class S57PainterInfo extends Editable.EditorType implements Serializable
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public S57PainterInfo(S57Layer data)
		{
			super(data, data.getName(), "");
		}

		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				PropertyDescriptor[] res = {
						prop("Visible", "whether S57 data is visible", VISIBILITY),
						prop("SourceFile", "the S57 data-file", FORMAT), };

				return res;
			}
			catch (IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public class GridPainterTest extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public GridPainterTest(String val)
		{
			super(val);
		}

		public void testMyParams()
		{
			MWC.GUI.Editable ed = new S57Layer();
			MWC.GUI.Editable.editableTesterSupport.testParams(ed, this);
			ed = null;
		}
	}

	public static void main(String[] args)
	{
		S57Layer layer = new S57Layer();
		layer.load("d://dev/AU411141.000");
		System.out.println("loaded!");
	}

	public void add(Editable point)
	{
		System.err.println("you can't add items to an S57 layer");
	}

	public void append(Layer other)
	{
	}

	public Enumeration<Editable> elements()
	{
		IteratorWrapper res = null;
		if (_myDatabase != null)
			res = new IteratorWrapper(_myDatabase.getFeatures().iterator());
		return res;
	}

	public void exportShape()
	{
	}

	public int getLineThickness()
	{
		return 0;
	}

	public boolean hasOrderedChildren()
	{
		return false;
	}

	public void removeElement(Editable point)
	{
		System.err.println("you can't remove items from an S57 layer");
	}

	public void setName(String val)
	{
		_myName = val;
	}

	/**
	 * @return the _sourceFile
	 */
	public final File getSourceFile()
	{
		return _sourceFile;
	}

	/**
	 * @param file
	 *          the _sourceFile to set
	 */
	public final void setSourceFile(File file)
	{
		_sourceFile = file;
	}
}
