package ASSET.GUI.Editors;

import static ASSET.GUI.Editors.VesselStatusEditor.*;
import ASSET.GUI.Editors.VesselStatusEditor.*;
import ASSET.Models.Movement.SSMovementCharacteristics;
import ASSET.Models.Vessels.SSN;
import ASSET.Participants.Status;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GUI.Properties.BoundedInteger;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

//////////////////////////////////////////////////////////////////////
//
//////////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////
// add testing code
//////////////////////////////////////////////////
public class VesselStatusEditorTest extends ASSET.Util.EditableTesting {
	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		final SSN theSSN = new SSN(12);
		final Status stat = new Status(12, 12);
		stat.setLocation(new WorldLocation(12, 12, 0));
		stat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));
		theSSN.setMovementChars(SSMovementCharacteristics.getSampleSSChars());
		theSSN.setStatus(stat);
		final VesselStatusEditor res = new VesselStatusEditor(theSSN);

		return res;
	}
}
