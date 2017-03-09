package org.mwc.debrief.core.providers.measured_data;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import junit.framework.TestCase;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesCore;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesDouble;
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
    
    
    final TimeSeriesCore _data;
    
    private transient DatasetWrapperInfo _myEditor = null;

    public DatasetWrapper(final TimeSeriesCore folder)
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
    
    public static class TestSerialize extends TestCase
    {
      public void testSerialise()
      {
        TimeSeriesDouble original =
            new TimeSeriesDouble("Data", "Seconds");

        original.add(12L, 100d);
        original.add(15L, 200d);
        
        DatasetWrapper wrapper = new DatasetWrapper(original);
        
        try
        {
          final java.io.ByteArrayOutputStream bas = new ByteArrayOutputStream();
          final java.io.ObjectOutputStream oos = new ObjectOutputStream(bas);
          oos.writeObject(wrapper);
          // get closure
          oos.close();
          bas.close();

          // now get the item
          final byte[] bt = bas.toByteArray();

          // and read it back in as a new item
          final java.io.ByteArrayInputStream bis = new ByteArrayInputStream(bt);

          // create the reader
          final java.io.ObjectInputStream iis = new ObjectInputStream(bis);

          // and read it in
          final Object oj = iis.readObject();

          // get more closure
          bis.close();
          iis.close();

          DatasetWrapper clone = (DatasetWrapper) oj;

          clone._data.printAll();
        }
        catch (final Exception e)
        {
          e.printStackTrace();
        }
      }

    }

    public TimeSeriesCore getDataset()
    {
      return _data;
    }
  }