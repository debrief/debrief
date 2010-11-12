package org.mwc.asset.comms.restlet.host;

import java.util.List;
import java.util.Vector;

import org.mwc.asset.comms.restlet.data.Scenario;
import org.mwc.asset.comms.restlet.data.ScenariosResource;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class ScenariosHandler extends ServerResource implements
		ScenariosResource
{
	
	@Get
	public List<Scenario> retrieve()
	{
		Vector<Scenario> res = new Vector<Scenario>();
		res.add(new Scenario("Scott", 434));
		res.add(new Scenario("Scott", 22));
		res.add(new Scenario("Scott", 33));
		res.add(new Scenario("Scott", 11));
		
		return res;
	}
}