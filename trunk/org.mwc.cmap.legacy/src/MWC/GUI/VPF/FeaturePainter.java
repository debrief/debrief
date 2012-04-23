package MWC.GUI.VPF;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: FeaturePainter.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.4 $
// $Log: FeaturePainter.java,v $
// Revision 1.4  2006/05/25 14:10:40  Ian.Mayo
// Make plottables comparable
//
// Revision 1.3  2006/01/13 15:27:27  Ian.Mayo
// Eclipse tidying, minor mods to improve serialization
//
// Revision 1.2  2004/05/25 15:37:24  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:27  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:48  Ian.Mayo
// Initial import
//
// Revision 1.3  2003-06-11 16:01:02+01  ian_mayo
// Tidy javadoc comments
//
// Revision 1.2  2002-05-28 09:26:04+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:13:59+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:30+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-07-23 11:55:06+01  administrator
// Just use the feature name itself when editing, don't bother with prepending "Feature Painter"
//
// Revision 1.0  2001-07-17 08:42:50+01  administrator
// Initial revision
//
// Revision 1.1  2001-07-16 14:59:19+01  novatech
// Initial revision
//


import java.awt.Color;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.ExcludeFromRightClickEdit;
import MWC.GUI.Plottable;

public class FeaturePainter implements Plottable, Serializable, ExcludeFromRightClickEdit
{

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String _featureType;
  private String _description;
  private boolean _isOn = false;
  private java.awt.Color _myColor = java.awt.Color.white;
  /** our editor
   */
  transient private Editable.EditorType _myEditor;

  public FeaturePainter(String name, String description)
  {
    _featureType = name;
    _description = description;
  }


  public void setVisible(boolean val){ _isOn = val; }
  public boolean getVisible(){ return _isOn; }

  public void setColor(Color val)
  {
    _myColor = val;
  }

  public Color getColor()
  {
    return _myColor;
  }


	public int compareTo(Plottable arg0)
	{
		Plottable other = (Plottable) arg0;
		return this.getName().compareTo(other.getName());
	}
  /** paint this object to the specified canvas
   */
  public void paint(CanvasType dest){}

  /** find the data area occupied by this item
   */
  public MWC.GenericData.WorldArea getBounds(){return null;}

  /** it this item currently visible?
   */
  /** Determine how far away we are from this point.
   * or return null if it can't be calculated
   */
  public double rangeFrom(MWC.GenericData.WorldLocation other){return -1;}

  /** the name of this object
  * @return the name of this editable object
  */
  public String getName(){return _description;}

  public String toString(){return getName();}

  public String getFeatureType(){return _featureType;}

  /** whether there is any edit information for this item
  * this is a convenience function to save creating the EditorType data
  * first
  * @return yes/no
  */
  public boolean hasEditor(){return true;}

  /** get the editor for this item
  * @return the BeanInfo data for this editable object
  */
  public Editable.EditorType getInfo()
  {
    if(_myEditor == null)
      _myEditor = new FeaturePainterInfo(this);

    return _myEditor;
  }
  /////////////////////////////////////////////////////////////
  // info class
  ////////////////////////////////////////////////////////////
  public class FeaturePainterInfo extends Editable.EditorType implements Serializable
  {

    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public FeaturePainterInfo(FeaturePainter data)
    {
      super(data, data.getName(), "");
    }

    public PropertyDescriptor[] getPropertyDescriptors()
    {
      try{
        PropertyDescriptor[] res={
          prop("Color", "the Color to draw this Feature"),
          prop("Visible", "whether this grid is visible"),
        };

        return res;
      }catch(IntrospectionException e){
        return super.getPropertyDescriptors();
      }
    }
  }
}

