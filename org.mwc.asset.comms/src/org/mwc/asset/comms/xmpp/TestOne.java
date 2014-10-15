/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.asset.comms.xmpp;

import java.io.IOException;
import java.util.Collection;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

public class TestOne implements MessageListener
{
	XMPPConnection connection;

	public void login(final String userName, final String password) throws XMPPException
	{
		final ConnectionConfiguration config = new ConnectionConfiguration(
				"Mayo-Mac.local", 5222, "Work");
		connection = new XMPPConnection(config);

		connection.connect();
		connection.login(userName, password);
	}

	public void sendMessage(final String message, final String to) throws XMPPException
	{
		final Chat chat = connection.getChatManager().createChat(to, this);
		chat.sendMessage(message);
	}

	public void displayBuddyList()
	{
		final Roster roster = connection.getRoster();
		final Collection<RosterEntry> entries = roster.getEntries();

		System.out.println("\n\n" + entries.size() + " buddy(ies):");
		for (final RosterEntry r : entries)
		{
			System.out.println(r.getUser());
		}
	}

	public void disconnect()
	{
		connection.disconnect();
	}

	public void processMessage(final Chat chat, final Message message)
	{
		if (message.getType() == Message.Type.chat)
			System.out.println(chat.getParticipant() + " says: " + message.getBody());
	}

	public static void main(final String args[]) throws XMPPException, IOException
	{
		// declare variables

		final TestOne c = new TestOne();
		// turn on the enhanced debugger
		XMPPConnection.DEBUG_ENABLED = true;

		// Enter your login information here
		c.login("ian", "asset");
		
		c.doRoom();

//		c.displayBuddyList();
//
//		System.out.println("-----");
//
//		System.out
//				.println("Who do you want to talk to? - Type contacts full email address:");
//		String talkTo = br.readLine();
//
//		System.out.println("-----");
//		System.out.println("All messages will be sent to " + talkTo);
//		System.out.println("Enter your message in the console:");
//		System.out.println("-----\n");
//
//		while (!(msg = br.readLine()).equals("bye"))
//		{
//			c.sendMessage(msg, talkTo);
//		}

		c.disconnect();
		System.exit(0);
	}

	private void doRoom()
	{
		final MultiUserChat multi = new MultiUserChat(connection, "Scenario1@conference.mayo-mac.local");
		try
		{
			multi.join("trial@mayo-mac.local");
			multi.sendMessage("ping");
			multi.sendMessage("ping 2");
			multi.sendMessage("ping 3");
		}
		catch (final XMPPException e)
		{
			e.printStackTrace();
		}
		
	}
}
