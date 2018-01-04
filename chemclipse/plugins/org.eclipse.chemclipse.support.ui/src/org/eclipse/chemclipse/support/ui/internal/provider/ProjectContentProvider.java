/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.internal.provider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.chemclipse.logging.core.Logger;

public class ProjectContentProvider implements ITreeContentProvider {

	private static final Logger logger = Logger.getLogger(ProjectContentProvider.class);

	@Override
	public void dispose() {

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	@Override
	public Object[] getElements(Object inputElement) {

		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {

		Object[] objects = null;
		if(parentElement instanceof IContainer) {
			IContainer container = (IContainer)parentElement;
			List<IFolder> folders = getFolders(container);
			return folders.toArray();
		}
		return objects;
	}

	@Override
	public Object getParent(Object element) {

		if(element instanceof IContainer) {
			return ((IContainer)element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {

		boolean hasChildren = false;
		if(element instanceof IContainer) {
			IContainer container = (IContainer)element;
			List<IFolder> folders = getFolders(container);
			hasChildren = folders.size() > 0 ? true : false;
		}
		return hasChildren;
	}

	private List<IFolder> getFolders(IContainer container) {

		List<IFolder> folders = new ArrayList<IFolder>();
		try {
			IResource[] resources = container.members();
			for(IResource resource : resources) {
				if(resource instanceof IFolder) {
					folders.add((IFolder)resource);
				}
			}
		} catch(CoreException e) {
			logger.warn(e);
		}
		return folders;
	}
}
