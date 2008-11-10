/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: Apr 22, 2002
 * Time: 12:22:01 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package Debrief.Tools.Operations.Plot3D;

import MWC.GUI.Java3d.Tactical.Label3D;
import MWC.GUI.Java3d.WorldPlottingOptions;
import MWC.GUI.Java3d.World;
import MWC.GUI.Java3d.ScaleTransform;
import MWC.GUI.StepperListener;
import MWC.GUI.CanvasType;
import MWC.GUI.PlainWrapper;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.HiResDate;
import Debrief.Wrappers.ShapeWrapper;
import Debrief.GUI.Tote.StepControl;

import java.beans.PropertyChangeEvent;

import com.sun.j3d.utils.geometry.Primitive;

import javax.vecmath.Color3f;
import javax.media.j3d.*;

public final class ShapeWrapper3D extends Label3D implements StepperListener
{
  ///////////////////////////
  // member variables
  ///////////////////////////
  /** the label we are plotting
   *
   */
  private final ShapeWrapper _myShapeWrapper;


  /** the step control we listen to
   *
   */
  private final StepperController _myStepper;


  /** the symbol for this label
   *
   */
  private Shape3D _theGraphic;

  ///////////////////////////
  // constructor
  ///////////////////////////
  public ShapeWrapper3D(WorldPlottingOptions options,
                        World world,
                        ShapeWrapper shape,
                        StepperController stepper)
  {
    super(options, world, shape);
    this._myShapeWrapper = shape;

    _myStepper = stepper;

    // now for the building bits
    build();

    // finish off by updating ourselves
    if(stepper != null)
    {
      this.updated(stepper.getCurrentTime());
    }

    // update the symbol visibility
    setVisible(shape.getVisible());
    setLabelVisible(shape.getLabelVisible());

    // initialise the line width
    updateLineWidth(1);

  }

  ///////////////////////////
  // member methods
  ///////////////////////////

  /** property changes, listen out for any coming from the label, else pass them onto the parent,
   * since they are probably WorldOptions related
   *
   * @param evt
   */
  public final void propertyChange(PropertyChangeEvent evt)
  {
    String evtName = evt.getPropertyName();

    if(evt.getSource() == _myShapeWrapper)
    {
      // see if it's the location!
      if(evtName.equals(PlainWrapper.LOCATION_CHANGED))
      {
        // yup, location it is - do the update
        setLocation();
      }
      else if(evtName.equals(PlainWrapper.TEXT_CHANGED))
      {
        // hey, somebody's changed the text in the label, we'd better update ours!
        setText(getText());
      }
      else if(evtName.equals(PlainWrapper.COLOR_CHANGED))
      {
        // hey, somebody's changed the text in the label, we'd better update ours!
        updateColor();
      }
      else if(evtName.equals(PlainWrapper.VISIBILITY_CHANGED))
      {
        Boolean newVis = (Boolean)evt.getNewValue();

        // hey, the visibility of the symbol has change - is it vis?
        setVisible(newVis.booleanValue());
      }
      else if(evtName.equals(ShapeWrapper.LABEL_VIS_CHANGED))
      {
        Boolean newVis = (Boolean)evt.getNewValue();

        // hey, the visibility of the symbol has change - is it vis?
        super.setLabelVisible(newVis.booleanValue());
      }


    }
    else
    {
      // see if it's the line width one
      if(evt.getPropertyName().equals(WorldPlottingOptions.SNAIL_LINE_WIDTH))
      {
        this.updateLineWidth(_options.getSnailLineWidth().getCurrent());
      }
      else
      {
        // just pass it up to the parent
        super.propertyChange(evt);
      }
    }
  }




  protected final Color3f getColor()
  {
    return new Color3f(_myShapeWrapper.getColor());
  }

  protected final WorldLocation getLocation()
  {
    return _myShapeWrapper.getLocation();
  }

  protected final String getText()
  {
    return _myShapeWrapper.getLabel();
  }

  protected final boolean visibleAt(HiResDate this_time)
  {
    boolean res = true;

    TimePeriod thePeriod = _myShapeWrapper.getTimePeriod();
    if(thePeriod != null)
    {
      res = thePeriod.contains(this_time);
    }
    return res;
  }

  /** initialise the listening to our host object
   *
   */
  protected final void listenToHost()
  {
    if(_myStepper != null)
      _myStepper.addStepperListener(this);

    // also  listen out for changes in the host
    if(_myShapeWrapper != null)
    {
      _myShapeWrapper.addPropertyChangeListener(PlainWrapper.LOCATION_CHANGED, this);
      _myShapeWrapper.addPropertyChangeListener(PlainWrapper.COLOR_CHANGED, this);
      _myShapeWrapper.addPropertyChangeListener(PlainWrapper.TEXT_CHANGED, this);
      _myShapeWrapper.addPropertyChangeListener(PlainWrapper.VISIBILITY_CHANGED, this);
    }
  }

  /** stop listening to the host - don't forget to call this in the parent!
   *
   */
  public final void doClose()
  {
    super.doClose();

    // and stop listening to the stepper
    if(_myStepper != null)
      _myStepper.removeStepperListener(this);

    // stop listening for property changes in the label
    if(_myShapeWrapper != null)
    {
      _myShapeWrapper.removePropertyChangeListener(PlainWrapper.LOCATION_CHANGED, this);
      _myShapeWrapper.removePropertyChangeListener(PlainWrapper.COLOR_CHANGED, this);
      _myShapeWrapper.removePropertyChangeListener(PlainWrapper.TEXT_CHANGED, this);
      _myShapeWrapper.removePropertyChangeListener(PlainWrapper.VISIBILITY_CHANGED, this);

    }
  }

  ///////////////////////////
  // stepper support
  ///////////////////////////
  public final void steppingModeChanged(boolean on) {
    // ignore this
  }

  public final void newTime(HiResDate oldDTG, HiResDate newDTG, CanvasType canvas) {
    super.updated(newDTG);
  }

  protected final void updateSymbolColor(Color3f theCol) {
    // now update the color of the symbol
    _theGraphic.getAppearance().setColoringAttributes(
        new ColoringAttributes(theCol,
        ColoringAttributes.FASTEST));
  }

  /** set the line width for the shape
   *
   * @param wid the new width to use
   */
  private void updateLineWidth(int wid)
  {
    _theGraphic.getAppearance().setLineAttributes(new LineAttributes(wid,
                                                                     LineAttributes.PATTERN_SOLID,
                                                                     true));
  }

  /** create the graphic used to represent the shape itself (not the text label)
   *
   * @return the shape
   */
  protected final Node createFormattedSymbol()
  {
    // get the graphic object itself
    _theGraphic = createShape(getScaledGeometry(_myShapeWrapper.getShape(), _myWorld), getColor());

    // did it work?
    if(_theGraphic == null)
      return null;

    // configure the shape
    _theGraphic.setCapability(Primitive.ALLOW_AUTO_COMPUTE_BOUNDS_READ);
    _theGraphic.setCapability(Primitive.ENABLE_GEOMETRY_PICKING);
    _theGraphic.setCapability(Primitive.ALLOW_BOUNDS_READ);
    _theGraphic.setCapability(Primitive.ALLOW_AUTO_COMPUTE_BOUNDS_READ);

    // allow the line width to be changed
    _theGraphic.getAppearance().getLineAttributes().setCapability(LineAttributes.ALLOW_WIDTH_WRITE);
    _theGraphic.getAppearance().setCapability(Appearance.ALLOW_LINE_ATTRIBUTES_WRITE);

    _theGraphic.setPickable(false);
    _theGraphic.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
    _theGraphic.getAppearance().setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);

    // store ourselves in it
    _theGraphic.setUserData(this);

    // set the colour
    updateSymbolColor(getColor());

    // and let the world object set the rest of the capabilities
    World.setShapeCapabilities(_theGraphic);

    return _theGraphic;

  }

  /** provide support for adding other components to the billboard, so that they always
   * rotate to face the user
   * @param parent the billboard transform
   */
  protected final void addOtherBillboardComponents(ScaleTransform parent) {
    // don't bother
  }

  /** add any components which we don't want to be billboarded
   *
   * @param parent the parent object
   */
  protected final void addStaticComponents(Group parent) {
    parent.addChild(createFormattedSymbol());
  }


}
