/*
*	@(#)Text2D.java 1.15 02/02/07 14:47:08
*
* Copyright (c) 1996-2002 Sun Microsystems, Inc. All Rights Reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
*
* - Redistributions of source code must retain the above copyright
*   notice, this list of conditions and the following disclaimer.
*
* - Redistribution in binary form must reproduce the above copyright
*   notice, this list of conditions and the following disclaimer in
*   the documentation and/or other materials provided with the
*   distribution.
*
* Neither the name of Sun Microsystems, Inc. or the names of
* contributors may be used to endorse or promote products derived
* from this software without specific prior written permission.
*
* This software is provided "AS IS," without a warranty of any
* kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
* WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
* EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
* SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
* DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN
* OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
* FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR
* PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF
* LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE,
* EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
*
* You acknowledge that Software is not designed,licensed or intended
* for use in the design, construction, operation or maintenance of
* any nuclear facility.
*/

package MWC.GUI.Java3d;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.util.Hashtable;

import javax.media.j3d.*;
import javax.vecmath.Color3f;


/**
 * A Text2D object is a representation of a string as a texture mapped
 * rectangle.  The texture for the rectangle shows the string as rendered in
 * the specified color with a transparent background.  The appearance of the
 * characters is specified using the font indicated by the font name, size
 * and style (see java.awt.Font).  The approximate height of the rendered
 * string will be the font size times the rectangle scale factor, which has a
 * default value of 1/256.  For example, a 12 point font will produce
 * characters that are about 12/256 = 0.047 meters tall. The lower left
 * corner of the rectangle is located at (0,0,0) with the height
 * extending along the positive y-axis and the width extending along the
 * positive x-axis.
 */
public class ColorChangeText2D extends Shape3D {

  // This table caches FontMetrics objects to avoid the huge cost
  // of re-retrieving metrics for a font we've already seen.
  private static Hashtable<Font, FontMetrics> metricsTable = new Hashtable<Font, FontMetrics>();
  float rectangleScaleFactor = 1f/256f;

  Color3f   color = new Color3f();
  String    fontName;
  int       fontSize, fontStyle;

  String text;


  /**
   * Creates a Shape3D object which holds a
   * rectangle that is texture-mapped with an image that has
   * the specified text written with the specified font
   * parameters.
   *
   * @param text The string to be written into the texture map.
   * @param color The color of the text string.
   * @param fontName The name of the Java font to be used for
   *  the text string.
   * @param fontSize The size of the Java font to be used.
   * @param fontStyle The style of the Java font to be used.
   */
  public ColorChangeText2D(String text, Color3f color, String fontName,
                           int fontSize, int fontStyle) {

    this.color.set(color);
    this.fontName = fontName;
    this.fontSize = fontSize;
    this.fontStyle = fontStyle;
    this.text = text;

    updateText2D(text, color, fontName, fontSize, fontStyle);
  }

  /*
  * Changes text of this Text2D to 'text'. All other
  * parameters (color, fontName, fontSize, fontStyle
  * remain the same.
  * @param text The string to be set.
  */
  public void setString(String text){
    this.text = text;
    updateText2D(text, color, fontName, fontSize, fontStyle);
  }


  /** change the color of the text message
   *
   * @param color the new color to use
   */
  public void setColor(Color3f color){
    this.color = color;
    updateText2D(text, color, fontName, fontSize, fontStyle);
  }



  private void updateText2D(String text1, Color3f color1, String fontName1,
                            int fontSize1, int fontStyle1) {
    BufferedImage bImage = setupImage(text1, color1, fontName1,
      fontSize1, fontStyle1);

    Texture2D t2d = setupTexture(bImage);

    QuadArray rect = setupGeometry(bImage.getWidth(),
      bImage.getHeight());

    Appearance appearance = setupAppearance(t2d);

    setGeometry(rect);
    setAppearance(appearance);
  }


  /**
   * Sets the scale factor used in converting the image width/height
   * to width/height values in 3D.
   *
   * @param newScaleFactor The new scale factor.
   */
  public void setRectangleScaleFactor(float newScaleFactor) {
    rectangleScaleFactor = newScaleFactor;
  }

  /**
   * Gets the current scale factor being used in converting the image
   * width/height to width/height values in 3D.
   *
   * @return The current scale factor.
   */
  public float getRectangleScaleFactor() {
    return rectangleScaleFactor;
  }

  /**
   * Create the ImageComponent and Texture object.
   */
  private Texture2D setupTexture(BufferedImage bImage) {

    ImageComponent imageComponent =
      new ImageComponent2D(ImageComponent.FORMAT_RGBA,
        bImage);
    Texture2D t2d = new Texture2D(Texture2D.BASE_LEVEL,
      Texture.RGBA,
      bImage.getWidth(),
      bImage.getHeight());
    t2d.setMinFilter(Texture.BASE_LEVEL_LINEAR);
    t2d.setMagFilter(Texture.BASE_LEVEL_LINEAR);
    t2d.setImage(0, imageComponent);
    t2d.setEnable(true);

    return t2d;
  }

  /**
   * Creates a BufferedImage of the correct dimensions for the
   * given font attributes.  Draw the given text into the image in
   * the given color.  The background of the image is transparent
   * (alpha = 0).
   */
  @SuppressWarnings("deprecation")
	private BufferedImage setupImage(String text, Color3f color,
                                   String fontName,
                                   int fontSize, int fontStyle) {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Font font = new Font(fontName, fontStyle, fontSize);

    FontMetrics metrics;
    if ((metrics = (FontMetrics)metricsTable.get(font)) == null)
    {
      metrics = toolkit.getFontMetrics(font);
      metricsTable.put(font, metrics);
    }
    int width = metrics.stringWidth(text);
    int descent = metrics.getMaxDescent();
    int ascent = metrics.getMaxAscent();
//    int leading = metrics.getLeading();
    int height = descent + ascent;

    // Need to make width/height powers of 2 because of Java3d texture
    // size restrictions
    int pow = 1;
    for (int i = 1; i < 32; ++i) {
      pow *= 2;
      if (width <= pow)
        break;
    }
    width = Math.max (width, pow);
    pow = 1;
    for (int i = 1; i < 32; ++i) {
      pow *= 2;
      if (height <= pow)
        break;
    }
    height = Math.max (height, pow);

    // For now, jdk 1.2 only handles ARGB format, not the RGBA we want
    BufferedImage bImage = new BufferedImage(width, height,
      BufferedImage.TYPE_INT_ARGB);
    Graphics offscreenGraphics = bImage.createGraphics();

    // First, erase the background to the text panel - set alpha to 0
    Color myFill = new Color(0f, 0f, 0f, 0f);
    offscreenGraphics.setColor(myFill);
    offscreenGraphics.fillRect(0, 0, width, height);

    // Next, set desired text properties (font, color) and draw String
    offscreenGraphics.setFont(font);
    Color myTextColor = new Color(color.x, color.y, color.z, 1f);
    offscreenGraphics.setColor(myTextColor);
    offscreenGraphics.drawString(text, 0, height - descent);

    return bImage;
  }

  /**
   * Creates a rectangle of the given width and height and sets up
   * texture coordinates to map the text image onto the whole surface
   * of the rectangle (the rectangle is the same size as the text image)
   */
  private QuadArray setupGeometry(int width, int height) {
    float zPosition = 0f;
    float rectWidth = (float)width * rectangleScaleFactor;
    float rectHeight = (float)height * rectangleScaleFactor;
    float[] verts1 = {
      rectWidth, 0f, zPosition,
      rectWidth, rectHeight, zPosition,
      0f, rectHeight, zPosition,
      0f, 0f, zPosition
    };
    float[] texCoords = {
      0f, -1f,
      0f, 0f,
      (-1f), 0f,
      (-1f), -1f
    };

    QuadArray rect = new QuadArray(4, QuadArray.COORDINATES |
      QuadArray.TEXTURE_COORDINATE_2);
    rect.setCoordinates(0, verts1);
    rect.setTextureCoordinates(0, 0, texCoords);

    return rect;
  }

  /**
   * Creates Appearance for this Shape3D.  This sets transparency
   * for the object (we want the text to be "floating" in space,
   * so only the text itself should be non-transparent.  Also, the
   * appearance disables lighting for the object; the text will
   * simply be colored, not lit.
   */
  private Appearance setupAppearance(Texture2D t2d) {
    TransparencyAttributes transp = new TransparencyAttributes();
    transp.setTransparencyMode(TransparencyAttributes.BLENDED);
    transp.setTransparency(0f);
    Appearance appearance = new Appearance();
    appearance.setTransparencyAttributes(transp);
    appearance.setTexture(t2d);

    Material m = new Material();
    m.setLightingEnable(false);
    appearance.setMaterial(m);

    return appearance;
  }

  /**
   * Returns the text string
   *
   * @since Java 3D 1.2.1
   */
  public String getString() {
    return text;
  }

  /**
   * Returns the color of the text
   *
   * @since Java 3D 1.2.1
   */
  public Color3f getColor() {
    return color;
  }

  /**
   * Returns the font
   *
   * @since Java 3D 1.2.1
   */
  public String getFontName() {
    return fontName;
  }

  /**
   * Returns the font size
   *
   * @since Java 3D 1.2.1
   */
  public int getFontSize() {
    return fontSize;
  }

  public void setFontSize(int fontSize) {
    this.fontSize = fontSize;

    updateText2D(text, color, fontName, fontSize, fontStyle);

  }

  /**
   * Returns the font style
   *
   * @since Java 3D 1.2.1
   */
  public int getFontStyle() {
    return fontStyle;
  }

}





