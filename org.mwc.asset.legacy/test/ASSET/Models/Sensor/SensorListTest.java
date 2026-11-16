package ASSET.Models.Sensor;

import static ASSET.Models.Sensor.SensorList.*;
import ASSET.Models.Sensor.SensorList.*;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import ASSET.Models.SensorType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.DemandedSensorStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.ParticipantDetectedListener;
import ASSET.Participants.Status;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;

//////////////////////////////////////////////////
// test suport
//////////////////////////////////////////////////
public class SensorListTest extends ASSET.Util.EditableTesting {
	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return new SensorList();
	}
}
