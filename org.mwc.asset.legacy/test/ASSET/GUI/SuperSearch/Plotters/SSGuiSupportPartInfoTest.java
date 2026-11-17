package ASSET.GUI.SuperSearch.Plotters;

import static ASSET.GUI.SuperSearch.Plotters.SSGuiSupport.*;
import ASSET.GUI.SuperSearch.Plotters.SSGuiSupport.*;
import java.util.Enumeration;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Scenario.MultiForceScenario;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GUI.Plottable;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldSpeed;

public class SSGuiSupportPartInfoTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		final SSGuiSupport ed = new SSGuiSupport();
		ed.setScenario(new MultiForceScenario());
		return ed;
	}
}
