/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc.model.contributions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.planetmayo.debrief.satc.model.ModelTestBase;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

import static org.junit.Assert.*;

@SuppressWarnings("deprecation")
public abstract class ForecastContributionTestBase extends ModelTestBase
{
	
	protected abstract Map<String, Object> getPropertiesForTest();
	
	protected abstract BaseContribution createContribution();
	
	protected ProblemSpace createTestSpace() throws Exception
	{
		ProblemSpace space = new ProblemSpace();
		space.add(new BoundedState(new Date(112, 11, 27, 1, 0)));
		space.add(new BoundedState(new Date(112, 11, 27, 1, 10)));
		space.add(new BoundedState(new Date(112, 11, 27, 1, 20)));
		space.add(new BoundedState(new Date(112, 11, 27, 1, 30)));
		space.add(new BoundedState(new Date(112, 11, 27, 1, 40)));
		space.add(new BoundedState(new Date(112, 11, 27, 1, 50)));
		space.add(new BoundedState(new Date(112, 11, 27, 2, 0)));
		space.add(new BoundedState(new Date(112, 11, 27, 2, 10)));
		space.add(new BoundedState(new Date(112, 11, 27, 2, 20)));
		space.add(new BoundedState(new Date(112, 11, 27, 2, 30)));
		return space;
	}
	
	@Test
	public void testProperties() throws Exception 
	{
		BaseContribution contribution = createContribution();
		Class<?> klass = contribution.getClass();
		for (Entry<String, Object> property : getPropertiesForTest().entrySet()) 
		{
			PropertyDescriptor descriptor = new PropertyDescriptor(property.getKey(), klass);
			final Object oldValue = descriptor.getReadMethod().invoke(contribution);
			final Object newValue = property.getValue();
			final boolean[] invoked = new boolean[1];
			contribution.addPropertyChangeListener(property.getKey(), new PropertyChangeListener()
			{
				@Override
				public void propertyChange(PropertyChangeEvent evt)
				{
					assertEquals(oldValue, evt.getOldValue());
					assertEquals(newValue, evt.getNewValue());
					invoked[0] = true;
				}
			});
			descriptor.getWriteMethod().invoke(contribution, property.getValue());
			assertTrue(invoked[0]);
		}
	}
}
