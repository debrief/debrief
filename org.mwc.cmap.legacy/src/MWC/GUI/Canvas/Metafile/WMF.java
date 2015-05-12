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
package MWC.GUI.Canvas.Metafile;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: WMF.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: WMF.java,v $
// Revision 1.3  2004/12/06 09:09:36  Ian.Mayo
// Optimise to reduce object creation
//
// Revision 1.2  2004/05/25 14:44:05  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:15  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:10  Ian.Mayo
// Initial import
//
// Revision 1.4  2002-11-28 09:56:46+00  ian_mayo
// Tidying up (mostly variable renaming)
//
// Revision 1.3  2002-11-25 14:43:47+00  ian_mayo
// Improve parameter naming
//
// Revision 1.2  2002-05-28 09:25:39+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:08+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:17+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:46:30+01  administrator
// Initial revision
//
// Revision 1.2  2001-06-04 09:38:49+01  novatech
// general tidying up of white space
//
// Revision 1.1  2001-01-03 13:43:01+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:43:30  ianmayo
// initial version
//
// Revision 1.5  2000-04-05 09:51:18+01  ian_mayo
// Corrected version MAKE SURE YOU USE THE DEPRECATED API
//
// Revision 1.3  2000-02-16 16:27:59+00  ian_mayo
// more corrections for formatting, since metafiles not currently working
//
// Revision 1.2  2000-02-03 15:07:53+00  ian_mayo
// First issue to Devron (modified files are mostly related to WMF)
//
//


import java.awt.Color;
import java.awt.Font;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;



public class WMF

{
	int maxobjectsize;
	Vector<Integer> wmf;
	Vector<Boolean> handles;

  private String[][] fontnames =
  {
    { "helvetica", "Arial"},
		{ "timesroman", "Times New Roman"},
	  {"courier", "Courier New"},
		{"zapfdingsbat", "Windings"}
  };
  public static final int PS_SOLID = 0;
  public static final int PS_DASH = 1;
  public static final int PS_DOT = 2;
  public static final int PS_DASHDOT = 3;
  public static final int PS_DASHDOTDOT = 4;
  public static final int PS_NULL = 5;
  public static final int PS_INSIDEFRAME = 6;
  public static final int BS_SOLID = 0;
  public static final int BS_HOLLOW = 1;
  public static final int BS_NULL = 1;
  public static final int BS_HATCHED = 2;
  public static final int BS_PATTERN = 3;
  public static final int HS_HORIZONTAL = 0;
  public static final int HS_VERTICAL = 1;
  public static final int HS_FDIAGONAL = 2;
  public static final int HS_BDIAGONAL = 3;
  public static final int HS_CROSS = 4;
  public static final int HS_DIAGCROSS = 5;
  public static final int DIB_RGB_COLORS = 0;
  public static final int DIB_PAL_COLORS = 1;
  public static final int FW_DONTCARE = 100;
  public static final int FW_THIN = 100;
  public static final int FW_NORMAL = 400;
  public static final int FW_BOLD = 700;
  public static final int FW_BLACK = 900;
  public static final byte ANSI_CHARSET = 0;
  public static final byte DEFAULT_CHARSET = 1;
  public static final byte SYMBOL_CHARSET = 2;
  public static final byte SHIFTJIS_CHARSET = -128;
  public static final byte OEM_CHARSET = -1;
  public static final byte OUT_DEFAULT_PRECIS = 0;
  public static final byte OUT_STRING_PRECIS = 1;
  public static final byte OUT_CHARACTER_PRECIS = 2;
  public static final byte OUT_STROKE_PRECIS = 3;
  public static final byte OUT_TT_PRECIS = 4;
  public static final byte OUT_DEVICE_PRECIS = 5;
  public static final byte OUT_RASTER_PRECIS = 6;
  public static final byte CLIP_DEFAULT_PRECIS = 0;
  public static final byte CLIP_CHARACTER_PRECIS = 1;
  public static final byte CLIP_STROKE_PRECIS = 2;
  public static final byte CLIP_MASK = 15;
  public static final byte CLIP_LH_ANGLES = 16;
  public static final byte CLIP_TT_ALWAYS = 32;
  public static final byte DEFAULT_QUALITY = 0;
  public static final byte DRAFT_QUALITY = 1;
  public static final byte PROOF_QUALITY = 2;
  public static final byte DEFAULT_PITCH = 0;
  public static final byte FIXED_PITCH = 1;
  public static final byte VARIABLE_PITCH = 2;
  public static final byte FF_DONTCARE = 0;
  public static final byte FF_ROMAN = 16;
  public static final byte FF_SWISS = 32;
  public static final byte FF_MODERN = 48;
  public static final byte FF_SCRIPT = 64;
  public static final byte FF_DECORATIVE = 80;
  public static final int TRANSPARENT = 1;
  public static final int OPAQUE = 2;
  public static final int MM_TEXT = 1;
  public static final int MM_LOMETRIC = 2;
  public static final int MM_HIMETRIC = 3;
  public static final int MM_LOENGLISH = 4;
  public static final int MM_HIENGLISH = 5;
  public static final int MM_HITWIPS = 6;
  public static final int MM_ISOTROPIC = 7;
  public static final int MM_ANISOTROPIC = 8;
  public static final int ALTERNATE = 1;
  public static final int WINDING = 2;
  public static final int STRETCH_ANDSCANS = 1;
  public static final int STRETCH_ORSCANS = 2;
  public static final int STRETCH_DELETESCANS = 3;
  public static final int TA_TOP = 0;
  public static final int TA_BOTTOM = 8;
  public static final int TA_BASELINE = 24;
  public static final int TA_LEFT = 0;
  public static final int TA_RIGHT = 2;
  public static final int TA_CENTER = 6;
  public static final int TA_NOUPDATECP = 0;
  public static final int TA_UPDATECP = 1;
  public static final int R2_BLACK = 1;
  public static final int R2_NOTMERGEPEN = 2;
  public static final int R2_MASKNOTPENNOT = 3;
  public static final int R2_NOTCOPYPEN = 4;
  public static final int R2_MASKPENNOT = 5;
  public static final int R2_NOT = 6;
  public static final int R2_XORPEN = 7;
  public static final int R2_NOTMASKPEN = 8;
  public static final int R2_MASKPEN = 9;
  public static final int R2_NOTXORPEN = 10;
  public static final int R2_NOP = 11;
  public static final int R2_MERGENOTPEN = 12;
  public static final int R2_COPYPEN = 13;
  public static final int R2_MERGEPENNOT = 14;
  public static final int R2_MERGEPEN = 15;
  public static final int R2_WHITE = 16;
  public static final int ETO_OPAQUE = 2;
  public static final int ETO_CLIPPED = 4;
  public static final int BLACKNESS = 66;
  public static final int NOTSRCERASE = 0x1100a6;
  public static final int NOTSRCCOPY = 0x330008;
  public static final int SRCERASE = 0x440328;
  public static final int DSTINVERT = 0x550009;
  public static final int PATINVERT = 0x5a0049;
  public static final int SRCINVERT = 0x660046;
  public static final int SRCAND = 0x8800c6;
  public static final int MERGEPAINT = 0xbb0226;
  public static final int SRCCOPY = 0xcc0020;
  public static final int SRCPAINT = 0xee0086;
  public static final int PATCOPY = 0xf00021;
  public static final int PATPAINT = 0xfb0a09;
  public static final int WHITENESS = 0xff0062;


	public WMF()
	{
		maxobjectsize=0;
		wmf = new Vector<Integer>(1000, 1000);
		handles = new Vector<Boolean>();
	}

  protected int addHandle()
  {
    for (int i = 0; i < handles.size(); i++)
    {
      if (!((Boolean) handles.elementAt(i)).booleanValue())
      {
        handles.setElementAt(new Boolean(true), i);
        return i;
      }
    }
    handles.addElement(new Boolean(true));
    return handles.size() - 1;
  }



  public void arc(final int i, final int i_0_,
									final int i_1_, final int i_2_,
									final int i_3_, final int i_4_,
									final int i_5_, final int i_6_)
  {
    metaRecord(2071, 8);
    writeWord(i_6_);
    writeWord(i_5_);
    writeWord(i_4_);
    writeWord(i_3_);
    writeWord(i_2_);
    writeWord(i_1_);
    writeWord(i_0_);
    writeWord(i);
  }

  public void bitBlt(final int i, final int i_7_,
										 final int i_8_, final int i_9_,
										 final int i_10_, final int i_11_,
										 final int i_12_, final int[] is,
										 final int i_13_, final int i_14_)
  {
    final int i_15_ = ((i_13_ * 3 + 3) / 4) * 4;
    metaRecord(2368, 28 + (i_15_ / 2) * i_14_);
    writeInteger(i_12_);
    writeWord(i_11_);
    writeWord(i_10_);
    writeWord(i_9_);
    writeWord(i_8_);
    writeWord(i_7_);
    writeWord(i);
    writeBitmap(is, i_13_, i_14_);
  }



  private int calcChecksum(final int i, final int i_16_,
													 final int i_17_, final int i_18_,
													 final int i_19_)
  {
    int i_20_ = 39622;
    i_20_ ^= 0xcdd7;
    i_20_ ^= i;
    i_20_ ^= i_16_;
    i_20_ ^= i_17_;
    i_20_ ^= i_18_;
    i_20_ ^= i_19_;
    return i_20_;
  }



  public void chord(final int i, final int i_21_,
										final int i_22_, final int i_23_,
										final int i_24_, final int i_25_,
										final int i_26_, final int i_27_)
  {
    metaRecord(2096, 8);
    writeWord(i_27_);
    writeWord(i_26_);
    writeWord(i_25_);
    writeWord(i_24_);
    writeWord(i_23_);
    writeWord(i_22_);
    writeWord(i_21_);
    writeWord(i);
  }



  public int createBrushIndirect(final int i, final Color color,
																 final int i_28_)
  {
    metaRecord(764, 4);
    writeWord(i);
    writeColor(color);
    writeWord(i_28_);
    return addHandle();
  }

  public int createFont(final Font font, final int escapement,
												final boolean isUnderline, final boolean isStrikeout)
  {
    char isBold = '\u0190';
    if (font.isBold())
      isBold = '\u02BC';
    return createFontIndirect(-font.getSize(), 0,
															escapement, 0, isBold, font.isItalic(),
															isUnderline, isStrikeout, (byte) 0,
															(byte) 0, (byte) 0,
															(byte) 0, (byte) 0,
															translateFontName(font.getName()));
  }

	@SuppressWarnings("deprecation")
	public int createFontIndirect(final int height, final int width,
																final int escapement, final int orientation,
																final int weight, final boolean isItalic,
																final boolean isUnderline, final boolean isStrikeOut,
																final byte charSet, final byte outputPrecision,
																final byte clipPrecision, final byte outputQuality,
																final byte pitchAndFamily, final String string)
  {
    metaRecord(763, 9 + (string.length() + 2) / 2);
    // scale the height up a bit
    final int theHeight = (int)(height * 1.3);
    writeWord(theHeight);
    writeWord(width);
    writeWord(escapement);
    writeWord(orientation);
    writeWord(weight);
    int i_42_ = 0;
    if (isItalic)
      i_42_ = 1;
    if (isUnderline)
      i_42_ += 256;
    writeWord(i_42_);
    i_42_ = charSet << 8 & 0xff00;
    if (isStrikeOut)
      i_42_++;
    writeWord(i_42_);
    writeWord(outputPrecision | clipPrecision << 8 & 0xff00);
    writeWord(outputQuality | pitchAndFamily << 8 & 0xff00);
    final byte[] is = new byte[string.length() + 2];
// @@IM next method is deprecated
		string.getBytes(0, string.length(), is, 0);
		//is = string.getBytes();
    is[is.length - 2] = 0;
    is[is.length - 1] = 0;
    for (int i_43_ = 0; i_43_ < is.length / 2; i_43_++)
      writeWord(is[i_43_ * 2] | is[i_43_ * 2 + 1] << 8 & 0xff00);
    return addHandle();
  }

  public int createPatternBrush(final int[] is, final int i,
																final int i_44_)
  {
    final int i_45_ = ((i * 3 + 3)) / 4 * 4;
    metaRecord(322, 22 + (i_45_ / 2) * i_44_);
    writeWord(3);
    writeWord(0);
    writeBitmap(is, i, i_44_);
    return addHandle();
  }



  public int createPenIndirect(final int i, final int i_46_,
															 final Color color)
  {
    metaRecord(762, 5);
    writeWord(i);
    writeInteger(i_46_);
    writeColor(color);
    return addHandle();
  }



  public void deleteObject(final int i)
  {
    if (i < handles.size() && ((Boolean) handles.elementAt(i)).booleanValue())
    {
      metaRecord(496, 1);
      writeWord(i);
      handles.setElementAt(new Boolean(false), i);
    }
    else
      throw new ArrayIndexOutOfBoundsException();
  }

  private void deleteObjects()
  {
    for (int i = 0; i < handles.size(); i++)
    {
      if (((Boolean) handles.elementAt(i)).booleanValue())
        deleteObject(i);
    }
  }

  public void ellipse(final int i, final int i_47_,
											final int i_48_, final int i_49_)
  {
    metaRecord(1048, 4);
    writeWord(i_49_);
    writeWord(i_48_);
    writeWord(i_47_);
    writeWord(i);
  }



  public void escape(final int i, final byte[] is)
  {
    metaRecord(1574, 2 + (is.length + 1) / 2);
    writeWord(i);
    writeWord(is.length);
    final byte[] is_50_ = new byte[((is.length + 1) / 2) * 2];
    System.arraycopy(is, 0, is_50_, 0, is.length);
    for (int i_51_ = 0; i_51_ < is_50_.length; i_51_ += 2)
      writeWord(is_50_[i_51_] | is_50_[i_51_ + 1] << 8 & 0xff00);

  }



  public void extTextOut(final int i, final int i_52_, final int i_53_,
												 final Rectangle rectangle, final String string)
  {
    extTextOut(i, i_52_, i_53_, rectangle, string, null);
  }



  @SuppressWarnings("deprecation")
	public void extTextOut(final int i, final int i_54_,
												 final int i_55_, final Rectangle rectangle,
												 final String string, final int[] is)
  {
    int i_56_ = 4 + (string.length() + 1) / 2;
    if (i_55_ != 0)
      i_56_ += 4;
    if (is != null)
      i_56_ += string.length();
    metaRecord(2610, i_56_);
    writeWord(i_54_);
    writeWord(i);
    writeWord(string.length());
    writeWord(i_55_);
    if (i_55_ != 0)
    {
      writeWord(rectangle.x);
      writeWord(rectangle.y);
      writeWord(rectangle.width);
      writeWord(rectangle.height);
    }

    final byte[] is_57_ = new byte[string.length() + 1];
// @@IM
		string.getBytes(0, string.length(), is_57_, 0);
		//is_57_ = string.getBytes();
    is_57_[is_57_.length - 1] = (byte) 0;
    for (int i_58_ = 0; i_58_ < is_57_.length / 2; i_58_++)
    {
			//@@ IM never used:  boolean bool = false;
      final int i_59_ = is_57_[i_58_ * 2] | is_57_[i_58_ * 2 + 1] << 8 & 0xff00;
      writeWord(i_59_);
    }

    if (is != null)
    {
      for (int i_60_ = 0; i_60_ < string.length(); i_60_++)
        writeWord(is[i_60_]);
    }
  }



  public String[][] getTranslateFontNames()
  {
    return fontnames;
  }

  private int highWord(final int i)
  {
    return (i & 0xffff0000) >> 16;
  }



  public void lineTo(final int i, final int i_61_)
  {
    metaRecord(531, 2);
    writeWord(i_61_);
    writeWord(i);
  }



  private int lowWord(final int i)
  {
    return i & 0xffff;
  }



  private void maxObjectSize(final int i)
  {
    if (i > maxobjectsize)
      maxobjectsize = i;
  }



  protected void metaRecord(final int i, final int i_62_)
  {
    final int i_63_ = i_62_ + 3;
    writeInteger(i_63_);
    writeWord(i);
    maxObjectSize(i_63_);
  }



  public void moveTo(final int i, final int i_64_)
  {
    metaRecord(532, 2);
    writeWord(i_64_);
    writeWord(i);
  }



  public void offsetViewportOrg(final int i, final int i_65_)
  {
    metaRecord(529, 2);
    writeWord(i_65_);
    writeWord(i);
  }


  public void offsetWindowOrg(final int i, final int i_66_)
  {
    metaRecord(527, 2);
    writeWord(i_66_);
    writeWord(i);
  }


  private void outputInteger(final OutputStream outputstream, final int i)
    throws IOException
  {
    outputWord(outputstream, lowWord(i));
    outputWord(outputstream, highWord(i));
  }


  private static byte[] _cachedByte = new byte[2];

  private void outputWord(final OutputStream outputstream, final int i)
    throws IOException
  {
//    byte[] is = new byte[2];
    final byte[] is = _cachedByte;
    is[0] = (byte) (i & 0xff);
    is[1] = (byte) ((i & 0xff00) >> 8);
    outputstream.write(is);
  }

  public void patBlt(final int i, final int i_67_,
										 final int i_68_, final int i_69_,
										 final int i_70_)
  {
    metaRecord(1565, 6);
    writeInteger(i_70_);
    writeWord(i_69_);
    writeWord(i_68_);
    writeWord(i_67_);
    writeWord(i);
  }

  public void pie(final int i, final int i_71_,
									final int i_72_, final int i_73_,
									final int i_74_, final int i_75_,
									final int i_76_, final int i_77_)
  {
    metaRecord(2074, 8);
    writeWord(i_77_);
    writeWord(i_76_);
    writeWord(i_75_);
    writeWord(i_74_);
    writeWord(i_73_);
    writeWord(i_72_);
    writeWord(i_71_);
    writeWord(i);
  }



  public void polygon(final int[] is, final int[] is_78_, final int i)
  {
    metaRecord(804, 1 + 2 * i);
    writeWord(i);
    for (int i_79_ = 0; i_79_ < i; i_79_++)
    {
      writeWord(is[i_79_]);
      writeWord(is_78_[i_79_]);
    }
  }

  /**
   * See Windows SDK.
   */
  public void polypolygon(Polygon[]  polys) {
    int npoints = 0;
    for (int j = 0; j < polys.length; j++) {
      npoints += polys[j].npoints;
    }
    metaRecord(0x0538, 1 + polys.length + 2 * npoints);
    writeWord(polys.length);
    for (int j = 0; j < polys.length; j++) {
      writeWord(polys[j].npoints);
    }
    for (int j = 0; j < polys.length; j++) {
      for (int i = 0; i < polys[j].npoints; i++) {
        writeWord(polys[j].xpoints[i]);
        writeWord(polys[j].ypoints[i]);
      }
    }
  }


  public void polyline(final int[] is, final int[] is_80_, final int i)
  {
    metaRecord(805, 1 + 2 * i);
    writeWord(i);
    for (int i_81_ = 0; i_81_ < i; i_81_++)
    {
      writeWord(is[i_81_]);
      writeWord(is_80_[i_81_]);
    }
  }



  public void rectangle(final int i, final int i_82_,
												final int i_83_, final int i_84_)
  {
    metaRecord(1051, 4);
    writeWord(i_84_);
    writeWord(i_83_);
    writeWord(i_82_);
    writeWord(i);
  }



  public void roundRect(final int i, final int i_85_,
												final int i_86_, final int i_87_,
												final int i_88_, final int i_89_)
  {
    metaRecord(1564, 6);
    writeWord(i_89_);
    writeWord(i_88_);
    writeWord(i_87_);
    writeWord(i_86_);
    writeWord(i_85_);
    writeWord(i);
  }



  public void scaleViewportExt(final int i, final int i_90_,
															 final int i_91_, final int i_92_)
  {
    metaRecord(1042, 4);
    writeWord(i_92_);
    writeWord(i_91_);
    writeWord(i_90_);
    writeWord(i);
  }



  public void scaleWindowExt(final int i, final int i_93_,
														 final int i_94_, final int i_95_)
  {
    metaRecord(1024, 4);
    writeWord(i_95_);
    writeWord(i_94_);
    writeWord(i_93_);
    writeWord(i);
  }



  public void selectObject(final int i)
  {
    if (i < handles.size() && ((Boolean) handles.elementAt(i)).booleanValue())
    {
      metaRecord(301, 1);
      writeWord(i);
    }
    else
      throw new ArrayIndexOutOfBoundsException();
  }



  public void setBKColor(final Color color)
  {
    metaRecord(513, 2);
    writeColor(color);
  }

  public void setBKMode(final int i)
  {
    metaRecord(258, 1);
    writeWord(i);
  }


  public void setMapMode(final int i)
  {
    metaRecord(259, 1);
    writeWord(i);
  }


  public void setPixel(final int i, final int i_96_, final Color color)
  {
    metaRecord(1055, 4);
    writeColor(color);
    writeWord(i_96_);
    writeWord(i);
  }


  public void setPolyFillMode(final int i)
  {
    metaRecord(262, 1);
    writeWord(i);
  }


  public void setROP2(final int i)
  {
    metaRecord(260, 1);
    writeWord(i);
  }

  public void setStretchBltMode(final int i)
  {
    metaRecord(263, 1);
    writeWord(i);
  }

  public void setTextAlign(final int i)
  {
    metaRecord(302, 1);
    writeWord(i);
  }

  public void setTextCharacterExtra(final int i)
  {
    metaRecord(264, 1);
    writeWord(i);
  }

  public void setTextColor(final Color color)
  {
    metaRecord(521, 2);
    writeColor(color);
  }

  public void setTextJustification(final int i, final int i_97_)
  {
    metaRecord(522, 2);
    writeWord(i_97_);
    writeWord(i);
  }

  public void setTranslateFontNames(final String strings[][])
  {
    fontnames = strings;
  }

  public void setViewportExt(final int i, final int i_98_)
  {
    metaRecord(526, 2);
    writeWord(i_98_);
    writeWord(i);
  }


  public void setViewportOrg(final int i, final int i_99_)
  {
    metaRecord(525, 2);
    writeWord(i_99_);
    writeWord(i);
  }

  public void setWindowExt(final int width, final int height)
  {
    metaRecord(524, 2);
    writeWord(height);
    writeWord(width);
  }

  public void setWindowOrg(final int i, final int i_101_)
  {
    metaRecord(523, 2);
    writeWord(i_101_);
    writeWord(i);
  }

  public void stretchBlt(final int i, final int i_102_,
												 final int i_103_, final int i_104_,
												 final int i_105_, final int i_106_,
												 final int i_107_, final int i_108_,
												 final int i_109_, final int[] is,
												 final int i_110_, final int i_111_)
  {
    final int i_112_ = ((i_110_ * 3 + 3) / 4) * 4;
    metaRecord(2881, 30 + (i_112_ / 2) * i_111_);
    writeInteger(i_109_);
    writeWord(i_108_);
    writeWord(i_107_);
    writeWord(i_106_);
    writeWord(i_105_);
    writeWord(i_104_);
    writeWord(i_103_);
    writeWord(i_102_);
    writeWord(i);
    writeBitmap(is, i_110_, i_111_);
  }

  @SuppressWarnings("deprecation")
	public void textOut(final int i, final int i_113_, final String string)
  {
    metaRecord(1313, 3 + (string.length() + 1) / 2);
    writeWord(string.length());
    final byte[] is = new byte[string.length() + 1];
    // @@IM next command is deprecated
		string.getBytes(0, string.length(), is, 0);
		//is = string.getBytes();
    is[is.length - 1] = (byte) 0;
    for (int i_114_ = 0; i_114_ < is.length / 2; i_114_++)
    {
      //@@ IM never used:  boolean bool = false;
      final int i_115_ = is[i_114_ * 2] | is[i_114_ * 2 + 1] << 8 & 0xff00;
      writeWord(i_115_);
    }
    writeWord(i_113_);
    writeWord(i);
  }

  public String translateFontName(final String string)
  {
    String string_116_ = string;
    for (int i = 0; i < fontnames.length; i++)
    {
      if (string.toLowerCase().equals(fontnames[i][0]))
        string_116_ = fontnames[i][1];
    }
    return string_116_;
  }

  protected void writeBitmap(final int[] is, final int i, final int i_117_)
  {
    final int i_118_ = ((i * 3 + 3) / 4) * 4;
    final byte[] is_119_ = new byte[i_118_ * i_117_];
    for (int i_120_ = 0; i_120_ < i_117_; i_120_++)
    {
      for (int i_121_ = 0; i_121_ < i; i_121_++)
      {
        final int i_122_ = i_121_ * 3 + i_120_ * i_118_;
        final int i_123_ = is[i_121_ + i_120_ * i];
        is_119_[i_122_ + 2] = (byte) (i_123_ >> 16 & 0xff);
        is_119_[i_122_ + 1] = (byte) (i_123_ >> 8 & 0xff);
        is_119_[i_122_] = (byte) (i_123_ & 0xff);
      }
    }
    writeInteger(40);
    writeInteger(i);
    writeInteger(i_117_);
    writeWord(1);
    writeWord(24);
    writeInteger(0);
    writeInteger(0);
    writeInteger(0);
    writeInteger(0);
    writeInteger(0);
    writeInteger(0);
    for (int i_124_ = i_117_ - 1; i_124_ >= 0; i_124_--)
    {
      final int i_125_ = i_124_ * i_118_;
      for (int i_126_ = 0; i_126_ < i_118_; i_126_ += 2)
      {
        final int i_127_ = i_126_ + i_125_;
        writeWord(is_119_[i_127_ + 1] << 8 & 0xff00 | is_119_[i_127_] & 0xff);
      }
    }
  }

  private void writeBody(final OutputStream outputstream)
    throws IOException
  {
    for (int i = 0; i < this.wmf.size(); i++)
      outputWord(outputstream, ((Integer) this.wmf.elementAt(i)).intValue());
  }

  protected void writeColor(final Color color)
  {
    writeInteger(color.getRed() & 0xff | color.getGreen() << 8 & 0xff00 | color.getBlue() << 16 & 0xff0000);
  }

  private void writeHeader(final OutputStream outputstream)
    throws IOException
  {
    setROP2(13);
    setTextCharacterExtra(0);
    setMapMode(1);
    setWindowOrg(0, 0);
    selectObject(createPenIndirect(0, 1, Color.black));
    selectObject(createBrushIndirect(2, Color.black, 2));
    deleteObjects();
    metaRecord(0, 0);
    outputWord(outputstream, 1);
    outputWord(outputstream, 9);
    outputWord(outputstream, 768);
    outputInteger(outputstream, this.wmf.size() + 9);
    outputWord(outputstream, handles.size());
    outputInteger(outputstream, maxobjectsize);
    outputWord(outputstream, 0);
  }



  protected void writeInteger(final int i)
  {
    writeWord(lowWord(i));
    writeWord(highWord(i));
  }



  private void writePlaceableHeader(final OutputStream outputstream,
																		final int i, final int i_134_,
																		final int i_135_, final int i_136_,
																		final int i_137_)
    throws IOException
  {
    outputInteger(outputstream, 0x9ac6cdd7);
    outputWord(outputstream, 0);
    outputWord(outputstream, i);
    outputWord(outputstream, i_134_);
    outputWord(outputstream, i_135_);
    outputWord(outputstream, i_136_);
    outputWord(outputstream, i_137_);
    outputInteger(outputstream, 0);
    outputWord(outputstream, calcChecksum(i_137_, i, i_134_, i_135_, i_136_));
  }



  public void writePlaceableWMF(final OutputStream outputstream, final int i,
																final int i_138_, final int i_139_,
																final int i_140_, final int i_141_)
    throws IOException
  {
    writePlaceableHeader(outputstream, i, i_138_, i_139_, i_140_, i_141_);
    writeWMF(outputstream);
  }

  public void writeWMF(final OutputStream outputstream)
    throws IOException
  {
    writeHeader(outputstream);
    writeBody(outputstream);
  }

  protected void writeWord(final int i)
  {
    this.wmf.addElement(new Integer(i));
  }

}

