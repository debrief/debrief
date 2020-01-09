package org.mwc.debrief.lite.custom;

import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;

import org.mwc.debrief.lite.gui.custom.RangeSlider;
import org.pushingpixels.flamingo.api.common.projection.Projection;
import org.pushingpixels.flamingo.api.ribbon.synapse.model.ComponentPresentationModel;

public class JRibbonRangeSlider extends RangeSlider 
{
  
  Projection<JRibbonRangeSlider,SliderComponentContentModel,ComponentPresentationModel> projection;
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  public JRibbonRangeSlider(Projection<JRibbonRangeSlider,
      SliderComponentContentModel, ComponentPresentationModel> projection) {
    this.projection=projection;
    setSize(new Dimension(250,40));
    setBackground(new Color(180,180,230));
    initialize(projection);
  }
  
  public void setMaximum(int maximum)
  {
    projection.getContentModel().setMaximum(maximum);
    getModel().setMaximum(maximum);
  }
  
  public void setMinimum(int min)
  {
    projection.getContentModel().setMinimum(min);
    getModel().setMinimum(min);
  }
  
  
  public void initialize(Projection<JRibbonRangeSlider,
      SliderComponentContentModel, ComponentPresentationModel> projection)
  {
    final SliderComponentContentModel contentModel = projection.getContentModel();
    setValue(contentModel.getValue());
    setEnabled(contentModel.isEnabled());
    setMaximum(contentModel.getMaximum());
    setMinimum(contentModel.getMinimum());

    addChangeListener((ChangeEvent ae) -> {
      contentModel.setValue(((JSlider)ae.getSource()).getValue());
      contentModel.setMinimum(((JSlider)ae.getSource()).getMinimum());
      contentModel.setMaximum(((JSlider)ae.getSource()).getMaximum());
      if (contentModel.getChangeListener() != null) {
        contentModel.getChangeListener().stateChanged(ae);
      }
    });

    contentModel.addPropertyChangeListener((PropertyChangeEvent event) -> {
      if ("value".equals(event.getPropertyName())) {
        setValue(contentModel.getValue());
      }
      if ("maximum".equals(event.getPropertyName())) {
        setMaximum(contentModel.getMaximum());
        setUpperValue(contentModel.getMaximum());
        
      }
      if ("minimum".equals(event.getPropertyName())) {
        setMinimum(contentModel.getMinimum());
      }
    });
  }

}
