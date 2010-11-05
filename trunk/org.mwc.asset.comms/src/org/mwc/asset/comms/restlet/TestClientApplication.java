package org.mwc.asset.comms.restlet;

import java.util.Iterator;
import java.util.List;

import org.mwc.asset.comms.restlet.data.Participant;
import org.mwc.asset.comms.restlet.data.ParticipantResource;
import org.mwc.asset.comms.restlet.data.ParticipantsResource;
import org.mwc.asset.comms.restlet.data.Scenario;
import org.mwc.asset.comms.restlet.data.ScenarioResource;
import org.mwc.asset.comms.restlet.data.ScenariosResource;
import org.restlet.resource.ClientResource;

import ASSET.Participants.Category;

public class TestClientApplication
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

		ClientResource cr = new ClientResource("http://localhost:8080/scenario");
		
		// does it have a scenario?
		ScenariosResource scenR = cr.wrap(ScenariosResource.class);
		List<Scenario> scen = scenR.retrieve();
		Scenario scen1 = null;
		
		for (Iterator<Scenario> iterator = scen.iterator(); iterator.hasNext();)
		{
			Scenario scenario = iterator.next();
			System.out.println("this scen is:" + scenario.getName());
			scen1 = scenario;
		}
		
		if(scen1 != null)
		{
			ClientResource pa = new ClientResource("http://localhost:8080/scenario/" + scen1.getId() + "/participant");
			ParticipantsResource partR = pa.wrap(ParticipantsResource.class);
			List<Participant> parts = partR.retrieve();
			if(parts != null)
			{
				Iterator<Participant> iter = parts.iterator();
				while(iter.hasNext())
				{
					Participant thisP = iter.next();
					System.out.println("this name is:" + thisP.getName());
					Category cat = thisP.getCategory();
					System.out.println("this cat is:" + cat);

//					Integer thisI = thisP.getId();
//					System.out.println("this is id:" + thisI);
//					
//					ClientResource pa = new ClientResource("http://localhost:8080/scenario/" + id + "/participant/" + thisI);
//					ParticipantResource partR = pa.wrap(ParticipantResource.class);
//					Participant part = partR.retrieve();
//					System.out.println("this name is:" + part.getName());
//					Category cat = part.getCategory();
//					System.out.println("this cat is:" + cat);
				}
			}
			
		}
//			
//		ContactResource resource = cr.wrap(ContactResource.class);
//		Contact contact = resource.retrieve();
//
//		if (contact != null)
//		{
//			System.out.println("firstname: " + contact.getFirstName());
//			System.out.println(" lastname: " + contact.getLastName());
//			System.out.println("     nage: " + contact.getAge());
//		}
//
//		// make the change
//		contact.setAge(contact.getAge()+1);
//		resource.store(contact);
//
//		contact = resource.retrieve();
//
//		if (contact != null)
//		{
//			System.out.println("firstname: " + contact.getFirstName());
//			System.out.println(" lastname: " + contact.getLastName());
//			System.out.println("     nage: " + contact.getAge());
//		}
//		
//		
//		int NUM = 10;
//		for(int i=0;i<NUM;i++)
//		{
//			// make the change
//			contact.setAge(contact.getAge()+1);
//			resource.store(contact);
//			System.out.print(".");
//			if((i % 10) == 0)
//				System.out.println();
//		}
//
//		contact = resource.retrieve();
//
//		if (contact != null)
//		{
//			System.out.println("firstname: " + contact.getFirstName());
//			System.out.println(" lastname: " + contact.getLastName());
//			System.out.println("     nage: " + contact.getAge());
//		}
//		
// 
//		try
//		{
//			cr.get(MediaType.APPLICATION_JSON).write(System.out);
//		}
//		catch (ResourceException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		catch (IOException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
