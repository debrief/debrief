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

package ASSET.Util;

import static ASSET.Util.SupportTesting.*;
import ASSET.Util.SupportTesting.*;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.GUI.Core.CoreGUISwing;
import ASSET.Models.Decision.TargetType;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.ParticipantMovedListener;
import ASSET.Participants.Status;
import ASSET.Scenario.Observers.TrackPlotObserver;
import ASSET.Scenario.Observers.Recording.CSVTrackObserver;
import ASSET.Scenario.Observers.Recording.DebriefReplayObserver;
import MWC.GUI.Editable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;

//////////////////////////////////////////////////
// add property editing testing
//////////////////////////////////////////////////
abstract public class EditableTesting extends SupportTesting {
	public EditableTesting() {
	}


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	abstract public Editable getEditable();

	/**
	 * run through tests of the editable properties
	 */
	// TODO FIX-TEST
	public final void NtestMyParams() {
		// just check that our ASSET-specific editors are loaded
		final PropertyEditor pe = PropertyEditorManager.findEditor(TargetType.class);
		if (pe == null)
			CoreGUISwing.registerEditors();

		// ok, get on with it
		final Editable toBeTested = getEditable();
		Editable.editableTesterSupport.testTheseParameters(toBeTested);
	}

}
