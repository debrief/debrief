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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
// $RCSfile: CreateShape.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.4 $
// $Log: CreateShape.java,v $
// Revision 1.4  2006/04/06 13:11:51  Ian.Mayo
// Provide greater access to my bits
//
// Revision 1.3  2005/06/30 11:30:30  Ian.Mayo
// Indicate which item has changed
//
// Revision 1.2  2005/06/28 07:06:47  Ian.Mayo
// Refactor to support over-riding in Eclipse
//
// Revision 1.1.1.2  2003/07/21 14:48:46  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.6  2003-05-08 16:00:44+01  ian_mayo
// Fire the extended event properly
//
// Revision 1.5  2003-03-19 15:37:24+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.4  2003-02-07 09:02:42+00  ian_mayo
// remove unnecessary toda comments
//
// Revision 1.3  2003-01-22 10:34:27+00  ian_mayo
// When we create new shape, default it's depth to be at the surface
//
// Revision 1.2  2002-05-28 09:25:09+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:48+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:49+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-01-24 14:23:37+00  administrator
// Reflect change in Layers reformat and modified events which take an indication of which layer has been modified - a step towards per-layer graphics repaints
//
// Revision 1.0  2001-07-17 08:41:16+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:40:27+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:48:56  ianmayo
// initial import of files
//
// Revision 1.7  2000-11-24 11:49:42+00  ian_mayo
// tidying up
//
// Revision 1.6  2000-11-02 16:45:51+00  ian_mayo
// changing Layer into Interface, replaced by BaseLayer, also changed TrackWrapper so that it implements Layer,  and as we read in files, we put them into track and add Track to Layers, not to Layer then Layers
//
// Revision 1.5  2000-04-19 11:27:06+01  ian_mayo
// implement Close method, clear local storage
//
// Revision 1.4  2000-04-05 08:37:23+01  ian_mayo
// check we have defined a Chart area before we add shape
//
// Revision 1.3  2000-03-14 09:47:54+00  ian_mayo
// process image names
//
// Revision 1.2  1999-11-11 18:23:04+00  ian_mayo
// new classes, to allow creation of shapes from palette
//

package Debrief.Tools.Palette;

import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainWrapper;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Tools.Action;
import MWC.GenericData.WorldLocation;

abstract public class CreateShape extends CoreCreateShape
{

  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  /** the properties panel
   */
  private PropertiesPanel _thePanel;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public CreateShape(final ToolParent theParent,
      final PropertiesPanel thePanel,
      final Layers theData,
      final String theName,
      final String theImage, BoundsProvider bounds)
  {
    super(theParent, theName, theImage,theData,bounds);
    _thePanel = thePanel;

  }


  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  @Override
  protected Action createAction(PropertiesPanel thePanel, Layer theLayer, PlainWrapper theWrapper, final Layers theLayers) 
  {
    return new CreateShapeAction(_thePanel, theLayer, (ShapeWrapper)theWrapper, _theData);
  }
  
  @Override
  protected PlainWrapper createWrapper(WorldLocation centre)
  {
    return getShape(centre);
  }
  
  /** get the actual instance of the shape we are creating
   * @return ShapeWrapper containing an instance of the new shape
   * @param centre the current centre of the screen, where the shape should be centred
   */
  abstract protected ShapeWrapper getShape(WorldLocation centre);


  ///////////////////////////////////////////////////////
  // store action information
  ///////////////////////////////////////////////////////
  public static class CreateShapeAction implements Action
  {
    /** the panel we are going to show the initial editor in
     */
    final protected PropertiesPanel _thePanel;
    final protected Layer _theLayer;
    final protected Debrief.Wrappers.ShapeWrapper _theShape;
    final protected Layers _theLayers;


    public CreateShapeAction(final PropertiesPanel thePanel,
        final Layer theLayer,
        final ShapeWrapper theShape,
        final Layers theLayers)
    {
      _thePanel = thePanel;
      _theLayer = theLayer;
      _theShape = theShape;
      _theLayers = theLayers;
    }

    public final boolean isUndoable()
    {
      return true;
    }

    public final boolean isRedoable()
    {
      return true;
    }

    public final String toString()
    {
      return "New shape:" + _theShape.getName();
    }

    /** take the shape away from the layer
     */
    public final void undo()
    {
      _theLayer.removeElement(_theShape);

      // and fire the extended event
      _theLayers.fireExtended();
    }

    public void execute()
    {
      // add the Shape to the layer, and put it
      // in the property editor
      _theLayer.add(_theShape);

      if(_thePanel != null)
        _thePanel.addEditor(_theShape.getInfo(), _theLayer);

      // and fire the extended event
      _theLayers.fireExtended(_theShape, _theLayer);
    }
  }


  public final void close()
  {
    super.close();

    // remove our local references
    _thePanel = null;
    _theData = null;
  }

}
