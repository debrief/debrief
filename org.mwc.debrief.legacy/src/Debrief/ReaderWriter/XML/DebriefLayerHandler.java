
package Debrief.ReaderWriter.XML;

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

import Debrief.ReaderWriter.XML.Formatters.CoreFormatHandler;
import Debrief.ReaderWriter.XML.Formatters.HideLayerFormatHandler;
import Debrief.ReaderWriter.XML.Formatters.SliceTargetFormatHandler;
import Debrief.ReaderWriter.XML.Formatters.TrackNameAtEndFormatHandler;
import Debrief.ReaderWriter.XML.Shapes.ArcHandler;
import Debrief.ReaderWriter.XML.Shapes.CircleHandler;
import Debrief.ReaderWriter.XML.Shapes.EllipseHandler;
import Debrief.ReaderWriter.XML.Shapes.FurthestOnCircleHandler;
import Debrief.ReaderWriter.XML.Shapes.LabelHandler;
import Debrief.ReaderWriter.XML.Shapes.LineHandler;
import Debrief.ReaderWriter.XML.Shapes.PolygonHandler;
import Debrief.ReaderWriter.XML.Shapes.RangeRingsHandler;
import Debrief.ReaderWriter.XML.Shapes.RectangleHandler;
import Debrief.ReaderWriter.XML.Shapes.VectorHandler;
import Debrief.ReaderWriter.XML.Shapes.WheelHandler;
import Debrief.ReaderWriter.XML.Tactical.HeavyWeightTrackHandler;
import Debrief.ReaderWriter.XML.Tactical.LightweightTrackHandler;
import Debrief.Wrappers.ShapeWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Formatters.CoreFormatItemListener;
import Debrief.Wrappers.Formatters.HideLayerFormatListener;
import Debrief.Wrappers.Formatters.SliceTrackFormatListener;
import Debrief.Wrappers.Formatters.TrackNameAtEndFormatListener;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layers.INewItemListener;
import MWC.GUI.Plottable;
import MWC.Utilities.ReaderWriter.XML.LayerHandler;
import MWC.Utilities.ReaderWriter.XML.PlottableExporter;

public class DebriefLayerHandler extends MWC.Utilities.ReaderWriter.XML.LayerHandler {
	/**
	 * we're having trouble ensuring that the Debrief exporters get declared.
	 * Introduce some extra checking to double-check it's all ok.
	 */
	private static boolean _exportersInitialised = false;

	protected static void checkExporters() {
		// have the Debrief-specific exporters been declared?
		if (!_exportersInitialised) {
			_exportersInitialised = true;

			// hey, the parent ones haven't either. Go for it.
			LayerHandler.checkExporters();
			_myExporters.put(TrackWrapper.class, new HeavyWeightTrackHandler() {
				
				@Override
				public void storeTrack(TrackWrapper track) {
				}
			});
			_myExporters.put(MWC.GUI.Shapes.ArcShape.class, new ArcHandler() {
				@Override
				public void addPlottable(final MWC.GUI.Plottable plottable) {
				}
			});
			_myExporters.put(LightweightTrackWrapper.class, new LightweightTrackHandler() {
				@Override
				public void storeTrack(final LightweightTrackWrapper track) {
				}
			});
			_myExporters.put(MWC.GUI.Shapes.CircleShape.class, new CircleHandler() {
				@Override
				public void addPlottable(final MWC.GUI.Plottable plottable) {
				}
			});
			_myExporters.put(MWC.GUI.Shapes.EllipseShape.class, new EllipseHandler() {
				@Override
				public void addPlottable(final MWC.GUI.Plottable plottable) {
				}
			});
			_myExporters.put(MWC.GUI.Shapes.LineShape.class, new LineHandler() {
				@Override
				public void addPlottable(final MWC.GUI.Plottable plottable) {
				}
			});
			_myExporters.put(MWC.GUI.Shapes.VectorShape.class, new VectorHandler() {
				@Override
				public void addPlottable(final MWC.GUI.Plottable plottable) {
				}
			});
			_myExporters.put(MWC.GUI.Shapes.PolygonShape.class, new PolygonHandler() {
				@Override
				public void addPlottable(final MWC.GUI.Plottable plottable) {
				}
			});
			_myExporters.put(MWC.GUI.Shapes.RectangleShape.class, new RectangleHandler() {
				@Override
				public void addPlottable(final MWC.GUI.Plottable plottable) {
				}
			});
			_myExporters.put(MWC.GUI.Shapes.WheelShape.class, new WheelHandler() {
				@Override
				public void addPlottable(final MWC.GUI.Plottable plottable) {
				}
			});
			_myExporters.put(MWC.GUI.Shapes.FurthestOnCircleShape.class, new FurthestOnCircleHandler() {
				@Override
				public void addPlottable(final MWC.GUI.Plottable plottable) {
				}
			});
			_myExporters.put(MWC.GUI.Shapes.RangeRingShape.class, new RangeRingsHandler() {
				@Override
				public void addPlottable(final MWC.GUI.Plottable plottable) {
				}
			});

			_myExporters.put(Debrief.Wrappers.LabelWrapper.class, new LabelHandler() {
				@Override
				public void addPlottable(final MWC.GUI.Plottable plottable) {
				}
			});
		}
	}

	public static void exportLayer(final MWC.GUI.BaseLayer layer, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// check our exporters
		checkExporters();

		final org.w3c.dom.Element eLayer = doc.createElement("layer");

		eLayer.setAttribute("Name", layer.getName());
		eLayer.setAttribute("Visible", writeThis(layer.getVisible()));
		eLayer.setAttribute("LineThickness", writeThis(layer.getLineThickness()));

		// step through the components of the layer
		final java.util.Enumeration<Editable> iter = layer.elements();
		while (iter.hasMoreElements()) {
			final MWC.GUI.Plottable nextPlottable = (MWC.GUI.Plottable) iter.nextElement();

			exportThisDebriefItem(nextPlottable, eLayer, doc);
		}

		parent.appendChild(eLayer);

	}

	public static void exportThisDebriefItem(final MWC.GUI.Plottable nextPlottable, final org.w3c.dom.Element eLayer,
			final org.w3c.dom.Document doc) {
		// get ready..
		checkExporters();

		// definition of what parameter we are going to search for (since
		// shapeWrappers are indexed by shape not actual class)
		Object classId = null;

		// if this is a shape then we've got to suck the shape out
		if (nextPlottable instanceof ShapeWrapper) {
			final ShapeWrapper sw = (ShapeWrapper) nextPlottable;
			classId = sw.getShape().getClass();
		} else
			classId = nextPlottable.getClass();

		// try to get this one
		final Object exporterType = _myExporters.get(classId);
		if (exporterType != null) {
			final PlottableExporter cl = (PlottableExporter) exporterType;
			cl.exportThisPlottable(nextPlottable, eLayer, doc);
		} else {
			// special handling, see if it's an item formatter
			if (nextPlottable instanceof INewItemListener) {
				// ok, which type is it?
				if (nextPlottable instanceof CoreFormatItemListener) {
					CoreFormatHandler.exportThisPlottable(nextPlottable, eLayer, doc);
				} else if (nextPlottable instanceof TrackNameAtEndFormatListener) {
					TrackNameAtEndFormatHandler.exportThisPlottable(nextPlottable, eLayer, doc);
				} else if (nextPlottable instanceof HideLayerFormatListener) {
					HideLayerFormatHandler.exportThisPlottable(nextPlottable, eLayer, doc);
				} else if (nextPlottable instanceof SliceTrackFormatListener) {
					SliceTargetFormatHandler.exportThisPlottable(nextPlottable, eLayer, doc);
				}
			} else {
				MWC.Utilities.Errors.Trace.trace("Debrief Exporter not found for " + nextPlottable.getName());
			}

		}

	}

	public DebriefLayerHandler(final MWC.GUI.Layers theLayers) {
		// inform our parent what type of class we are
		super(theLayers);

		addHandler(new LineHandler() {
			@Override
			public void addPlottable(final MWC.GUI.Plottable plottable) {
				addThis(plottable);
			}
		});

		addHandler(new VectorHandler() {

			@Override
			public void addPlottable(final Plottable plottable) {
				addThis(plottable);
			}
		});

		addHandler(new PolygonHandler() {
			@Override
			public void addPlottable(final MWC.GUI.Plottable plottable) {
				addThis(plottable);
			}
		});

		addHandler(new LightweightTrackHandler() {
			@Override
			public void storeTrack(final LightweightTrackWrapper track) {
				addThis(track);
			}
		});
		
		addHandler(new HeavyWeightTrackHandler() {
			@Override
			public void storeTrack(final TrackWrapper track) {
				addThis(track);
			}
		});

		addHandler(new EllipseHandler() {
			@Override
			public void addPlottable(final MWC.GUI.Plottable plottable) {
				addThis(plottable);
			}
		});

		addHandler(new RectangleHandler() {
			@Override
			public void addPlottable(final MWC.GUI.Plottable plottable) {
				addThis(plottable);
			}
		});

		addHandler(new ArcHandler() {
			@Override
			public void addPlottable(final MWC.GUI.Plottable plottable) {
				addThis(plottable);
			}
		});

		addHandler(new CircleHandler() {
			@Override
			public void addPlottable(final MWC.GUI.Plottable plottable) {
				addThis(plottable);
			}
		});

		addHandler(new WheelHandler() {
			@Override
			public void addPlottable(final MWC.GUI.Plottable plottable) {
				addThis(plottable);
			}
		});
		addHandler(new RangeRingsHandler() {
			@Override
			public void addPlottable(final MWC.GUI.Plottable plottable) {
				addThis(plottable);
			}
		});
		addHandler(new FurthestOnCircleHandler() {
			@Override
			public void addPlottable(final MWC.GUI.Plottable plottable) {
				addThis(plottable);
			}
		});
		addHandler(new LabelHandler() {
			@Override
			public void addPlottable(final MWC.GUI.Plottable plottable) {
				addThis(plottable);
			}
		});
		addHandler(new CoreFormatHandler() {
			@Override
			public void addFormatter(final Editable editable) {
				addThis(editable);
			}
		});
		addHandler(new TrackNameAtEndFormatHandler() {
			@Override
			public void addFormatter(final Editable editable) {
				addThis(editable);
			}
		});
		addHandler(new HideLayerFormatHandler() {
			@Override
			public void addFormatter(final Editable editable) {
				addThis(editable);
			}
		});
		addHandler(new SliceTargetFormatHandler() {
			@Override
			public void addFormatter(final Editable editable) {
				addThis(editable);
			}
		});

	}

}