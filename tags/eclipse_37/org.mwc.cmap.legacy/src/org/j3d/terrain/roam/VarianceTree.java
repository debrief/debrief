/*****************************************************************************
 *                      Modified version (c) j3d.org 2002
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

/*
 * @(#)VarianceTree.java 1.1 02/01/10 09:27:37
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
// none

// Application specific imports
import org.j3d.terrain.TerrainData;

/**
 *
 * @author  paulby
 * @version
 */
class VarianceTree
{
    private int[] vTree;
    private int maxLevels;

    /** The maximum Y for this patch */
    private float maxY = -Float.MAX_VALUE;

    /** The minimumY for this patch */
    private float minY = Float.MAX_VALUE;

    /**
     * Creates new VarianceTree that represents the given patch of data within
     * the full data collection.
     */
    VarianceTree(TerrainData terrainData,
                 int patchSize,
                 int leftX,
                 int leftY,
                 int rightX,
                 int rightY,
                 int apexX,
                 int apexY)
    {
        maxLevels = (int)Math.sqrt(patchSize);

        vTree = new int[ (1 << maxLevels) ];

        computeVariance(terrainData,
                        leftX,
                        leftY,
                        rightX,
                        rightY,
                        apexX,
                        apexY,
                        1,
                        0 );
    }

    /**
     * Compute the maximum variance between this tree and the patch area
     * defined by the parameters.
     *
     * @return The percentage difference
     */
    float computeVariance(TerrainData terrainData,
                          int leftX,
                          int leftY,
                          int rightX,
                          int rightY,
                          int apexX,
                          int apexY,
                          int node,
                          int level)
    {
        int splitX = (leftX + rightX) >> 1;
        int splitY = (leftY + rightY) >> 1;

        float actualHeight = terrainData.getHeightFromGrid( splitX, splitY );

        if(actualHeight > maxY)
            maxY = actualHeight;

        if(actualHeight < minY)
            minY = actualHeight;

        float l_height = terrainData.getHeightFromGrid(leftX, leftY);
        float r_height = terrainData.getHeightFromGrid(rightX, rightY);

        float variance = Math.abs(actualHeight - (l_height + r_height) * 0.5f);

        if(level < (maxLevels - 1))
        {
            float var = computeVariance(terrainData,
                                        apexX,
                                        apexY,
                                        leftX,
                                        leftY,
                                        splitX,
                                        splitY,
                                        node << 1,
                                        level + 1);

            variance = Math.max(variance, var);

            var = computeVariance(terrainData,
                                  rightX,
                                  rightY,
                                  apexX,
                                  apexY,
                                  splitX,
                                  splitY,
                                  1 + (node << 1),
                                  level+1);

            variance = Math.max(variance, var);
        }

        vTree[node] = 1 + (int)variance;

        return variance;
    }

    /**
     * Get the pre-computed variance for the given node of the tree
     */
    int getVariance(int node)
    {
        return vTree[node];
    }

    int getMaxDepth()
    {
        return maxLevels;
    }

    float getMaxY()
    {
        return maxY;
    }

    float getMinY()
    {
        return minY;
    }
}
