package org.mwc.cmap.naturalearth.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class NEResolutionGroupTest
{
	@Test
	public void test()
	{
		NEResolutionGroup grp = new NEResolutionGroup(null,new Double(10));
		assertEquals("no plot,  too large", false, grp.canHandle(12));
		assertEquals("can plot,  small", true, grp.canHandle(9));
		assertEquals("can plot, very small", true, grp.canHandle(-2));

		grp = new NEResolutionGroup(new Double(10), null);
		assertEquals("can plot,  good size", true, grp.canHandle(12));
		assertEquals("cant plot,  small", false, grp.canHandle(9));
		assertEquals("cant plot, very small", false, grp.canHandle(-2));

		grp = new NEResolutionGroup(null, null);
		assertEquals("can plot,  good size", true, grp.canHandle(12));
		assertEquals("can plot,  small", true, grp.canHandle(9));
		assertEquals("can plot, very small", true, grp.canHandle(-2));

		grp = new NEResolutionGroup(new Double(0), new Double(10));
		assertEquals("cant plot,  too large", false, grp.canHandle(12));
		assertEquals("can plot,  small", true, grp.canHandle(9));
		assertEquals("cant plot, very small", false, grp.canHandle(-2));
	}

}
