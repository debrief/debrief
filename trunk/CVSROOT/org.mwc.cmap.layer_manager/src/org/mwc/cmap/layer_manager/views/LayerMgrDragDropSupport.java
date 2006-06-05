package org.mwc.cmap.layer_manager.views;

import java.util.Iterator;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.widgets.TreeItem;
import org.mwc.cmap.core.operations.RightClickCutCopyAdaptor.EditableTransfer;
import org.mwc.cmap.core.property_support.*;

import Debrief.Wrappers.*;
import Debrief.Wrappers.NarrativeWrapper.NarrativeEntry;
import MWC.GUI.*;
import MWC.GUI.Chart.Painters.ETOPOPainter;

public class LayerMgrDragDropSupport implements DragSourceListener, DropTargetListener
{

	private TreeViewer _parent;

	public LayerMgrDragDropSupport(TreeViewer parent)
	{
		_parent = parent;
	}

	public void dragFinished(DragSourceEvent event)
	{
		System.out.println("drag finished");
	}

	public void dragSetData(DragSourceEvent event)
	{
		StructuredSelection sel = getSelection();
		event.data = sel;
	}

	public void dragStart(DragSourceEvent event)
	{
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
		System.out.println("drag enter");
	}

	public void dragLeave(DropTargetEvent event)
	{
		System.out.println("drag leave");
	}

	public void dragOperationChanged(DropTargetEvent event)
	{
		System.out.println("drag op changed");
	}

	public void dragOver(DropTargetEvent event)
	{
		TreeItem ti = (TreeItem) event.item;
		PlottableWrapper pw = (PlottableWrapper) ti.getData();
		Plottable pl = pw.getPlottable();
		if (pl instanceof ETOPOPainter)
		{
			event.feedback = DND.FEEDBACK_NONE;
			event.detail = DND.DROP_NONE;
		}
		else if (pl instanceof BaseLayer)
		{
			event.feedback = DND.FEEDBACK_SELECT;
			event.detail = DND.DROP_MOVE;
		}
		else
		{
			event.feedback = DND.FEEDBACK_NONE;
			event.detail = DND.DROP_NONE;
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
			
			// remove from current parent
			PlottableWrapper parent = thisP.getParent();
			
			// is this a top-level item?
			if(parent == null)
			{
				Layers layers = thisP.getLayers();
				layers.removeThisLayer((Layer) dragee);
			}
			else
			{
				BaseLayer parentLayer = (BaseLayer) parent.getPlottable();
				parentLayer.removeElement(dragee);
			}
			

			// add to new parent
			TreeItem ti = (TreeItem) event.item;
			PlottableWrapper pw = (PlottableWrapper) ti.getData();
			BaseLayer dest = (BaseLayer) pw.getPlottable();
			dest.add(dragee);
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
