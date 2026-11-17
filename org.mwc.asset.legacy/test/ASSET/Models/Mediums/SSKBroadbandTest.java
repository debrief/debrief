package ASSET.Models.Mediums;

import static ASSET.Models.Mediums.SSKBroadband.*;
import ASSET.Models.Mediums.SSKBroadband.*;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;

//////////////////////////////////////////////////
// testing
//////////////////////////////////////////////////
public class SSKBroadbandTest extends ASSET.Util.EditableTesting {
	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return new SSKBroadband(12, 12);
	}
}
