/**
 * 
 */
package com.planetmayo.debrief.satc.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.planetmayo.debrief.satc.model.generator.TrackGenerator;
import com.planetmayo.debrief.satc.support.TestSupport;

/**
 * @author Akash-Gupta
 * 
 */
public class TestHarness extends Composite {

	private static TestHarnessUiBinder uiBinder = GWT
			.create(TestHarnessUiBinder.class);

	interface TestHarnessUiBinder extends UiBinder<Widget, TestHarness> {
	}
	
	@UiField Button populate;
	
	private TrackGenerator _generator;
	private TestSupport _tester;

	public TestHarness() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setGenerator(TrackGenerator genny)
	{
		_generator = genny;
	}

  @UiHandler("populate")
  void handleClick(ClickEvent e) {
  	_tester.loadTinyData();
  }
  
	public void setTestSupport(TestSupport testP)
	{
		_tester = testP;
	}

}
