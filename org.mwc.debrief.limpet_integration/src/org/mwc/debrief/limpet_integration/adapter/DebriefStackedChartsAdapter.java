package org.mwc.debrief.limpet_integration.adapter;

import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.impl.StackedchartsFactoryImpl;
import info.limpet.stackedcharts.ui.view.adapter.IStackedAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorWrapper;
import MWC.GUI.Editable;
import MWC.GenericData.WatchableList;

public class DebriefStackedChartsAdapter implements IStackedAdapter
{

//  protected Dataset convertDataset(StackedchartsFactoryImpl factory, TemporalQuantityCollection<Quantity> tqc)
//  {
//
//    // get ready to store the data
//    Dataset dataset  = factory.createDataset();
//    dataset.setName(tqc.getName() + "(" + tqc.getUnits() + ")");
//
//    final Unit<Quantity> hisUnits = tqc.getUnits();
//    Iterator<Long> times = tqc.getTimes().iterator();
//    Iterator<?> values = tqc.getValues().iterator();
//    while(times.hasNext())
//    {
//      long thisTime = times.next();
//      @SuppressWarnings("unchecked")
//      Measurable<Quantity> meas = (Measurable<Quantity>) values.next();            
//      DataItem item = factory.createDataItem();
//      item.setIndependentVal(thisTime);
//      Double value = meas.doubleValue(hisUnits);
//      item.setDependentVal(value);
//      
//      // and store it
//      dataset.getMeasurements().add(item);
//    }
//    
//    return dataset;
//  }
  
  @SuppressWarnings({"unused"})
  @Override
  public List<Dataset> convert(Object data)
  {
    List<Dataset> res  = null;
    
    // we should have already checked, but just
    // double-check we can handle it
    if(canConvert(data))
    {
      final StackedchartsFactoryImpl factory = new StackedchartsFactoryImpl();

      // have a look at the type
      if(data instanceof WatchableList)
      {
        WatchableList list = (WatchableList) data;
        
        Dataset dataset = factory.createDataset();
        
        Iterator<Editable> itemIter = list.getItemsBetween(list.getStartDTG(), list.getEndDTG()).iterator();
        while (itemIter.hasNext())
        {
          FixWrapper fix = (FixWrapper) itemIter.next();
          DataItem dataItem = factory.createDataItem();
          dataItem.setIndependentVal(fix.getDateTimeGroup().getDate().getTime());
          dataItem.setDependentVal(fix.getCourseDegs());
          dataset.getMeasurements().add(dataItem);
        }

        if(res == null)
        {
          res = new ArrayList<Dataset>();
          res.add(dataset);
        }
        
        
//        CollectionWrapper cw = (CollectionWrapper) data;
//        ICollection collection = cw.getCollection();
//        if(collection.isQuantity() && collection.isTemporal())
//        {
//          
//          TemporalQuantityCollection<Quantity> qq = (TemporalQuantityCollection<Quantity>) collection;
//          Dataset dataset = convertDataset(factory, qq);
//          // have we got a results object yet?
//          if(res == null)
//          {
//            res = new ArrayList<Dataset>();
//          }
//          
//          // give it some style
//          dataset.setStyling(factory.createPlainStyling());
//              
//          res.add(dataset);
//        }
        
        // now store the data
        // hook up listener
      } 
      else if(data instanceof SensorWrapper)
      {
        SensorWrapper sensor = (SensorWrapper) data;
        
        // and store the data
      }
    }
    
    return res;
  }

  @Override
  public boolean canConvert(Object data)
  {
    boolean res = false;
    
    // have a look at the type
    if(data instanceof WatchableList)
    {
      res = true;
    }
    else if(data instanceof SensorWrapper)
    {
      res = true;
    }
    
    return res;
  }

}
