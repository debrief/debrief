/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/
package info.limpet.impl;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Iterator;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.IndexIterator;
import org.eclipse.january.dataset.ObjectDataset;
import org.eclipse.january.metadata.AxesMetadata;

import info.limpet.ICommand;
import info.limpet.operations.spatial.GeoSupport;
import info.limpet.operations.spatial.IGeoCalculator;

public class LocationDocument extends Document<Point2D> {

	public class MyStats {
		public double max() {
			final DoubleDataset ds = (DoubleDataset) dataset;
			return (Double) ds.max();

		}

		public double mean() {
			final DoubleDataset ds = (DoubleDataset) dataset;
			return (Double) ds.mean(true);
		}

		public double min() {
			final DoubleDataset ds = (DoubleDataset) dataset;
			return (Double) ds.min(true);
		}

		public double sd() {
			final DoubleDataset ds = (DoubleDataset) dataset;
			return ds.stdDeviation(true);
		}

		public double variance() {
			final DoubleDataset ds = (DoubleDataset) dataset;
			return ds.variance(true);
		}
	}

	private final Unit<?> _distanceUnits;

	public LocationDocument(final ObjectDataset dataset, final ICommand predecessor) {
		this(dataset, predecessor, SampleData.DEGREE_ANGLE);
	}

	public LocationDocument(final ObjectDataset dataset, final ICommand predecessor, final Unit<?> units) {
		super(dataset, predecessor);
		_distanceUnits = units;
	}

	public IGeoCalculator getCalculator() {
		return GeoSupport.calculatorFor(_distanceUnits);
	}

	@Override
	public Iterator<Point2D> getIterator() {
		return getLocationIterator();
	}

	public Iterator<Point2D> getLocationIterator() {
		final Iterator<?> oIter = getObjectIterator();
		return new Iterator<Point2D>() {

			@Override
			public boolean hasNext() {
				return oIter.hasNext();
			}

			@Override
			public Point2D next() {
				return (Point2D) oIter.next();
			}

			@Override
			public void remove() {
				oIter.remove();
			}
		};
	}

	public Iterator<?> getObjectIterator() {
		final ObjectDataset od = (ObjectDataset) dataset;
		final Object[] strings = od.getData();
		final Iterable<Object> iterable = Arrays.asList(strings);
		return iterable.iterator();
	}

	/**
	 * we've introduced this method as a workaround. The "visibleWhen" operator
	 * for getRange doesn't work with "size==1". Numerical comparisions don't
	 * seem to work. So, we're wrapping the numberical comparison in this
	 * boolean method.
	 *
	 * @return
	 */
	public boolean getShowRange() {
		return size() == 1;
	}

	public Unit<?> getUnits() {
		return _distanceUnits;
	}

	@UIProperty(name = "Value", category = UIProperty.CATEGORY_VALUE, visibleWhen = "showRange == true")
	public String getValue() {
		final ObjectDataset data = (ObjectDataset) getDataset();
		final Point2D point = (Point2D) data.get();
		return point.getY() + "," + point.getX();
	}

	//
	// public Point2D interpolateValue(long i, InterpMethod linear)
	// {
	// Point2D res = null;
	//
	// // do we have axes?
	// AxesMetadata index = dataset.getFirstMetadata(AxesMetadata.class);
	// ILazyDataset indexDataLazy = index.getAxes()[0];
	// try
	// {
	// Dataset indexData =
	// DatasetUtils.sliceAndConvertLazyDataset(indexDataLazy);
	//
	// // check the target index is within the range
	// double lowerIndex = indexData.getDouble(0);
	// int indexSize = indexData.getSize();
	// double upperVal = indexData.getDouble(indexSize - 1);
	// if(i >= lowerIndex && i <= upperVal)
	// {
	// // ok, create an dataset that captures this specific time
	// LongDataset indexes = (LongDataset) DatasetFactory.createFromObject(new
	// Long[]{i});
	//
	// // perform the interpolation
	// Dataset dOut = Maths.interpolate(indexData, ds, indexes, 0, 0);
	//
	// // get the single matching value out
	// res = dOut.getDouble(0);
	// }
	// }
	// catch (DatasetException e)
	// {
	// e.printStackTrace();
	// }
	//
	// return res;
	// }

	private Point2D interpolateValue(final double time) {
		final Point2D res;

		final IGeoCalculator calculator = getCalculator();

		// ok, find the values either side
		int beforeIndex = -1, afterIndex = -1;
		double beforeTime = 0, afterTime = 0;

		final Iterator<Double> tIter = getIndexIterator();
		int ctr = 0;
		while (tIter.hasNext()) {
			final Double thisT = tIter.next();
			if (thisT <= time) {
				beforeIndex = ctr;
				beforeTime = thisT;
			}
			if (thisT >= time) {
				afterIndex = ctr;
				afterTime = thisT;
				break;
			}

			ctr++;
		}

		if (beforeIndex >= 0 && afterIndex == 0) {
			final ObjectDataset od = (ObjectDataset) getDataset();
			res = (Point2D) od.get(beforeIndex);
		} else if (beforeIndex >= 0 && afterIndex >= 0) {
			if (beforeIndex == afterIndex) {
				// special case - it falls on one of our values
				final ObjectDataset od = (ObjectDataset) getDataset();
				res = (Point2D) od.get(beforeIndex);
			} else {
				final ObjectDataset od = (ObjectDataset) getDataset();
				final Point2D beforeVal = (Point2D) od.get(beforeIndex);
				final Point2D afterVal = (Point2D) od.get(afterIndex);

				final double latY0 = beforeVal.getY();
				final double latY1 = afterVal.getY();

				final double longY0 = beforeVal.getX();
				final double longY1 = afterVal.getX();

				final double x0 = beforeTime;
				final double x1 = afterTime;
				final double x = time;

				final double newResLat = latY0 + (latY1 - latY0) * (x - x0) / (x1 - x0);
				final double newResLong = longY0 + (longY1 - longY0) * (x - x0) / (x1 - x0);

				// ok, we can do the calc
				res = calculator.createPoint(newResLong, newResLat);
			}
		} else {
			res = null;
		}

		return res;
	}

	@Override
	public boolean isQuantity() {
		return false;
	}

	/**
	 * retrieve the location at the specified time (even if it's a non-temporal
	 * collection)
	 *
	 * @param iCollection set of locations to use
	 * @param thisTime time we're need a location for
	 * @return
	 */
	public Point2D locationAt(final double thisTime) {
		Point2D res = null;
		if (isIndexed()) {
			res = interpolateValue(thisTime);
		} else {
			res = getLocationIterator().next();
		}
		return res;
	}

	@Override
	public void setDataset(final IDataset dataset) {
		if (dataset instanceof ObjectDataset) {
			super.setDataset(dataset);
		} else {
			throw new IllegalArgumentException("We only store object datasets");
		}
	}

	public void setValue(final String val) {
		// try to parse it
		final String[] items = val.split(",");
		if (items.length == 2) {
			try {
				final double y = Double.parseDouble(items[0]);
				final double x = Double.parseDouble(items[1]);
				final ObjectDataset data = (ObjectDataset) getDataset();
				final Point2D point = (Point2D) data.get();
				point.setLocation(x, y);

				// successful, fire modified
				this.fireDataChanged();

			} catch (final NumberFormatException dd) {
				dd.printStackTrace();
			}
		}

	}

	public MyStats stats() {
		return new MyStats();
	}

	@Override
	public String toListing() {
		final StringBuffer res = new StringBuffer();

		final ObjectDataset dataset = (ObjectDataset) this.getDataset();
		final AxesMetadata axesMetadata = dataset.getFirstMetadata(AxesMetadata.class);
		final IndexIterator iterator = dataset.getIterator();

		final DoubleDataset axisDataset;
		if (axesMetadata != null && axesMetadata.getAxes().length > 0) {
			final DoubleDataset doubleAxis = (DoubleDataset) axesMetadata.getAxes()[0];
			axisDataset = doubleAxis != null ? doubleAxis : null;
		} else {
			axisDataset = null;
		}

		res.append(dataset.getName() + ":\n");
		while (iterator.hasNext()) {
			final String indexVal;
			if (axisDataset != null) {
				indexVal = "" + axisDataset.getElementDoubleAbs(iterator.index);
			} else {
				indexVal = "N/A";
			}

			res.append(indexVal + " : " + dataset.get(iterator.index));
			res.append(";");
		}
		res.append("\n");

		return res.toString();
	}
}
