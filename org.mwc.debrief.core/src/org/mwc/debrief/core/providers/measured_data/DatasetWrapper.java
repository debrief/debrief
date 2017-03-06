package org.mwc.debrief.core.providers.measured_data;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;

import Debrief.Wrappers.Extensions.Measurements.CoreDataset;
import MWC.GUI.Editable;

public class DatasetWrapper implements Editable, Serializable
  {
    
 /**
     * 
     */
    private static final long serialVersionUID = 1L;

// ////////////////////////////////////////////////////
    // bean info for this class
    // ///////////////////////////////////////////////////
    public final class DatasetWrapperInfo extends Editable.EditorType
    {

      public DatasetWrapperInfo(final DatasetWrapper data, final String theName)
      {
        super(data, theName, data.toString());
      }

      @Override
      public final PropertyDescriptor[] getPropertyDescriptors()
      {
        try
        {
          final PropertyDescriptor[] myRes = {
              displayProp("ItemCount", "Number of items", "Number of items in this dataset", TEMPORAL),
              displayProp("Units", "Units for data", "Units for this dataset", TEMPORAL)
              };

          return myRes;
        }
        catch (final IntrospectionException e)
        {
          e.printStackTrace();
          return super.getPropertyDescriptors();
        }
      }
    }
    
    
    final CoreDataset<?, ?> _data;
    
    private transient DatasetWrapperInfo _myEditor = null;

    public DatasetWrapper(final CoreDataset<?, ?> folder)
    {
      _data = folder;
    }
    
    public String getItemCount()
    {
      return "" + _data.size();
    }
    
    public void setItemCount(String val)
    {
      // ignore
    }
    
    public String getUnits()
    {
      return _data.getUnits();
    }
    
    public void setUnits(String val)
    {
      // ignore
    }


    @Override
    public EditorType getInfo()
    {
      if (_myEditor == null)
        _myEditor  = new DatasetWrapperInfo(this, this.getName());

      return _myEditor;
    }

    @Override
    public String getName()
    {
      return _data.getName();
    }

    @Override
    public boolean hasEditor()
    {
      return true;
    }

    @Override
    public String toString()
    {
      return getName() + " (" + _data.size() + " items)";
    }
  }