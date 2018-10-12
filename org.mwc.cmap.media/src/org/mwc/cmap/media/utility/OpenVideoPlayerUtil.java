package org.mwc.cmap.media.utility;

import java.io.File;
import java.util.Date;

import org.eclipse.jface.window.Window;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.media.PlanetmayoFormats;
import org.mwc.cmap.media.dialog.VideoPlayerStartTimeDialog;
import org.mwc.cmap.media.views.VideoPlayerView;

public class OpenVideoPlayerUtil
{
  public static void openVideoPlayer(final String fileName, final Date scenarioStart)
  {
    //#2940 #6
    //if we cannot get the start time from filename open the dialog
    Date start = PlanetmayoFormats.getInstance().parseDateFromFileName(new File(fileName).getName());
    if(start==null) {
      //try to get the start time from last video start time.
      long startTime = PlatformUI.getPreferenceStore().getLong(new File(fileName).getName());
      if(startTime>0) {
        start = new Date(startTime);
      }
      else
      {
        start = scenarioStart;
      }

      if(start == null)
      {
        start = new Date();
      }
      
      VideoPlayerStartTimeDialog dialog = new VideoPlayerStartTimeDialog();
      dialog.setStartTime(start);
      dialog.setBlockOnOpen(true);
      if(dialog.open()==Window.OK) {
        showVideoPlayer(fileName,dialog.getStartTime());
      }
    }
    else {
      showVideoPlayer(fileName, start);
    }
  }
  
  private static void showVideoPlayer(final String fileName,final Date start) {
    IViewPart view = CorePlugin.openSecondaryView(CorePlugin.VIDEO_PLAYER_VIEW,fileNamePartOf(fileName),IWorkbenchPage.VIEW_ACTIVATE);
    if(view instanceof VideoPlayerView) {
      VideoPlayerView videoView = (VideoPlayerView)view;
      videoView.open(fileName,start);
    }
  }
  private static String fileNamePartOf(final String fileName)
  {
    if (fileName == null)
    {
      throw new IllegalArgumentException("file name == null");
    }

    // ok, extract the parent portion
    final String fileNamePart = new File(fileName).getName();
    if(fileNamePart.indexOf(".")>0) {
      return fileNamePart.substring(0, fileNamePart.lastIndexOf("."));
    }
    return "";
  }

}
