/*******************************************************************************
. * Debrief - the Open Source Maritime Analysis Application
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

package org.mwc.debrief.pepys.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.pepys.model.AbstractConfiguration;
import org.mwc.debrief.pepys.model.ModelConfiguration;
import org.mwc.debrief.pepys.model.PepsysException;
import org.mwc.debrief.pepys.model.PepysConnectorBridge;
import org.mwc.debrief.pepys.model.TypeDomain;
import org.mwc.debrief.pepys.model.bean.Comment;
import org.mwc.debrief.pepys.model.bean.Contact;
import org.mwc.debrief.pepys.model.bean.State;
import org.mwc.debrief.pepys.model.db.DatabaseConnection;
import org.mwc.debrief.pepys.model.db.config.ConfigurationReader;
import org.mwc.debrief.pepys.model.db.config.DatabaseConfiguration;
import org.mwc.debrief.pepys.model.tree.TreeNode;
import org.mwc.debrief.pepys.view.AbstractViewSWT;
import org.mwc.debrief.pepys.view.PepysImportView;

import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class PepysImportController {

	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		try {
			final DatabaseConfiguration _config = new DatabaseConfiguration();
			ConfigurationReader.loadDatabaseConfiguration(_config, DatabaseConnection.DEFAULT_SQLITE_DATABASE_FILE,
					DatabaseConnection.DEFAULT_SQLITE_DATABASE_FILE);

			final AbstractConfiguration model = new ModelConfiguration();
			model.loadDatabaseConfiguration(_config);
			final AbstractViewSWT view = new PepysImportView(model, shell);

			new PepysImportController(shell, model, view);
		} catch (final PropertyVetoException | IOException e) {
			e.printStackTrace();
		}

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private final AbstractConfiguration _model;

	private final AbstractViewSWT _view;

	private final Shell _parent;

	private final String IMAGE_PREFIX = "/icons/16/";

	private final String INI_FILE_SUFFIX = "ini";

	private final String SQLITE_FILE_SUFFIX = "sqlite";

	public PepysImportController(final Shell parent, final AbstractConfiguration model, final AbstractViewSWT view) {
		model.addDatafileTypeFilter(new TypeDomain(State.class, TreeNode.STATE, true, IMAGE_PREFIX + "fix.png"));
		model.addDatafileTypeFilter(
				new TypeDomain(Contact.class, TreeNode.CONTACTS, true, IMAGE_PREFIX + "bearing.png"));
		model.addDatafileTypeFilter(
				new TypeDomain(Comment.class, TreeNode.COMMENT, true, IMAGE_PREFIX + "narrative.png"));

		_model = model;
		_view = view;
		_parent = parent;

		addDataTypeFilters(model, view);
		addDatabindings(model, view);
	}

	public PepysImportController(final Shell parent, final AbstractConfiguration model, final AbstractViewSWT view,
			final PepysConnectorBridge pepysBridge) {
		this(parent, model, view);

		_model.setPepysConnectorBridge(pepysBridge);
	}

	protected void addDatabindings(final AbstractConfiguration model, final AbstractViewSWT view) {

		view.getStartDate().addSelectionListener(new SelectionListener() {

			public void setStartTime(final AbstractConfiguration model) {
				final HiResDate start = new HiResDate(view.getStartDate().getSelection());
				final HiResDate oldStart = model.getTimePeriod().getStartDTG();
				model.setTimePeriod(new TimePeriod.BaseTimePeriod(HiResDate.copyOnlyDate(start, oldStart),
						model.getTimePeriod().getEndDTG()));
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent sel) {
				setStartTime(model);
			}

			@Override
			public void widgetSelected(final SelectionEvent sel) {
				setStartTime(model);
			}
		});

		view.getEndDate().addSelectionListener(new SelectionListener() {

			public void setEndTime(final AbstractConfiguration model) {
				final HiResDate end = new HiResDate(view.getEndDate().getSelection());
				final HiResDate oldEnd = model.getTimePeriod().getEndDTG();
				model.setTimePeriod(new TimePeriod.BaseTimePeriod(model.getTimePeriod().getStartDTG(),
						HiResDate.copyOnlyDate(end, oldEnd)));
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent sel) {
				setEndTime(model);
			}

			@Override
			public void widgetSelected(final SelectionEvent sel) {
				setEndTime(model);
			}
		});

		view.getStartTime().addSelectionListener(new SelectionListener() {

			public void setStartTime(final AbstractConfiguration model) {
				final HiResDate start = new HiResDate(view.getStartTime().getSelection());
				final HiResDate oldStart = model.getTimePeriod().getStartDTG();
				model.setTimePeriod(new TimePeriod.BaseTimePeriod(HiResDate.copyOnlyTime(start, oldStart),
						model.getTimePeriod().getEndDTG()));
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent sel) {
				setStartTime(model);
			}

			@Override
			public void widgetSelected(final SelectionEvent arg0) {
				setStartTime(model);
			}
		});

		view.getEndTime().addSelectionListener(new SelectionListener() {

			public void setEndTime(final AbstractConfiguration model) {
				final HiResDate end = new HiResDate(view.getEndTime().getSelection());
				final HiResDate oldEnd = model.getTimePeriod().getEndDTG();
				model.setTimePeriod(new TimePeriod.BaseTimePeriod(model.getTimePeriod().getStartDTG(),
						HiResDate.copyOnlyTime(end, oldEnd)));
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				setEndTime(model);
			}

			@Override
			public void widgetSelected(final SelectionEvent arg0) {
				setEndTime(model);
			}
		});

		model.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				if (AbstractConfiguration.PERIOD_PROPERTY.equals(evt.getPropertyName())
						&& !evt.getOldValue().equals(evt.getNewValue())) {
					view.getStartDate().setSelection(model.getTimePeriod().getStartDTG().getDate());
				}
			}
		});

		model.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				if (AbstractConfiguration.TREE_MODEL.equals(evt.getPropertyName())) {
					view.getTree().setInput(model.getTreeModel());
				}
			}
		});

		model.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				if (AbstractConfiguration.SEARCH_PROPERTY.equals(evt.getPropertyName())) {

					// In case we are modifying the model directly.
					if (!view.getSearchText().getText().equals(evt.getNewValue())) {
						view.getSearchText().setText((String) evt.getNewValue());
					}
					view.getTree().refresh();
				}
			}
		});

		model.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				if (AbstractConfiguration.HIGHLIGHT_PROPERTY.equals(evt.getPropertyName())) {
					final List<TreeNode> path = new ArrayList<TreeNode>();
					TreeNode currentNode = (TreeNode) evt.getNewValue();
					while (currentNode != null) {
						path.add(currentNode);
						currentNode = currentNode.getParent();
					}
					Collections.reverse(path);

					TreeItem item = view.getTree().getTree().getItem(0);
					for (final TreeNode itemPath : path) {
						if (item.getData() != itemPath) {
							for (final TreeItem child : item.getItems()) {
								if (child.getData() == itemPath) {
									item = child;
									break;
								}
							}
						}
						if (!item.getExpanded()) {
							item.setExpanded(true);
						}
						view.getTree().refresh();
					}
					
					// There is a bug in Window which does not
					// update the selection if it is the same node.
					// So, we need to force an update 
					view.getTree().getTree().deselectAll();
					view.getTree().getTree().setSelection(item);
				}
			}
		});

		view.getSearchText().addListener(SWT.Traverse, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				if (event.detail == SWT.TRAVERSE_RETURN) {
					model.getNextSearch();
					view.getTree().refresh();
				}
			}
		});

		view.getTree().addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				final TreeItem[] selected = view.getTree().getTree().getSelection();
				if (selected.length > 0) {
					model.setHighlightedElement((TreeNode) selected[0].getData());
					model.searchFromUser(true);
				} else {
					model.setHighlightedElement(null);
				}
			}
		});

		view.getApplyButton().addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event event) {
				if (event.type == SWT.Selection) {
					final Cursor _cursor = new Cursor(Display.getCurrent(), SWT.CURSOR_WAIT);
					_parent.setCursor(_cursor);
					Display.getCurrent().asyncExec(new Runnable() {
						@Override
						public void run() {
							try {
								updateAreaView2Model(model, view);
								model.apply();
							} catch (final PepsysException e) {
								e.printStackTrace();
								final MessageBox messageBox = new MessageBox(_parent, SWT.ERROR | SWT.OK);
								messageBox.setMessage(e.getMessage());
								messageBox.setText(e.getTitle());
								messageBox.open();
							} catch (final Exception e) {
								e.printStackTrace();
								final MessageBox messageBox = new MessageBox(_parent, SWT.ERROR | SWT.OK);
								messageBox.setMessage(DatabaseConnection.GENERIC_CONNECTION_ERROR);
								messageBox.setText("DebriefNG");
								messageBox.open();
							} finally {
								_parent.setCursor(null);
								_cursor.dispose();
							}
						}
					});
				}
			}
		});

		view.getImportButton().addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				if (event.type == SWT.Selection) {
					model.doImport();
				}
			}
		});

		view.getTestConnectionButton().addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				if (event.type == SWT.Selection) {
					boolean showError = false;
					String errorMessage = "";
					try {
						showError = !model.doTestQuery();
						errorMessage = "Database didn't contain the basic State, Contacts or Comments";
					} catch (final SQLException e) {
						e.printStackTrace();

						errorMessage = DatabaseConnection.GENERIC_CONNECTION_ERROR;
						showError = true;
					} catch (Exception e) {
						errorMessage = "You have incorrect database type.\nPlease provide the correct database type in the config file";
						showError = true;
					}
					if (showError) {
						final MessageBox messageBox = new MessageBox(_parent, SWT.ERROR | SWT.OK);

						messageBox.setMessage(errorMessage);
						messageBox.setText("DebriefNG");
						messageBox.open();

						return;
					} else {
						final MessageBox messageBox = new MessageBox(_parent, SWT.OK);
						messageBox.setMessage("Successful database connection");
						messageBox.setText("Debrief NG");
						messageBox.open();

						return;
					}
				}
			}
		});

		view.getUseCurrentViewportButton().addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				if (event.type == SWT.Selection) {
					model.setCurrentViewport();
					updateAreaModel2View(model, view);
				}
			}
		});

		view.getTree().addCheckStateListener(new ICheckStateListener() {

			@Override
			public void checkStateChanged(final CheckStateChangedEvent event) {
				view.getTree().setSubtreeChecked(event.getElement(), event.getChecked());
				((TreeNode) event.getElement()).setCheckedRecursive(event.getChecked());
			}
		});

		view.getTree().addCheckStateListener(new ICheckStateListener() {

			@Override
			public void checkStateChanged(final CheckStateChangedEvent event) {
				if (event.getElement() instanceof TreeNode) {
					((TreeNode) event.getElement()).setChecked(event.getChecked());
				}
			}
		});

		view.getSearchText().addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent evt) {
				model.setSearch(view.getSearchText().getText());
			}
		});

		view.getFilterText().addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent evt) {
				model.setFilter(view.getFilterText().getText());
			}
		});

		view.getSearchNextButton().addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				model.getNextSearch();

				view.getTree().refresh();
			}
		});

		view.getSearchPreviousButton().addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				model.getPreviousSearch();
				view.getTree().refresh();
			}
		});

		model.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				if (AbstractConfiguration.SEARCH_RESULT_PROPERTY.equals(evt.getPropertyName())) {
					view.getTextSearchResults().setText(model.getSearchResultsText());
				}
			}
		});

		view.getTree().setInput(model.getTreeModel());

		view.getTree().addDropSupport(DND.DROP_MOVE, new FileTransfer[] { FileTransfer.getInstance() },
				new DropTargetListener() {

					@Override
					public void dragEnter(final DropTargetEvent arg0) {

					}

					@Override
					public void dragLeave(final DropTargetEvent arg0) {

					}

					@Override
					public void dragOperationChanged(final DropTargetEvent arg0) {

					}

					@Override
					public void dragOver(final DropTargetEvent arg0) {

					}

					@Override
					public void drop(final DropTargetEvent event) {
						final Object dataObject = event.data;
						if (dataObject instanceof String[]) {
							final String[] filesDropped = (String[]) dataObject;
							if (filesDropped.length == 1) {
								final String fileName = filesDropped[0];
								try {

									final DatabaseConfiguration _config;
									if (fileName.toLowerCase().endsWith(INI_FILE_SUFFIX)) {
										// Lets try to load the file as a configuration file.
										_config = new DatabaseConfiguration();
										ConfigurationReader.loadDatabaseConfiguration(_config, fileName, null);

									} else if (fileName.toLowerCase().endsWith(SQLITE_FILE_SUFFIX)) {
										_config = DatabaseConfiguration.DatabaseConfigurationFactory
												.createSqliteConfiguration(fileName);
									} else {
										_config = new DatabaseConfiguration();
										final MessageBox messageBox = new MessageBox(_parent, SWT.ERROR | SWT.OK);
										messageBox.setMessage("Dragged object not recognized. Wrote file extension");
										messageBox.setText("Error processing dragged object.");
										messageBox.open();

										return;
									}
									model.loadDatabaseConfiguration(_config);
									final MessageBox messageBox = new MessageBox(_parent, SWT.OK | SWT.OK);
									final String filePath = _config.getSourcePath() == null ? ""
											: _config.getSourcePath();
									messageBox.setMessage("File loaded successfully\n" + filePath);
									messageBox.setText("File processing finished successfully");
									messageBox.open();

									return;
								} catch (PropertyVetoException | IOException e) {
									final MessageBox messageBox = new MessageBox(_parent, SWT.ERROR | SWT.OK);
									messageBox.setMessage(
											"Unable to load database specified in the configuration file\n" + fileName);
									messageBox.setText("Error processing dragged object.");
									messageBox.open();

									return;
								}
							} else {
								System.out.println("No se pueden agregar mas de 2 archivos");
								return;
							}
						} else {
							final MessageBox messageBox = new MessageBox(_parent, SWT.ERROR | SWT.OK);
							messageBox.setMessage("Dragged object not recognized");
							messageBox.setText("Error processing dragged object.");
							messageBox.open();

							return;
						}
					}

					@Override
					public void dropAccept(final DropTargetEvent arg0) {

					}
				});
	}

	private void addDataTypeFilters(final AbstractConfiguration _model, final AbstractViewSWT _view) {
		final Composite composite = _view.getDataTypesComposite();

		for (final TypeDomain type : _model.getDatafileTypeFilters()) {
			final Button typeButton = new Button(composite, SWT.CHECK);
			typeButton.setText(type.getName());
			typeButton.setImage(DebriefPlugin.getImageDescriptor(type.getImagePath()).createImage());
			typeButton.setSelection(type.isChecked());
			type.removeAllPropertyChangeListeners();
			type.addPropertyChangeListener(new PropertyChangeListener() {

				@Override
				public void propertyChange(final PropertyChangeEvent evt) {
					typeButton.setSelection(type.isChecked());
				}
			});

			typeButton.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetDefaultSelected(final SelectionEvent event) {
					type.setChecked(typeButton.getSelection());
				}

				@Override
				public void widgetSelected(final SelectionEvent event) {
					type.setChecked(typeButton.getSelection());
				}
			});
		}
	}

	public AbstractConfiguration getModel() {
		return _model;
	}

	public AbstractViewSWT getView() {
		return _view;
	}

	public void updateAreaModel2View(final AbstractConfiguration model, final AbstractViewSWT view) {
		view.getTopLeftLocation().setValue(model.getCurrentArea().getTopLeft());
		view.getBottomRightLocation().setValue(model.getCurrentArea().getBottomRight());
	}

	public void updateAreaView2Model(final AbstractConfiguration model, final AbstractViewSWT view) {
		final WorldLocation topLeft;

		if (view.getTopLeftLocation().getValue() == null) {
			topLeft = model.getDefaultTopLeft();
			view.getTopLeftLocation().clean();
		} else {
			topLeft = view.getTopLeftLocation().getValue();
		}
		final WorldLocation bottomRight;
		if (view.getBottomRightLocation().getValue() == null) {
			bottomRight = model.getDefaultBottomRight();
			view.getBottomRightLocation().clean();
		} else {
			bottomRight = view.getBottomRightLocation().getValue();
		}

		model.setArea(new WorldArea(topLeft, bottomRight));
	}
}
