// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: View3dPlot.java,v $
// @author $Author: ian.mayo $
// @version $Revision: 1.8 $
// $Log: View3dPlot.java,v $
// Revision 1.8  2007/06/01 13:46:08  ian.mayo
// Improve performance of export text to clipboard
//
// Revision 1.7  2006/03/09 16:27:18  Ian.Mayo
// Indicate that we want it to be SWT version
//
// Revision 1.6  2005/12/13 09:04:48  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.5  2004/11/26 11:37:50  Ian.Mayo
// Moving closer, supporting checking for time resolution
//
// Revision 1.4  2004/11/25 10:24:33  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.3  2004/11/22 14:05:06  Ian.Mayo
// Replace variable name previously used for counting through enumeration - now part of JDK1.5
//
// Revision 1.2  2004/09/09 10:23:08  Ian.Mayo
// Reflect method name change in Layer interface
//
// Revision 1.1.1.2  2003/07/21 14:48:39  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.19  2003-07-01 16:31:58+01  ian_mayo
// Remove third party mouse libraries
//
// Revision 1.18  2003-04-01 15:50:41+01  ian_mayo
// Perform detach/attach before adding new layers
//
// Revision 1.17  2003-03-28 12:08:49+00  ian_mayo
// Finish off restructuring 3d object graph
//
// Revision 1.16  2003-03-27 16:51:09+00  ian_mayo
// switch 3d graph to reflect 2d object hierarchy (including layers)
//
// Revision 1.15  2003-03-25 15:55:46+00  ian_mayo
// working on Chuck's problems with lots of annotations on 3-d plot.
//
// Revision 1.14  2003-03-21 15:43:01+00  ian_mayo
// Replace stuff which shouldn't have been deleted by IntelliJ inspector
//
// Revision 1.13  2003-03-19 15:38:11+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.12  2003-03-18 16:17:57+00  ian_mayo
// Javadocs
//
// Revision 1.11  2003-03-14 16:02:19+00  ian_mayo
// changes to comments
//
// Revision 1.10  2003-02-25 14:37:48+00  ian_mayo
// Remove unused code
//
// Revision 1.9  2003-02-21 11:13:24+00  ian_mayo
// Finish implementation. Read in bathy from correct (configured) location
//
// Revision 1.8  2003-02-07 09:02:38+00  ian_mayo
// Remove unnecessary
//
// Revision 1.7  2003-01-24 12:21:33+00  ian_mayo
// Put 3d view into frame which supports mouse wheel under JDK1.3
//
// Revision 1.6  2003-01-21 16:28:49+00  ian_mayo
// Some refactoring, add testing code, some comment tidying
//
// Revision 1.5  2002-11-27 15:28:16+00  ian_mayo
// Tidying according to Idea inspection
//
// Revision 1.4  2002-05-28 09:25:08+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:52+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-05-23 13:08:19+01  ian
// Reflect class name changes
//
// Revision 1.3  2002-05-07 16:09:30+01  ian_mayo
// Reflect participant name changes
//
// Revision 1.2  2002-05-07 08:45:24+01  ian_mayo
// Restructure how we listen out for layers changing
//
// Revision 1.1  2002-05-01 15:46:43+01  ian
// Add support for listening out for tracks being added/removed
//
// Revision 1.0  2002-04-30 09:14:54+01  ian
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:06+01  ian_mayo
// Initial revision
//
// Revision 1.3  2001-11-20 11:23:17+00  administrator
// changed method signature
//
// Revision 1.2  2001-08-21 12:14:23+01  administrator
// Remove file drop support
//
// Revision 1.1  2001-08-17 08:05:14+01  administrator
// Try to clear up memory leaks
//
// Revision 1.0  2001-07-17 08:41:20+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:40:31+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:48:27  ianmayo
// initial import of files
//
// Revision 1.6  2000-10-10 13:05:29+01  ian_mayo
// Update to reflect new FileDrag/Drop code
//
// Revision 1.5  2000-10-10 12:20:31+01  ian_mayo
// location of drag drop has changed
//
// Revision 1.4  2000-10-09 13:37:29+01  ian_mayo
// Switch stackTrace to go to file
//
// Revision 1.3  2000-06-19 15:05:36+01  ian_mayo
// moved the code which puts the tracks into the plot into the TimedFrame3d_unused class
//
// Revision 1.2  2000-06-15 18:06:33+01  ian_mayo
// replaced DropFile support, and recognised sub-classing of Frame3d_unused to TimedFrame3d_unused
//
// Revision 1.1  2000-06-15 13:45:49+01  ian_mayo
// Initial revision
//

package Debrief.Tools.Operations;

import Debrief.GUI.Tote.StepControl;
import Debrief.Tools.Operations.Plot3D.LabelWrapper3D;
import Debrief.Tools.Operations.Plot3D.ShapeWrapper3D;
import Debrief.Tools.Operations.Plot3D.Track3D;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.ShapeWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.*;
import MWC.GUI.Chart.Painters.SpatialRasterPainter;
import MWC.GUI.ETOPO.BathyProvider;
import MWC.GUI.ETOPO.ETOPO_2_Minute;
import MWC.GUI.Java3d.MouseWheelWorldHolder;
import MWC.GUI.Java3d.Tactical.Layer3D;
import MWC.GUI.Java3d.Tactical.Participant3D;
import MWC.GUI.Java3d.World;
import MWC.GUI.Java3d.WorldHolder;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.Palette.CreateTOPO;
import MWC.GUI.Tools.PlainTool;
import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.PlainImporterBase;

import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.media.j3d.Group;
import javax.media.j3d.Node;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;

/**
 * command to import a file (initially just Replay) into Debrief.
 * The data used to implement the command is stored as a command,
 * so that it may be added to an undo buffer.
 */
public final class View3dPlot extends PlainTool // implements Layers.DataListener
{ //implements FileDropSupport.FileDropListener {

  ///////////////////////////////////////////////////////////////
  // member variables
  ///////////////////////////////////////////////////////////////

  /**
   * the properties panel to put ourselves into
   */
  private final PropertiesPanel _thePanel;

  /**
   * the layers we should add data to
   */
  private final Layers _theLayers;

  /**
   * the step control the 3d viewer listens to
   */
  private final StepControl _theStepper;

  /** the class which provides bathy data
   *
   */
  //  protected BathyProvider _bathyProvider;

  ///////////////////////////////////////////////////////////////
  // constructor
  ///////////////////////////////////////////////////////////////
  /**
   * constructor, taking information ready for when the button
   * gets pressed
   *
   * @param theParent  the ToolParent window which we control the cursor of
   * @param thePanel   the Application to create a blank
   *                   session to import the file into, if the session val is null
   * @param theStepper the Session to add the file to (or null, see above)
   */
  public View3dPlot(final ToolParent theParent,
                    final PropertiesPanel thePanel,
                    final Layers theData,
                    final StepControl theStepper)
  {
    super(theParent, "View in 3d", "images/view3d.gif");
    _theStepper = theStepper;
    // store the Session
    _thePanel = thePanel;
    _theLayers = theData;
  }


  public final Action getData()
  {
    return null;
  }


  /**
   * ok, lets do it!
   */
  public final void execute()
  {

    try
    {
      WorldHolder tmpHolder = null;

      // prepare the bathy data
      BathyProvider _bathyProvider = new ETOPO_2_Minute(CreateTOPO.getETOPOPath());

      // SPECIAL PROCESSING, open up a mouse-less view if we are running jdk1.3
      try
      {
        tmpHolder = new MouseWheelWorldHolder(_thePanel, _theStepper, _theLayers, _bathyProvider, false)
        {
          /**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					public void dataExtended(final Layers theData)
          {
            //
            System.out.println("extended!");
            doDataExtended(theData, this);
          }

          public void dataModified(final Layers theData, final Layer changedLayer)
          {
            System.out.println("modified!");
            // just pass it on to the extended method
            //     dataExtended(theData);
          }

          public void dataReformatted(final Layers theData, final Layer changedLayer)
          {
            //
            System.out.println("reformatted!");
            //    doDataReformatted(theData, changedLayer, this);
          }
        };
      }
      catch (java.lang.NoClassDefFoundError er)
      {
        tmpHolder = new WorldHolder(_thePanel, _theStepper, _theLayers, _bathyProvider, true)
        {
          /**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					public void dataExtended(final Layers theData)
          {
            doDataExtended(theData, this);
          }

          public void dataModified(final Layers theData, final Layer changedLayer)
          {
            // just pass it on to the extended method
            dataExtended(theData);
          }

          public void dataReformatted(final Layers theData, final Layer changedLayer)
          {
            // see if one of our tracks has changed colour or symbol
            //   doDataReformatted(theData, changedLayer, this);

          }
        };
      }

      final WorldHolder worldHolder = tmpHolder;

      doDataExtended(_theLayers, worldHolder);

      // add the buoyfields

      // done
      worldHolder.finish();

      // and put it into a frame
      JFrame worldF = new JFrame("Debrief 3D View");

      worldF.setSize(750, 400);
      worldF.getContentPane().setLayout(new BorderLayout());
      worldF.getContentPane().add(worldHolder);
      worldF.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      worldF.addWindowListener(new WindowAdapter()
      {
        public void windowClosing(final WindowEvent e)
        {
          worldHolder.doClose();
        }
      });

      // over-ride the close operation if we haven't got a parent or a properties window
      if ((_thePanel == null) && (super.getParent() == null))
      {
        worldF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      }

      worldF.setVisible(true);

    }
    catch (NoClassDefFoundError e)
    {
      MWC.Utilities.Errors.Trace.trace(e);
      String msg = "3D Java libraries not found, please check installation";
      MWC.GUI.Dialogs.DialogFactory.showMessage("View 3d",
                                                msg);
    }

  }


  /**
   * **************************************************************
   * layers support listening
   * **************************************************************
   */


  private Group createThisLayer(Layer thisLayer, World theWorld, WorldHolder holder)
  {
    Group res = null;

    // so, is this a track?
    if (thisLayer instanceof TrackWrapper)
    {
      // go on, stick it in!
      TrackWrapper thisTrack = (TrackWrapper) thisLayer;
      res = new Track3D(theWorld.getWorldPlottingOptions(),
                        theWorld, thisTrack, _theStepper);
      // also tell the world holder that a new track is loaded
      holder.addTrack((Track3D) res);

    }
    // is it an ETOPO layer?
    else if (thisLayer instanceof SpatialRasterPainter)
    {
      // ok, get the world to add it's bathy
      theWorld.populateBathy((SpatialRasterPainter) thisLayer, holder.getUniverse());
    }
    else if (thisLayer instanceof BaseLayer)
    {
      // create the layer
      res = new Layer3D((BaseLayer) thisLayer);
    }
    return res;
  }


  private void checkThisPlottable(Plottable thisP, World world, Group layer)
  {
    // do we hold this plottable

    Node theLabel = world.containsThis(thisP);

    if (theLabel == null)
    {
      if (thisP instanceof LabelWrapper)
      {
        LabelWrapper thisLW = (LabelWrapper) thisP;
        theLabel = new LabelWrapper3D(world.getWorldPlottingOptions(),
                                      world, thisLW, _theStepper);
      }
      else if (thisP instanceof ShapeWrapper)
      {
        ShapeWrapper sw = (ShapeWrapper) thisP;
        theLabel = new ShapeWrapper3D(world.getWorldPlottingOptions(),
                                      world, sw, _theStepper);
      }

      if (theLabel != null)
      {
        layer.addChild(theLabel);
      }
    }
  }

  private void checkThis2dLayerIsPresentIn3d(Layer thisLayer, WorldHolder holder, World world, Group parent)
  {
    // right, check if we contain this layer
    Group layer = (Group) world.containsThis(thisLayer);
    if (layer == null)
    {
      // right, create this layer
      layer = createThisLayer(thisLayer, world, holder);

      // and add it (if applicable)
      if (layer != null)
      {
        // yup, we need to create it.
        // do we know our parent?
        if (parent != null)
        {
          world.detachForRemoval();
          parent.addChild(layer);
          world.andReattach(holder.getUniverse());
        }
        else
        {
          world.detachForRemoval();
          world.addThisItem(layer);
          world.andReattach(holder.getUniverse());
        }
      }
    }

    // just check if it was an ETOPO, in which case we drop out
    if (thisLayer instanceof SpatialRasterPainter)
      return;

    // also check if it was a track wrapper, in which case we can return
    if (thisLayer instanceof TrackWrapper)
      return;

    // now check for the children of the base layer
    Enumeration<Editable> iter = thisLayer.elements();
    while (iter.hasMoreElements())
    {
      Plottable thisP = (Plottable) iter.nextElement();

      // what sort is it?
      if (thisP instanceof Layer)
      {
        checkThis2dLayerIsPresentIn3d((Layer) thisP, holder, world, layer);
      }
      else
      {
        checkThisPlottable(thisP, world, layer);
      }
    }

  }


  void doDataExtended(final Layers theData,
                              final WorldHolder theHolder)
  {
    // so, first pass down through our set of layers, and check that everything in there
    // is present in 3d
    World myWorld = theHolder.getWorld();

    for (int i = 0; i < theData.size(); i++)
    {
      // get the next layer
      Layer thisLayer = theData.elementAt(i);

      // see if we have it
      checkThis2dLayerIsPresentIn3d(thisLayer, theHolder, myWorld, null);
    }


    // now do the reverse check, to see if all of the 3d items are in our layers object
    Group parent = myWorld.getTransform();

    // ok, go for it!
    checkThis3dGroupIn2d(parent, null, myWorld, theHolder.getUniverse(), theHolder);

  }


  private void checkThis3dGroupIn2d(Group group,
                                    Group parent,
                                    World world,
                                    SimpleUniverse universe,
                                    WorldHolder holder)
  {
    if (group.getCapability(Group.ALLOW_CHILDREN_READ))
    {
      // first run through this group to look a the children
      for (int i = 0; i < group.numChildren(); i++)
      {
        Node child = group.getChild(i);

        // firstly see if this is a layer itself
        if (child instanceof Group)
        {
          Group grp = (Group) child;
          checkThis3dGroupIn2d(grp, group, world, universe, holder);
        }

        // now, does it have user data?
        Object o = child.getUserData();
        if (!containsThisItem(o))
        {
          world.detachForRemoval();
          group.removeChild(child);
          world.andReattach(universe);

          // right, was it a track?
          if (o instanceof TrackWrapper)
          {
            // yup, we've also got to remove it from the list of views
            holder.removeThisTrack((Participant3D) child);
          }

        }

      }

      // now check this parent
      if (!containsThisItem(group.getUserData()))
      {
        // we want to get rid of it, ditch it!
        if (parent != null)
        {
          world.detachForRemoval();
          parent.removeChild(group);
          world.andReattach(universe);
        }
        else
        {
          // hey, we're not going to ditch the top level!
        }
      } // whether there is user data
    } // whether we can read the children
  }

  public boolean containsThisItem(Object o)
  {
    boolean res = false;

    if (o == null)
      return true;

    if (o instanceof Plottable)
    {
      Plottable plottable = (Plottable) o;

      for (int i = 0; i < _theLayers.size(); i++)
      {
        // get the next layer
        Layer thisLayer = _theLayers.elementAt(i);

        // see if this layer contains our plottable
        boolean found = checkThisLayer(thisLayer, plottable);

        if (found)
        {
          res = true;
          break;
        } // whether it was found
      } // through the layers
    } // whether this is a plottable
    else
    {
      // user data is not a plottable - must be for our own 3d management, make it "acceptable"
      res = true;
    }

    return res;
  }

  private boolean checkThisLayer(Layer thisLayer, Plottable plottable)
  {
    boolean res = false;

    // check the layer itself
    if (thisLayer == plottable)
    {
      res = true;
    }
    else
    {
      Enumeration<Editable> iter = thisLayer.elements();
      while (iter.hasMoreElements())
      {
        Plottable pl = (Plottable) iter.nextElement();

        if (pl == plottable)
        {
          res = true;
          break;
        }

        if (pl instanceof Layer)
        {
          res = checkThisLayer((Layer) pl, plottable);
          if (res == true)
            break;
        }
      }
    }

    return res;
  }

  //////////////////////////////////////////////////
  // testing code
  //////////////////////////////////////////////////

  public static void main(String[] args)
  {
    MWC.GUI.Layers theLayers = new MWC.GUI.Layers();
    try
    {

      final String theFileName = "d:\\dev\\debrief\\debrief_out\\SOVEREIGN.REP";

      PlainImporterBase pi = new Debrief.ReaderWriter.Replay.ImportReplay();
      pi.importThis(theFileName,
                    new java.io.FileInputStream(theFileName),
                    theLayers);
    }
    catch (Exception e)
    {
      MWC.Utilities.Errors.Trace.trace(e);
    }

    StepControl myStepper = new StepControl(null)
    {
      protected void doEditPainter()
      {
      }

      protected void formatTimeText()
      {
      }

      protected PropertiesPanel getPropertiesPanel()
      {
        return null;
      }

      public HiResDate getToolboxEndTime()
      {
        return null;
      }

      public HiResDate getToolboxStartTime()
      {
        return null;
      }

      protected void initForm()
      {
      }

      protected void painterIsDefined()
      {
      }

      public void setToolboxEndTime(HiResDate val)
      {
      }

      public void setToolboxStartTime(HiResDate val)
      {
      }

      protected void updateForm(HiResDate DTG)
      {
      }
    };
    myStepper.setStepSmall(1000);

    TrackWrapper tw = (TrackWrapper) theLayers.findLayer("sovere");

    myStepper.addParticipant(tw, tw.getStartDTG(), tw.getEndDTG());
    myStepper.doStep(true, true);

    // ok, now create the View3dPlot button
    View3dPlot plotter = new View3dPlot(null, null, theLayers, myStepper);

    plotter.execute();

    myStepper.gotoEnd();
  }
}
