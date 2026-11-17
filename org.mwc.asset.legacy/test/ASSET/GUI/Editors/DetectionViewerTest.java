package ASSET.GUI.Editors;

import static org.junit.jupiter.api.Assertions.*;

import static ASSET.GUI.Editors.DetectionViewer.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ASSET.GUI.Editors.DetectionViewer.*;
import java.awt.BorderLayout;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingConstants;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import org.junit.jupiter.api.Test;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Sensor.SensorDataProvider;
import ASSET.Models.Sensor.Initial.BroadbandSensor;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.ParticipantDetectedListener;

////////////////////////////////////////////////////
// member objects
////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public class DetectionViewerTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";

	boolean set = false;


	@Test
	public void testMe() {
		// create the object
		final DetectionViewer dv = new DetectionViewer();

		set = false;
		final ParticipantType cp = new CoreParticipant(12) {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void addParticipantDetectedListener(final ParticipantDetectedListener list) {
				super.addParticipantDetectedListener(list);
				if (list instanceof DetectionViewer) {
					set = true;
				}
			}
		};

		dv.setObject(cp);

		assertTrue(set == true, "has been set as detected listener");

		assertEquals(dv.detList.getModel().getSize(), 0, "list starts off being empty");

		// try setting the list
		dv.newDetections(null);
		assertEquals(dv.detList.getModel().getSize(), 0, "we can set null data");

		final DetectionList dl = new DetectionList();
		dv.newDetections(dl);
		assertEquals(dv.detList.getModel().getSize(), 0, "we can set zero length data");

		final BroadbandSensor bb = new BroadbandSensor(122);
		final ParticipantType target = new ASSET.Models.Vessels.Surface(12);
		target.setName("surfare target");
		dl.add(new DetectionEvent(0, cp.getId(), null, bb, null, null, null, null, null, null, null, null, target));
		dv.newDetections(dl);
		assertEquals(dv.detList.getModel().getSize(), 1, "we can set  data");

		// check restart
		dv.restart(null);
		assertEquals(dv.detList.getModel().getSize(), 0, "we can do restart");

		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println("POSSIBLE JUNIT PROBLEM - THIS THREAD (DetectionViewer) SHOULD DIE");

		// close down
		dv.doClose();

	}
}
