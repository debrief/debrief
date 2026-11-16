package ASSET.Models.Mediums;

import static ASSET.Models.Mediums.BroadbandRadNoise.*;
import ASSET.Models.Mediums.BroadbandRadNoise.*;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldSpeed;

//////////////////////////////////////////////////
// testing
//////////////////////////////////////////////////
public class BroadbandRadNoiseTest extends ASSET.Util.EditableTesting {

	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return new BroadbandRadNoise(12);
	}
}
