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

package ASSET.Util.XML.Decisions;

import ASSET.Models.Decision.Tactical.PatternSearch_Core;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;
import ASSET.Util.XML.Utils.ASSETLocationHandler;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

public abstract class PatternSearchCore_Handler extends CoreDecisionHandler {

	protected static final String START_POINT = "StartPoint";
	protected static final String TRACK_SPACING = "TrackSpacing";
	protected static final String SEARCH_HEIGHT = "SearchHeight";
	protected static final String SEARCH_SPEED = "SearchSpeed";
	protected static final String HEIGHT = "Height";
	protected static final String WIDTH = "Width";

	public static void exportCore(final Object toExport, final org.w3c.dom.Element parent, final String type,
			final org.w3c.dom.Document doc) {
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final PatternSearch_Core bb = (PatternSearch_Core) toExport;

		// first the parent attributes
		CoreDecisionHandler.exportThis(bb, thisPart, doc);

		// and now the objects
		ASSETLocationHandler.exportLocation(bb.getOrigin(), START_POINT, thisPart, doc);
		MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler.exportDistance(TRACK_SPACING, bb.getTrackSpacing(),
				thisPart, doc);
		MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler.exportDistance(WIDTH, bb.getWidth(), thisPart, doc);
		MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler.exportDistance(HEIGHT, bb.getHeight(), thisPart, doc);

		if (bb.getSearchHeight() != null)
			MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler.exportDistance(SEARCH_HEIGHT, bb.getSearchHeight(),
					thisPart, doc);

		if (bb.getSearchSpeed() != null)
			MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler.exportSpeed(SEARCH_SPEED, bb.getSearchSpeed(),
					thisPart, doc);

		// and store it
		parent.appendChild(thisPart);

	}

	protected WorldLocation _origin;
	protected WorldDistance _searchHeight;
	protected WorldDistance _width;
	protected WorldDistance _spacing;
	protected WorldDistance _height;

	protected WorldSpeed _searchSpeed;

	public PatternSearchCore_Handler(final String myType) {
		super(myType);

		addHandler(new ASSETLocationHandler(START_POINT) {
			@Override
			public void setLocation(final WorldLocation res) {
				_origin = res;
			}
		});
		addHandler(new MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler(TRACK_SPACING) {
			@Override
			public void setWorldDistance(final WorldDistance res) {
				_spacing = res;
			}
		});

		addHandler(new MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler(WIDTH) {
			@Override
			public void setWorldDistance(final WorldDistance res) {
				_width = res;
			}
		});

		addHandler(new MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler(HEIGHT) {
			@Override
			public void setWorldDistance(final WorldDistance res) {
				_height = res;
			}
		});

		addHandler(new MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler(SEARCH_HEIGHT) {
			@Override
			public void setWorldDistance(final WorldDistance res) {
				_searchHeight = res;
			}
		});

		addHandler(new MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler(SEARCH_SPEED) {
			@Override
			public void setSpeed(final WorldSpeed res) {
				_searchSpeed = res;
			}
		});

	}

	@Override
	public final void elementClosed() {
		final PatternSearch_Core ess = getModel();

		this.setAttributes(ess);
		setModel(ess);

		_height = null;
		_width = null;
		_origin = null;
		_spacing = null;
		_searchHeight = null;
		_searchSpeed = null;
	}

	abstract protected PatternSearch_Core getModel();

	public abstract void setModel(ASSET.Models.DecisionType dec);

}