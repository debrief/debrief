package org.mwc.cmap.layer_manager.views;

import java.util.Iterator;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.widgets.TreeItem;
import org.mwc.cmap.core.operations.RightClickPasteAdaptor;
import org.mwc.cmap.core.operations.RightClickCutCopyAdaptor.EditableTransfer;
import org.mwc.cmap.core.property_support.*;

import Debrief.Wrappers.*;
import MWC.GUI.*;
import MWC.GUI.Chart.Painters.ETOPOPainter;
import MWC.TacticalData.NarrativeEntry;

/**
 * nice drag-drop support for layer manager
 * 
 * @author ian.mayo
 */
public class LayerMgrDragDropSupport implements DragSourceListener, DropTargetListener
{

	/**
	 * the control that's providing us with our selection
	 */
	private StructuredViewer _parent;

	/** it appears that the copy/move operations gets cancelled after we mark
	 * something as "don't drop".  remember the previous setting, so that when
	 * we want to indicate that something is a valid drop-target, it can be dropped.
	 * that's all/
	 */
	private int _oldDetail = -1;
	

	/**
	 * constructor - something that tells us about the current selection
	 * 
	 * @param parent
	 */
	public LayerMgrDragDropSupport(StructuredViewer parent)
	{
		_parent = parent;
	}

	public void dragFinished(DragSourceEvent event)
	{
	}

	public void dragSetData(DragSourceEvent event)
	{
		StructuredSelection sel = getSelection();
		event.data = sel;
	}

	public void dragStart(DragSourceEvent event)
	{
		// ok, clear the old detail flag
		_oldDetail = -1;
		
		boolean res = true;

		// get what's selected
		StructuredSelection sel = getSelection();
		EditableWrapper first = (EditableWrapper) sel.getFirstElement();
		Editable pl = first.getEditable();

		// so, is this draggable?
		if (pl instanceof BaseLayer)
			res = true;
		else if (pl instanceof ETOPOPainter)
			res = false;
		else if (pl instanceof TrackWrapper)
			res = false;
		else if (pl instanceof FixWrapper)
			res = false;
		else if (pl instanceof NarrativeWrapper)
			res = false;
		else if (pl instanceof NarrativeEntry)
			res = false;
		else if (pl instanceof TacticalDataWrapper)
			res = false;

		event.doit = res;
	}

	/**
	 * find out what's currently selected
	 * 
	 * @return
	 */
	private StructuredSelection getSelection()
	{
		// ok, get the selection
		StructuredSelection sel = (StructuredSelection) _parent.getSelection();
		return sel;
	}

	public void dragEnter(DropTargetEvent event)
	{
	}

	public void dragLeave(DropTargetEvent event)
	{
	}

	public void dragOperationChanged(DropTargetEvent event)
	{
	}

	public void dragOver(DropTargetEvent event)
	{
		boolean allowDrop = false;
		TreeItem ti = (TreeItem) event.item;
		// right, do we have a target?
		if (ti != null)
		{
			PlottableWrapper pw = (PlottableWrapper) ti.getData();
			Plottable pl = pw.getPlottable();
			if (pl instanceof ETOPOPainter)
			{
				allowDrop = false;
			}
			else if (pl instanceof BaseLayer)
			{
				allowDrop = true;
			}
			else
			{
				allowDrop = false;
			}

			if (allowDrop)
			{
				// restore what we were looking at...
				if(event.detail == DND.DROP_NONE)
				{
					event.detail = _oldDetail;
				}			

				// ok - and the update status of the component under the cursor
				event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
			}
			else
			{
				if(event.detail != DND.DROP_NONE)
				{
					_oldDetail = event.detail;
				}
				
				event.feedback = DND.FEEDBACK_NONE;
				event.detail = DND.DROP_NONE;
			}
		}
	}

	public void drop(DropTargetEvent event)
	{
		StructuredSelection sel = getSelection();

		// cycle through the elements
		for (Iterator iter = sel.iterator(); iter.hasNext();)
		{
			PlottableWrapper thisP = (PlottableWrapper) iter.next();
			Plottable dragee = thisP.getPlottable();

			// right, are we cutting?
			if ((_oldDetail & DND.DROP_MOVE) != 0)
			{
				// remove from current parent
				PlottableWrapper parent = thisP.getParent();

				// is this a top-level item?
				if (parent == null)
				{
					Layers layers = thisP.getLayers();
					layers.removeThisLayer((Layer) dragee);
				}
				else
				{
					BaseLayer parentLayer = (BaseLayer) parent.getPlottable();
					parentLayer.removeElement(dragee);
				}
			}

			// add to new parent
			TreeItem ti = (TreeItem) event.item;
			PlottableWrapper destination = (PlottableWrapper) ti.getData();
			
			// ok, we need to add a new instance of the dragee (so we can support multiple instances)
			Plottable newDragee = (Plottable) RightClickPasteAdaptor.cloneThis(dragee);
			
			// also add it to the plottable layer target
			BaseLayer dest = (BaseLayer) destination.getPlottable();
			dest.add(newDragee);
		}

		// fire update
		Layers destL = (Layers) _parent.getInput();
		destL.fireExtended();
	}

	public void dropAccept(DropTargetEvent event)
	{
		// right, is htis
	}

	public Transfer[] getTypes()
	{
		Transfer[] res = new Transfer[] { EditableTransfer.getInstance() };
		return res;
	}

}
