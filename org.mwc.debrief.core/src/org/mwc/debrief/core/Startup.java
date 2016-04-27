/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.dynamichelpers.IExtensionChangeHandler;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.navigator.ResourceNavigator;
import org.eclipse.ui.views.properties.PropertySheet;

@SuppressWarnings("deprecation")
public class Startup implements IStartup
{

  private IPartListener partListener = new IPartListener()
  {

    @Override
    public void partOpened(IWorkbenchPart part)
    {
      if (part instanceof ContentOutline || part instanceof PropertySheet
          || part instanceof ResourceNavigator)
      {
        changeIcon(part);
      }
    }

    @Override
    public void partDeactivated(IWorkbenchPart part)
    {
    }

    @Override
    public void partClosed(IWorkbenchPart part)
    {
    }

    @Override
    public void partBroughtToTop(IWorkbenchPart part)
    {
    }

    @Override
    public void partActivated(IWorkbenchPart part)
    {
    }
  };
  private static Image outlineImage, propertiesImage, navigatorImage;

  @Override
  public void earlyStartup()
  {
    removePerspective();
    removePreferencePages();
    updateMenuIcons();
    updateViewIcons();
    new ResetPerspective().resetPerspective();
    if (DebriefPlugin.getDefault().getCreateProject())
    {
      new CreateDebriefProject().createStartProject();
    }
  }

  private void updateMenuIcons()
  {

    Display.getDefault().asyncExec(new Runnable()
    {

      @Override
      public void run()
      {

        ActionAccessSupport accessSupport = new ActionAccessSupport();
        IAction saveall = accessSupport.getAction("saveAll");
        if (saveall != null)
        {
          saveall.setImageDescriptor(DebriefPlugin
              .getImageDescriptor("icons/16/save.png"));
        }
        IAction saveas = accessSupport.getAction("saveAs");
        if (saveas != null)
        {
          saveas.setImageDescriptor(DebriefPlugin
              .getImageDescriptor("icons/16/save.png"));
        }
        IAction save = accessSupport.getAction("save");
        if (save != null)
        {
          save.setImageDescriptor(DebriefPlugin
              .getImageDescriptor("icons/16/save.png"));
        }
        IAction redo = accessSupport.getAction("redo");
        if (redo != null)
        {
          redo.setImageDescriptor(DebriefPlugin
              .getImageDescriptor("icons/16/redo.png"));
        }
        IAction undo = accessSupport.getAction("undo");
        if (undo != null)
        {
          undo.setImageDescriptor(DebriefPlugin
              .getImageDescriptor("icons/16/undo.png"));
        }
      }
    });
  }

  private void updateViewIcons()
  {
    Display.getDefault().asyncExec(new Runnable()
    {

      @Override
      public void run()
      {
        IWorkbenchPage page =
            PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getActivePage();
        page.addPartListener(partListener);
        IViewPart view = page.findView(IPageLayout.ID_OUTLINE);
        if (view instanceof ContentOutline)
        {
          changeIcon(view);
        }
        view = page.findView(IPageLayout.ID_PROP_SHEET);
        if (view instanceof PropertySheet)
        {
          changeIcon(view);
        }
        view = page.findView(IPageLayout.ID_RES_NAV);
        if (view instanceof ResourceNavigator)
        {
          changeIcon(view);
        }
      }
    });
  }
  
  protected void changeIcon(IWorkbenchPart part)
  {
    try
    {
      Class<?> clazz = WorkbenchPart.class;
      Method method = clazz.getDeclaredMethod("setTitleImage", new Class[]
      {Image.class});
      method.setAccessible(true);
      Image image = null;
      if (part instanceof ContentOutline)
      {
        if (outlineImage == null)
        {
          ImageDescriptor descriptor =
              DebriefPlugin.getImageDescriptor("icons/16/outline.png");
          outlineImage =
              JFaceResources.getResources().createImageWithDefault(descriptor);
        }
        image = outlineImage;
      }
      if (part instanceof PropertySheet)
      {
        if (propertiesImage == null)
        {
          ImageDescriptor descriptor =
              DebriefPlugin.getImageDescriptor("icons/16/properties.png");
          propertiesImage =
              JFaceResources.getResources().createImageWithDefault(descriptor);
        }
        image = propertiesImage;
      }
      if (part instanceof ResourceNavigator)
      {
        if (navigatorImage == null)
        {
          ImageDescriptor descriptor =
              DebriefPlugin.getImageDescriptor("icons/16/navigator.png");
          navigatorImage =
              JFaceResources.getResources().createImageWithDefault(descriptor);
        }
        image = navigatorImage;
      }
      if (image != null)
      {
        method.invoke(part, new Object[]
        {image});
      }
    }
    catch (Exception e)
    {
      IStatus status =
          new Status(IStatus.INFO, DebriefPlugin.PLUGIN_NAME,
              "Can't change the icon of the Outline view", e);
      DebriefPlugin.getDefault().getLog().log(status);
    }
  }

  private void removePreferencePages()
  {
    PreferenceManager preferenceManager =
        PlatformUI.getWorkbench().getPreferenceManager();
    if (preferenceManager == null)
    {
      return;
    }
    preferenceManager.remove("org.eclipse.debug.ui.DebugPreferencePage");
    preferenceManager.remove("org.eclipse.debug.ui.LaunchingPreferencePage");
    preferenceManager
        .remove("org.eclipse.debug.ui.ViewManagementPreferencePage");
    preferenceManager.remove("org.eclipse.debug.ui.ConsolePreferencePage");
    preferenceManager
        .remove("org.eclipse.debug.ui.StringVariablePreferencePage");
    preferenceManager.remove("org.eclipse.debug.ui.PerspectivePreferencePage");
    preferenceManager.remove("org.eclipse.debug.ui.LaunchConfigurations");
    preferenceManager
        .remove("org.eclipse.debug.ui.LaunchDelegatesPreferencePage");
    preferenceManager.remove("org.eclipse.team.ui.TeamPreferences");
    preferenceManager
        .remove("org.eclipse.wst.xml.ui.propertyPage.project.validation");
  }

  private void removePerspective()
  {
    IPerspectiveRegistry registry =
        PlatformUI.getWorkbench().getPerspectiveRegistry();
    if (registry == null)
    {
      return;
    }
    List<IPerspectiveDescriptor> descriptors =
        new ArrayList<IPerspectiveDescriptor>();

    addDescriptor(registry, descriptors,
        "org.eclipse.debug.ui.DebugPerspective");
    addDescriptor(registry, descriptors,
        "org.eclipse.team.ui.TeamSynchronizingPerspective");

    // FIXME this method doesn't work on Eclipse E4 (Juno/Kepler)
    if (registry instanceof IExtensionChangeHandler && !descriptors.isEmpty())
    {
      IExtensionChangeHandler handler = (IExtensionChangeHandler) registry;
      handler.removeExtension(null, descriptors.toArray());
    }
  }

  private void addDescriptor(IPerspectiveRegistry registry,
      List<IPerspectiveDescriptor> descriptors, String id)
  {
    IPerspectiveDescriptor perspectiveDescriptor =
        registry.findPerspectiveWithId(id);
    if (perspectiveDescriptor != null)
    {
      descriptors.add(perspectiveDescriptor);
    }
  }

  private static class ActionAccessSupport
  {
    Method actionAccess;
    ActionBarAdvisor actionBarAdvisor = null;

    public ActionAccessSupport()
    {
      IWorkbenchWindow activeWorkbenchWindow =
          PlatformUI.getWorkbench().getActiveWorkbenchWindow();

      @SuppressWarnings("restriction")
      Class<?> clazz = WorkbenchWindow.class;
      Method method;
      try
      {
        method = clazz.getDeclaredMethod("getActionBarAdvisor", new Class[]
        {});
        method.setAccessible(true);
        actionBarAdvisor =
            (ActionBarAdvisor) method.invoke(activeWorkbenchWindow);
        assert actionBarAdvisor != null : "API CHANGE!";
        actionAccess =
            ActionBarAdvisor.class.getDeclaredMethod("getAction", new Class[]
            {String.class});
        actionAccess.setAccessible(true);
        assert actionAccess != null : "API CHANGE!";

      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }

    IAction getAction(String id)
    {
      try
      {

        return (IAction) actionAccess.invoke(actionBarAdvisor, id);

      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      return null;
    }

  }

}
