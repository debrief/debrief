package ASSET.Models.Vessels.Radiated;

import ASSET.Models.Environment.CoreEnvironment;
import ASSET.Models.MWCModel;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 *
 * @author
 * @version 1.0
 */

public class RadiatedCharacteristics implements MWC.GUI.Editable, java.io.Serializable
{

  //////////////////////////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////////////////////////

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * the set of mediums we know about
   */
  private Medium[] _myMediums = new Medium[CoreEnvironment.MAX_NUM_MEDIUMS];

  /**
   * the editor for radiated noise levels
   */
  private MWC.GUI.Editable.EditorType _myEditor = null;

  /**
   * the type of noise we represent
   */
  private String _myNoiseType;

  //////////////////////////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////////////////////////

  public RadiatedCharacteristics()
  {
    this("Radiated Noise");
  }

  RadiatedCharacteristics(final String type)
  {
    _myNoiseType = type;
  }

  //////////////////////////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////////////////////////

  public void add(final int index, final Medium medium)
  {
    // try to remove any existing medium
    _myMediums[index] = medium;
  }

  public boolean radiatesThis(final int medium)
  {
    // see if the vessel itself radiates this
    boolean vesselRadiates = _myMediums[medium] != null;

    // and return it
    return vesselRadiates;
  }

  public double radiatedEnergyFor(final int medium, final ASSET.Participants.Status status,
                                  final double absBearingDegs)
  {
    double res = 0;

    if (radiatesThis(medium))
    {
      final Medium thisMed = (Medium) _myMediums[medium];
      if (thisMed != null)
        res = thisMed.radiatedEnergyFor(status, absBearingDegs);
    }
    else
      res = 0;

    return res;
  }

  public java.util.Collection<Medium> getMediums()
  {
    Vector<Medium> res = new Vector<Medium>();
    for (int i = 0; i < _myMediums.length; i++)
    {
      Medium medium = _myMediums[i];
      if(medium != null)
        res.add(medium);
    }
    return res;
  }

  public Medium getMedium(final Integer index)
  {
    return _myMediums[index.intValue()];
  }


  /****************************************************
   * editor support
   ***************************************************/
  /**
   * the name of this object
   *
   * @return the name of this editable object
   */
  public String getName()
  {
    return _myNoiseType;
  }

  public void setName(String name)
  {
    _myNoiseType = name;
  }

  public String toString()
  {
    return getName();
  }

  /**
   * whether there is any edit information for this item
   * this is a convenience function to save creating the EditorType data
   * first
   *
   * @return yes/no
   */
  public boolean hasEditor()
  {
    return true;
  }

  /**
   * get the editor for this item
   *
   * @return the BeanInfo data for this editable object
   */
  public Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new RadiatedInfo(this);

    return _myEditor;
  }


  /**
   * *************************************************
   * radiated noise levels
   * *************************************************
   */
  static public class RadiatedInfo extends MWC.GUI.Editable.EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public RadiatedInfo(final RadiatedCharacteristics data)
    {
      super(data, data.getName(), data.getName());
    }

    /**
     * editable GUI properties for our participant
     *
     * @return property descriptions
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res = {
          prop("Name", "the name of this set of radiated characteristics"),
        };
        return res;
      }
      catch (IntrospectionException e)
      {
        e.printStackTrace();
        return super.getPropertyDescriptors();
      }
    }

    /**
     * return a description of this bean, also specifies the custom editor we use
     *
     * @return the BeanDescriptor
     */
    public java.beans.BeanDescriptor getBeanDescriptor()
    {
      final java.beans.BeanDescriptor bp = new java.beans.BeanDescriptor(RadiatedCharacteristics.class,
                                                                         ASSET.GUI.Editors.RadiatedNoiseViewer.class);
      bp.setDisplayName(super.getData().toString());
      return bp;
    }
  }


  //////////////////////////////////////////////////////////////////////
  // interface for mediums we handle
  //////////////////////////////////////////////////////////////////////
  static public interface Medium extends MWCModel
  {
    public double radiatedEnergyFor(ASSET.Participants.Status status, double absBearingDegs);

    public double reflectedEnergyFor(ASSET.Participants.Status status, double absBearingDegs);
    
    public String getName();
  }

  static public class NoiseComponent
  {
    ////////////////////////////////////////////////////
    // member components
    ////////////////////////////////////////////////////
    private double _bearing;
    private double _speed;
    private double _value;

    ////////////////////////////////////////////////////
    // constructor
    ////////////////////////////////////////////////////


    /**
     * constructor for a noise component
     *
     * @param bearing - the relative bearing (+/- 180 degs)
     * @param speed   - the speed at which this datum was recorded (kts)
     * @param value   - the radiated noise (medium-specific units)
     */
    public NoiseComponent(final double bearing, final double speed, final double value)
    {
      _bearing = bearing;
      _speed = speed;
      _value = value;
    }


    /**
     * @return the relative bearing (+/- 180 degs)
     */
    public double getBearing()
    {
      return _bearing;
    }

    /**
     * @return the speed at which this datum was recorded (kts)
     */
    public double getSpeed()
    {
      return _speed;
    }

    /**
     * radiated noise value
     *
     * @return the radiated noise (medium-specific units)
     */
    public double getValue()
    {
      return _value;
    }
  }

  ////////////////////////////////////////////////////
  // class which builds up a list of noise components, and is able to interpolate between them
  // todo - create import/export handlers for these.
  ////////////////////////////////////////////////////
  public static class NoiseSignature extends TreeSet<BearingSet>
  {
    ////////////////////////////////////////////////////
    // member objects
    ////////////////////////////////////////////////////

    ////////////////////////////////////////////////////
    // constructor
    ////////////////////////////////////////////////////


    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public NoiseSignature()
    {
      super(new Comparator<BearingSet>()
      {
        public int compare(final BearingSet first, final BearingSet second)
        {
          int res = 0;
          if (first.getBearing() < second.getBearing())
            res = -1;
          else if (first.getBearing() > second.getBearing())
            res = 1;
          else
            res = 0;

          return res;
        }
      });
    }

    ////////////////////////////////////////////////////
    // member methods
    ////////////////////////////////////////////////////

    public double getValueFor(final double bearing, final double speed)
    {

      final Iterator<BearingSet> it = this.iterator();
      BearingSet nearestBearing = null;
      double nearestDelta = -1;
      while (it.hasNext())
      {
        final BearingSet thisN = (BearingSet) it.next();

        // how far is this data value from our required one?
        final double thisDelta = Math.abs(thisN.getBearing() - bearing);

        // just initialise our values
        if (nearestBearing == null)
        {
          nearestBearing = thisN;
          nearestDelta = thisDelta;
        }
        else
        {
          // see if this delta is nearer than our existing one
          if (thisDelta < nearestDelta)
          {
            nearestDelta = thisDelta;
            nearestBearing = thisN;
          }
          else
          {
            // we must have passed it, drop out
            break;
          }
        }
      }

      // get the value from this list
      final double res = nearestBearing.valueFor(speed);

      // done
      return res;

    }

    /**
     * add the indicated noise component to our data
     */
    public void addComponent(final NoiseComponent component)
    {

      BearingSet thisList = null;

      // retrieve the bearing
      final double brg = component.getBearing();

      // find out if we already store data for this bearing
      final Iterator<BearingSet> it = this.iterator();
      while (it.hasNext())
      {
        final BearingSet bs = (BearingSet) it.next();
        if (bs.getBearing() == brg)
        {
          thisList = bs;
          break;
        }
      }

      // did we find it?
      if (thisList == null)
      {
        thisList = new BearingSet(brg);

        // put this list into the big object
        this.add(thisList);
      }

      // now add the component
      thisList.add(component);
    }
  }

  /**
   * class which stores a list of noise components for a particular bearing
   */
  private static class BearingSet extends TreeSet<NoiseComponent>
  {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		/**
     * the bearing which this set is for
     */
    private double _myBearing;

    ////////////////////////////////////////////////////
    // constructor
    ////////////////////////////////////////////////////

    public BearingSet(final double myBearing)
    {
      super(new Comparator<NoiseComponent>()
      {
        /**
         * compare the speeds of the supplied items
         */
        public int compare(final NoiseComponent first, final NoiseComponent second)
        {
          int res = 0;
          if (first.getSpeed() < second.getSpeed())
            res = -1;
          else if (first.getSpeed() > second.getSpeed())
            res = 1;
          else
            res = 0;

          return res;
        }
      });

      _myBearing = myBearing;
    }

    public double valueFor(final double speed)
    {
      // find the noise value at this particular speed
      double res = -1;

      final Iterator<NoiseComponent> it = this.iterator();
      NoiseComponent before = null;
      NoiseComponent after = null;
      while (it.hasNext())
      {
        final NoiseComponent thisN = (NoiseComponent) it.next();

        // just initialise our values
        if (before == null)
        {
          before = thisN;
        }

        // we've already got some data, see if we've passed our required value yet
        if (thisN.getSpeed() >= speed)
        {
          // yes, we've passed what we're looking for, and we have components
          // either side of it
          after = thisN;
          break;
        }
        else
        {
          // no, we still haven't reached the required speed, this value
          // becomes the "before" value
          before = thisN;
        }
      }

      // just check we found a value (to handle where the requested speed is
      // greater than those supplied
      if (after == null)
      {
        after = before;
      }

      // do the interpolation
      res = interpolateBetween(before, after, speed);

      return res;
    }

    static double interpolateBetween(final NoiseComponent before,
                                             final NoiseComponent after,
                                             final double atSpeed)
    {
      double res = 0;

      // just check if we have the same noise component twice
      if (before == after)
      {
        res = before.getValue();
      }
      else
      {
        // we'll have to interpolate
        // find the value delta
        final double vDelta = after.getValue() - before.getValue();

        // find the speed delta
        final double sDelta = after.getSpeed() - before.getSpeed();

        final double proportion = (atSpeed - before.getSpeed()) / sDelta;

        res = before.getValue() + proportion * vDelta;

      }

      return res;
    }

    /**
     * retrieve the bearing for this set of data
     *
     * @return the bearing for this data
     */
    public double getBearing()
    {
      return _myBearing;
    }
  }


  ////////////////////////////////////////////////////
  // testing for this class
  ////////////////////////////////////////////////////
  public static class RadCharsTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public RadCharsTest(final String val)
    {
      super(val);
    }

    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      return new RadiatedCharacteristics("h334");
    }

    /**
     * test the string swapping
     */
    public void testInterpolation()
    {
      final NoiseComponent nc = new NoiseComponent(0, 4, 100);
      final NoiseComponent n2 = new NoiseComponent(0, 8, 200);
      double res = BearingSet.interpolateBetween(nc, n2, 6);
      assertEquals("correct interpolated value", res, 150d, 0.001);
      res = BearingSet.interpolateBetween(nc, n2, 7);
      assertEquals("correct interpolated value", res, 175d, 0.001);
      res = BearingSet.interpolateBetween(nc, n2, 4);
      assertEquals("correct interpolated value", res, 100d, 0.001);
      res = BearingSet.interpolateBetween(nc, n2, 8);
      assertEquals("correct interpolated value", res, 200d, 0.001);
      res = BearingSet.interpolateBetween(nc, nc, 2);
      assertEquals("correct interpolated value", res, 100d, 0.001);
      res = BearingSet.interpolateBetween(n2, n2, 10);
      assertEquals("correct interpolated value", res, 200d, 0.001);
    }

    /**
     * test the list management
     */
    public void testBearingSet()
    {
      // create a few noise components
      final NoiseComponent nc = new NoiseComponent(0, 4, 100);
      final NoiseComponent n2 = new NoiseComponent(0, 8, 200);
      final NoiseComponent n3 = new NoiseComponent(0, 16, 300);
      final NoiseComponent n4 = new NoiseComponent(0, 24, 600);

      final BearingSet bs = new BearingSet(22);
      bs.add(nc);
      bs.add(n2);
      bs.add(n3);
      bs.add(n4);

      double res = bs.valueFor(8);
      assertEquals("found correct value", 200, res, 0.001);
      res = bs.valueFor(4);
      assertEquals("found correct value", 100, res, 0.001);
      res = bs.valueFor(2);
      assertEquals("found correct value", 100, res, 0.001);
      res = bs.valueFor(16);
      assertEquals("found correct value", 300, res, 0.001);
      res = bs.valueFor(20);
      assertEquals("found correct value", 450, res, 0.001);
      res = bs.valueFor(24);
      assertEquals("found correct value", 600, res, 0.001);
      res = bs.valueFor(28);
      assertEquals("found correct value", 600, res, 0.001);
    }

    public void testNoiseSignature()
    {
      final NoiseSignature ns = new NoiseSignature();

      // create a few noise components
      final NoiseComponent nc = new NoiseComponent(0, 4, 100);
      final NoiseComponent n2 = new NoiseComponent(0, 8, 200);
      final NoiseComponent n3 = new NoiseComponent(0, 16, 300);
      final NoiseComponent n4 = new NoiseComponent(0, 24, 500);

      final NoiseComponent pc = new NoiseComponent(90, 4, 1000);
      final NoiseComponent p2 = new NoiseComponent(90, 8, 2000);
      final NoiseComponent p3 = new NoiseComponent(90, 16, 3000);
      final NoiseComponent p4 = new NoiseComponent(90, 24, 5000);

      final NoiseComponent qc = new NoiseComponent(160, 4, 10000);
      final NoiseComponent q2 = new NoiseComponent(160, 8, 20000);
      final NoiseComponent q3 = new NoiseComponent(160, 16, 30000);
      final NoiseComponent q4 = new NoiseComponent(160, 24, 50000);

      ns.addComponent(nc);
      ns.addComponent(n2);
      ns.addComponent(n3);
      ns.addComponent(n4);
      ns.addComponent(pc);
      ns.addComponent(p2);
      ns.addComponent(p3);
      ns.addComponent(p4);
      ns.addComponent(qc);
      ns.addComponent(q2);
      ns.addComponent(q3);
      ns.addComponent(q4);

      // see how we get on
      double res = ns.getValueFor(90, 16);
      assertEquals("found speed for existing bearing", res, 3000, 0.001);
      res = ns.getValueFor(90, 2);
      assertEquals("found speed for existing bearing", res, 1000, 0.001);
      res = ns.getValueFor(90, 24);
      assertEquals("found speed for existing bearing", res, 5000, 0.001);
      res = ns.getValueFor(90, 28);
      assertEquals("found speed for existing bearing", res, 5000, 0.001);
      res = ns.getValueFor(0, 2);
      assertEquals("found speed for existing bearing", res, 100, 0.001);
      res = ns.getValueFor(0, 28);
      assertEquals("found speed for existing bearing", res, 500, 0.001);
      res = ns.getValueFor(160, 2);
      assertEquals("found speed for existing bearing", res, 10000, 0.001);
      res = ns.getValueFor(160, 28);
      assertEquals("found speed for existing bearing", res, 50000, 0.001);
      res = ns.getValueFor(180, 2);
      assertEquals("found speed for existing bearing", res, 10000, 0.001);
      res = ns.getValueFor(180, 28);
      assertEquals("found speed for existing bearing", res, 50000, 0.001);
      res = ns.getValueFor(30, 2);
      assertEquals("found speed for existing bearing", res, 100, 0.001);
      res = ns.getValueFor(30, 24);
      assertEquals("found speed for existing bearing", res, 500, 0.001);
      res = ns.getValueFor(60, 2);
      assertEquals("found speed for existing bearing", res, 1000, 0.001);
      res = ns.getValueFor(60, 24);
      assertEquals("found speed for existing bearing", res, 5000, 0.001);

    }
  }

}