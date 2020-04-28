/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.mwc.debrief.pepys.view;

import java.util.ArrayList;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.mwc.cmap.core.custom_widget.CWorldLocation;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.model.utils.OSUtils;
import org.mwc.debrief.pepys.model.AbstractConfiguration;
import org.mwc.debrief.pepys.model.tree.TreeContentProvider;
import org.mwc.debrief.pepys.model.tree.TreeNode;
import org.mwc.debrief.pepys.nebula.PShelf;
import org.mwc.debrief.pepys.nebula.PShelfItem;
import org.mwc.debrief.pepys.nebula.RedmondShelfRenderer;
import org.mwc.debrief.pepys.view.tree.TreeNameLabelProvider;

public class PepysImportView extends Dialog implements AbstractViewSWT {

	final static RGB SWT_ORANGE = new RGB(255, 165, 0);
	private Label startLabel;
	private Label endLabel;
	private Label topLeftLabel;
	private Label bottomRightLabel;
	private Label titleLabel;
	private Label searchLabel;
	private Label textSearchLabel;

	private Label textSearchResults;
	private Button applyButton;
	private Button importButton;
	private Button testConnectionButton;
	private Button useCurrentViewportButton;
	private Button searchNextButton;

	private Button searchPreviousButton;
	private CDateTime startDate;
	private CDateTime startTime;
	private CDateTime endDate;

	private CDateTime endTime;
	private PShelf shelf;

	private CheckboxTreeViewer tree;

	private Text searchText;

	private Text filterText;

	private final ArrayList<Button> dataTypesCheckBox = new ArrayList<Button>();

	private CWorldLocation topLeftLocation;

	private CWorldLocation bottomRightLocation;

	private Composite dataTypesComposite;

	public PepysImportView(final AbstractConfiguration model, final Shell parent) {
		super(parent);

		initGUI(model, parent);
	}

	@Override
	public Button getApplyButton() {
		return applyButton;
	}

	@Override
	public CWorldLocation getBottomRightLocation() {
		return bottomRightLocation;
	}

	@Override
	public ArrayList<Button> getDataTypesCheckBox() {
		return dataTypesCheckBox;
	}

	@Override
	public Composite getDataTypesComposite() {
		return dataTypesComposite;
	}

	@Override
	public CDateTime getEndDate() {
		return endDate;
	}

	@Override
	public CDateTime getEndTime() {
		return endTime;
	}

	@Override
	public Text getFilterText() {
		return filterText;
	}

	@Override
	public Button getImportButton() {
		return importButton;
	}

	@Override
	public Button getSearchNextButton() {
		return searchNextButton;
	}

	@Override
	public Button getSearchPreviousButton() {
		return searchPreviousButton;
	}

	@Override
	public Text getSearchText() {
		return searchText;
	}

	@Override
	public CDateTime getStartDate() {
		return startDate;
	}

	@Override
	public CDateTime getStartTime() {
		return startTime;
	}

	@Override
	public Button getTestConnectionButton() {
		return testConnectionButton;
	}

	@Override
	public Label getTextSearchResults() {
		return textSearchResults;
	}

	@Override
	public CWorldLocation getTopLeftLocation() {
		return topLeftLocation;
	}

	@Override
	public CheckboxTreeViewer getTree() {
		return tree;
	}

	@Override
	public Button getUseCurrentViewportButton() {
		return useCurrentViewportButton;
	}

	public void initGUI(final AbstractConfiguration model, final Shell parent) {
		final GridLayout mainLayout = new GridLayout();
		mainLayout.numColumns = 6;
		mainLayout.marginWidth = 20;
		mainLayout.marginHeight = 20;
		parent.setLayout(mainLayout);

		this.titleLabel = new Label(parent, SWT.NONE);
		this.titleLabel.setText("Pepys Import");
		final GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 3;
		titleLabel.setLayoutData(gridData);

		final GridData testConnectionGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		testConnectionGridData.horizontalAlignment = GridData.END;
		testConnectionGridData.horizontalSpan = 3;
		this.testConnectionButton = new Button(parent, SWT.PUSH);
		this.testConnectionButton.setText("Connection Test");
		this.testConnectionButton.setLayoutData(testConnectionGridData);
		this.testConnectionButton.setImage(DebriefPlugin.getImageDescriptor("/icons/16/direction.png").createImage());

		this.shelf = new PShelf(parent, SWT.BORDER);
		this.shelf.setRenderer(new RedmondShelfRenderer());
		final GridData shelfGridData = new GridData();
		shelfGridData.verticalAlignment = GridData.FILL;
		shelfGridData.verticalSpan = 2;
		shelfGridData.grabExcessVerticalSpace = true;
		shelfGridData.widthHint = 270;
		this.shelf.setLayoutData(shelfGridData);

		this.searchLabel = new Label(parent, SWT.PUSH);
		this.searchLabel.setText("Search:");

		final GridData searchGrid = new GridData(GridData.FILL_HORIZONTAL);
		searchGrid.horizontalAlignment = GridData.FILL;
		searchGrid.grabExcessHorizontalSpace = true;
		searchGrid.horizontalSpan = 1;

		this.searchText = new Text(parent, SWT.SEARCH | SWT.ICON_SEARCH | SWT.ICON_CANCEL);
		this.searchText.setLayoutData(searchGrid);

		this.searchPreviousButton = new Button(parent, SWT.PUSH);
		this.searchPreviousButton.setText("<");
		this.searchNextButton = new Button(parent, SWT.PUSH);
		this.searchNextButton.setText(">");

		final GridData textSearchResultsGrid = new GridData();
		this.textSearchResults = new Label(parent, SWT.PUSH);
		this.textSearchResults.setText("            ");
		this.textSearchResults.setLayoutData(textSearchResultsGrid);

		// TIME PERIOD
		final PShelfItem timePeriodItem = new PShelfItem(shelf, SWT.NONE);
		timePeriodItem.setText("Time Period");
		final GridLayout timePeriodLayout = new GridLayout();
		timePeriodLayout.numColumns = 2;
		timePeriodLayout.marginWidth = 10;
		timePeriodLayout.marginHeight = 10;
		timePeriodItem.setImage(DebriefPlugin.getImageDescriptor("/icons/16/control_time.png").createImage());
		timePeriodItem.getBody().setLayout(timePeriodLayout);

		this.startLabel = new Label(timePeriodItem.getBody(), SWT.PUSH);
		this.startLabel.setText("Start:");
		this.startLabel.setLayoutData(gridData);

		this.startDate = new CDateTime(timePeriodItem.getBody(), CDT.BORDER | CDT.DROP_DOWN | CDT.DATE_SHORT);
		this.startDate.setPattern("dd/MM/yyyy");
		this.startDate.setSelection(model.getTimePeriod().getStartDTG().getDate());

		this.startTime = new CDateTime(timePeriodItem.getBody(), CDT.BORDER | CDT.SPINNER | CDT.TIME_MEDIUM);
		this.startTime.setSelection(model.getTimePeriod().getStartDTG().getDate());

		this.endLabel = new Label(timePeriodItem.getBody(), SWT.NONE);
		this.endLabel.setText("End:");
		this.endLabel.setLayoutData(gridData);

		this.endDate = new CDateTime(timePeriodItem.getBody(), CDT.BORDER | CDT.DROP_DOWN | CDT.DATE_SHORT);
		this.endDate.setPattern("dd/MM/yyyy");
		this.endDate.setSelection(model.getTimePeriod().getEndDTG().getDate());

		this.endTime = new CDateTime(timePeriodItem.getBody(), CDT.BORDER | CDT.SPINNER | CDT.TIME_MEDIUM);
		this.endTime.setSelection(model.getTimePeriod().getEndDTG().getDate());

		// AREA
		final GridLayout areaItemLayout = new GridLayout();
		areaItemLayout.numColumns = 1;
		areaItemLayout.marginWidth = 10;
		areaItemLayout.marginHeight = 10;
		final PShelfItem areaItem = new PShelfItem(shelf, SWT.NONE);
		areaItem.setText("Area");
		areaItem.setImage(DebriefPlugin.getImageDescriptor("/icons/16/map.png").createImage());
		areaItem.getBody().setLayout(areaItemLayout);

		this.topLeftLabel = new Label(areaItem.getBody(), SWT.NONE);
		this.topLeftLabel.setText("Top Left:");

		this.topLeftLocation = new CWorldLocation(areaItem.getBody(), SWT.NONE);

		this.bottomRightLabel = new Label(areaItem.getBody(), SWT.NONE);
		this.bottomRightLabel.setText("Bottom Right");

		this.bottomRightLocation = new CWorldLocation(areaItem.getBody(), SWT.NONE);

		final GridData useCurrentButtonGridData = new GridData();
		this.useCurrentViewportButton = new Button(areaItem.getBody(), SWT.PUSH);
		this.useCurrentViewportButton.setText("Use current viewport");
		this.useCurrentViewportButton.setLayoutData(useCurrentButtonGridData);
		useCurrentButtonGridData.widthHint = 150;
		useCurrentButtonGridData.heightHint = 40;
		useCurrentButtonGridData.horizontalAlignment = SWT.END;

		// Data Type
		final GridLayout dataTypeItemLayout = new GridLayout();
		dataTypeItemLayout.numColumns = 1;
		dataTypeItemLayout.marginWidth = 10;
		dataTypeItemLayout.marginHeight = 10;
		final PShelfItem dataTypeItem = new PShelfItem(shelf, SWT.NONE);
		dataTypeItem.setText("Data Type");
		dataTypeItem.setImage(DebriefPlugin.getImageDescriptor("/icons/16/filter.png").createImage());
		dataTypeItem.getBody().setLayout(dataTypeItemLayout);
		dataTypesComposite = dataTypeItem.getBody();

		// Text Search Area
		final GridLayout textFilterLayout = new GridLayout();
		textFilterLayout.numColumns = 1;
		textFilterLayout.marginWidth = 10;
		textFilterLayout.marginHeight = 10;
		final PShelfItem textFilterItem = new PShelfItem(shelf, SWT.NONE);
		textFilterItem.setText("Text");
		textFilterItem.setImage(DebriefPlugin.getImageDescriptor("/icons/16/search.png").createImage());
		textFilterItem.getBody().setLayout(textFilterLayout);

		this.textSearchLabel = new Label(textFilterItem.getBody(), SWT.NONE);
		this.textSearchLabel.setText("Enemy Contact:");

		final GridData filterTextGridData = new GridData();
		filterTextGridData.widthHint = 150;
		this.filterText = new Text(textFilterItem.getBody(), SWT.SEARCH | SWT.ICON_SEARCH | SWT.ICON_CANCEL);
		this.filterText.setLayoutData(filterTextGridData);

		// Tree Area
		final GridData treeGrid = new GridData(GridData.FILL_BOTH);
		treeGrid.horizontalAlignment = GridData.FILL;
		treeGrid.grabExcessHorizontalSpace = true;
		treeGrid.verticalAlignment = GridData.FILL;
		treeGrid.grabExcessVerticalSpace = true;
		treeGrid.horizontalSpan = 5;

		final TreeNameLabelProvider labelProvider = new TreeNameLabelProvider();
		this.tree = new CheckboxTreeViewer(parent, SWT.BORDER);
		this.tree.setContentProvider(new TreeContentProvider());
		this.tree.setLabelProvider(new TreeNameLabelProvider());
		this.tree.getTree().setLayoutData(treeGrid);
		this.tree.setAutoExpandLevel(2);
		this.tree.getControl().addListener(SWT.PaintItem, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				if (event.item instanceof TreeItem) {
					final TreeItem item = (TreeItem) event.item;
					final String textToSearch = model.getSearch();
					if (item.getData() instanceof TreeNode && textToSearch != null && !textToSearch.isBlank()) {
						final TreeNode node = (TreeNode) item.getData();

						if (node.getName() != null
								&& node.getName().toLowerCase().contains(textToSearch.toLowerCase())) {
							int offsetIcon = 0;
							final Image itemLabel = labelProvider.getImage(node);
							if (itemLabel != null) {
								offsetIcon = itemLabel.getBounds().width;
							}

							final int offsetCheck; // WARNING. THIS MIGHT CHANGE ACCODING TO THE THEME. Saul
							if (OSUtils.LINUX) {
								offsetCheck = 19;
							}else if (OSUtils.WIN) {
								offsetCheck = 5;
							}else {
								offsetCheck = 19;
							}

							final GC gc = event.gc;

							final Color oldForeground = gc.getForeground();
							final Color oldBackground = gc.getBackground();

							int currentIndex = 0;
							int currentOcurrence = 0;
							while (true) {
								currentIndex = node.getName().toLowerCase().indexOf(textToSearch.toLowerCase(),
										currentIndex);
								if (currentIndex < 0) {
									break;
								}

								final String textToDraw = node.getName().substring(currentIndex,
										currentIndex + textToSearch.length());
								final Point sizeOfTextToSearch = gc.stringExtent(textToDraw);
								final int offsetStart = gc.stringExtent(node.getName().substring(0, currentIndex)).x;
								final int totalOffset = offsetIcon + offsetStart + offsetCheck;

								// Y offset
								final int yOffset = (event.height - sizeOfTextToSearch.y) / 2;

								if (model.getCurrentSearchTreeResultModel().getItem() == item.getData()
										&& currentOcurrence == model.getCurrentSearchTreeResultModel().getOcurrence()) {
									gc.setBackground(new Color(parent.getDisplay(), SWT_ORANGE));
								} else {
									gc.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_YELLOW));
								}
								gc.fillRectangle(event.x + totalOffset, event.y + yOffset, sizeOfTextToSearch.x,
										sizeOfTextToSearch.y);

								gc.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_BLACK));
								gc.drawText(textToDraw, event.x + totalOffset, event.y + yOffset, true);

								++currentOcurrence;
								++currentIndex;
							}

							gc.setForeground(oldForeground);
							gc.setBackground(oldBackground);
							event.detail &= ~SWT.BACKGROUND;
							event.detail &= ~SWT.HOT;
						}
					}
				}
			}

		});

		final GridData applyGridDataButton = new GridData();
		applyGridDataButton.horizontalAlignment = GridData.END;
		this.applyButton = new Button(parent, SWT.PUSH);
		this.applyButton.setText("Apply");
		this.applyButton.setLayoutData(applyGridDataButton);
		this.applyButton.setImage(DebriefPlugin.getImageDescriptor("/icons/24/search.png").createImage());
		applyGridDataButton.widthHint = 120;
		applyGridDataButton.heightHint = 40;

		final GridData importGridDataButton = new GridData();
		importGridDataButton.horizontalAlignment = GridData.END;
		importGridDataButton.minimumWidth = 200;
		importGridDataButton.horizontalSpan = 5;

		this.importButton = new Button(parent, SWT.PUSH);
		this.importButton.setText("Import");
		this.importButton.setLayoutData(importGridDataButton);
		this.importButton.setImage(DebriefPlugin.getImageDescriptor("/icons/24/import.png").createImage());

		importGridDataButton.widthHint = 120;
		importGridDataButton.heightHint = 40;

	}

}
