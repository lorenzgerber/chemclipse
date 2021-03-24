/*******************************************************************************
 * Copyright (c) 2018, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - make UI configurable, support selection of existing process methods, support for init with different datatypes
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;

import org.eclipse.chemclipse.converter.methods.MethodConverter;
import org.eclipse.chemclipse.model.methods.ListProcessEntryContainer;
import org.eclipse.chemclipse.model.methods.ProcessEntry;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.model.updates.IUpdateListener;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierContext;
import org.eclipse.chemclipse.processing.supplier.ProcessorPreferences;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.MethodSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.ProcessingWizard;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.SettingsWizard;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class ProcessMethodToolbar extends ToolBar {

	private ToolItem buttonAdd;
	private ToolItem buttonCopy;
	private ToolItem buttonRemove;
	private ToolItem buttonMoveUp;
	private ToolItem buttonMoveDown;
	private ToolItem buttonClipboard;
	private ToolItem buttonModifySettings;
	//
	private ProcessMethod processMethod;
	private StructuredViewer structuredViewer;
	private ProcessSupplierContext processingSupport;
	private BiFunction<IProcessEntry, ProcessSupplierContext, ProcessorPreferences<?>> preferencesSupplier;
	private DataCategory[] dataCategories;
	private IUpdateListener updateListener = null;
	private boolean readOnly = false;

	public ProcessMethodToolbar(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	@Override
	protected void checkSubclass() {

		/*
		 * Subclassing is allowed here.
		 */
	}

	public void setInput(ProcessMethod processMethod) {

		this.processMethod = processMethod;
	}

	public void setStructuredViewer(StructuredViewer structuredViewer) {

		this.structuredViewer = structuredViewer;
	}

	public void setProcessingSupport(ProcessSupplierContext processingSupport) {

		this.processingSupport = processingSupport;
	}

	public void setPreferencesSupplier(BiFunction<IProcessEntry, ProcessSupplierContext, ProcessorPreferences<?>> preferencesSupplier) {

		this.preferencesSupplier = preferencesSupplier;
	}

	public void setDataCategories(DataCategory[] dataCategories) {

		this.dataCategories = dataCategories;
	}

	public void setReadOnly(boolean readOnly) {

		this.readOnly = readOnly;
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	public void updateTableButtons() {

		boolean isEditable = isEditable(processMethod);
		buttonAdd.setEnabled(isEditable);
		//
		IStructuredSelection selection = structuredViewer.getStructuredSelection();
		boolean writeable = processMethod != null && !processMethod.isFinal() && !selection.isEmpty();
		Iterator<?> iterator = selection.iterator();
		while(iterator.hasNext() && writeable) {
			Object object = iterator.next();
			ListProcessEntryContainer container = MethodSupport.getContainer(object);
			if(container == null) {
				writeable = false;
			}
		}
		//
		boolean readOnly = processMethod != null && (processMethod.isReadOnly() || this.readOnly);
		buttonCopy.setEnabled(writeable && !readOnly);
		buttonRemove.setEnabled(writeable && !readOnly);
		buttonMoveUp.setEnabled(writeable && !readOnly);
		buttonMoveDown.setEnabled(writeable && !readOnly);
		buttonClipboard.setEnabled(true);
		buttonModifySettings.setEnabled(writeable && preferencesSupplier != null);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public void copyToClipboard(Display display) {

		Clipboard clipboard = new Clipboard(display);
		StringBuilder builder = new StringBuilder();
		String lineDelimiter = OperatingSystemUtils.getLineDelimiter();
		//
		List<Object> objects = new ArrayList<>();
		Object input = structuredViewer.getInput();
		if(input instanceof ProcessMethod) {
			objects.addAll(((ProcessMethod)input).getEntries());
		} else if(input instanceof List) {
			objects.addAll((List)input);
		}
		/*
		 * Header
		 */
		builder.append("Name");
		builder.append("\t");
		builder.append("Description");
		builder.append("\t");
		builder.append("Settings");
		builder.append(lineDelimiter);
		/*
		 * Data
		 */
		for(Object object : objects) {
			if(object instanceof IProcessEntry) {
				IProcessEntry processEntry = (IProcessEntry)object;
				builder.append(processEntry.getName());
				builder.append("\t");
				builder.append(processEntry.getDescription());
				builder.append("\t");
				builder.append(processEntry.getSettings());
				builder.append(lineDelimiter);
			}
		}
		//
		Object[] data = new Object[]{builder.toString()};
		TextTransfer textTransfer = TextTransfer.getInstance();
		Transfer[] dataTypes = new Transfer[]{textTransfer};
		clipboard.setContents(data, dataTypes);
	}

	public boolean modifyProcessEntry(Shell shell, IProcessEntry processEntry, ProcessSupplierContext supplierContext, boolean showHint) {

		ProcessorPreferences<?> preferences = preferencesSupplier.apply(processEntry, supplierContext);
		if(preferences == null) {
			return false;
		}
		//
		if(preferences.getSupplier().getSettingsParser().getInputValues().isEmpty()) {
			if(showHint) {
				MessageDialog.openInformation(shell, "Settings", "This processor does not offer any options.");
			}
			/*
			 * OK
			 */
			return true;
		}
		//
		try {
			return SettingsWizard.openEditPreferencesWizard(shell, preferences, false);
		} catch(IOException e) {
			return false;
		}
	}

	private boolean isEditable(ProcessMethod processMethod) {

		return !readOnly && processMethod != null && !processMethod.isFinal() && !processMethod.isReadOnly();
	}

	private void createControl() {

		buttonAdd = createAddButton(this);
		buttonRemove = createRemoveButton(this);
		buttonCopy = createCopyButton(this);
		buttonMoveUp = createMoveUpButton(this);
		buttonMoveDown = createMoveDownButton(this);
		buttonClipboard = createClipboardButton(this);
		buttonModifySettings = createModifySettingsButton(this);
	}

	private ToolItem createAddButton(ToolBar toolBar) {

		final ToolItem item = new ToolItem(toolBar, SWT.DROP_DOWN);
		item.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		item.setToolTipText("Add a process method.");
		final Menu menu = new Menu(toolBar.getShell(), SWT.POP_UP);
		//
		toolBar.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {

				menu.dispose();
			}
		});
		//
		item.addListener(SWT.Selection, event -> {
			if(event.detail == SWT.ARROW) {
				Rectangle rect = item.getBounds();
				Point pt = new Point(rect.x, rect.y + rect.height);
				pt = toolBar.toDisplay(pt);
				for(MenuItem menuItem : menu.getItems()) {
					menuItem.dispose();
				}
				Collection<IProcessMethod> userMethods = MethodConverter.getUserMethods();
				for(IProcessMethod method : userMethods) {
					MenuItem menuItem = new MenuItem(menu, SWT.NONE);
					menuItem.setText(method.getName());
					menuItem.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(SelectionEvent e) {

							loadMethodFile(method);
						}
					});
				}
				if(!userMethods.isEmpty()) {
					new MenuItem(menu, SWT.SEPARATOR);
				}
				MenuItem loadItem = new MenuItem(menu, SWT.NONE);
				loadItem.setText("Load from file...");
				loadItem.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {

						FileDialog fileDialog = new FileDialog(toolBar.getShell(), SWT.OPEN);
						fileDialog.setText("Select Process Method File");
						fileDialog.setFileName(MethodConverter.DEFAULT_METHOD_FILE_NAME);
						fileDialog.setFilterExtensions(MethodConverter.DEFAULT_METHOD_FILE_EXTENSIONS);
						fileDialog.setFilterNames(MethodConverter.DEFAULT_METHOD_FILE_NAMES);
						//
						String filePath = fileDialog.open();
						if(filePath != null) {
							File file = new File(filePath);
							loadMethodFile(Adapters.adapt(file, IProcessMethod.class));
						}
					}
				});
				menu.setLocation(pt.x, pt.y);
				menu.setVisible(true);
			} else {
				if(processMethod != null) {
					Map<ProcessSupplierContext, String> contextList = new LinkedHashMap<>();
					Object element = structuredViewer.getStructuredSelection().getFirstElement();
					ProcessEntry selectedEntry = null;
					if(element instanceof ProcessEntry) {
						selectedEntry = (ProcessEntry)element;
						String id = selectedEntry.getProcessorId();
						IProcessSupplier<?> supplier = processingSupport.getSupplier(id);
						if(supplier instanceof ProcessSupplierContext) {
							contextList.put((ProcessSupplierContext)supplier, supplier.getName());
						}
					}
					contextList.put(processingSupport, processMethod.getName());
					Map<ProcessSupplierContext, IProcessEntry> map = ProcessingWizard.open(getShell(), contextList, dataCategories);
					if(map != null) {
						for(Entry<ProcessSupplierContext, IProcessEntry> entry : map.entrySet()) {
							ProcessSupplierContext supplierContext = entry.getKey();
							IProcessEntry editedEntry = entry.getValue();
							boolean edit = modifyProcessEntry(getShell(), editedEntry, supplierContext, false);
							if(!edit) {
								continue;
							}
							IProcessEntry newEntry;
							if(supplierContext == processingSupport) {
								// add to global context
								newEntry = processMethod.addProcessEntry(editedEntry);
							} else {
								// add to local context
								newEntry = selectedEntry.addProcessEntry(editedEntry);
							}
							//
							fireUpdate();
							select(Collections.singletonList(newEntry));
						}
					}
				}
			}
		});
		return item;
	}

	private ToolItem createRemoveButton(ToolBar toolBar) {

		final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
		item.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		item.setToolTipText("Remove the selected process method(s).");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(MessageDialog.openQuestion(toolBar.getShell(), "Delete Process Method(s)", "Would you like to delete the selected processor(s)?")) {
					for(Object object : structuredViewer.getStructuredSelection().toArray()) {
						ListProcessEntryContainer container = MethodSupport.getContainer(object);
						if(container != null) {
							container.removeProcessEntry((IProcessEntry)object);
						}
					}
					//
					fireUpdate();
					select(Collections.emptyList());
				}
			}
		});
		return item;
	}

	private ToolItem createCopyButton(ToolBar toolBar) {

		final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
		item.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_COPY, IApplicationImage.SIZE_16x16));
		item.setToolTipText("Copy a process method.");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Iterator<?> selection = structuredViewer.getStructuredSelection().iterator();
				while(selection.hasNext()) {
					Object object = selection.next();
					ListProcessEntryContainer container = MethodSupport.getContainer(object);
					if(container != null) {
						List<IProcessEntry> entries = container.getEntries();
						int index = entries.indexOf(object);
						if(index > -1) {
							IProcessEntry processEntry = entries.get(index);
							IProcessEntry processEntryCopy = new ProcessEntry(processEntry, container);
							entries.add(index, processEntryCopy);
						}
					}
				}
				//
				fireUpdate();
			}
		});
		//
		return item;
	}

	private ToolItem createMoveUpButton(ToolBar toolBar) {

		final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
		item.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_UP_2, IApplicationImage.SIZE_16x16));
		item.setToolTipText("Move the process method(s) up.");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IStructuredSelection selection = structuredViewer.getStructuredSelection();
				Iterator<?> iterator = selection.iterator();
				while(iterator.hasNext()) {
					Object object = iterator.next();
					ListProcessEntryContainer container = MethodSupport.getContainer(object);
					if(container != null) {
						List<IProcessEntry> entries = container.getEntries();
						int index = entries.indexOf(object);
						if(index > 0) {
							Collections.swap(entries, index, index - 1);
						}
					}
				}
				//
				fireUpdate();
				structuredViewer.setSelection(selection);
			}
		});
		//
		return item;
	}

	private ToolItem createMoveDownButton(ToolBar toolBar) {

		final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
		item.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_DOWN_2, IApplicationImage.SIZE_16x16));
		item.setToolTipText("Move the process method(s) down.");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IStructuredSelection selection = structuredViewer.getStructuredSelection();
				Iterator<?> iterator = selection.iterator();
				while(iterator.hasNext()) {
					Object object = iterator.next();
					ListProcessEntryContainer container = MethodSupport.getContainer(object);
					if(container != null) {
						List<IProcessEntry> entries = container.getEntries();
						int index = entries.indexOf(object);
						if(index > -1 && index < entries.size() - 1) {
							Collections.swap(entries, index, index + 1);
						}
					}
				}
				//
				fireUpdate();
				structuredViewer.setSelection(selection);
			}
		});
		//
		return item;
	}

	private ToolItem createClipboardButton(ToolBar toolBar) {

		final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
		item.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_COPY_CLIPBOARD, IApplicationImage.SIZE_16x16));
		item.setToolTipText("Copy method to clipboard.");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				copyToClipboard(e.display);
			}
		});
		//
		return item;
	}

	private ToolItem createModifySettingsButton(ToolBar toolBar) {

		final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
		item.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		item.setToolTipText("Modify the process method settings.");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(processMethod != null) {
					Object object = structuredViewer.getStructuredSelection().getFirstElement();
					if(object instanceof IProcessEntry) {
						IProcessEntry processEntry = (IProcessEntry)object;
						modifyProcessEntry(toolBar.getShell(), processEntry, IProcessEntry.getContext(processEntry, processingSupport), true);
						fireUpdate();
					}
				}
			}
		});
		//
		return item;
	}

	private void loadMethodFile(IProcessMethod method) {

		if(method != null) {
			List<IProcessEntry> copied = new ArrayList<>();
			method.forEach(entry -> {
				copied.add(processMethod.addProcessEntry(entry));
			});
			//
			fireUpdate();
			select(copied);
		}
	}

	private void select(Iterable<? extends IProcessEntry> entries) {

		ArrayList<IProcessEntry> list = new ArrayList<>();
		entries.forEach(list::add);
		StructuredSelection structuredSelection = new StructuredSelection(list);
		structuredViewer.setSelection(structuredSelection);
		Object firstElement = structuredSelection.getFirstElement();
		if(firstElement != null) {
			structuredViewer.reveal(firstElement);
		}
	}

	private void fireUpdate() {

		if(updateListener != null) {
			updateListener.update();
		}
	}
}