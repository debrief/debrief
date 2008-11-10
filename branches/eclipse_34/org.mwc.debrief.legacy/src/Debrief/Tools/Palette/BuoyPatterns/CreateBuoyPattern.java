// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: CreateBuoyPattern.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.1.1.2 $
// $Log: CreateBuoyPattern.java,v $
// Revision 1.1.1.2  2003/07/21 14:48:50  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.5  2003-03-19 15:37:18+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.4  2003-02-12 16:18:42+00  ian_mayo
// Remove unused imports
//
// Revision 1.3  2003-02-11 08:37:37+00  ian_mayo
// remove unnecessary toda statement
//
// Revision 1.2  2002-05-28 09:25:09+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:46+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:44+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-01-24 14:23:36+00  administrator
// Reflect change in Layers reformat and modified events which take an indication of which layer has been modified - a step towards per-layer graphics repaints
//
// Revision 1.0  2001-07-17 08:41:15+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-17 13:20:31+00  novatech
// Initial revision
//
// Revision 1.2  2001-01-05 10:32:04+00  novatech
// Finishing off, adding undo functionality
//
// Revision 1.1  2001-01-03 16:02:38+00  novatech
// Initial revision
//

package Debrief.Tools.Palette.BuoyPatterns;

import MWC.GUI.*;
import MWC.GUI.Tools.*;
import MWC.GUI.Properties.*;
import MWC.GenericData.*;

public final class CreateBuoyPattern extends PlainTool
{

  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  /** the properties panel
   */
  private final PropertiesPanel _thePanel;

  /** the layers we are going to drop this shape into
   */
  private final Layers _theData;

  /** the chart we are using (since want our 'duff' item to appear in the middle)
   */
  private final MWC.GUI.PlainChart _theChart;

  /** the buoypattern director I work with
   */
  private final BuoyPatternDirector _theDirector;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  /** constructor for label
   * @param theParent parent where we can change cursor
   * @param thePanel panel
   */
  public CreateBuoyPattern(ToolParent theParent,
                     PropertiesPanel thePanel,
                     Layers theData,
                     MWC.GUI.PlainChart theChart,
                     String theName,
                     String theImage)
  {
    super(theParent, theName, theImage);

    _thePanel = thePanel;
    _theData = theData;
    _theChart = theChart;

    _theDirector = new BuoyPatternDirector();
  }


  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  public final Action getData()
  {
      return new CreateBuoyPatternAction(_thePanel, _theData, _theChart, _theDirector);
  }

  /** get the actual instance of the shape we are creating
   * @return LabelWrapper containing an instance of the new shape
   * @param centre the current centre of the screen, where the shape should be centred
   */
 // abstract protected LabelWrapper getShape(WorldLocation centre);


  ///////////////////////////////////////////////////////
  // store action information
  ///////////////////////////////////////////////////////
  protected final class CreateBuoyPatternAction implements Action
  {
    /** the panel we are going to show the initial editor in
     */
    final PropertiesPanel _thePanel;
    final Layers _theLayers;
    final MWC.GUI.PlainChart _theChart;
    final BuoyPatternDirector _theDirector;

    PatternBuilderType _myBuilder;

    public CreateBuoyPatternAction(PropertiesPanel thePanel,
                               Layers theLayers,
                               MWC.GUI.PlainChart theChart,
                               BuoyPatternDirector theDirector)
    {
      _thePanel = thePanel;
      _theChart = theChart;
      _theDirector = theDirector;
      _theLayers = theLayers;
    }

    /** specify is this is an operation which can be undone
     */
    public final boolean isUndoable()
    {
      return true;
    }

    /** specify is this is an operation which can be redone
     */
    public final boolean isRedoable()
    {
      return true;
    }

    /** return string describing this operation
     * @return String describing this operation
     */
    public final String toString()
    {
      return "New BuoyPattern";
    }

    /** take the shape away from the layer
     */
    public final void undo()
    {
      // check that we got as far as creating a builder
      if(_myBuilder != null)
      {
        // get the builder to undo the work
        _myBuilder.undo();
      }
    }

    /** make it so!
     */
    public final void execute()
    {
      // find the centre of the plot
      WorldArea wa = _theChart.getDataArea();

      // have we actually got data?
      if(wa == null)
      {
        // drop out
        return;
      }

      // retrieve the centre of this area
      WorldLocation centre = wa.getCentre();

      // find out which type of shape we want
      String selection = getChoice();

      // check that the user has entered something
      if(selection == null)
      {
        // oh well, just drop out
        return;
      }

      // create the pattern builder, informing it of the Layers object which
      // it is to insert itself into
      _myBuilder = _theDirector.createBuilder(centre,
                                              selection,
                                              _thePanel,
                                              _theLayers);

      // pass the pattern builder to the property editor
      _thePanel.addConstructor(_myBuilder.getInfo(), null);

      // finished.
    }
  }


  private String getChoice()
  {
    Object[] opts = _theDirector.getPatterns();
    String res = (String)javax.swing.JOptionPane.showInputDialog(null,
                                            "Which pattern?",
                                            "Create Buoy Pattern",
                                            javax.swing.JOptionPane.QUESTION_MESSAGE,
                                            null,
                                            opts,
                                            null);
    return res;
  }


}
