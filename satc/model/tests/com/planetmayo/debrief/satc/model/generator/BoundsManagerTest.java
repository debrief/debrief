package com.planetmayo.debrief.satc.model.generator;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.planetmayo.debrief.satc.model.ModelTestBase;
import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.model.generator.ISteppingListener.IConstrainSpaceListener;
import com.planetmayo.debrief.satc.model.manager.mock.MockVehicleTypesManager;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.SpeedRange;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.support.TestSupport;

import static org.junit.Assert.*;

@SuppressWarnings("deprecation")
public class BoundsManagerTest extends ModelTestBase
{
	private IBoundsManager boundsManager;
	private BearingMeasurementContribution bearingMeasurementContribution;
	private CourseForecastContribution courseForecastContribution;

	private boolean restarted;
	private boolean complete;
	private IncompatibleStateException error;

	@Before
	public void prepareBoundsManager()
	{
		bearingMeasurementContribution = new BearingMeasurementContribution();
		bearingMeasurementContribution.loadFrom(TestSupport.getShortData());

		courseForecastContribution = new CourseForecastContribution();
		courseForecastContribution.setStartDate(new Date(110, 0, 12, 12, 15, 0));
		courseForecastContribution.setFinishDate(new Date(110, 0, 12, 12, 20, 0));
		courseForecastContribution.setMinCourse(50d);
		courseForecastContribution.setMaxCourse(100d);

		boundsManager = new BoundsManager();
		boundsManager.addContribution(bearingMeasurementContribution);
		boundsManager.addContribution(courseForecastContribution);
		boundsManager
				.addBoundStatesListener(new ISteppingListener.IConstrainSpaceListener()
				{

					@Override
					public void stepped(IBoundsManager eventManager, int thisStep,
							int totalSteps)
					{
						assertSame(boundsManager, eventManager);
					}

					@Override
					public void restarted(IBoundsManager eventManager)
					{
						assertSame(boundsManager, eventManager);
						restarted = true;
					}

					@Override
					public void error(IBoundsManager eventManager,
							IncompatibleStateException ex)
					{
						assertSame(boundsManager, eventManager);
						error = ex;
					}

					@Override
					public void statesBounded(IBoundsManager eventManager)
					{
						assertSame(boundsManager, eventManager);
						complete = true;
					}
				});
	}

	private boolean isRestarted()
	{
		boolean result = restarted;
		restarted = false;
		return result;
	}

	private boolean isComplete()
	{
		boolean result = complete;
		complete = false;
		return result;
	}

	private IncompatibleStateException hasError()
	{
		IncompatibleStateException result = error;
		error = null;
		return result;
	}

	@Test
	public void testAddRemoveContribution()
	{
		final SpeedForecastContribution speedContribution = new SpeedForecastContribution();
		final boolean[] fired =
		{ false, false };
		boundsManager.addContributionsListener(new IContributionsChangedListener()
		{

			@Override
			public void removed(BaseContribution contribution)
			{
				assertSame(speedContribution, contribution);
				fired[1] = true;
			}

			@Override
			public void added(BaseContribution contribution)
			{
				assertSame(speedContribution, contribution);
				fired[0] = true;
			}
		});
		assertEquals(0, speedContribution.getPropertyChangeListenersCount());

		boundsManager.addContribution(speedContribution);
		assertTrue(fired[0]);
		assertFalse(fired[1]);
		assertEquals(3, boundsManager.getContributions().size());
		assertFalse(0 == speedContribution.getPropertyChangeListenersCount());
		fired[0] = false;

		boundsManager.removeContribution(speedContribution);
		assertFalse(fired[0]);
		assertTrue(fired[1]);
		assertEquals(2, boundsManager.getContributions().size());
		assertEquals(0, speedContribution.getPropertyChangeListenersCount());
		fired[1] = false;

		boundsManager.removeContribution(new LocationForecastContribution());
		assertFalse(fired[0]);
		assertFalse(fired[1]);
	}

	@Test
	public void testSetVehicleType()
	{
		VehicleType vehicleType = new MockVehicleTypesManager().getAllTypes()
				.get(0);
		boundsManager.setVehicleType(vehicleType);
		boundsManager.run();

		assertSame(vehicleType, boundsManager.getSpace().getVehicleType());
		for (BoundedState state : boundsManager.getSpace().states())
		{
			assertNotNull(state.getSpeed());
			SpeedRange range = state.getSpeed();
			assertEquals(vehicleType.getMinSpeed(), range.getMin(), EPS);
			assertEquals(vehicleType.getMaxSpeed(), range.getMax(), EPS);
		}
	}

	@Test
	public void testRestart()
	{
		boundsManager.run();
		assertFalse(isRestarted());
		assertFalse(boundsManager.getSpace().size() == 0);
		boundsManager.restart();
		assertTrue(isRestarted());
		assertEquals(0, boundsManager.getSpace().size());
	}

	@Test
	public void testStep()
	{
		final int[] stepCounter =
		{ 0 };
		final int[] eventCounter =
		{ 0 };
		boundsManager.addBoundStatesListener(new SteppingAdapter()
		{

			@Override
			public void stepped(IBoundsManager boundsManager, int thisStep,
					int totalSteps)
			{
				assertEquals(2, totalSteps);
				assertEquals(stepCounter[0], thisStep);
				eventCounter[0]++;
			}
		});
		assertEquals(0, boundsManager.getSpace().size());

		boundsManager.step();
		assertEquals(bearingMeasurementContribution.getNumObservations() + 2,
				boundsManager.getSpace().size());
		assertEquals(1, eventCounter[0]);

		stepCounter[0]++;
		boundsManager.step();
		assertEquals(2, eventCounter[0]);

		boundsManager.step();
		assertEquals(2, eventCounter[0]);
	}

	@Test
	public void testVehicleTypeChangeReaction()
	{
		List<VehicleType> types = new MockVehicleTypesManager().getAllTypes();
		boundsManager.setVehicleType(types.get(0));
		assertFalse(isRestarted());
		boundsManager.run();
		assertTrue(isComplete());
		boundsManager.setVehicleType(types.get(1));
		assertTrue(isRestarted());
	}

	@Test
	public void testContributionChangeReaction()
	{
		courseForecastContribution.setMinCourse(25d);
		assertFalse(isRestarted());
		boundsManager.run();
		assertTrue(isComplete());
		courseForecastContribution.setMinCourse(30d);
		assertTrue(isRestarted());
	}

	@Test
	public void testIsCompleted()
	{
		assertFalse(boundsManager.isCompleted());
		boundsManager.step();
		assertFalse(boundsManager.isCompleted());
		boundsManager.step();
		assertTrue(boundsManager.isCompleted());
		assertTrue(isComplete());

		boundsManager.restart();
		assertFalse(boundsManager.isCompleted());
	}

	@Test
	public void testGetCurrentContributionAndStep()
	{
		assertEquals(0, boundsManager.getCurrentStep());
		assertNull(boundsManager.getCurrentContribution());
		boundsManager.step();
		assertEquals(1, boundsManager.getCurrentStep());
		assertSame(bearingMeasurementContribution,
				boundsManager.getCurrentContribution());
		boundsManager.step();
		assertEquals(2, boundsManager.getCurrentStep());
		assertSame(courseForecastContribution,
				boundsManager.getCurrentContribution());
		boundsManager.restart();
		assertEquals(0, boundsManager.getCurrentStep());
		assertNull(boundsManager.getCurrentContribution());
	}

	@Test
	public void testListenersCleanup()
	{
		assertFalse(0 == bearingMeasurementContribution
				.getPropertyChangeListenersCount());
		assertFalse(0 == courseForecastContribution
				.getPropertyChangeListenersCount());
		boundsManager.removeContribution(bearingMeasurementContribution);
		assertEquals(0,
				bearingMeasurementContribution.getPropertyChangeListenersCount());
		assertFalse(0 == courseForecastContribution
				.getPropertyChangeListenersCount());
		boundsManager.clear();
		assertEquals(0,
				courseForecastContribution.getPropertyChangeListenersCount());
	}

	@Test
	public void testError()
	{
		LocationForecastContribution locationForecastContribution = new LocationForecastContribution();
		locationForecastContribution.setStartDate(new Date(110, 0, 12, 12, 15, 0));
		locationForecastContribution.setFinishDate(new Date(110, 0, 12, 12, 20, 0));
		locationForecastContribution.setLimit(3d);
		locationForecastContribution.setLocation(new GeoPoint(0, 0));
		boundsManager.addContribution(locationForecastContribution);

		boundsManager.step();
		assertNull(hasError());
		boundsManager.step();
		assertNull(hasError());
		boundsManager.step();
		assertNotNull(hasError());
	}

}
