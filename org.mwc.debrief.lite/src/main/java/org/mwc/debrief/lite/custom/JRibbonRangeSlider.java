package org.mwc.debrief.lite.custom;

import java.beans.PropertyChangeEvent;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;

import org.mwc.debrief.lite.gui.custom.LabelledRangeSlider;
import org.pushingpixels.flamingo.api.common.projection.Projection;
import org.pushingpixels.flamingo.api.ribbon.synapse.model.ComponentPresentationModel;

public class JRibbonRangeSlider extends LabelledRangeSlider 
{
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  public JRibbonRangeSlider(Projection<JRibbonRangeSlider,
      SliderComponentContentModel, ComponentPresentationModel> projection) {
    initialize(projection);
  }
  
  
  
  public void initialize(Projection<JRibbonRangeSlider,
      SliderComponentContentModel, ComponentPresentationModel> projection)
  {
    final SliderComponentContentModel contentModel = projection.getContentModel();
    this.getRangeSlider().setValue(contentModel.getValue());
    this.getRangeSlider().setEnabled(contentModel.isEnabled());

    this.getRangeSlider().addChangeListener((ChangeEvent ae) -> {
      //TODO is this correct logic here?
      contentModel.setValue(((JSlider)ae.getSource()).getValue());
      if (contentModel.getChangeListener() != null) {
        contentModel.getChangeListener().stateChanged(ae);
      }
    });

    contentModel.addPropertyChangeListener((PropertyChangeEvent event) -> {
      if ("value".equals(event.getPropertyName())) {
        this.getRangeSlider().setValue(contentModel.getValue());
      }
    });
  }

}
