/**
 * 
 */
package MWC.TacticalData;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.Date;

import MWC.GUI.Editable;
import MWC.GUI.ExcludeFromRightClickEdit;
import MWC.GenericData.HiResDate;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

public final class NarrativeEntry implements java.lang.Comparable,
        MWC.GUI.Plottable, Serializable, ExcludeFromRightClickEdit
{
    // ///////////////////////////////////////////
    // member variables
    // ///////////////////////////////////////////
    private String _track;
    private HiResDate _DTG;
    private String _entry;
    private String _type;
    String _DTGString = null;
    
    private NarrativeEntryInfo _myInfo;

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    // ///////////////////////////////////////////
    // constructor
    // ///////////////////////////////////////////

    /**
     * new constructor - for narrative entries which include the type of entry
     * (typically for SMNT narratives)
     * 
     * @param track
     *            name of the track this applies to
     * @param type
     *            what sort of entry this is (or null)
     * @param DTG
     *            when the entry was recorded
     * @param entry
     *            the content of the entry
     */
    public NarrativeEntry(final String track, final String type,
            final HiResDate DTG, final String entry)
    {
        _track = track;
        _DTG = DTG;
        _entry = entry;
        _type = type;
    }

    /**
     * old constructor - for when narratives didn't include the type attribute
     * 
     * @param track
     *            name of the track this applies to
     * @param DTG
     *            when the entry was recorded
     * @param entry
     *            the content of the entry
     */
    public NarrativeEntry(final String track, final HiResDate DTG,
            final String entry)
    {
        this(track, null, DTG, entry);
    }

    // ///////////////////////////////////////////
    // accessor methods
    // ///////////////////////////////////////////
    public final String getTrackName()
    {
        return _track;
    }

    public final String getSource()
    {
        return _track;
    }
    public final void setSource(String track)
    {
        _track = track;
    }
    
    public final String getEntry()
    {
        return _entry;
    }

    public final HiResDate getDTG()
    {
        return _DTG;
    }
    
    public void setDTG(HiResDate date)
    {
        _DTG = date;
    }

    public final String getType()
    {
        return _type;
    }

    public void setType(String type)
    {
        _type = type;
    }
    
    public final String getDTGString()
    {
        if (_DTGString == null)
            _DTGString = DebriefFormatDateTime.toStringHiRes(_DTG);

        return _DTGString;
    }

    /**
     * member function to meet requirements of comparable interface *
     */
    public final int compareTo(final Object o)
    {
        final NarrativeEntry other = (NarrativeEntry) o;
        int res = 0;

        // are we looking at the same item?
        if (o.equals(this))
        {
            res = 0;
        }
        else if (_DTG.lessThan(other._DTG))
            res = -1;
        else
            res = 1;

        return res;

    }

    // ///////////////////////////////////////////
    // member methods to meet requirements of Plottable interface
    // ///////////////////////////////////////////

    /**
     * paint this object to the specified canvas
     */
    public final void paint(final MWC.GUI.CanvasType dest)
    {
    }

    /**
     * find the data area occupied by this item
     */
    public final MWC.GenericData.WorldArea getBounds()
    {
        return null;
    }

    /**
     * it this item currently visible?
     */
    public final boolean getVisible()
    {
        return true;
    }

    /**
     * set the visibility (although we ignore this)
     */
    public final void setVisible(final boolean val)
    {
    }

    /**
     * how far away are we from this point? or return null if it can't be
     * calculated
     */
    public final double rangeFrom(final MWC.GenericData.WorldLocation other)
    {
        return -1;
    }

    /**
     * get the editor for this item
     * 
     * @return the BeanInfo data for this editable object
     */
    public final MWC.GUI.Editable.EditorType getInfo()
    {
        if(_myInfo == null)
            _myInfo = new NarrativeEntryInfo(this, this.toString());
        return _myInfo;
    }

    /**
     * whether there is any edit information for this item this is a convenience
     * function to save creating the EditorType data first
     * 
     * @return yes/no
     */
    public final boolean hasEditor()
    {
        return true;
    }

    /**
     * get the name of this entry, using the formatted DTG
     */
    public final String getName()
    {
        return DebriefFormatDateTime.toStringHiRes(_DTG);
    }

    public final String toString()
    {
        return getName();
    }
    
    // ////////////////////////////////////////////////////
    // bean info for this class
    // ///////////////////////////////////////////////////
    public final class NarrativeEntryInfo extends Editable.EditorType
    {

        public NarrativeEntryInfo(final NarrativeEntry data, final String theName)
        {
            super(data, theName, data.toString());
        }

        public final PropertyDescriptor[] getPropertyDescriptors()
        {
            try
            {
                final PropertyDescriptor[] myRes = {
                        
                        prop("Type", "the type of entry", FORMAT),
                        prop("Source", "the source for this entry", FORMAT),
                        prop("DTG", "the time this entry was recorded", FORMAT),
                        };

                return myRes;

            }
            catch (IntrospectionException e)
            {
                e.printStackTrace();
                return super.getPropertyDescriptors();
            }
        }

    }    

 // ////////////////////////////////////////////////////////////////////////////////////////////////
    // testing for this class
    // ////////////////////////////////////////////////////////////////////////////////////////////////
    static public final class testMe extends junit.framework.TestCase
    {
        static public final String TEST_ALL_TEST_TYPE = "UNIT";

        public testMe(final String val)
        {
            super(val);
        }

        public final void testMyParams()
        {
            HiResDate hd = new HiResDate(new Date());
            final NarrativeEntry ne = new NarrativeEntry("aaa", "bbb", hd, "vvvv"); 
            editableTesterSupport.testParams(ne, this);
        }
    }

}