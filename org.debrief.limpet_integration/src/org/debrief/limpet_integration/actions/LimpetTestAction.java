package org.debrief.limpet_integration.actions;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IObjectCollection;
import info.limpet.IQuantityCollection;
import info.limpet.IStore.IStoreItem;
import info.limpet.QuantityRange;
import info.limpet.data.impl.MockContext;
import info.limpet.data.impl.ObjectCollection;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet.data.impl.samples.StockTypes.Temporal.ElapsedTimeSec;
import info.limpet.data.operations.arithmetic.AddQuantityOperation;
import info.limpet.data.operations.arithmetic.MultiplyQuantityOperation;
import info.limpet.data.store.InMemoryStore;
import info.limpet.data.store.InMemoryStore.StoreGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.measure.Measure;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Velocity;

import org.debrief.limpet_integration.data.StoreWrapper;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layers;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class LimpetTestAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow _window;

	/**
	 * The constructor.
	 */
	public LimpetTestAction() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		System.out.println("ACTION IS ABOUT TO RUN");
		
		// ok, get the editor
		IEditorPart editor = _window.getActivePage().getActiveEditor();
		if(editor != null)
		{
			Layers layers = (Layers) editor.getAdapter(Layers.class);
			if(layers != null)
			{
				TrackWrapper firstTrack = null;
				Enumeration<Editable> iter = layers.elements();
				while (iter.hasMoreElements())
				{
					Editable editable = (Editable) iter.nextElement();
					if(editable instanceof TrackWrapper)
					{
						firstTrack = (TrackWrapper) editable;
						break;
					}
				}
				
				if(firstTrack != null)
				{
					// create some limpet data
					InMemoryStore store = createData();
					
					StoreWrapper data = new StoreWrapper(store);
					data.setName("Data_" + new Date());
					
					// add it to the track
					firstTrack.add(data);
					
					layers.fireExtended(data, firstTrack);
				}
			}
		}
	}

	private InMemoryStore createData()
	{
		InMemoryStore data = getData(30);
		
		return data;
	}
	
	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		_window = window;
	}
	

	public InMemoryStore getData(long count)
	{
		
		InMemoryStore res = new InMemoryStore();

		final String SPEED_THREE_LONGER = "Speed Three (longer)";
		final String SPEED_IRREGULAR2 = "Speed two irregular time";
		final String TIME_INTERVALS = "Time intervals";
		final String STRING_TWO = "String two";
		final String STRING_ONE = "String one";
		final String LENGTH_SINGLETON = "Length Singleton";
		final String LENGTH_TWO = "Length Two non-Time";
		final String LENGTH_ONE = "Length One non-Time";
		final String ANGLE_ONE = "Angle One Time";
		final String SPEED_ONE = "Speed One Time";
		final String SPEED_TWO = "Speed Two Time";
		final String SPEED_EARLY = "Speed Two Time (earlier)";
		final String RANGED_SPEED_SINGLETON = "Ranged Speed Singleton";
		final String FLOATING_POINT_FACTOR = "Floating point factor";
		
		// // collate our data series
		StockTypes.Temporal.AngleDegrees angle1 = new StockTypes.Temporal.AngleDegrees(
				ANGLE_ONE, null);
		StockTypes.Temporal.SpeedMSec speedSeries1 = new StockTypes.Temporal.SpeedMSec(
				SPEED_ONE, null);
		StockTypes.Temporal.SpeedMSec speedSeries2 = new StockTypes.Temporal.SpeedMSec(
				SPEED_TWO, null);
		StockTypes.Temporal.SpeedMSec speedSeries3 = new StockTypes.Temporal.SpeedMSec(
				SPEED_THREE_LONGER, null);
		StockTypes.Temporal.SpeedMSec speedEarly1 = new StockTypes.Temporal.SpeedMSec(
				SPEED_EARLY, null);
		StockTypes.Temporal.SpeedMSec speedIrregular = new StockTypes.Temporal.SpeedMSec(
				SPEED_IRREGULAR2, null);
		StockTypes.NonTemporal.LengthM length1 = new StockTypes.NonTemporal.LengthM(
				LENGTH_ONE, null);
		StockTypes.NonTemporal.LengthM length2 = new StockTypes.NonTemporal.LengthM(
				LENGTH_TWO, null);
		IObjectCollection<String> string1 = new ObjectCollection<String>(STRING_ONE);
		IObjectCollection<String> string2 = new ObjectCollection<String>(STRING_TWO);
		IQuantityCollection<Dimensionless> singleton1 = new QuantityCollection<Dimensionless>(
				FLOATING_POINT_FACTOR, null, Dimensionless.UNIT);
		StockTypes.NonTemporal.SpeedMSec singletonRange1 = new StockTypes.NonTemporal.SpeedMSec(
				RANGED_SPEED_SINGLETON, null);
		StockTypes.NonTemporal.LengthM singletonLength = new StockTypes.NonTemporal.LengthM(
				LENGTH_SINGLETON, null);
		ElapsedTimeSec timeIntervals = new StockTypes.Temporal.ElapsedTimeSec(
				TIME_INTERVALS, null);

		long thisTime = 0;

		// get ready for the track generation

		for (int i = 1; i <= count; i++)
		{
			thisTime = new Date().getTime() + i * 500L * 60;

			final long earlyTime = thisTime - (1000 * 60 * 60 * 24 * 365 * 20);

			angle1.add(thisTime,
					90 + 1.1 * Math.toDegrees(Math.sin(Math.toRadians(i * 52.5))));
			speedSeries1.add(thisTime, 1 / Math.sin(i));
			speedSeries2.add(thisTime, 7 + 2 * Math.sin(i));

			// we want the irregular series to only have occasional
			if (i % 5 == 0)
			{
				speedIrregular.add(thisTime + 500 * 45, 7 + 2 * Math.sin(i + 1));
			}
			else
			{
				if (Math.random() > 0.6)
				{
					speedIrregular.add(thisTime + 500 * 25 * 2, 7 + 2 * Math.sin(i - 1));
				}
			}

			speedSeries3.add(thisTime, 3d * Math.cos(i));
			speedEarly1.add(earlyTime, Math.sin(i));
			length1.add((double) i % 3);
			length2.add((double) i % 5);
			string1.add("item " + i);
			string2.add("item " + (i % 3));
			timeIntervals.add(thisTime,
					(4 + Math.sin(Math.toRadians(i) + 3.4 * Math.random())));

		}

		// add an extra item to speedSeries3
		speedSeries3.add(thisTime + 12 * 500 * 60, 12);

		// give the singleton a value
		singleton1.add(4d);
		singletonRange1.add(998);
		Measure<Double, Velocity> minR = Measure.valueOf(940d,
				singletonRange1.getUnits());
		Measure<Double, Velocity> maxR = Measure.valueOf(1050d,
				singletonRange1.getUnits());
		QuantityRange<Velocity> speedRange = new QuantityRange<Velocity>(minR, maxR);
		singletonRange1.setRange(speedRange);

		singletonLength.add(12d);

		List<IStoreItem> list = new ArrayList<IStoreItem>();

		StoreGroup group1 = new StoreGroup("Speed data");
		group1.add(speedSeries1);
		group1.add(speedSeries2);
		group1.add(speedIrregular);
		group1.add(speedEarly1);
		group1.add(speedSeries3);

		list.add(group1);

		list.add(angle1);
		list.add(length1);
		list.add(length2);
		list.add(string1);
		list.add(string2);
		list.add(singleton1);
		list.add(singletonRange1);
		list.add(singletonLength);
		list.add(timeIntervals);

		res.addAll(list);

		// perform an operation, so we have some audit trail
		List<IStoreItem> selection = new ArrayList<IStoreItem>();
		selection.add(speedSeries1);
		selection.add(speedSeries2);
		IContext context = new MockContext();
		@SuppressWarnings(
		{ "unchecked", "rawtypes" })
		Collection<ICommand<?>> actions = new AddQuantityOperation().actionsFor(
				selection, res, context);
		Iterator<ICommand<?>> addIter = actions.iterator();
		addIter.next();
		ICommand<?> addAction = addIter.next();
		addAction.execute();

		// and an operation using our speed factor
		selection.clear();
		selection.add(speedSeries1);
		selection.add(singleton1);
		Collection<ICommand<IStoreItem>> actions2 = new MultiplyQuantityOperation()
				.actionsFor(selection, res, context);
		addAction = actions2.iterator().next();
		addAction.execute();

		// calculate the distance travelled
		selection.clear();
		selection.add(timeIntervals);
		selection.add(singletonRange1);
		Collection<ICommand<IStoreItem>> actions3 = new MultiplyQuantityOperation()
				.actionsFor(selection, res, context);
		addAction = actions3.iterator().next();
		addAction.execute();
		
		return res;
	}
	

}