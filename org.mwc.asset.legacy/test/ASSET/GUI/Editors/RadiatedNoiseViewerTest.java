package ASSET.GUI.Editors;

import static org.junit.jupiter.api.Assertions.*;

import static ASSET.GUI.Editors.RadiatedNoiseViewer.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ASSET.GUI.Editors.RadiatedNoiseViewer.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import ASSET.Models.Mediums.BroadbandRadNoise;
import ASSET.Models.Mediums.Optic;
import ASSET.Models.Vessels.Radiated.RadiatedCharacteristics;
import ASSET.Models.Vessels.Radiated.RadiatedCharacteristics.Medium;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.PlainPropertyEditor;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Properties.Swing.SwingPropertiesPanel;
import MWC.GUI.Properties.Swing.SwingPropertyEditor2;
import MWC.GenericData.WorldDistance;
import org.junit.jupiter.api.Test;

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public class RadiatedNoiseViewerTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";

	boolean set = false;


	@Test
	public void testMe() {
		// create the object
		final RadiatedNoiseViewer dv = new RadiatedNoiseViewer();

		set = false;
		final ASSET.Models.Vessels.SSN cp = new ASSET.Models.Vessels.SSN(12);

		final RadiatedCharacteristics rc = new RadiatedCharacteristics();
		rc.add(1, new Optic(12, new WorldDistance(12, WorldDistance.METRES)));
		rc.add(3, new BroadbandRadNoise(23));
		cp.setRadiatedChars(rc);

		// store the radiated chars
		dv.setObject(cp.getRadiatedChars());

		assertEquals(dv._chars, cp.getRadiatedChars(), "our data stored");

		// check data loaded
		assertEquals(dv._chars.getMediums().size(), 2, "list shows our data");

		// check gui control created
		assertEquals(dv._noiseList.getName(), "List of noise mediums", "form correctly laid out");

		// close down
		dv.doClose();

	}
}
