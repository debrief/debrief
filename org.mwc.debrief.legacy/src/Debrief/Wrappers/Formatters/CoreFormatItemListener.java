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
package Debrief.Wrappers.Formatters;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers.INewItemListener;
import MWC.GUI.PlainWrapper;
import MWC.GUI.TimeStampedDataItem;
import MWC.GUI.Properties.AttributeTypePropertyEditor;
import MWC.GUI.Properties.TimeFrequencyPropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import junit.framework.TestCase;

public class CoreFormatItemListener extends PlainWrapper implements INewItemListener {

	// ///////////////////////////////////////////////////////////
	// info class
	// //////////////////////////////////////////////////////////
	final public class CoreFormatInfo extends Editable.EditorType implements Serializable {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public CoreFormatInfo(final CoreFormatItemListener data) {
			super(data, data.getName(), "");
		}

		@Override
		final public PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] res = { displayProp("Name", "Name", "Name for this formatter"),
						displayProp("Visible", "Active", "Whether this formatter is active"),
						displayLongProp("Interval", "Interval (millis)", "How frequently to apply the format",
								MWC.GUI.Properties.TimeFrequencyPropertyEditor.class),
						displayProp("LayerName", "Target layer", "Which layer to format (optional)"),
						displayProp("Symbology", "Symbology match", "Which symbology attributes to match (optional)"),
						displayProp("RegularIntervals", "Regular intervals",
								"Whether to apply format to regular intervals (e.g. on the hour)"),
						displayProp("AttributeType", "Attribute type", "Type of attribute to format") };

				res[6].setPropertyEditorClass(AttributeTypePropertyEditor.class);

				return res;
			} catch (final IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}
	}

	public static class TestMe extends TestCase {
		private FixWrapper createFix(final int time) {
			final Fix newF = new Fix(new HiResDate(time), new WorldLocation(2, 2, 0), 22, 33);
			final FixWrapper fw = new FixWrapper(newF);
			return fw;

		}

		public void testIntervals() {
			final CoreFormatItemListener cf = new CoreFormatItemListener("Test", null, null, 5000, false,
					AttributeTypePropertyEditor.SYMBOL, true) {

				/**
				*
				*/
				private static final long serialVersionUID = 1L;

				@Override
				protected void applyFormat(final FixWrapper fix) {
					fix.setSymbolShowing(true);
				}

				@Override
				protected void formatTrack(final TrackWrapper track, final HiResDate interval) {
					// skip
				}
			};
			final TrackWrapper tw = new TrackWrapper();
			tw.setName("Name");
			final FixWrapper f1 = createFix(4000);
			final FixWrapper f2 = createFix(5000);
			final FixWrapper f3 = createFix(9000);
			final FixWrapper f4 = createFix(10000);
			final FixWrapper f5 = createFix(14100);

			cf.newItem(tw, f1, null);
			cf.newItem(tw, f2, null);
			cf.newItem(tw, f3, null);
			cf.newItem(tw, f4, null);
			cf.newItem(tw, f5, null);

			assertTrue(f1.getSymbolShowing());
			assertFalse(f2.getSymbolShowing());
			assertTrue(f3.getSymbolShowing());
			assertFalse(f4.getSymbolShowing());
			assertTrue(f5.getSymbolShowing());
		}

		public void testRegularIntervals() {
			final CoreFormatItemListener cf = new CoreFormatItemListener("Test", null, null, 5000, true,
					AttributeTypePropertyEditor.SYMBOL, true) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void applyFormat(final FixWrapper fix) {
					fix.setSymbolShowing(true);
				}

				@Override
				protected void formatTrack(final TrackWrapper track, final HiResDate interval) {
					// skip
				}
			};
			final TrackWrapper tw = new TrackWrapper();
			tw.setName("Name");
			final FixWrapper f1 = createFix(4000);
			final FixWrapper f2 = createFix(5000);
			final FixWrapper f3 = createFix(6000);
			final FixWrapper f4 = createFix(10000);
			final FixWrapper f5 = createFix(12000);

			cf.newItem(tw, f1, null);
			cf.newItem(tw, f2, null);
			cf.newItem(tw, f3, null);
			cf.newItem(tw, f4, null);
			cf.newItem(tw, f5, null);

			assertTrue(f1.getSymbolShowing());
			assertTrue(f2.getSymbolShowing());
			assertFalse(f3.getSymbolShowing());
			assertTrue(f4.getSymbolShowing());
			assertFalse(f5.getSymbolShowing());
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String _layerName;
	private String _sym;
	private HiResDate _interval;
	private final Map<String, Long> _lastTimes = new HashMap<String, Long>();
	private String _formatName;
	private Integer _type;

	private boolean _regularInterval;

	private EditorType _myEditor;

	public CoreFormatItemListener(final String name, final String layerName, final String symbology, final int interval,
			final boolean regularInterval, final Integer type, final boolean active) {
		_formatName = name;
		_layerName = layerName;
		_sym = symbology;
		_interval = new HiResDate(interval);
		_regularInterval = regularInterval;
		_type = type;
		super.setVisible(active);
	}

	protected void applyFormat(final FixWrapper fix) {
		switch (_type) {
		case AttributeTypePropertyEditor.ARROW:
			fix.setArrowShowing(true);
			break;
		case AttributeTypePropertyEditor.SYMBOL:
			fix.setSymbolShowing(true);
			break;
		case AttributeTypePropertyEditor.LABEL:
			fix.setLabelShowing(true);
			break;
		}
	}

	@Override
	public void fileComplete() {
	}

	protected void formatTrack(final TrackWrapper track, final HiResDate interval) {
		switch (_type) {
		case AttributeTypePropertyEditor.ARROW:
			track.setArrowFrequency(interval);
			break;
		case AttributeTypePropertyEditor.SYMBOL:
			track.setSymbolFrequency(interval);
			break;
		case AttributeTypePropertyEditor.LABEL:
			track.setLabelFrequency(interval);
			break;
		}
	}

	public int getAttributeType() {
		return _type;
	}

	@Override
	public WorldArea getBounds() {
		return null;
	}

	@Override
	public EditorType getInfo() {
		if (_myEditor == null) {
			_myEditor = new CoreFormatInfo(this);
		}
		return _myEditor;
	}

	public HiResDate getInterval() {
		return _interval;
	}

	public String getLayerName() {
		return _layerName;
	}

	@Override
	public String getName() {
		return _formatName;
	}

	public boolean getRegularIntervals() {
		return _regularInterval;
	}

	public String getSymbology() {
		return _sym;
	}

	@Override
	public boolean hasEditor() {
		return true;
	}

	@Override
	public void newItem(final Layer parent, final Editable item, final String symbology) {
		// are we active
		if (!getVisible()) {
			return;
		}

		// just check if this is actually a new layer call
		if (item == null) {
			if (parent instanceof TrackWrapper) {
				formatTrack((TrackWrapper) parent, new HiResDate(_interval));
			}
		}

		// is this a fix?
		if (!(item instanceof FixWrapper)) {
			return;
		}

		final String hisName = parent.getName();

		if (_layerName != null && _layerName.length() > 0 && !_layerName.equals(parent.getName())) {
			return;
		}
		if (_sym != null && _sym.length() > 0) {
			if (parent instanceof TrackWrapper) {
				// match the symbology
				// return;
			}
		}

		if (item instanceof TimeStampedDataItem) {
			final long longInt = _interval.getDate().getTime();

			final Long lastTime = _lastTimes.get(hisName);
			final TimeStampedDataItem tsd = (TimeStampedDataItem) item;
			long thisTime = tsd.getDTG().getDate().getTime();

			// just check if we're doing "apply all"
			if (longInt == TimeFrequencyPropertyEditor.SHOW_ALL_FREQUENCY) {
				// ok, skip it
			} else if (longInt == 0) {
				applyFormat((FixWrapper) item);
			} else if (lastTime == null || thisTime >= lastTime + longInt) {
				// ok, we need to test if we're actually doing it

				// do we need to clip the time to a regular interval?
				if (_regularInterval) {
					// ok - store the last instance of
					thisTime = thisTime - (thisTime % longInt);
				}

				// store the new time
				_lastTimes.put(hisName, thisTime);

				// and apply the formatting
				applyFormat((FixWrapper) item);
			}
		}
	}

	@Override
	public void paint(final CanvasType dest) {
		// don't bother, it can't be plotted
	}

	@Override
	public void reset() {
		_lastTimes.clear();
	}

	public void setAttributeType(final int type) {
		_type = type;
	}

	public void setInterval(final HiResDate interval) {
		this._interval = interval;
	}

	public void setLayerName(final String _layerName) {
		this._layerName = _layerName;
	}

	@Override
	public void setName(final String formatName) {
		this._formatName = formatName;
	}

	public void setRegularIntervals(final boolean regularInterval) {
		this._regularInterval = regularInterval;
	}

	public void setSymbology(final String _sym) {
		this._sym = _sym;
	}

	public void setType(final Integer type) {
		this._type = type;
	}

	@Override
	public String toString() {
		return getName();
	}
}