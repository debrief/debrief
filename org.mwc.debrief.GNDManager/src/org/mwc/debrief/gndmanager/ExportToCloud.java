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
package org.mwc.debrief.gndmanager;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.TextNode;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.javalite.http.Post;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.debrief.gndmanager.Tracks.TrackStoreWrapper;
import org.mwc.debrief.gndmanager.Tracks.TrackStoreWrapper.CouchTrack;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

/**
 * @author ian.mayo
 * 
 */
public class ExportToCloud implements RightClickContextItemGenerator
{

	/**
	 * @param parent
	 * @param theLayers
	 * @param parentLayers
	 * @param subjects
	 */
	public void generate(final IMenuManager parent, final Layers theLayers,
			final Layer[] parentLayers, final Editable[] subjects)
	{
		int layersValidForConvertToTrack = 0;

		// right, work through the subjects
		for (int i = 0; i < subjects.length; i++)
		{
			final Editable thisE = subjects[i];
			if (thisE instanceof TrackWrapper)
			{
				// cool, go for it!
				layersValidForConvertToTrack++;
			}
		}

		// ok, is it worth going for?
		if (layersValidForConvertToTrack > 0)
		{
			final String title;
			if (layersValidForConvertToTrack > 1)
				title = "Export tracks to cloud";
			else
				title = "Export track to cloud";

			// yes, create the action
			final Action exportToCloud = new Action(title)
			{
				public void run()
				{
					// ok, go for it.
					// sort it out as an operation
					final IUndoableOperation convertToTrack1 = new ConvertTrack(title, subjects);

					// ok, stick it on the buffer
					runIt(convertToTrack1);
				}
			};

			// right,stick in a separator
			parent.add(new Separator());

			// ok - flash up the menu item
			parent.add(exportToCloud);
		}

	}

	/**
	 * put the operation firer onto the undo history. We've refactored this into a
	 * separate method so testing classes don't have to simulate the CorePlugin
	 * 
	 * @param operation
	 */
	protected void runIt(final IUndoableOperation operation)
	{
		CorePlugin.run(operation);
	}

	private static class ConvertTrack extends CMAPOperation
	{

		private final Editable[] _subjects;

		public ConvertTrack(final String title, final Editable[] subjects)
		{
			super(title);
			_subjects = subjects;
		}

		@Override
		public boolean canUndo()
		{
			return false;
		}

		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info)
				throws ExecutionException
		{
			final ObjectMapper mapper = new ObjectMapper();
			final ArrayNode docs = mapper.createArrayNode();

			// right, get going through the track
			for (int i = 0; i < _subjects.length; i++)
			{
				final Editable thisE = _subjects[i];
				if (thisE instanceof TrackWrapper)
				{
					final TrackWrapper tw = (TrackWrapper) thisE;

					final CouchTrack newT = new CouchTrack(tw);

					docs.add(newT.getDocument());
				}
			}

			if (docs.size() > 0)
			{
				// ok, do the push
				final ObjectNode res = mapper.createObjectNode();
				res.put("docs", docs);
				try
				{
					final String bulkTxt = mapper.writeValueAsString(res);

					// ok, and push
					final String couchURL = Activator.getDefault().getPreferenceStore()
							.getString(TrackStoreWrapper.COUCHDB_LOCATION);

					final String newURL = couchURL + "/tracks/_bulk_docs";
					final byte[] content = bulkTxt.getBytes();
					CorePlugin.logError(Status.INFO, "CouchDb POST (upload) to: " + newURL, null);
					final Post doIt = new Post(newURL, content, 5000, 10000);
					doIt.header("Content-Type", "application/json");
					final int result = doIt.responseCode();
					if (result == 201)
					{
						final byte[] resB = doIt.bytes();
						final String tmp = new String(resB);
						final JsonNode list = mapper.readValue(resB, JsonNode.class);
						if (list.isArray())
						{
							final ArrayNode arr = (ArrayNode) list;

							{
								final int len = arr.size();
								for (int i = 0; i < len; i++)
								{
									final JsonNode item = arr.get(i);
									final ObjectNode oo = (ObjectNode) item;
									
									if(item.has("error"))
									{
										final TextNode reason = (TextNode) item.get("reason");
										Activator.logError(Status.ERROR, "Import to Cloud failed:" + reason.asText(), null);
									}
									else
									{
										Activator.logError(Status.INFO,
												"Post succeeded, " + oo.get("id") + " uploaded", null);
									}
								}
							}
						}

					}
					else
					{
						Activator.logError(Status.ERROR, "Post failed: " + result, null);
					}

				}
				catch (final IOException e)
				{
					Activator.logError(Status.ERROR, "Failed writing to String", e);
				}
			}

			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
				throws ExecutionException
		{
			// duh, ignore
			return null;
		}
	}
}
