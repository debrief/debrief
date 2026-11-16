package ASSET.Models.Mediums;

import static ASSET.Models.Mediums.Optic.*;
import ASSET.Models.Mediums.Optic.*;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;

//////////////////////////////////////////////////
// testing
//////////////////////////////////////////////////
public class OpticTest extends ASSET.Util.EditableTesting {
	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return new Optic(12, new WorldDistance(12, WorldDistance.NM));
	}
}
