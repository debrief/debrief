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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Iterator;
import java.util.SortedSet;

import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.MovingPlottable;
import MWC.GUI.Plottable;
import MWC.GenericData.WorldArea;

@SuppressWarnings("serial")
public class DynamicShapeLayer extends BaseLayer implements MovingPlottable
{

	private boolean plotAllShapes = false;
	
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
				PropertyDescriptor[] res = new PropertyDescriptor[sres.length+1];
				System.arraycopy(sres, 0, res, 0, sres.length);
				res[sres.length] =
				expertProp("PlotAllShapes", "Plot All Shapes", VISIBILITY);
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
		  // copied from Plottables.paint(dest)
			// note, we used to only test it the subject was in the data area,
			// but that left some items outside the user-dragged area not being visible.
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

			final SortedSet<Editable> _thePlottables = (SortedSet<Editable>) getData();
			synchronized (_thePlottables)
			{
				final Iterator<Editable> enumer = _thePlottables.iterator();

				while (enumer.hasNext())
				{
					final Object next = enumer.next();
					if (next instanceof Plottable)
					{
						final Plottable thisP = (Plottable) next;

						// is this plottable visible
						if (thisP.getVisible())
						{

							// see if this plottable is within the data area
							final WorldArea wp = thisP.getBounds();

							if (wp != null)
							{
								// it has an area, see if it is in view
								if (wp.overlaps(wa))
								{
									paintElement(dest, time, thisP);
								}
							}
							else
							{
								// it doesn't have an area, so plot it anyway
								paintElement(dest, time, thisP);
							}
						}
					}
				}
			}
	}


	private void paintElement(CanvasType dest, long time, final Plottable thisP)
	{
		if (thisP instanceof DynamicShapeWrapper)
		{
			DynamicShapeWrapper dsw = (DynamicShapeWrapper) thisP;
			if (dsw.getStartDTG() != null)
			{
				long startDTG = dsw.getStartDTG().getMicros();
				if (time >= startDTG)
				{
					thisP.paint(dest);
				}
			}
		}
		else
		{
			// FIXME if element isn't DynamicShapeWrapper we will ignore or paint it ???
			// thisP.paint(dest);
		}
	}

}
