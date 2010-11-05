package org.mwc.asset.comms.restlet;

import org.mwc.asset.comms.restlet.data.Participant;
import org.mwc.asset.comms.restlet.data.ParticipantResource;
import org.mwc.asset.comms.restlet.data.Scenario;
import org.mwc.asset.comms.restlet.data.ScenarioResource;
import org.restlet.resource.ClientResource;

public class TestClientApplication
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

		ClientResource cr = new ClientResource("http://localhost:8080/scenario/123");
		
		// does it have a scenario?
		ScenarioResource scenR = cr.wrap(ScenarioResource.class);
		Scenario scen = scenR.retrieve();
		
		if(scen != null)
		{
			System.out.println("name:" + scen.getName());
			
			// have a look at the participants
			Integer[] parts = scen.getListOfParticipants();
			if(parts != null)
			{
				for(int i=0;i<parts.length;i++)
				{
					Integer thisI = parts[i];
					System.out.println("this is id:" + thisI);
					
					ClientResource pa = new ClientResource("http://localhost:8080/participant/" + thisI);
					ParticipantResource partR = pa.wrap(ParticipantResource.class);
					Participant part = partR.retrieve();
					System.out.println("this name is:" + part.getName());
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
