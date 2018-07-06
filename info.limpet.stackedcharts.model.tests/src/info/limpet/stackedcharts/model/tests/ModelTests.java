package info.limpet.stackedcharts.model.tests;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Assert;
import org.junit.Test;

import info.limpet.stackedcharts.model.AxisDirection;
import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.Datum;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.IndependentAxis;
import info.limpet.stackedcharts.model.Marker;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.model.SelectiveAnnotation;
import info.limpet.stackedcharts.model.StackedchartsFactory;
import info.limpet.stackedcharts.model.StackedchartsPackage;
import info.limpet.stackedcharts.model.Zone;

/**
 * A JUnit Plug-in Test to demonstrate basic EMF operations, such as model manipulaton, persistnce
 * and event handling
 */
public class ModelTests
{

  @Test
  public void testReadModel()
  {
    URI resourceURI = URI.createFileURI("testRead.stackedcharts");
    Resource resource = new ResourceSetImpl().createResource(resourceURI);
    try
    {
      resource.load(new HashMap<>());
    }
    catch (IOException e)
    {
      e.printStackTrace();
      Assert.fail("Could not read model: " + e.getMessage());
    }
    ChartSet chartsSet = (ChartSet) resource.getContents().get(0);

    Assert.assertNotNull(chartsSet);
    Assert.assertEquals(2, chartsSet.getCharts().size());

    Chart chart = chartsSet.getCharts().get(0);
    Assert.assertEquals("Temperature & Salinity", chart.getName());
    
    // have a look at the innads
    EList<DependentAxis> axes = chart.getMinAxes();
    Assert.assertEquals("Correct number",  1, axes.size());
    DependentAxis axis1 = axes.get(0);
    Assert.assertEquals("correct name", "Temperature", axis1.getName());
    Assert.assertEquals("correct direction", AxisDirection.ASCENDING, axis1.getDirection());
    axes = chart.getMaxAxes();
    DependentAxis axis2 = axes.get(0);
    Assert.assertEquals("correct name", "Salinity", axis2.getName());
  }

  @Test
  public void testWriteModel()
  {
    ChartSet chartsSet = createModel();
    URI resourceURI = URI.createFileURI("testWrite.stackedcharts");
    Resource resource = new ResourceSetImpl().createResource(resourceURI);
    resource.getContents().add(chartsSet);
    try
    {
      resource.save(null);
    }
    catch (IOException e)
    {
      e.printStackTrace();
      Assert.fail("Could not write model: " + e.getMessage());
    }
  }

  @Test
  public void testNotifications()
  {
    Chart chart = createModel().getCharts().get(0);

    ChartNameChangeListener listener = new ChartNameChangeListener();
    chart.eAdapters().add(listener);

    Assert.assertFalse(listener.isNotified());

    chart.setName("Changed");
    Assert.assertTrue(listener.isNotified());
  }

  private ChartSet createModel()
  {
    StackedchartsFactory factory = StackedchartsFactory.eINSTANCE;

    ChartSet chartsSet = factory.createChartSet();

    // set the common x axis
    IndependentAxis depthAxis = factory.createIndependentAxis();
    depthAxis.setName("Depth");
    chartsSet.setSharedAxis(depthAxis);

    // first chart
    Chart tempChart = factory.createChart();
    tempChart.setName("Temperature & Salinity");
    chartsSet.getCharts().add(tempChart);

    DependentAxis yAxis1 = factory.createDependentAxis();
    yAxis1.setName("Temperature");
    tempChart.getMinAxes().add(yAxis1);

    Dataset temperatureVsDepth1 = factory.createDataset();
    temperatureVsDepth1.setName("Temp vs Depth");
    yAxis1.getDatasets().add(temperatureVsDepth1);

    DataItem item1 = factory.createDataItem();
    item1.setIndependentVal(1000);
    item1.setDependentVal(30);
    temperatureVsDepth1.getMeasurements().add(item1);

    item1 = factory.createDataItem();
    item1.setIndependentVal(2000);
    item1.setDependentVal(50);
    temperatureVsDepth1.getMeasurements().add(item1);

    item1 = factory.createDataItem();
    item1.setIndependentVal(3000);
    item1.setDependentVal(60);
    temperatureVsDepth1.getMeasurements().add(item1);
    
    // second axis/dataset on this first graph
    DependentAxis yAxis2 = factory.createDependentAxis();
    yAxis2.setName("Salinity");
    tempChart.getMaxAxes().add(yAxis2);

    Dataset salinityVsDepth1 = factory.createDataset();
    salinityVsDepth1.setName("Salinity Vs Depth");
    yAxis2.getDatasets().add(salinityVsDepth1);

    item1 = factory.createDataItem();
    item1.setIndependentVal(1000);
    item1.setDependentVal(3000);
    salinityVsDepth1.getMeasurements().add(item1);

    item1 = factory.createDataItem();
    item1.setIndependentVal(2000);
    item1.setDependentVal(5000);
    salinityVsDepth1.getMeasurements().add(item1);

    item1 = factory.createDataItem();
    item1.setIndependentVal(3000);
    item1.setDependentVal(9000);
    salinityVsDepth1.getMeasurements().add(item1);
    
    // create a second chart
    // first chart
    Chart pressureChart = factory.createChart();
    pressureChart.setName("Pressure Gradient");
    chartsSet.getCharts().add(pressureChart);
    
    // have a go at an annotation on the x axis
    IndependentAxis shared = chartsSet.getSharedAxis();
    Marker marker = factory.createMarker();
    marker.setValue(1200);
    marker.setName("A marker");
    SelectiveAnnotation sel = factory.createSelectiveAnnotation();
    sel.setAnnotation(marker);
    shared.getAnnotations().add(sel);
    Zone zone = factory.createZone();
    zone.setStart(2100);
    zone.setEnd(2500);
    zone.setName("A Zone");    
    sel = factory.createSelectiveAnnotation();
    sel.setAnnotation(zone);
    shared.getAnnotations().add(sel);

    DependentAxis pressureAxis = factory.createDependentAxis();
    pressureAxis.setName("Pressure");
    pressureChart.getMinAxes().add(pressureAxis);

    Dataset pressureVsDepth = factory.createDataset();
    pressureVsDepth.setName("Pressure vs Depth");
    pressureAxis.getDatasets().add(pressureVsDepth);

    DataItem item = factory.createDataItem();
    item.setIndependentVal(1000);
    item.setDependentVal(400);
    pressureVsDepth.getMeasurements().add(item);

    item = factory.createDataItem();
    item.setIndependentVal(2000);
    item.setDependentVal(500);
    pressureVsDepth.getMeasurements().add(item);

    item = factory.createDataItem();
    item.setIndependentVal(3000);
    item.setDependentVal(100);
    pressureVsDepth.getMeasurements().add(item);
    
    // have a go at a scatter set
    ScatterSet scatter = factory.createScatterSet();
    scatter.setName("Pressure Markers");
    Datum datum = factory.createDatum();
    datum.setVal(1650d);
    scatter.getDatums().add(datum);
    datum = factory.createDatum();
    datum.setVal(1700d);
    scatter.getDatums().add(datum);
    datum = factory.createDatum();
    datum.setVal(1720d);
    scatter.getDatums().add(datum);
    datum = factory.createDatum();
    datum.setVal(1790d);
    scatter.getDatums().add(datum);    
    SelectiveAnnotation sa = factory.createSelectiveAnnotation();
    sa.setAnnotation(scatter);
    sa.getAppearsIn().add(pressureChart);
    chartsSet.getSharedAxis().getAnnotations().add(sa);
    
    // and another one
    scatter = factory.createScatterSet();
    scatter.setName("Common Markers");
    datum = factory.createDatum();
    datum.setVal(650d);
    scatter.getDatums().add(datum);
    datum = factory.createDatum();
    datum.setVal(700d);
    scatter.getDatums().add(datum);
    datum = factory.createDatum();
    datum.setVal(720d);
    scatter.getDatums().add(datum);
    datum = factory.createDatum();
    datum.setVal(790d);
    scatter.getDatums().add(datum);    
    sa = factory.createSelectiveAnnotation();
    sa.setAnnotation(scatter);
    chartsSet.getSharedAxis().getAnnotations().add(sa);
    
    // oh, try markers on the dependent axis
    ScatterSet pScatter = factory.createScatterSet();
    datum = factory.createDatum();
    datum.setVal(100d);
    pScatter.getDatums().add(datum);
    datum = factory.createDatum();
    datum.setVal(500d);
    pScatter.getDatums().add(datum);
    pressureAxis.getAnnotations().add(pScatter);

    // hey, how about a zone on the dependent axis?
    Zone pZone = factory.createZone();
    pZone.setStart(380);
    pZone.setEnd(450);
    pZone.setColor(Color.yellow);
    pZone.setName("Pixel Zone");
    pressureAxis.getAnnotations().add(pZone);
    

    return chartsSet;
  }

  /**
   * Helper class to test notifications
   */
  private static class ChartNameChangeListener implements Adapter
  {

    private Notifier notifier;
    private boolean notified;

    @Override
    public void notifyChanged(Notification notification)
    {
      int featureId = notification.getFeatureID(StackedchartsPackage.class);
      switch (featureId)
      {
      case StackedchartsPackage.CHART__NAME:
        notified = true;
      }
    }

    @Override
    public Notifier getTarget()
    {
      return notifier;
    }

    @Override
    public void setTarget(Notifier newTarget)
    {
      this.notifier = newTarget;
    }

    @Override
    public boolean isAdapterForType(Object type)
    {
      return ChartSet.class.equals(type);
    }

    public boolean isNotified()
    {
      return notified;
    }

  }

}
