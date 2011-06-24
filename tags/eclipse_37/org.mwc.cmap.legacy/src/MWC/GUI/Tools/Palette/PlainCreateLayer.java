 // Copyright MWC 1999, Debrief 3 Project
// $RCSfile: PlainCreateLayer.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: PlainCreateLayer.java,v $
// Revision 1.2  2004/05/25 15:44:30  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:26  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:46  Ian.Mayo
// Initial import
//
// Revision 1.1  2002-05-28 09:14:05+01  ian_mayo
// Initial revision
//
// Revision 1.0  2002-05-23 13:13:22+01  ian
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:36+01  ian_mayo
// Initial revision
//
// Revision 1.3  2002-01-24 14:22:30+00  administrator
// Reflect fact that Layers events for reformat and modified take a Layer parameter (which is possibly null).  These changes are a step towards implementing per-layer graphics updates
//
// Revision 1.2  2001-10-29 12:58:07+00  administrator
// Check that shape creation was successful
//
// Revision 1.1  2001-08-23 13:27:56+01  administrator
// Reflect new signature for PlainCreate class, to allow it to fireExtended()
//
// Revision 1.0  2001-07-17 08:42:52+01  administrator
// Initial revision
//
// Revision 1.2  2001-07-16 15:38:07+01  novatech
// add comments
//
// Revision 1.1  2001-01-03 13:41:41+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:58  ianmayo
// initial version
//
// Revision 1.3  1999-11-26 15:51:40+00  ian_mayo
// tidying up
//
// Revision 1.2  1999-11-11 18:23:03+00  ian_mayo
// new classes, to allow creation of shapes from palette
//

package MWC.GUI.Tools.Palette;

import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.PlainTool;

abstract public class PlainCreateLayer extends PlainTool
{

	/** the panel used to edit this item
	 */
	PropertiesPanel _thePanel;

	/** the chart we are dropping onto
	 */
	PlainChart _theChart;

  /** the Layers object, which we need in order to fire data extended event
   *
   */
  Layers _theData;


  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
	/** constructor for label
	 * @param theParent parent where we can change cursor
	 * @param thePanel panel
	 * @param theData the layer we are adding the item to
	 */
	public PlainCreateLayer(ToolParent theParent,
										PropertiesPanel thePanel,
                    Layers theData,
										PlainChart theChart,
										String theName,
										String theImage)
	{
		super(theParent, theName, theImage);

		_thePanel = thePanel;
		_theChart = theChart;
    _theData = theData;
	}


  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  protected PlainChart getChart()
  {
    return _theChart;
  }

  /** accessor to retrieve the layered data
   *
   */
  public Layers getLayers()
  {
    return _theData;
  }

	protected abstract Layer createItem(PlainChart theChart);

	public Action getData()
	{
    Action res = null;

    // ask the child class to create itself
		Layer theLayer = createItem(_theChart);

    // did it work?
    if(theLayer != null)
    {
      // wrap it up in an action
      res=  new CreateLabelAction(_thePanel,
                                  theLayer,
                                  _theData);
    }

    return res;
	}

  ///////////////////////////////////////////////////////
  // store action information
  ///////////////////////////////////////////////////////
	protected class CreateLabelAction implements Action
	{
		/** the panel we are going to show the initial editor in
		 */
		final protected PropertiesPanel _thePanel1;
		final protected Layer _theLayer;
    final protected Layers _theData1;


		public CreateLabelAction(PropertiesPanel thePanel,
															 Layer theLayer,
                               Layers theData)
		{
			_thePanel1 = thePanel;
			_theLayer = theLayer;
      _theData1 = theData;
		}

		/** specify is this is an operation which can be undone
		 */
		public boolean isUndoable()
		{
			return true;
		}

		/** specify is this is an operation which can be redone
		 */
		public boolean isRedoable()
		{
			return true;
		}

		/** return string describing this operation
		 * @return String describing this operation
		 */
		public String toString()
		{
			return "New grid:" + _theLayer.getName();
		}

		/** take the shape away from the layer
		 */
		public void undo()
		{
      _theData1.removeThisLayer(_theLayer);
      _theData1.fireExtended();
		}

    /** make it so!
     */
    public void execute()
    {
      // check that the creation worked
      if(_theLayer != null)
      {
        // add the Shape to the layer, and put it
        // in the property editor
        _theData1.addThisLayer(_theLayer);
        _thePanel1.addEditor(_theLayer.getInfo(), _theLayer);
        _theData1.fireModified(_theLayer);
      }
    }
  }

}
