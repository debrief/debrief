package ASSET.Models.Mediums;

import static ASSET.Models.Mediums.NarrowbandRadNoise.*;
import ASSET.Models.Mediums.NarrowbandRadNoise.*;
import MWC.GUI.Editable;
import MWC.GenericData.WorldSpeed;

//////////////////////////////////////////////////
// testing
//////////////////////////////////////////////////
public class NarrowbandRadNoiseTest extends ASSET.Util.EditableTesting {

	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return new NarrowbandRadNoise(12, 12);
	}
}
