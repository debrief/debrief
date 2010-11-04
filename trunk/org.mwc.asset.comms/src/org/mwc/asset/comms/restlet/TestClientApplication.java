package org.mwc.asset.comms.restlet;

import java.io.IOException;

import org.mwc.asset.comms.restlet.test.data.Contact;
import org.mwc.asset.comms.restlet.test.data.ContactResource;
import org.restlet.data.MediaType;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

public class TestClientApplication
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

		ClientResource cr = new ClientResource("http://localhost:8080/contacts/123");
		// Get the Contact object
		ContactResource resource = cr.wrap(ContactResource.class);
		Contact contact = resource.retrieve();

		if (contact != null)
		{
			System.out.println("firstname: " + contact.getFirstName());
			System.out.println(" lastname: " + contact.getLastName());
			System.out.println("     nage: " + contact.getAge());
		}

		// make the change
		contact.setAge(contact.getAge()+1);
		resource.store(contact);

		contact = resource.retrieve();

		if (contact != null)
		{
			System.out.println("firstname: " + contact.getFirstName());
			System.out.println(" lastname: " + contact.getLastName());
			System.out.println("     nage: " + contact.getAge());
		}
		
		
		int NUM = 10;
		for(int i=0;i<NUM;i++)
		{
			// make the change
			contact.setAge(contact.getAge()+1);
			resource.store(contact);
			System.out.print(".");
			if((i % 10) == 0)
				System.out.println();
		}

		contact = resource.retrieve();

		if (contact != null)
		{
			System.out.println("firstname: " + contact.getFirstName());
			System.out.println(" lastname: " + contact.getLastName());
			System.out.println("     nage: " + contact.getAge());
		}
		
 
		try
		{
			cr.get(MediaType.APPLICATION_JSON).write(System.out);
		}
		catch (ResourceException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
