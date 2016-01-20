package org.mwc.debrief.dis.listener;

import static org.junit.Assert.*;

import org.junit.Test;

public class DISListenerTest extends DISListener {

	@Test
	public void test() {
		Object subject = new DISListener();
		assertNotNull(subject);
	}

}
