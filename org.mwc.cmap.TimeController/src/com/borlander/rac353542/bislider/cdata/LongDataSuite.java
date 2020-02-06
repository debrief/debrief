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

package com.borlander.rac353542.bislider.cdata;

import com.borlander.rac353542.bislider.BiSliderUIModel;
import com.borlander.rac353542.bislider.DefaultBiSliderUIModel;

/**
 * Single pack of objects required to instantiate BiSlider working with custom
 * object data model of <code>java.lang.Long</code> values.
 */
public class LongDataSuite {

	public static class LongDataModel extends DataObjectDataModel {
		public LongDataModel() {
			super(MAPPER_LONG);
		}

		public Long getTotalMaximumLong() {
			return (Long) getTotalMaximumObject();
		}

		public Long getTotalMinimumLong() {
			return (Long) getTotalMinimumObject();
		}

		public Long getUserMaximumLong() {
			return (Long) getUserMaximumObject();
		}

		public Long getUserMinimumLong() {
			return (Long) getUserMinimumObject();
		}

		public void setTotalRange(final Long minimum, final Long maximum) {
			setTotalObjectRange(minimum, maximum);
		}

		public void setUserMaximum(final Long longMaximum) {
			setUserMaximumObject(longMaximum);
		}

		public void setUserMinimum(final Long longMinimum) {
			setUserMinimumObject(longMinimum);
		}

	}

	public static DataObjectMapper MAPPER_LONG = new DataObjectMapper() {
		@Override
		public Object double2object(final double value) {
			final long longValue = Math.round(value);
			return new Long(longValue);
		}

		@Override
		public double getPrecision() {
			return 1;
		}

		@Override
		public double object2double(final Object object) {
			final Long longValue = (Long) object;
			return longValue.doubleValue();
		}
	};

	public LongDataModel createDataModel(final long totalMin, final long totalMax, final long userMin,
			final long userMax) {
		final LongDataModel result = new LongDataModel();
		result.setTotalRange(new Long(totalMin), new Long(totalMax));
		result.setUserMinimum(new Long(userMin));
		result.setUserMaximum(new Long(userMax));
		return result;
	}

	public DataObjectLabelProvider createLabelProvider() {
		return new DataObjectLabelProvider(MAPPER_LONG) {
			@Override
			public String getLabel(final Object dataObject) {
				final Long longValue = (Long) dataObject;
				return longValue.toString();
			}
		};
	}

	public BiSliderUIModel createUIModel() {
		final DefaultBiSliderUIModel result = new DefaultBiSliderUIModel();
		result.setLabelProvider(createLabelProvider());
		// assuming that long values are big enough
		result.setVerticalLabels(true);
		return result;
	}

}
