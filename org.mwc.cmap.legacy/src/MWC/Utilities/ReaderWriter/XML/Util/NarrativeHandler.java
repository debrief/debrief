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
package MWC.Utilities.ReaderWriter.XML.Util;

import java.text.ParseException;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import java.util.Enumeration;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WatchableList;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;
import MWC.Utilities.Errors.Trace;
import MWC.Utilities.ReaderWriter.XML.LayerHandler;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

public final class NarrativeHandler extends
		MWCXMLReader
{

	static private final String _myType = "narrative";

	private final Layers _theLayers;

	// our "working" Narrative
	private NarrativeWrapper _myNarrative;

	public NarrativeHandler(final Layers theLayers)
	{
		// inform our parent what type of class we are
		super(_myType);

		// store the layers object, so that we can add ourselves to it
		_theLayers = theLayers;

		addAttributeHandler(new HandleAttribute("Name")
		{
			public void setValue(final String name, final String val)
			{
				_myNarrative.setName(val);
			}
		});
		addHandler(new EntryHandler()
		{
			public void addEntry(final NarrativeEntry entry)
			{
				addThis(entry);
			}
		});
	}

	// this is one of ours, so get on with it!
	protected final void handleOurselves(final String name, final Attributes attributes)
	{
		_myNarrative = new NarrativeWrapper("");

		super.handleOurselves(name, attributes);

	}

	private void addThis(final NarrativeEntry entry)
	{
		// see if we have a color code for this entry type
		String source = entry.getSource();
		Layer thisL = _theLayers.findLayer(source);
    if (thisL != null && thisL instanceof WatchableList)
    {
      WatchableList tw = (WatchableList) thisL;
      entry.setColor(tw.getColor());
    }
		
		_myNarrative.add(entry);
	}

	public final void elementClosed()
	{
		// is this one of those funny narratives?
		if (!_myNarrative.getName().equals(LayerHandler.NARRATIVE_LAYER))
		{
			// yes, better put the narrative name into the type field, since the user won't see it 
			// in the layer manager
			final String narrName = _myNarrative.getName();

			final Enumeration<Editable> enumer = _myNarrative.elements();
			while (enumer.hasMoreElements())
			{
				final NarrativeEntry ne = (NarrativeEntry) enumer.nextElement();
				final String theType = ne.getType();
				// does it already have a source?
				if (theType == null)
					ne.setType(narrName);
			}
		}

		// is there already a narratives layer?
		final Layer oldNarr = _theLayers.findLayer(LayerHandler.NARRATIVE_LAYER);

		if (oldNarr != null)
		{
			// ok, add the new narrative to the old one
			final Enumeration<Editable> enumer = _myNarrative.elements();
			while (enumer.hasMoreElements())
			{
				final NarrativeEntry ne = (NarrativeEntry) enumer.nextElement();
				oldNarr.add(ne);
			}
		}
		else
		{
			// we don't already have a narrative, create a new one (with the correct name)
			
			// ok, do we have the right name?
			if (_myNarrative.getName().equals(LayerHandler.NARRATIVE_LAYER))
			{
				_myNarrative.setName(LayerHandler.NARRATIVE_LAYER);
			}

			// ok, now add it
			_theLayers.addThisLayer(_myNarrative);
		}
		// forget who we are
		_myNarrative = null;
	}

	public static void exportNarrative(
			final NarrativeWrapper Narrative, final Element parent,
			final Document doc)
	{

		final Element trk = doc.createElement(_myType);
		trk.setAttribute("Name", Narrative.getName());
		// now the entries
		final Enumeration<Editable> iter = Narrative.elements();
		while (iter.hasMoreElements())
		{
			final Plottable pl = (Plottable) iter.nextElement();
			if (pl instanceof NarrativeEntry)
			{
				final NarrativeEntry fw = (NarrativeEntry) pl;
				EntryHandler.exportEntry(fw, trk, doc);
			}

		}

		parent.appendChild(trk);

	}

	// //////////////////////////////////////////////////
	// handler for narrative entries
	// //////////////////////////////////////////

	static abstract public class EntryHandler extends
			MWCXMLReader
	{

		private static final String _myType1 = "narrative_entry";
		private String _entry;
		private HiResDate _dtg;
		private String _track;
		protected String _type;

		public EntryHandler()
		{
			// inform our parent what type of class we are
			super(_myType1);

			addAttributeHandler(new HandleAttribute("Entry")
			{
				public void setValue(final String name, final String value)
				{
					_entry = value;
				}
			});

			addAttributeHandler(new HandleAttribute("Track")
			{
				public void setValue(final String name, final String value)
				{
					_track = value;
				}
			});

			addAttributeHandler(new HandleAttribute("Type")
			{
				public void setValue(final String name, final String value)
				{
					_type = value;
				}
			});

			addAttributeHandler(new HandleAttribute("Dtg")
			{
				public void setValue(final String name, final String value)
				{
					try
          {
            _dtg = DebriefFormatDateTime.parseThis(value);
          }
          catch (ParseException e)
          {
            Trace.trace(e, "While parsing date");
          }
				}
			});

		}

		public final void handleOurselves(final String name, final Attributes atts)
		{
			// create the new items
			_entry = "";
			_dtg = null;
			_track = "";
			_type = null;

			super.handleOurselves(name, atts);
		}

		public final void elementClosed()
		{
			// create the new object
			final NarrativeEntry ne = new NarrativeEntry(
					_track, _type, _dtg, _entry);

			// pass it to the parent
			addEntry(ne);

		}

		abstract public void addEntry(NarrativeEntry entry);

		public static void exportEntry(final NarrativeEntry Entry,
				final Element parent, final Document doc)
		{

			final Element eEntry = doc.createElement(_myType1);
			eEntry.setAttribute("Dtg", writeThis(Entry.getDTG()));
			eEntry.setAttribute("Entry", Entry.getEntry());
			eEntry.setAttribute("Track", Entry.getTrackName());

			// do we have an type attribute?
			final String typeStr = Entry.getType();
			if (typeStr != null)
				eEntry.setAttribute("Type", typeStr);

			parent.appendChild(eEntry);

		}

	}

}