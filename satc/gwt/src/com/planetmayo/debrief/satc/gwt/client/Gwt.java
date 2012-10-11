package com.planetmayo.debrief.satc.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.planetmayo.debrief.satc.gwt.client.ui.RootLayout;

public class Gwt implements EntryPoint
{

	public void onModuleLoad()
	{
		RootPanel.get().add(new RootLayout());

		/*
		SupportServices.INSTANCE.initialize(new GWTLogService(), new GWTConverterService(), new GWTIOService());
		
		BaseContribution contribution = SpeedForecastContribution.getSample();
		
		final Button sendButton = new Button("Send");
		final TextBox nameField = new TextBox();
		nameField.setText(contribution.getHardConstraints());
		final Label errorLabel = new Label();

		// We can add style names to widgets
		sendButton.addStyleName("sendButton");

		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel.get("nameFieldContainer").add(nameField);
		RootPanel.get("sendButtonContainer").add(sendButton);
		RootPanel.get("errorLabelContainer").add(errorLabel);

		// Focus the cursor on the name field when the app loads
		nameField.setFocus(true);
		nameField.selectAll();

		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		final Label textToServerLabel = new Label();
		final HTML serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
		dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		sendButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				LocationAnalysisTest tst = new LocationAnalysisTest();
				try {
					tst.testBoundary();
				} catch (IncompatibleStateException e) {
					e.printStackTrace();
				}
			}
		});

	*/}
}
