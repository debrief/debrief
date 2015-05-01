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
package Debrief.Wrappers;

import java.awt.Color;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Vector;

import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.MovingPlottable;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;

public class DynamicShapeLayer extends BaseLayer implements MovingPlottable,
		WatchableList
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean plotAllShapes = false;

	public DynamicShapeLayer()
	{
		super(true);
	}

	/**
	 * class containing editable details of a layer
	 */
	public final class DynamicShapeInfo extends LayerInfo implements
			Editable.DynamicDescriptors
	{

		/**
		 * constructor for this editor, takes the actual layer as a parameter
		 * 
		 * @param data
		 *          track being edited
		 */
		public DynamicShapeInfo(final DynamicShapeLayer data)
		{
			super(data);
		}

		@Override
		public final PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				PropertyDescriptor[] sres = super.getPropertyDescriptors();
				PropertyDescriptor[] res = new PropertyDescriptor[sres.length + 1];
				System.arraycopy(sres, 0, res, 0, sres.length);
				res[sres.length] = expertProp("PlotAllShapes", "Plot All Shapes",
						VISIBILITY);
				return res;
			}
			catch (final IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}

	}

	public final MWC.GUI.Editable.EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new DynamicShapeInfo(this);

		return _myEditor;
	}

	@Override
	public void paint(CanvasType dest)
	{
		// ignore
	}

	public boolean isPlotAllShapes()
	{
		return plotAllShapes;
	}

	public void setPlotAllShapes(boolean plotAllShapes)
	{
		this.plotAllShapes = plotAllShapes;
	}

	@Override
	public void paint(CanvasType dest, long time)
	{
		// see if I am visible
		if (!getVisible())
			return;

		if (plotAllShapes)
		{
			super.paint(dest);
		}
		else
		{
			final float oldThick = dest.getLineWidth();
			dest.setLineWidth(getLineThickness());

			// get the plottables to do the painting
			internalPaint(dest, time);

			// and restort
			dest.setLineWidth((int) oldThick);
		}
	}

	private void internalPaint(CanvasType dest, long time)
	{
		Watchable[] nearest = this.getNearestTo(new HiResDate(time));
		ShapeWrapper nearestShape = null;
		if ((nearest != null) && (nearest.length > 0))
		{
			nearestShape = (ShapeWrapper) nearest[0];
		}

		if (nearestShape != null)
		{
			// copied from Plottables.paint(dest)
			// note, we used to only test it the subject was in the data area,
			// but that left some items outside the user-dragged area not being
			// visible.
			// - instead we calculate the visible data-area from the current screen
			// area, and
			// compare against that
			WorldArea wa = dest.getProjection().getVisibleDataArea();

			// drop out if we don't have a data area for the projection
			if (wa == null)
			{
				dest.getProjection().zoom(0.0);
				wa = dest.getProjection().getVisibleDataArea();
			}

			// is this plottable visible
			if (nearestShape.getVisible())
			{

				// see if this plottable is within the data area
				final WorldArea wp = nearestShape.getBounds();

				if ((wp == null) || (wp.overlaps(wa)))
				{
					nearestShape.paint(dest);
				}

			}
		}
	}

	// private boolean paintElement(CanvasType dest, long timeMillis,
	// final Plottable thisP)
	// {
	// if (thisP instanceof DynamicShapeWrapper)
	// {
	// DynamicShapeWrapper dsw = (DynamicShapeWrapper) thisP;
	// if (dsw.getStartDTG() != null)
	// {
	// long startMillis = dsw.getStartDTG().getDate().getTime();
	// HiResDate endDTG = dsw.getEndDTG();
	// if (endDTG != null)
	// {
	// // ok, let's check if this
	// long endMillis = endDTG.getDate().getTime();
	// if ((timeMillis >= startMillis) && (timeMillis <= endMillis))
	// {
	// thisP.paint(dest);
	// }
	// }
	// if (timeMillis >= startMillis)
	// {
	// thisP.paint(dest);
	// return true;
	// }
	// }
	// }
	// else
	// {
	// // FIXME if element isn't DynamicShapeWrapper we will ignore or paint it
	// // ???
	// // thisP.paint(dest);
	// }
	// return false;
	// }

	@Override
	public Color getColor()
	{
		Color res = null;
		// just return the color of the first item
		if (size() > 0)
		{
			Editable first = this.elements().nextElement();
			DynamicShapeWrapper dsw = (DynamicShapeWrapper) first;
			res = dsw.getColor();
		}
		return res;
	}

	@Override
	public HiResDate getStartDTG()
	{
		HiResDate res = null;
		// just return the color of the first item
		if (size() > 0)
		{
			DynamicShapeWrapper dsw = (DynamicShapeWrapper) first();
			res = dsw.getStartDTG();
		}
		return res;
	}

	@Override
	public HiResDate getEndDTG()
	{
		HiResDate res = null;
		// just return the color of the first item
		if (size() > 0)
		{
			DynamicShapeWrapper dsw = (DynamicShapeWrapper) first();
			res = dsw.getEndDTG();
		}
		return res;
	}

	@Override
	public Watchable[] getNearestTo(HiResDate DTG)
	{

		Watchable[] res = new Watchable[] {};

		// just check it's worth testing
		if (size() > 0)
		{
			final ShapeWrapper firstS = (ShapeWrapper) first();
			if (firstS.getStartDTG().lessThanOrEqualTo(DTG))
			{
				boolean inRange = false;
				// does the last one have and end time
				ShapeWrapper end = (ShapeWrapper) last();
				if (end.getEndDTG() != null)
				{
					// ok, test the end
					if (end.getEndDTG().greaterThanOrEqualTo(DTG))
					{
						inRange = true;
					}
				}
				else
				{
					// no end point, use start time of last point
					if (end.getStartDTG().greaterThanOrEqualTo(DTG))
					{
						inRange = true;
					}
				}

				// ok, is it worth bothering with?
				if (inRange)
				{
					// ok, persevere to find the nearest (first one after)
					Enumeration<Editable> ele = elements();
					while (ele.hasMoreElements())
					{
						Editable editable = (Editable) ele.nextElement();
						DynamicShapeWrapper dsw = (DynamicShapeWrapper) editable;
						if (dsw.getVisible())
						{
							if (dsw.getStartDTG().greaterThanOrEqualTo(DTG))
							{
								res = new Watchable[] { dsw };
								break;
							}
						}
					}
				}
			}
		}
		return res;
	}

	@Override
	public void filterListTo(HiResDate start, HiResDate end)
	{
		// ok, get the matching items
		Collection<Editable> list = getItemsBetween(start, end);

		// ok, now loop through, and hide/reveal as appropriate
		Enumeration<Editable> ele = this.elements();
		while (ele.hasMoreElements())
		{
			DynamicShapeWrapper wrapper = (DynamicShapeWrapper) ele.nextElement();
			if (list.contains(wrapper))
			{
				wrapper.setVisible(true);
			}
			else
			{
				wrapper.setVisible(false);
			}
		}
	}

	@Override
	public Collection<Editable> getItemsBetween(HiResDate start, HiResDate end)
	{
		Vector<Editable> list = new Vector<Editable>();

		// just check it's worth testing
		if (size() > 0)
		{
			if (((ShapeWrapper) first()).getStartDTG().lessThanOrEqualTo(end))
			{
				boolean inRange = false;
				// does the last one have and end time
				ShapeWrapper endT = (ShapeWrapper) last();
				if (endT.getEndDTG() != null)
				{
					// ok, test the end
					if (endT.getEndDTG().greaterThanOrEqualTo(start))
					{
						inRange = true;
					}
				}
				else
				{
					// no end point, use start time of last point
					if (endT.getStartDTG().greaterThanOrEqualTo(start))
					{
						inRange = true;
					}
				}

				// ok, is it worth bothering with?
				if (inRange)
				{
					// ok, persevere to find the nearest (first one after)
					Enumeration<Editable> ele = elements();
					while (ele.hasMoreElements())
					{
						Editable editable = (Editable) ele.nextElement();
						DynamicShapeWrapper dsw = (DynamicShapeWrapper) editable;
						if (dsw.getStartDTG().greaterThan(start))
						{
							boolean itemInRange = false;
							// ok, check the end point of this shape
							if (dsw.getEndDTG() != null)
							{
								// ok, test the end
								if (dsw.getEndDTG().lessThanOrEqualTo(end))
								{
									itemInRange = true;
								}
							}
							else
							{
								// no end point, use start time of last point
								if (dsw.getStartDTG().lessThanOrEqualTo(end))
								{
									itemInRange = true;
								}
							}

							if (itemInRange)
							{
								list.add(dsw);
							}
						}
					}
				}
			}
		}
		// ok, convert the output

		return list;
	}

	@Override
	public PlainSymbol getSnailShape()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
