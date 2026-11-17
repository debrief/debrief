package ASSET.Models.Movement;

import static ASSET.Models.Movement.MovementCharacteristics.*;
import ASSET.Models.Movement.MovementCharacteristics.*;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldAcceleration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;

//////////////////////////////////////////////////////
// member variables
//////////////////////////////////////////////////////

//////////////////////////////////////////////////
// testing
//////////////////////////////////////////////////
public class MovementCharacteristicsTest extends ASSET.Util.EditableTesting {
	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return MovementCharacteristics.getSampleChars();
	}
}
