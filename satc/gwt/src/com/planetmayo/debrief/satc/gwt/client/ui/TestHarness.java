/**
 * 
 */
package com.planetmayo.debrief.satc.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.planetmayo.debrief.satc.model.generator.TrackGenerator;
import com.planetmayo.debrief.satc.support.TestSupport;

/**
 * @author Akash-Gupta
 * 
 */
public class TestHarness extends Composite
{

	interface TestHarnessUiBinder extends UiBinder<Widget, TestHarness>
	{
	}

	private static TestHarnessUiBinder uiBinder = GWT
			.create(TestHarnessUiBinder.class);

	@UiField
	Button populate;

	@UiField
	Anchor clear;
	@UiField
	Anchor restart;
	@UiField
	Anchor populateShort;
	@UiField
	Anchor populateLink;
	@UiField
	Anchor step;
	@UiField
	Anchor play;
	@UiField
	Anchor live;
	@UiField
	Anchor one;

	private TrackGenerator _generator;
	private TestSupport _tester;

	public TestHarness()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("clear")
	void clearClick(ClickEvent e)
	{
		_generator.clear();
	}

	@UiHandler("live")
	void liveClick(ClickEvent e)
	{		
		_generator.setLiveRunning(live.getText().equals("Live-"));
		updateLiveLabel();
	}

	@UiHandler("one")
	void oneClick(ClickEvent e)
	{
		_tester.nextTest();
	}

	@UiHandler("play")
	void playClick(ClickEvent e)
	{
		_generator.run();
	}

	@UiHandler("populateLink")
	void populateLinkClick(ClickEvent e)
	{
		_tester.loadSampleData(true);
	}

	@UiHandler("populateShort")
	void populateShortClick(ClickEvent e)
	{
		_tester.loadSampleData(false);
	}

	@UiHandler("restart")
	void restartClick(ClickEvent e)
	{
		_generator.restart();
	}

	public void setGenerator(TrackGenerator genny)
	{
		_generator = genny;
		updateLiveLabel();
	}

	private void updateLiveLabel()
	{
		if(_generator.isLiveEnabled())
			live.setText("Live+");
		else
			live.setText("Live-");
	}

	public void setTestSupport(TestSupport testP)
	{
		_tester = testP;
	}

	@UiHandler("step")
	void stepClick(ClickEvent e)
	{
		_generator.step();
	}

}
