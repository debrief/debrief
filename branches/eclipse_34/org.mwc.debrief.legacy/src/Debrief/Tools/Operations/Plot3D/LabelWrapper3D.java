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
import Debrief.Wrappers.LabelWrapper;
import Debrief.GUI.Tote.StepControl;

import java.beans.PropertyChangeEvent;
import java.util.Collection;

import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.geometry.Primitive;

import javax.vecmath.Color3f;
import javax.media.j3d.*;

public final class LabelWrapper3D extends Label3D implements StepperListener
{
  ///////////////////////////
  // member variables
  ///////////////////////////
  /** the label we are plotting
   *
   */
  private final LabelWrapper _myLabel;


  /** the step control we listen to
   *
   */
  private final StepperController _myStepper;


  /** the symbol for this label
   *
   */
//  protected Sphere _theSymbol;
  private Node _theSymbol;

  ///////////////////////////
  // constructor
  ///////////////////////////
  public LabelWrapper3D(WorldPlottingOptions options, World world, LabelWrapper label, StepperController stepper)
  {
    super(options, world, label);
    this._myLabel = label;

    _myStepper = stepper;

    // now for the building bits
    build();

    // finish off by updating ourselves
    if(stepper != null)
    {
      this.updated(stepper.getCurrentTime());
    }

    // update the symbol visibility
    updateLabelVisibility();

    // and listen out for our label changing visibility
    label.addPropertyChangeListener(LabelWrapper.LABEL_VIS_CHANGED, this);
    label.addPropertyChangeListener(LabelWrapper.SYMBOL_VIS_CHANGED, this);
  }

  ///////////////////////////
  // member methods
  ///////////////////////////


  public final void propertyChange(PropertyChangeEvent evt)
  {
    if(evt.getSource() == _myLabel)
    {
      // see if it's the location!
      if(evt.getPropertyName().equals(PlainWrapper.LOCATION_CHANGED))
      {
        // yup, location it is - do the update
        setLocation();
      }
      else if(evt.getPropertyName().equals(LabelWrapper.TEXT_CHANGED))
      {
        // hey, somebody's changed the text in the label, we'd better update ours!
        setText(getText());
      }
      else if(evt.getPropertyName().equals(PlainWrapper.COLOR_CHANGED))
      {
        // hey, somebody's changed the text in the label, we'd better update ours!
        updateColor();
      }
      else if((evt.getPropertyName().equals(LabelWrapper.SYMBOL_VIS_CHANGED)) ||
             (evt.getPropertyName().equals(LabelWrapper.LABEL_VIS_CHANGED)) ||
             (evt.getPropertyName().equals(PlainWrapper.VISIBILITY_CHANGED)))
      {
        updateLabelVisibility();
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
    }  }

  /** update our visibility depending on whether the label
   * and/or the whole thing are visible
   */
  private void updateLabelVisibility()
  {
    // is the whole thing visible
    if(_myLabel.getVisible())
    {
      // we only show the whole thing if the 2-d symbol is visible
      setVisible(_myLabel.getSymbolVisible());

      // and lastly the label visibility
      setLabelVisible(_myLabel.getLabelVisible());
    }
    else
    {
      // the whole label's hidden, so remove it
      setVisible(false);
    }
  }

  /** property changes, listen out for any coming from the label, else pass them onto the parent,
   * since the are probably WorldOptions related
   */



  protected final Color3f getColor()
  {
    return new Color3f(_myLabel.getColor());
  }

  protected final WorldLocation getLocation()
  {
    return _myLabel.getLocation();
  }

  protected final String getText()
  {
    return _myLabel.getLabel();
  }

  protected final boolean visibleAt(HiResDate this_time)
  {
    boolean res = true;

    // do we have a time period?
    TimePeriod period = _myLabel.getTimePeriod();

    if(period != null)
    {
      // does it include this time?
      res = period.contains(this_time);
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
    if(_myLabel != null)
    {
      _myLabel.addPropertyChangeListener(PlainWrapper.LOCATION_CHANGED, this);
      _myLabel.addPropertyChangeListener(PlainWrapper.COLOR_CHANGED, this);
      _myLabel.addPropertyChangeListener(PlainWrapper.TEXT_CHANGED, this);
      _myLabel.addPropertyChangeListener(LabelWrapper.SYMBOL_VIS_CHANGED, this);
      _myLabel.addPropertyChangeListener(LabelWrapper.LABEL_VIS_CHANGED, this);
      _myLabel.addPropertyChangeListener(PlainWrapper.VISIBILITY_CHANGED, this);
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
    if(_myLabel != null)
    {
      _myLabel.removePropertyChangeListener(PlainWrapper.LOCATION_CHANGED, this);
      _myLabel.removePropertyChangeListener(PlainWrapper.COLOR_CHANGED, this);
      _myLabel.removePropertyChangeListener(PlainWrapper.TEXT_CHANGED, this);
      _myLabel.removePropertyChangeListener(LabelWrapper.SYMBOL_VIS_CHANGED, this);
      _myLabel.removePropertyChangeListener(LabelWrapper.LABEL_VIS_CHANGED, this);
      _myLabel.removePropertyChangeListener(PlainWrapper.VISIBILITY_CHANGED, this);

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
    if(_theSymbol instanceof Sphere)
    {
      Sphere theSphere = (Sphere)_theSymbol;
      // now the color of the symbol
      theSphere.getShape().getAppearance().setColoringAttributes(
        new ColoringAttributes(theCol,
                               ColoringAttributes.FASTEST));
    }
    else if(_theSymbol instanceof Shape3D)
    {

      Shape3D theShape = (Shape3D)_theSymbol;
      // now update the color of the symbol
      theShape.getAppearance().setColoringAttributes(
        new ColoringAttributes(theCol,
                               ColoringAttributes.FASTEST));
    }
  }

  private Node createSphere()
  {
    Sphere theSphere = new Sphere(World.BASE_SIZE, Primitive.GEOMETRY_NOT_SHARED, null);

    // set the colour
    World.setColor(theSphere, getColor());

    theSphere.getShape().setUserData(this);
    theSphere.setCapability(Primitive.ALLOW_AUTO_COMPUTE_BOUNDS_READ);
    theSphere.setCapability(Primitive.ENABLE_GEOMETRY_PICKING);
    theSphere.setCapability(Primitive.ALLOW_BOUNDS_READ);
    theSphere.setCapability(Primitive.ALLOW_AUTO_COMPUTE_BOUNDS_READ);
    theSphere.getShape().setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
    World.setShapeCapabilities(theSphere.getShape());
    theSphere.getShape().setPickable(false);
    return theSphere;
  }

  protected final Node createFormattedSymbol()
  {
    // can we get this symbol as a vector?
    Collection rawLines =_myLabel.getShape().getCoordinates();
    if(rawLines != null)
    {
      // just make it into a shape
      _theSymbol = create2dSymbol(rawLines);
    }
    else
    {
      _theSymbol = createSphere();
    }

    return _theSymbol;
  }

  /** set the line width for the shape
   *
   * @param wid the new width to use
   */
  private void updateLineWidth(int wid)
  {
    if(_theSymbol instanceof Sphere)
    {
      Sphere theSphere = (Sphere)_theSymbol;
      theSphere.getAppearance().setLineAttributes(new LineAttributes(wid,
                                                                       LineAttributes.PATTERN_SOLID,
                                                                       true));
    }
    else
    {
      Shape3D theShape = (Shape3D)_theSymbol;
      theShape.getAppearance().setLineAttributes(new LineAttributes(wid,
                                                                       LineAttributes.PATTERN_SOLID,
                                                                       true));
    }
  }

  /** create the graphic used to represent the shape itself (not the text label)
   *
   * @return the shape
   */
  private Node create2dSymbol(Collection rawLines)
  {

    // get the graphic object itself
    LineArray processedLines = getUnscaledGeometry(rawLines);
    Shape3D theGraphic = createShape(processedLines, getColor());

    // did it work?
    if(theGraphic == null)
      return null;

    // configure the shape
    theGraphic.setCapability(Primitive.ALLOW_AUTO_COMPUTE_BOUNDS_READ);
    theGraphic.setCapability(Primitive.ENABLE_GEOMETRY_PICKING);
    theGraphic.setCapability(Primitive.ALLOW_BOUNDS_READ);
    theGraphic.setCapability(Primitive.ALLOW_AUTO_COMPUTE_BOUNDS_READ);

    // allow the line width to be changed
    theGraphic.getAppearance().getLineAttributes().setCapability(LineAttributes.ALLOW_WIDTH_WRITE);
    theGraphic.getAppearance().setCapability(Appearance.ALLOW_LINE_ATTRIBUTES_WRITE);

    theGraphic.setPickable(false);
    theGraphic.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
    theGraphic.getAppearance().setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);

    // store ourselves in it
    theGraphic.setUserData(this);

    // set the colour
    updateSymbolColor(getColor());

    // and let the world object set the rest of the capabilities
    World.setShapeCapabilities(theGraphic);

    return theGraphic;

  }

  /** provide support for adding other components to the billboard, so that they always
   * rotate to face the user
   * @param parent the billboard transform
   */
  protected final void addOtherBillboardComponents(ScaleTransform parent) {
  }


  /** add any components which we don't want to be billboarded
   *
   * @param parent the parent object
   */
  protected final void addStaticComponents(Group parent) {
      parent.addChild(createFormattedSymbol());
  }

}
