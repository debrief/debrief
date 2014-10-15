/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.asset.netCore.test;

import java.io.IOException;
import java.util.Arrays;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;

public class ThreadTest
{
	public static void main(final String[] args) throws IOException,
			InterruptedException
	{
		Server s = new Server();
		s.start();
		s.bind(1927);
		printThreads("server started");

		final Client c = new Client();
		c.start();
		c.connect(5000, "LOCALHOST", 1927);
		printThreads("client connected");
		final Server s1 = s;
		s.stop();
		printThreads("server stopped");

		s = new Server();
		s.start();
		s.bind(1928);
		printThreads("new server started"); // new server thread will be last on
		// the list.

		c.stop();
		printThreads("client stopped");

		c.start();
		c.connect(5000, "localhost", 1928);
		printThreads("client connected to second server");

		c.stop();
		s.stop();
		s1.stop();
		printThreads("both stopped");
	}

	private static void printThreads(final String message) throws InterruptedException
	{
		// tick:
		Thread.sleep(2000L);
		final Thread[] threads = new Thread[Thread.activeCount()];
		Thread.enumerate(threads);
		System.out.println(message + " :  " + Arrays.asList(threads));
	}
}
