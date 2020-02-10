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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.mwc.cmap.TimeController.controls.DTGBiSlider.DoFineControl;

import com.borlander.rac353542.bislider.BiSlider;
import com.borlander.rac353542.bislider.BiSliderDataModel;
import com.borlander.rac353542.bislider.BiSliderUIModel;

/**
 * Actual BiLsider implementation.
 * <p>
 * Intentionally package local
 */
class BiSliderImpl extends BiSlider implements Disposable, BiSliderDataModel.Listener, BiSliderUIModel.Listener {
	private final static int MIN_NORMAL_SIZE = 20;
	private final BiSliderDataModel.Writable myDataModel;
	private final BiSliderUIModel myUiModel;
	private final CoordinateMapper myMapper;
	BiSliderPointer myMinPointer;
	BiSliderPointer myMaxPointer;
	private final BiSliderContents myContents;
	private final BiSliderOutline myOutline;
	private Rectangle myCachedClientArea;
	private Rectangle myCachedDrawArea;
	private Segmenter mySegmenter;
	private LabelSupport myLabelSupport;
	private UserRangePanner myUserRangePanner;

	public BiSliderImpl(final Composite parent, final int style, final BiSliderDataModel.Writable dataModel,
			final BiSliderUIModel uiModel, final DoFineControl handler) {
		super(parent, style | SWT.DOUBLE_BUFFERED);
		setFont(parent.getFont());
		myDataModel = dataModel;
		myUiModel = uiModel;
		myLabelSupport = new LabelSupport(this);
		myMapper = new CoordinateMapperImpl(myUiModel.isVertical());
		mySegmenter = new Segmenter(myDataModel, myUiModel);
		myMaxPointer = new BiSliderPointer(this, true, mySegmenter, handler);
		myMinPointer = new BiSliderPointer(this, false, mySegmenter, handler);

		myContents = new BiSliderContents(this, mySegmenter);
		myOutline = new BiSliderOutline(this, myLabelSupport);
		myUserRangePanner = new UserRangePanner(this, myMinPointer, myMaxPointer, mySegmenter);
		dataModel.addListener(this);
		uiModel.addListener(this);
		addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(final DisposeEvent e) {
				freeResources();
			}
		});
		addPaintListener(new PaintListener() {

			@Override
			public void paintControl(final PaintEvent e) {
				BiSliderImpl.this.paintBiSlider(e.gc);
			}
		});
		addMouseListener(new SegmentSelector(this, mySegmenter));
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(final MouseEvent e) {
				final boolean isFocusControl = isFocusControl();
				if (!isFocusControl) {
					if (myMaxPointer != null) {
						myMaxPointer.disposeFineTuneAdjuster(true);
					}
					if (myMinPointer != null) {
						myMinPointer.disposeFineTuneAdjuster(true);
					}
					setFocus();
				}
			}
		});
	}

	private Rectangle computeDrawArea(final Rectangle clientArea, final GC gc) {
		int x = clientArea.x;
		int y = clientArea.y;
		int width = clientArea.width;
		int height = clientArea.height;
		final int labelInsets = getLabelInsets(gc);
		final int nonLabelInsets = myUiModel.getNonLabelInsets();
		// allocate space for labels but only if there is enough space
		if (myUiModel.isVertical()) {
			if (myUiModel.hasLabelsAboveOrLeft() && width > labelInsets + MIN_NORMAL_SIZE) {
				width -= labelInsets;
				x += labelInsets;
			}
			if (myUiModel.hasLabelsBelowOrRight() && width > labelInsets + MIN_NORMAL_SIZE) {
				width -= labelInsets;
			}
			if (height > nonLabelInsets) {
				height -= nonLabelInsets;
				y += nonLabelInsets;
			}
			if (height > nonLabelInsets) {
				height -= nonLabelInsets;
			}
		} else {
			if (myUiModel.hasLabelsAboveOrLeft() && height > labelInsets + MIN_NORMAL_SIZE) {
				height -= labelInsets;
				y += labelInsets;
			}
			if (myUiModel.hasLabelsBelowOrRight() && height > labelInsets + MIN_NORMAL_SIZE) {
				height -= labelInsets;
			}
			if (width > nonLabelInsets) {
				width -= nonLabelInsets;
				x += nonLabelInsets;
			}
			if (width > nonLabelInsets) {
				width -= nonLabelInsets;
			}
		}
		return new Rectangle(x, y, width, height);
	}

	@Override
	public void dataModelChanged(final BiSliderDataModel dataModel, final boolean moreChangesExpectedInNearFuture) {
		// reload in any case to provide visual feedback immediately
		reloadChanges();

	}

	@Override
	public void freeResources() {
		myDataModel.removeListener(this);
		if (myMaxPointer != null) {
			myMaxPointer.freeResources();
			myMaxPointer = null;
		}
		if (myMinPointer != null) {
			myMinPointer.freeResources();
			myMinPointer = null;
		}
		if (mySegmenter != null) {
			mySegmenter.freeResources();
			mySegmenter = null;
		}
		if (myUserRangePanner != null) {
			myUserRangePanner.freeResources();
			myUserRangePanner = null;
		}
		if (myLabelSupport != null) {
			myLabelSupport.freeResources();
			myLabelSupport = null;
		}
	}

	/**
	 * intentionally package local
	 */
	CoordinateMapper getCoordinateMapper() {
		return myMapper;
	}

	@Override
	public BiSliderDataModel getDataModel() {
		return getWritableDataModel();
	}

	private Rectangle getDrawArea(final GC gc) {
		final Rectangle clientArea = getClientArea();
		if (!clientArea.equals(myCachedClientArea)) {
			myCachedClientArea = Util.cloneRectangle(clientArea);
			myCachedDrawArea = computeDrawArea(myCachedClientArea, gc);
		}
		return myCachedDrawArea;
	}

	private int getLabelInsets(final GC gc) {
		int result = myUiModel.getLabelInsets();
		if (result == SWT.DEFAULT) {
			result = myLabelSupport.getPrefferedLabelInsets(gc);
		}
		return result;
	}

	@Override
	public BiSliderUIModel getUIModel() {
		return myUiModel;
	}

	@Override
	public BiSliderDataModel.Writable getWritableDataModel() {
		return myDataModel;
	}

	/**
	 * @return <code>true</code> if given point is inside one of pointers.
	 */
	boolean isInsidePointer(final int pointX, final int pointY) {
		return (myMaxPointer != null && myMaxPointer.isInsideArea(pointX, pointY))
				|| (myMinPointer != null && myMinPointer.isInsideArea(pointX, pointY));
	}

	void paintBiSlider(final GC gc) {
		final Rectangle drawArea = getDrawArea(gc);
		myMapper.setContext(myDataModel, drawArea, getClientArea());
		myContents.paintContents(gc);
		myMinPointer.paintPointer(gc);
		myMaxPointer.paintPointer(gc);
		myOutline.paintOutline(gc);
	}

	private void reloadChanges() {
		if (!isDisposed()) {
			redraw();
		}
	}

	@Override
	public void resetMinMaxPointers() {
		if (myMinPointer != null && !isDisposed()) {
			myMinPointer.reset();
		}
		if (myMaxPointer != null && !isDisposed()) {
			myMaxPointer.reset();
		}
	}

	@Override
	public void setShowLabels(final boolean showLabels) {
		if (myUserRangePanner != null) {
			myUserRangePanner.setShowValueLabels(showLabels);
		}
	}

	@Override
	public void uiModelChanged(final BiSliderUIModel uiModel) {
		reloadChanges();
	}

}
