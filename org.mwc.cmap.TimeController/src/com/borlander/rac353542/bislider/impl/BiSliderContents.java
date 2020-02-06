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

package com.borlander.rac353542.bislider.impl;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import com.borlander.rac353542.bislider.BiSliderContentsDataProvider;
import com.borlander.rac353542.bislider.BiSliderDataModel;
import com.borlander.rac353542.bislider.BiSliderUIModel;

class BiSliderContents extends BiSliderComponentBase {
	private BiSliderContentsDataProvider myContentsDataProvider;
	private ColorDescriptor mySegmentForeground;
	private ColorDescriptor myNotColoredSegmentsBackground;
	private final Segmenter mySegmenter;
	private BiSliderUIModel.Listener myConfigListener;

	public BiSliderContents(final BiSliderImpl biSlider, final Segmenter segmenter) {
		super(biSlider);
		mySegmenter = segmenter;
		reloadConfig();
		myConfigListener = new BiSliderUIModel.Listener() {
			@Override
			public void uiModelChanged(final BiSliderUIModel uiModel) {
				reloadConfig();
			}
		};
		getUIModel().addListener(myConfigListener);
	}

	@Override
	public void freeResources() {
		if (myConfigListener != null) {
			getUIModel().removeListener(myConfigListener);
			myConfigListener = null;
		}
		if (mySegmentForeground != null) {
			mySegmentForeground.freeResources();
			mySegmentForeground = null;
		}
		if (myNotColoredSegmentsBackground != null) {
			myNotColoredSegmentsBackground.freeResources();
			myNotColoredSegmentsBackground = null;
		}
	}

	private ColoredSegmentEnumeration getSegments() {
		// BiSliderDataModel dataModel = getDataModel();
		// return mySegmenter.segments(dataModel.getUserMinimum(),
		// dataModel.getUserMaximum());
		return mySegmenter.allSegments();
	}

	private double getValueAt(final ColoredSegment segment) {
		final BiSliderDataModel dataModel = getDataModel();
		return myContentsDataProvider.getNormalValueAt( //
				dataModel.getTotalMinimum(), dataModel.getTotalMaximum(), //
				segment.getMinValue(), segment.getMaxValue());
	}

	public void paintContents(final GC gc) {
		final CoordinateMapper mapper = getMapper();
		final BiSliderDataModel dataModel = getDataModel();
		final double userMin = dataModel.getUserMinimum();
		final double userMax = dataModel.getUserMaximum();
		for (final ColoredSegmentEnumeration segments = getSegments(); segments.hasMoreElements();) {
			final ColoredSegment nextSegment = segments.next();
			final double segmentMin = nextSegment.getMinValue();
			final double segmentMax = nextSegment.getMaxValue();
			final double normalValue = getValueAt(nextSegment);
			if (userMin == userMax || segmentMax < userMin || segmentMin > userMax) {
				// completely outside user range
				final Rectangle segmentInnerArea = mapper.segment2rectangle(segmentMin, segmentMax, 1.0, true);
				gc.setBackground(myNotColoredSegmentsBackground.getColor());
				gc.fillRectangle(segmentInnerArea);
			} else if (segmentMin >= userMin && segmentMax <= userMin) {
				// completely inside user range
				final Rectangle segmentInnerArea = mapper.segment2rectangle(segmentMin, segmentMax, normalValue, true);
				gc.setBackground(nextSegment.getColorDescriptor().getColor());
				gc.fillRectangle(segmentInnerArea);
			} else {
				// at least one (may be both!!!) borders should be drawn in this
				// segment
				if (segmentMin < userMin) {
					final Rectangle leftArea = mapper.segment2rectangle(segmentMin, userMin, 1.0, true);
					gc.setBackground(myNotColoredSegmentsBackground.getColor());
					gc.fillRectangle(leftArea);
				}
				if (segmentMax > userMax) {
					final Rectangle rightArea = mapper.segment2rectangle(userMax, segmentMax, 1.0, true);
					gc.setBackground(myNotColoredSegmentsBackground.getColor());
					gc.fillRectangle(rightArea);
				}
				final double leftMostColored = Math.max(segmentMin, userMin);
				final double rightMostColored = Math.min(segmentMax, userMax);
				final Rectangle coloredArea = mapper.segment2rectangle(leftMostColored, rightMostColored, normalValue,
						true);
				gc.setBackground(nextSegment.getColorDescriptor().getColor());
				gc.fillRectangle(coloredArea);
			}
		}
		// outline should be drawn after color fill
		for (final ColoredSegmentEnumeration segments = getSegments(); segments.hasMoreElements();) {
			final ColoredSegment nextSegment = segments.next();
			final double segmentMin = nextSegment.getMinValue();
			final double segmentMax = nextSegment.getMaxValue();
			final double normalValue = getValueAt(nextSegment);
			final Rectangle segmentOutline = mapper.segment2rectangle(segmentMin, segmentMax, normalValue, false);
			gc.setForeground(mySegmentForeground.getColor());
			gc.drawRectangle(segmentOutline);
		}
	}

	void reloadConfig() {
		final BiSliderUIModel uiModel = getUIModel();
		myContentsDataProvider = uiModel.getContentsDataProvider();
		mySegmentForeground = updateColorDescriptor( //
				mySegmentForeground, uiModel.getBiSliderForegroundRGB());
		myNotColoredSegmentsBackground = updateColorDescriptor( //
				myNotColoredSegmentsBackground, uiModel.getNotColoredSegmentRGB());
	}

}
