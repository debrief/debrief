package Debrief.ReaderWriter.XML.Tactical;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import java.util.Enumeration;

import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import Debrief.ReaderWriter.Replay.ImportReplay;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GenericData.HiResDate;
import MWC.TacticalData.NarrativeEntry;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

public final class NarrativeHandler extends
		MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

	/**
	 * temporary variable used to inform the Narrative of the StepControl which it
	 * should be watching
	 */
	static private Debrief.GUI.Tote.StepControl _theStepper;

	static private final String _myType = "narrative";

	private final MWC.GUI.Layers _theLayers;

	// our "working" Narrative
	Debrief.Wrappers.NarrativeWrapper _myNarrative;

	/**
	 * temporarily set the step control
	 */
	static public void setStepper(Debrief.GUI.Tote.StepControl stepper)
	{
		_theStepper = stepper;
	}

	public NarrativeHandler(MWC.GUI.Layers theLayers)
	{
		// inform our parent what type of class we are
		super(_myType);

		// store the layers object, so that we can add ourselves to it
		_theLayers = theLayers;

		addAttributeHandler(new HandleAttribute("Name")
		{
			public void setValue(String name, String val)
			{
				_myNarrative.setName(val);
			}
		});
		addHandler(new EntryHandler()
		{
			public void addEntry(MWC.TacticalData.NarrativeEntry entry)
			{
				addThis(entry);
			}
		});
	}

	// this is one of ours, so get on with it!
	protected final void handleOurselves(String name, Attributes attributes)
	{
		_myNarrative = new Debrief.Wrappers.NarrativeWrapper("", _theStepper);

		super.handleOurselves(name, attributes);

	}

	void addThis(MWC.TacticalData.NarrativeEntry entry)
	{
		_myNarrative.add(entry);
	}

	public final void elementClosed()
	{
		// is this one of those funny narratives?
		if (_myNarrative.getName() != ImportReplay.NARRATIVE_LAYER)
		{
			// yes, better put the narrative name into the type field, since the user won't see it 
			// in the layer manager
			String narrName = _myNarrative.getName();

			Enumeration<Editable> enumer = _myNarrative.elements();
			while (enumer.hasMoreElements())
			{
				NarrativeEntry ne = (NarrativeEntry) enumer.nextElement();
				String theType = ne.getType();
				// does it already have a source?
				if (theType == null)
					ne.setType(narrName);
			}
		}

		// is there already a narratives layer?
		Layer oldNarr = _theLayers.findLayer(ImportReplay.NARRATIVE_LAYER);

		if (oldNarr != null)
		{
			// ok, add the new narrative to the old one
			Enumeration<Editable> enumer = _myNarrative.elements();
			while (enumer.hasMoreElements())
			{
				NarrativeEntry ne = (NarrativeEntry) enumer.nextElement();
				oldNarr.add(ne);
			}
		}
		else
		{
			// we don't already have a narrative, create a new one (with the correct name)
			
			// ok, do we have the right name?
			if (_myNarrative.getName() != ImportReplay.NARRATIVE_LAYER)
			{
				_myNarrative.setName(ImportReplay.NARRATIVE_LAYER);
			}

			// ok, now add it
			_theLayers.addThisLayer(_myNarrative);
		}
		// forget who we are
		_myNarrative = null;
	}

	public static void exportNarrative(
			Debrief.Wrappers.NarrativeWrapper Narrative, org.w3c.dom.Element parent,
			org.w3c.dom.Document doc)
	{

		Element trk = doc.createElement(_myType);
		trk.setAttribute("Name", Narrative.getName());
		// now the entries
		java.util.Enumeration<Editable> iter = Narrative.elements();
		while (iter.hasMoreElements())
		{
			MWC.GUI.Plottable pl = (MWC.GUI.Plottable) iter.nextElement();
			if (pl instanceof MWC.TacticalData.NarrativeEntry)
			{
				MWC.TacticalData.NarrativeEntry fw = (MWC.TacticalData.NarrativeEntry) pl;
				EntryHandler.exportEntry(fw, trk, doc);
			}

		}

		parent.appendChild(trk);

	}

	// //////////////////////////////////////////////////
	// handler for narrative entries
	// //////////////////////////////////////////

	static abstract public class EntryHandler extends
			MWC.Utilities.ReaderWriter.XML.MWCXMLReader
	{

		private static final String _myType1 = "narrative_entry";
		String _entry;
		HiResDate _dtg;
		String _track;
		protected String _type;

		public EntryHandler()
		{
			// inform our parent what type of class we are
			super(_myType1);

			addAttributeHandler(new HandleAttribute("Entry")
			{
				public void setValue(String name, String value)
				{
					_entry = value;
				}
			});

			addAttributeHandler(new HandleAttribute("Track")
			{
				public void setValue(String name, String value)
				{
					_track = value;
				}
			});

			addAttributeHandler(new HandleAttribute("Type")
			{
				public void setValue(String name, String value)
				{
					_type = value;
				}
			});

			addAttributeHandler(new HandleAttribute("Dtg")
			{
				public void setValue(String name, String value)
				{
					_dtg = DebriefFormatDateTime.parseThis(value);
				}
			});

		}

		public final void handleOurselves(String name, Attributes atts)
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
			MWC.TacticalData.NarrativeEntry ne = new MWC.TacticalData.NarrativeEntry(
					_track, _type, _dtg, _entry);

			// pass it to the parent
			addEntry(ne);

		}

		abstract public void addEntry(MWC.TacticalData.NarrativeEntry entry);

		public static void exportEntry(MWC.TacticalData.NarrativeEntry Entry,
				org.w3c.dom.Element parent, org.w3c.dom.Document doc)
		{

			Element eEntry = doc.createElement(_myType1);
			eEntry.setAttribute("Dtg", writeThis(Entry.getDTG()));
			eEntry.setAttribute("Entry", Entry.getEntry());
			eEntry.setAttribute("Track", Entry.getTrackName());

			// do we have an type attribute?
			String typeStr = Entry.getType();
			if (typeStr != null)
				eEntry.setAttribute("Type", typeStr);

			parent.appendChild(eEntry);

		}

	}

}