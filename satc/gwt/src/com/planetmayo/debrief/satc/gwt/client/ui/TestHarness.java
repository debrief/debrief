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
import com.planetmayo.debrief.satc.model.generator.SteppingGenerator;
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

	private SteppingGenerator _generator;
	private TestSupport _tester;

	public TestHarness() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setGenerator(SteppingGenerator genny) {
		_generator = genny;
	}

	@UiHandler("populate")
	void handleClick(ClickEvent e) {
		_tester.loadTinyData();
	}

	@UiHandler("clear")
	void clearClick(ClickEvent e) {
		// TODO _generator.clear() ??
	}

	@UiHandler("restart")
	void restartClick(ClickEvent e) {
		_generator.restart();
	}

	@UiHandler("populateShort")
	void populateShortClick(ClickEvent e) {
		_tester.loadSampleData(false);
	}

	@UiHandler("populateLink")
	void populateLinkClick(ClickEvent e) {
		_tester.loadSampleData(true);
	}

	@UiHandler("step")
	void stepClick(ClickEvent e) {
		_generator.step();
	}

	@UiHandler("play")
	void playClick(ClickEvent e) {
		// TODO implement play
	}

	@UiHandler("live")
	void liveClick(ClickEvent e) {
		// TODO TrackGenerator unavailable
	}

	@UiHandler("one")
	void oneClick(ClickEvent e) {
		_tester.nextTest();
	}

	public void setTestSupport(TestSupport testP) {
		_tester = testP;
	}

}
