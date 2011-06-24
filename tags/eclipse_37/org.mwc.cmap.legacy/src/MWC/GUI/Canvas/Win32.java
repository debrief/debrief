package MWC.GUI.Canvas;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: Win32.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: Win32.java,v $
// Revision 1.2  2004/05/25 14:43:59  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:15  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:07  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:37+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:10+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:21+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:46:32+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:43:04+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:43:09  ianmayo
// initial version
//
// Revision 1.2  1999-11-15 15:43:02+00  ian_mayo
// removing @dll import command
//
// Revision 1.1  1999-10-12 15:37:03+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:48+01  administrator
// Initial revision
//
// Revision 1.1  1999-07-19 12:39:45+01  administrator
// Initial revision
//


public class Win32
{
  /**
   * @dl.import("GDI32", auto) 
   */
  public static native int CopyMetaFile(int anonymous0, String anonymous1);

  /**
   * @dl.import("USER32",auto) 
   */
  public static native int SetClipboardData(int uFormat, int hMem);

  /**
   * @dl.import("KERNEL32",auto) 
   */
  public static native int GlobalLock(int hMem);

  /**
   * @dl.import("KERNEL32",auto) 
   */
  public static native int GlobalAlloc(int uFlags, int dwBytes);

  /**
   * @dl.import("USER32",auto) 
   */
  public static native boolean CloseClipboard();

  /**
   * @dl.import("KERNEL32",auto) 
   */
  public static native int GlobalUnlock(int hMem);

  /**
   * @dl.import("USER32",auto) 
   */
  public static native boolean OpenClipboard(int hWndNewOwner);

  /**
   * @dl.import("kernel32",auto) 
   */
  public static native boolean CopyMemory(int addr, METAFILEPICT mp, int size);
  
  /**
   * @dl.import("USER32",auto) 
   */
  public static native boolean EmptyClipboard();
  public static final int GMEM_MOVEABLE = 0x0002;
  /**
   * @dl.struct() 
   */
  public static class METAFILEPICT
  {
    public int mm;
    public int xExt;
    public int yExt;
    public int hMF;
  }

  /**
   * @dl.import("GDI32", auto) 
   */
  public static native int CreateMetaFile(String anonymous0);

  /**
   * @dl.import("GDI32",auto) 
   */
  public static native int SetMapMode(int anonymous0, int anonymous1);

  /**
   * @dl.import("GDI32",auto) 
   */
  public static native boolean LineTo(int anonymous0, int anonymous1, int anonymous2);

  /**
   * @dl.import("GDI32",auto) 
   */
  public static native int CloseMetaFile(int anonymous0);
  public static final int MM_ANISOTROPIC = 8;
  public static final int CF_METAFILEPICT = 3;

	
}
