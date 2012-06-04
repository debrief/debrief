package MWC.GUI.Shapes;

import java.awt.Color;
import java.util.Enumeration;

import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;

public class ChartFolio extends BaseLayer
{

	public static final String SHOW_NAMES = "showNames";
	public static final String LINE_COLOR = "lineColor";

	private boolean _showNames = false;
	private Color _lineColor = Color.red;

	public ChartFolio(boolean showNames, Color lineColor)
	{
		_showNames = showNames;
		_lineColor = lineColor;
	}

	@Override
	public EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new ChartFolioInfo(this);

		return super.getInfo();
	}

	@Override
	public void add(Editable thePlottable)
	{
		if (thePlottable instanceof ChartBoundsWrapper)
			super.add(thePlottable);
		else
			throw new RuntimeException("Can only hold chart wrappers!");
	}

	public boolean isShowNames()
	{
		return _showNames;
	}

	public void setShowNames(boolean showNames)
	{
		this._showNames = showNames;

		// go through kiddies setting name vis
		Enumeration<Editable> iter = super.elements();
		while (iter.hasMoreElements())
		{
			Editable editable = (Editable) iter.nextElement();
			ChartBoundsWrapper chart = (ChartBoundsWrapper) editable;
			chart.setLabelVisible(showNames);
		}
	}

	public Color getLineColor()
	{
		return _lineColor;
	}

	public void setLineColor(Color lineColor)
	{
		this._lineColor = lineColor;
		// go through kiddies setting name vis
		Enumeration<Editable> iter = super.elements();
		while (iter.hasMoreElements())
		{
			Editable editable = (Editable) iter.nextElement();
			ChartBoundsWrapper chart = (ChartBoundsWrapper) editable;
			chart.setColor(lineColor);
		}
	}

	// //////////////////////////////////////////////////////////////////////////
	// embedded class, used for editing the plotter
	// //////////////////////////////////////////////////////////////////////////
	/**
	 * the definition of what is editable about this object
	 */
	public class ChartFolioInfo extends Editable.EditorType
	{

		/**
		 * constructor for editable details of a set of Layers
		 * 
		 * @param data
		 *          the Layers themselves
		 */
		public ChartFolioInfo(final ChartFolio data)
		{
			super(data, data.getName(), "Edit");
		}

		/**
		 * editable GUI properties for our participant
		 * 
		 * @return property descriptions
		 */
		public java.beans.PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final java.beans.PropertyDescriptor[] res =
				{ prop(SHOW_NAMES, "show names for charts"),
						prop(LINE_COLOR, "color for chart rectangles") };
				return res;
			}
			catch (java.beans.IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
