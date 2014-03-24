/**  Class providing functionality to shade a series of sensor contacts. When there is a dense block of 
 * sensor contacts, it can be different to distinguish the "flow" of contacts.
 * 
 */
package org.mwc.debrief.core.ContextOperations;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;

import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;

/**
 * @author ian.mayo
 */
public class RainbowShadeSonarCuts implements RightClickContextItemGenerator {

	/**
	 * @param parent
	 * @param theLayers
	 * @param parentLayers
	 * @param subjects
	 */
	public void generate(final IMenuManager parent, final Layers theLayers,
			final Layer[] parentLayers, final Editable[] subjects)
	{

		ArrayList<SensorContactWrapper> cuts = new ArrayList<SensorContactWrapper>();
		SensorWrapper theSensor = null;

		Layer parentLayer = null;

		if (parentLayers != null)
		{
			if (parentLayers.length == 1)
			{
				parentLayer = parentLayers[0];
			}
		}

		// are they items we're interested in?
		HiResDate startDTG = new HiResDate(Long.MAX_VALUE / 1000, 0);
		HiResDate endDTG = new HiResDate(0);
		for (int i = 0; i < subjects.length; i++)
		{
			final Editable thisE = subjects[i];
			if (thisE instanceof SensorWrapper)
			{
				// just check that there's only one item selected
				if (subjects.length == 1)
				{
					theSensor = (SensorWrapper) thisE;
				}
			}
			else if (thisE instanceof SensorContactWrapper)
			{
				cuts.add((SensorContactWrapper) thisE);
				if (startDTG.compareTo(((SensorContactWrapper) thisE).getDTG()) > 0)
				{
					startDTG = ((SensorContactWrapper) thisE).getDTG();
				}
				if (endDTG.compareTo(((SensorContactWrapper) thisE).getDTG()) < 0)
				{
					endDTG = ((SensorContactWrapper) thisE).getDTG();
				}
			}
		}

		// ok, do we have a single sensor?
		if (theSensor != null)
		{
			startDTG = theSensor.getStartDTG();
			endDTG = theSensor.getEndDTG();
			Collection<Editable> editables = theSensor.getItemsBetween(
					theSensor.getStartDTG(), theSensor.getEndDTG());
			for (Editable editable : editables)
			{
				if (editable instanceof SensorContactWrapper)
				{
					cuts.add((SensorContactWrapper) editable);
				}
			}
		}
		// right,stick in a separator
		parent.add(new Separator());

		// convert the list to an array
		SensorContactWrapper[] listTemplate = new SensorContactWrapper[]
		{};
		final SensorContactWrapper[] list = cuts.toArray(listTemplate);
		final Layer parentFinal = parentLayer;

		final HiResDate start = startDTG;
		final HiResDate end = endDTG;
		// create this operation
		final String title1 = "Shade in rainbow colors";
		final Action doRainbowShade = new Action(title1)
		{
			public void run()
			{
				final IUndoableOperation theAction = new ShadeCutsOperation(title1,
						theLayers, parentFinal, list, start, end,
						ShadeOperation.RAINBOW_SHADE);
				CorePlugin.run(theAction);
			}
		};

		parent.add(doRainbowShade);
		// create this operation
		final String title2 = "Shade in blue-red spectrum";
		final Action doBlueShade = new Action(title2)
		{
			public void run()
			{
				final IUndoableOperation theAction = new ShadeCutsOperation(title2,
						theLayers, parentFinal, list, start, end,
						ShadeOperation.BLUE_RED_SPECTRUM);
				CorePlugin.run(theAction);
			}
		};
		parent.add(doBlueShade);

		final String title3 = "Clear shading";
		final Action clearShade = new Action(title3)
		{
			public void run()
			{
				final IUndoableOperation theAction = new ShadeCutsOperation(title3,
						theLayers, parentFinal, list, start, end,
						ShadeOperation.CLEAR_SHADE);
				CorePlugin.run(theAction);
			}
		};
		parent.add(clearShade);
	}

	private static interface ShadeOperation {
		/**
		 * apply shading to the supplied cut
		 * 
		 * @param cut
		 *            the cut to shade
		 */
		//public void shadeThis(SensorContactWrapper cut);
		/**
		 * provide shading through the blue-red spectrum
		 * 
		 * @author ian
		 * 
		 */
		final static int BLUE_RED_SPECTRUM = 0;
		/**
		 * provide shading through the full rainbow spectrum
		 * 
		 * @author ian
		 * 
		 */
		final static int RAINBOW_SHADE = 1;
		/**
		 * clear shading
		 */
		final static int CLEAR_SHADE = 2;
	}

	/**
	 * provide shading through the blue-red spectrum
	 * 
	 * @author ian
	 * 
	 */
//	private static class BlueShade implements ShadeOperation {
//
//		@Override
//		public void shadeThis(SensorContactWrapper cut) {
//			// TODO Auto-generated method stub
//
//		}
//
//	}
	
	/**
	 * provide shading through the full rainbow spectrum
	 * 
	 * @author ian
	 * 
	 */
//	private static class RainbowShade implements ShadeOperation {
//
//		@Override
//		public void shadeThis(SensorContactWrapper cut) {
//			// TODO Auto-generated method stub
//
//		}
//
//	}

	private static class ShadeCutsOperation extends CMAPOperation {

		/**
		 * the parent to update on completion
		 */
		private final Layers _layers;
		private final Layer _parent;
		private final SensorContactWrapper[] _subjects;
		private final int _shader;
		private HiResDate _endDTG;
		private HiResDate _startDTG;
		private long _delta;
		
		public ShadeCutsOperation(final String title, final Layers theLayers,
				final Layer parentLayer, final SensorContactWrapper[] subjects,
				HiResDate start, HiResDate end, final int shader) {
			super(title);
			_layers = theLayers;
			_parent = parentLayer;
			_subjects = subjects;
			_startDTG = start;
			_endDTG = end;
			_delta = (_endDTG.getMicros() - _startDTG.getMicros())/1000000;
			_shader = shader;
		}

		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info)
				throws ExecutionException
		{
			if (_subjects == null || _delta <= 0)
			{
				return Status.OK_STATUS;
			}
			for (SensorContactWrapper swc : _subjects)
			{
				if (_shader == ShadeOperation.CLEAR_SHADE)
				{
					swc.resetColor();
				}
				else
				{
					long time = (swc.getDTG().getMicros() - _startDTG.getMicros()) / 1000000;
					
					//long center = 0;
			    double width = 255f;

			    double i = (double)time/(double)_delta;
			    double freq = 255f;
			    
			    long r = (long) (Math.sin(freq*i + 0) * width);
			    long g = 0;
			    if (_shader == ShadeOperation.RAINBOW_SHADE)
			    {
			    	g = (long) (Math.sin(freq*i + 80f) * width);
			    }
			    long b = (long) (Math.sin(freq*i - 160f) * width);
			    
					swc.setColor(new Color(checkRBG(r), checkRBG(g), checkRBG(b)));
				}
			}

			// ok, done - let the layers object declare what has been edited
			fireModified();

			return Status.OK_STATUS;
		}

		private int checkRBG(long rgb)
		{
			while (rgb < 0) {
				rgb = rgb+255;
			}
			while (rgb > 255) {
				rgb = rgb-255;
			}
			return (int) rgb;
		}

		@Override
		public boolean canRedo() {
			return false;
		}

		@Override
		public boolean canUndo() {
			return false;
		}

		private void fireModified() {
			if (_parent != null) {
				_layers.fireReformatted(_parent);
			} else {
				_layers.fireReformatted(null);
			}
		}

		@Override
		public IStatus undo(final IProgressMonitor monitor,
				final IAdaptable info) throws ExecutionException {
			CorePlugin.logError(Status.INFO,
					"Undo not permitted for merge operation", null);
			return null;
		}

	}
}
