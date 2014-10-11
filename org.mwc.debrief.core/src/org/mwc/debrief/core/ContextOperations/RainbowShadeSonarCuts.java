/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
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
import org.eclipse.jface.action.MenuManager;
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
public class RainbowShadeSonarCuts implements RightClickContextItemGenerator
{

	/**
	 * list of the types of shading operation we support
	 * 
	 */
	private enum ShadeOperation
	{
		BLUE_RED_SPECTRUM, RAINBOW_SHADE, CLEAR_SHADE
	}

	/**
	 * @param parent
	 * @param theLayers
	 * @param parentLayers
	 * @param subjects
	 */
	public void generate(final IMenuManager parent, final Layers theLayers,
			final Layer[] parentLayers, final Editable[] subjects)
	{

		ArrayList<SensorContactWrapper> cuts =
				new ArrayList<SensorContactWrapper>();
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
			Collection<Editable> editables =
					theSensor.getItemsBetween(theSensor.getStartDTG(),
							theSensor.getEndDTG());
			for (Editable editable : editables)
			{
				if (editable instanceof SensorContactWrapper)
				{
					cuts.add((SensorContactWrapper) editable);
				}
			}
		}

		// have we found any cuts?
		if (cuts.size() > 0)
		{
			// right,stick in a separator
			parent.add(new Separator());
			
			// ok - introduce the shading menu
			MenuManager newMenu = new MenuManager("Shading");
			parent.add(newMenu);

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
					final IUndoableOperation theAction =
							new ShadeCutsOperation(title1, theLayers, parentFinal, list,
									start, end, ShadeOperation.RAINBOW_SHADE);
					CorePlugin.run(theAction);
				}
			};

			newMenu.add(doRainbowShade);
			// create this operation
			final String title2 = "Shade in blue-red spectrum";
			final Action doBlueShade = new Action(title2)
			{
				public void run()
				{
					final IUndoableOperation theAction =
							new ShadeCutsOperation(title2, theLayers, parentFinal, list,
									start, end, ShadeOperation.BLUE_RED_SPECTRUM);
					CorePlugin.run(theAction);
				}
			};
			newMenu.add(doBlueShade);

			final String title3 = "Reset shading";
			final Action clearShade = new Action(title3)
			{
				public void run()
				{
					final IUndoableOperation theAction =
							new ShadeCutsOperation(title3, theLayers, parentFinal, list,
									start, end, ShadeOperation.CLEAR_SHADE);
					CorePlugin.run(theAction);
				}
			};
			newMenu.add(clearShade);
		}
	}

	/**
	 * encapsulate the action into an operation - so we "could" support an undo
	 * function
	 * 
	 */

	private static class ShadeCutsOperation extends CMAPOperation
	{

		/**
		 * the parent to update on completion
		 */
		private final Layers _layers;
		private final Layer _parent;
		private final SensorContactWrapper[] _subjects;
		private final ShadeOperation _shader;
		private HiResDate _endDTG;
		private HiResDate _startDTG;
		private long _delta;

		public ShadeCutsOperation(final String title, final Layers theLayers,
				final Layer parentLayer, final SensorContactWrapper[] subjects,
				HiResDate start, HiResDate end, final ShadeOperation shader)
		{
			super(title);
			_layers = theLayers;
			_parent = parentLayer;
			_subjects = subjects;
			_startDTG = start;
			_endDTG = end;
			_delta = (_endDTG.getMicros() - _startDTG.getMicros()) / 1000000;
			_shader = shader;
		}

		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info)
				throws ExecutionException
		{
			if (_subjects == null || _delta <= 0)
			{
				return Status.OK_STATUS;
			}

			else
				return doIt(_shader);
		}

		private IStatus doIt(final ShadeOperation operation)
		{
			for (SensorContactWrapper swc : _subjects)
			{
				final long time =
						(swc.getDTG().getMicros() - _startDTG.getMicros()) / 1000000;
				switch (operation)
				{
				case CLEAR_SHADE:
					swc.resetColor();
					break;
				case BLUE_RED_SPECTRUM:
					long r = (long) (255.0 * ( (long)time / (double) _delta));
					long g = 0;
					long b = 255 - r;
					swc.setColor(new Color(checkRBG(r), checkRBG(g), checkRBG(b)));
					break;
				case RAINBOW_SHADE:
					// produce value from 0..1 for how far through the rainbow we
					// require
					float hue = (float) ((double) time / (double) _delta);
					swc.setColor(new Color(Color.HSBtoRGB(hue, 0.8f, 0.7f)));
					break;
				}
			}

			// ok, done - let the layers object declare what has been edited
			fireModified();

			return Status.OK_STATUS;
		}

		private int checkRBG(long rgb)
		{
			while (rgb < 0)
			{
				rgb = rgb + 255;
			}
			while (rgb > 255)
			{
				rgb = rgb - 255;
			}
			return (int) rgb;
		}

		@Override
		public boolean canRedo()
		{
			return true;
		}

		@Override
		public boolean canUndo()
		{
			return true;
		}

		private void fireModified()
		{
			if (_parent != null)
			{
				_layers.fireReformatted(_parent);
			}
			else
			{
				_layers.fireReformatted(null);
			}
		}

		@Override
		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
				throws ExecutionException
		{
			return doIt(ShadeOperation.CLEAR_SHADE);
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			return doIt(_shader);
		}
		
		

	}
}
