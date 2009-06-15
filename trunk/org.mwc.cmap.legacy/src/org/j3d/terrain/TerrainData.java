/*****************************************************************************
 *                      Modified version (c) j3d.org 2002
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

/*
 * @(#)TerrainData.java 1.1 02/01/10 09:29:18
 *
 * Copyright (c) 2000-2002 Sun Microsystems, Inc.  All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *    -Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *    -Redistribution in binary form must reproduct the above copyright notice,
 *     this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that Software is not designed,licensed or intended for use in
 * the design, construction, operation or maintenance of any nuclear facility.
 */
package org.j3d.terrain;

// Standard imports
import java.awt.Rectangle;

import javax.media.j3d.Texture;

import org.j3d.ui.navigation.HeightDataSource;

/**
 * This class provides a generic interface to the terrain dataset.
 * <p>
 *
 * The dataset is represented as a regular grid of heights which use the
 * carto-based notion of the ground being the X-Y plane, rather than the
 * 3D graphics convention of X-Z.
 *
 * @author  Paul Byrne, Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public interface TerrainData extends HeightDataSource
{
    /**
     * Get the coordinate of the point in the grid. This should translate
     * between the grid position (x,y) and the 3D axis system (x,y,z), which
     * don't use the same coordinate system.
     *
     * @param coord the x, y, and z coordinates will be placed in the
     *    first three elements of the array.
     * @param gridX The X coordinate of the position in the grid
     * @param gridY The Y coordinate of the position in the grid
     */
    public void getCoordinate(float[] coord, int gridX, int gridY);

    /**
     * Get the coordinate of the point and corresponding texture coordinate in
     * the grid. Assumes that the grid covers a single large texture rather
     * than multiple smaller textures. This should translate between the grid
     * position (x,y) and the 3D axis system (x,y,z), which don't use the same
     * coordinate system.
     *
     * @param coord he x, y, and z coordinates will be placed in the first
     *   three elements of the array.
     * @param tex 2D coordinates are placed in the first two elements
     * @param gridX The X coordinate of the position in the grid
     * @param gridY The Y coordinate of the position in the grid
     */
    public void getCoordinateWithTexture(float[] coord,
                                         float[] tex,
                                         int gridX,
                                         int gridY);

    /**
     * Get the coordinate of the point and the corresponding color value in
     * the grid. Color values are used when there is no texture supplied, so
     * this should always provide something useful.
     *
     * @param coord he x, y, and z coordinates will be placed in the first
     *   three elements of the array.
     * @param color 3 component colors are placed in the first 3 elements
     * @param gridX The X coordinate of the position in the grid
     * @param gridY The Y coordinate of the position in the grid
     */
    public void getCoordinateWithColor(float[] coord,
                                       float[] color,
                                       int gridX,
                                       int gridY);

    /**
     * Check to see if this terrain data has any texturing at all - either
     * tiled or simple.
     *
     * @return true If a texture is available
     */
    public boolean hasTexture();

    /**
     * Notify the terrain data handler that when generating texture coordinates
     * that we are using tiled textures and that the coordinates generated
     * should be based on the tiled versions of the images rather than a single
     * large texture.
     *
     * @param enabled True to set the mode to tiled, false for single
     * @see #getCoordinateWithTexture(float[], float[], int, int)
     */
    public void setTiledTextures(boolean enabled);

    /**
     * Check to see if the texture coordinates are being tiled.
     *
     * @return true if texture coordinates are currently being tiled
     */
    public boolean isTiledTextures();

    /**
     * Fetch the Texture that is used to cover the entire terrain. If no
     * texture is used, then return null. Assumes a single large texture for
     * the entire terrain.
     *
     * @return The texture instance to use or null
     */
    public Texture getTexture();

    /**
     * Fetch the texture or part of a texture that can be applied to the
     * sub-region of the overall object. This is to allow for texture tiling
     * of very large texture images or terrain items. If there is no texture
     * or no texture for that region, then this should return null.
     *
     * @param bounds The bounds of the region based on the grid positions
     * @return The texture object suitable for that bounds or null
     */
    public Texture getTexture(Rectangle bounds);

    /**
     * Get the height at the specified grid position.
     *
     * @param gridX The X coordinate of the position in the grid
     * @param gridY The Y coordinate of the position in the grid
     * @return The height at the given grid position
     */
    public float getHeightFromGrid(int gridX, int gridY);

    /**
     * Get the width (number of points on the Y axis) of the grid.
     *
     * @return The number of points in the width if the grid
     */
    public int getGridWidth();

    /**
     * Get the depth (number of points on the X axis) of the grid.
     *
     * @return The number of points in the depth of the grid
     */
    public int getGridDepth();

    /**
     * Get the real world distance between consecutive X values in the grid.
     *
     * @return The distance between each step of the grid
     */
    public double getGridXStep();

    /**
     * Get the real world distance between consecutive Y values in the grid.
     *
     * @return The distance between each step of the grid
     */
    public double getGridYStep();
}
