/*****************************************************************************
 *                      Modified version (c) j3d.org 2002
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

/*
 * @(#)Patch.java 1.1 02/01/10 09:27:28
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
package org.j3d.terrain.roam;

// Standard imports
import java.util.LinkedList;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.TriangleArray;
import javax.media.j3d.BoundingBox;
import javax.media.j3d.Shape3D;
import javax.media.j3d.GeometryUpdater;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3f;

// Application specific imports
import org.j3d.terrain.ViewFrustum;
import org.j3d.terrain.TerrainData;

/**
 * A patch represents a single piece of terrain geometry that can be
 * rendered as a standalone block.
 * <p>
 *
 * A patch represents a single block of geometry within the overall scheme
 * of the terrain data. Apart from a fixed size nothing else is fixed in this
 * patch. The patch consists of a single TriangleArray that uses a geometry
 * updater (geometry by reference is used) to update the geometry each frame
 * as necessary. It will, when instructed, dynamically recalculate what
 * vertices need to be shown and set those into the geometry array.
 *
 * @author  Paul Byrne, Justin Couch
 * @version
 */
class Patch implements GeometryUpdater
{
    /** The final size in number of grid points for this patch */
    private final int PATCH_SIZE;

    /** The values of the nodes in the NW triangle of this patch */
    TreeNode NWTree;

    /** The values of the nodes in the NW triangle of this patch */
    TreeNode SETree;

    private VarianceTree NWVariance;
    private VarianceTree SEVariance;

    /** The J3D geometry for this patch */
    private Shape3D shape3D;

    private int xOrig;
    private int yOrig;

    private TerrainData terrainData;
    private Patch westPatchNeighbour;
    private Patch southPatchNeighbour;
    private VertexData vertexData;

    private TriangleArray geom;

    /** The maximum Y for this patch */
    private float maxY;

    /** The minimumY for this patch */
    private float minY;

    /**
     * Create a new patch based on the terrain and appearance information.
     *
     * @param terrainData The raw height map info to use for this terrain
     * @param patchSize The number of grid points to use in the patch on a side
     * @param xOrig The origin of the X grid coord for this patch in the
     *    global set of grid coordinates
     * @param yOrig The origin of the Y grid coord for this patch in the
     *    global set of grid coordinates
     * @param app The global appearance object to use for this patch
     * @param landscapeView The view frustum container used
     * @param westPatchNeighbour the Patch to the west of this patch
     * @param southPatchNeighbour the Patch to the south of this patch
     */
    Patch(TerrainData terrainData,
          int patchSize,
          int xOrig,
          int yOrig,
          Appearance app,
          ViewFrustum landscapeView,
          Patch westPatchNeighbour,
          Patch southPatchNeighbour)
    {
        int height = yOrig + patchSize;
        int width = xOrig + patchSize;

        this.xOrig = xOrig;
        this.yOrig = yOrig;
        this.PATCH_SIZE = patchSize;
        this.terrainData = terrainData;
        this.westPatchNeighbour = westPatchNeighbour;
        this.southPatchNeighbour = southPatchNeighbour;

        boolean has_texture = (app.getTexture() != null);

        vertexData = new VertexData(PATCH_SIZE, has_texture);

        int format = TriangleArray.COORDINATES |
                     TriangleArray.BY_REFERENCE;

        if(has_texture)
            format |= TriangleArray.TEXTURE_COORDINATE_2;
        else
            format |= TriangleArray.COLOR_3;

        geom = new TriangleArray(PATCH_SIZE * PATCH_SIZE * 2 * 3, format);

        geom.setCapability(TriangleArray.ALLOW_REF_DATA_WRITE);
        geom.setCapability(TriangleArray.ALLOW_COUNT_WRITE);
        geom.setCoordRefFloat(vertexData.getCoords());

        if(has_texture)
            geom.setTexCoordRefFloat(0, vertexData.getTextureCoords());
        else
            geom.setColorRefByte(vertexData.getColors());

        NWVariance = new VarianceTree(terrainData,
                                       PATCH_SIZE,
                                       xOrig, yOrig,
                                       width, height,
                                       xOrig, height);

        NWTree = new TreeNode(xOrig, yOrig,        // Left X, Y
                               width, height,       // Right X, Y
                               xOrig, height,       // Apex X, Y
                               1,
                               terrainData,
                               landscapeView,
                               TreeNode.UNDEFINED,
                               1,
                               NWVariance);

        SEVariance = new VarianceTree(terrainData,
                                       PATCH_SIZE,
                                       width, height,       // Left X, Y
                                       xOrig, yOrig,        // Right X, Y
                                       width, yOrig);       // Apex X, Y


        SETree = new TreeNode(width, height,       // Left X, Y
                               xOrig, yOrig,        // Right X, Y
                               width, yOrig,        // Apex X, Y
                               1,
                               terrainData,
                               landscapeView,
                               TreeNode.UNDEFINED,
                               1,
                               SEVariance);

        maxY = Math.max(NWVariance.getMaxY(), SEVariance.getMaxY());
        minY = Math.min(NWVariance.getMinY(), SEVariance.getMinY());

        NWTree.baseNeighbour = SETree;
        SETree.baseNeighbour = NWTree;

        if(westPatchNeighbour!=null)
        {
            NWTree.leftNeighbour = westPatchNeighbour.SETree;
            westPatchNeighbour.SETree.leftNeighbour = NWTree;
        }

        if(southPatchNeighbour!=null)
        {
            SETree.rightNeighbour = southPatchNeighbour.NWTree;
            southPatchNeighbour.NWTree.rightNeighbour = SETree;
        }

        Point3d min_bounds =
            new Point3d(xOrig * terrainData.getGridXStep(),
                        minY,
                        -(yOrig + height) * terrainData.getGridYStep());

        Point3d max_bounds =
            new Point3d((xOrig + width) * terrainData.getGridXStep(),
                        maxY,
                        -yOrig * terrainData.getGridYStep());

        shape3D = new Shape3D(geom, app);
        shape3D.setBoundsAutoCompute(false);
        shape3D.setBounds(new BoundingBox(min_bounds, max_bounds));

        // Just as a failsafe, always set the terrain data in the user
        // data section of the node so that terrain code will find it
        // again, even if the top user is stupid.
        shape3D.setUserData(terrainData);
    }

    //----------------------------------------------------------
    // Methods required by GeometryUpdater
    //----------------------------------------------------------

    /**
     * Update the J3D geometry array for data now.
     *
     * @param geom The geometry object to update
     */
    public void updateData(Geometry geom)
    {
        createGeometry((TriangleArray)geom);
    }

    //----------------------------------------------------------
    // local convenience methods
    //----------------------------------------------------------

    void reset(ViewFrustum landscapeView)
    {
        NWTree.reset(landscapeView);
        SETree.reset(landscapeView);

        NWTree.baseNeighbour = SETree;
        SETree.baseNeighbour = NWTree;

        if(westPatchNeighbour != null)
        {
            NWTree.leftNeighbour = westPatchNeighbour.SETree;
            westPatchNeighbour.SETree.leftNeighbour = NWTree;
        }

        if(southPatchNeighbour != null)
        {
            SETree.rightNeighbour = southPatchNeighbour.NWTree;
            southPatchNeighbour.NWTree.rightNeighbour = SETree;
        }
    }

    /**
     * Change the view to the new position and orientation. In this
     * implementation the direction information is ignored because we have
     * the view frustum to use.
     *
     * @param position The location of the user in space
     * @param landscapeView The viewing frustum information for clipping
     * @param queueManager Manager for ordering terrain chunks
     */
    void setView(Tuple3f position,
                 ViewFrustum landscapeView,
                 QueueManager queueManager)
    {
        NWTree.updateTree(position,
                          landscapeView,
                          NWVariance,
                          TreeNode.UNDEFINED,
                          queueManager);

        SETree.updateTree(position,
                          landscapeView,
                          SEVariance,
                          TreeNode.UNDEFINED,
                          queueManager);
    }

    /**
     * Request an update to the geometry. If the geometry is visible then
     * tell J3D that we would like to update the geometry. It does not directly
     * do the update because we are using GeomByRef and so need to wait for the
     * renderer to tell us when it is OK to do the updates.
     */
    void updateGeometry()
    {
        if(NWTree.visible != ViewFrustum.OUT ||
           SETree.visible != ViewFrustum.OUT ||
            vertexData.getVertexCount() != 0)
        {
            geom.updateData(this);
        }
    }

    /**
     * Fetch the number of triangles that are currently visible in this patch.
     *
     * @return The number of visible triangles
     */
    int getTriangleCount()
    {
        return vertexData.getVertexCount() / 3;
    }

    /**
     * Get the shape node that is used to represent this patch.
     *
     * @return The shape node
     */
    Shape3D getShape3D()
    {
        return shape3D;
    }

    /**
     * Create the geometry needed for this patch. Just sets how many vertices
     * are to be used based on the triangles of the two halves of the tree.
     *
     * @param geom The geometry array to work with
     */
    private void createGeometry(TriangleArray geom)
    {
        vertexData.reset();

        if(NWTree.visible!=ViewFrustum.OUT)
            NWTree.getTriangles(vertexData);

        if(SETree.visible != ViewFrustum.OUT)
            SETree.getTriangles(vertexData);

        geom.setValidVertexCount(vertexData.getVertexCount());
    }
}
