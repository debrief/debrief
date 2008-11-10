/*****************************************************************************
 *                      J3D.org Copyright (c) 2000
 *                              Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom;

// Standard imports
import javax.media.j3d.*;

import javax.vecmath.Color3f;

// Application specific imports

/**
 * Representation of a set of axis around the coordinates.
 * <p>
 *
 * Each axis is color coordinated and the length can be adjusted.
 * <p>
 * X axis: Red<br>
 * Y axis: Green<br>
 * Z axis: Blue
 * <p>
 *
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class Axis extends Group
{
    /** The default length of the axis */
    private static final float DEFAULT_AXIS_LENGTH = 5;

    /** The size of the box shape on the end */
    private static final float X_SIZE = 0.05f;

    /**
     * Create a default axis object with each item length 5 from the origin
     */
    public Axis()
    {
        this(DEFAULT_AXIS_LENGTH);
    }

    /**
     * Create an axis object with the given axis length from the origin.
     *
     * @param length The length to use. Must be positive
     */
    public Axis(float length)
    {
        if(length <= 0)
            throw new IllegalArgumentException("Axis length is not positive");

        int format = GeometryArray.COORDINATES | GeometryArray.NORMALS;
        BoxGenerator gen = new BoxGenerator(X_SIZE, X_SIZE, length);

        GeometryData data = new GeometryData();
        data.geometryType = GeometryData.TRIANGLE_STRIPS;
        data.geometryComponents = GeometryData.NORMAL_DATA;

        gen.generate(data);

        TriangleStripArray x_array =
            new TriangleStripArray(data.vertexCount, format, data.stripCounts);

        x_array.setCoordinates(0, data.coordinates);
        x_array.setNormals(0, data.normals);

        gen.setDimensions(X_SIZE, length, X_SIZE);
        gen.generate(data);

        TriangleStripArray y_array =
            new TriangleStripArray(data.vertexCount, format, data.stripCounts);
        y_array.setCoordinates(0, data.coordinates);
        y_array.setNormals(0, data.normals);

        gen.setDimensions(length, X_SIZE, X_SIZE);
        gen.generate(data);

        TriangleStripArray z_array =
            new TriangleStripArray(data.vertexCount, format, data.stripCounts);

        z_array.setCoordinates(0, data.coordinates);
        z_array.setNormals(0, data.normals);

        Color3f blue = new Color3f(0, 0, 0.8f);
        Material blue_material = new Material();
        blue_material.setDiffuseColor(blue);
        blue_material.setLightingEnable(true);

        Color3f red = new Color3f(0.8f,0, 0);
        Material red_material = new Material();
        red_material.setDiffuseColor(red);
        red_material.setLightingEnable(true);

        Color3f green = new Color3f(0, 0.8f, 0);
        Material green_material = new Material();
        green_material.setDiffuseColor(green);
        green_material.setLightingEnable(true);

        Appearance x_app = new Appearance();
        x_app.setMaterial(red_material);

        Appearance y_app = new Appearance();
        y_app.setMaterial(green_material);

        Appearance z_app = new Appearance();
        z_app.setMaterial(blue_material);

        Shape3D x_shape = new Shape3D();
        x_shape.setAppearance(x_app);
        x_shape.addGeometry(x_array);

        Shape3D y_shape = new Shape3D();
        y_shape.setAppearance(y_app);
        y_shape.addGeometry(y_array);

        Shape3D z_shape = new Shape3D();
        z_shape.setAppearance(z_app);
        z_shape.addGeometry(z_array);

        addChild(x_shape);
        addChild(y_shape);
        addChild(z_shape);
    }
}