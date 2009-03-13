/*****************************************************************************
 *                            (c) j3d.org 2002
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.loaders.vterrain;

// Standard imports
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TriangleStripArray;
import javax.vecmath.Point2d;

import org.j3d.geom.GeometryData;
import org.j3d.geom.terrain.ElevationGridGenerator;
import org.j3d.loaders.HeightMapLoader;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.SceneBase;

/**
 * Loader for the VTerrain Project's BT file format.
 * <p>
 *
 * The mesh produced is, by default, triangle strip arrays. The X axis
 * represents East-West and the Z-axis represents North-South. +X is east,
 * -Z is North. Texture coordinates are generated for the extents based on
 * a single 0-1 scale for the width of the object.
 * <p>
 *
 * The loader produces a single mesh that represents the file's contents. No
 * further processing is performed in the current implementation to break the
 * points into smaller tiles or use multi-resolution terrain structures.
 * <p>
 *
 * The definition of the file format can be found at:
 * <a href="http://www.vterrain.org/Implementation/BT.html">
 *  http://www.vterrain.org/Implementation/BT.html
 * </a>
 *
 * @author  Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class BTLoader extends HeightMapLoader
{
    /** Input stream used to read values from */
    private BufferedInputStream input;

    /** Current parser */
    private BTParser parser;

    /** Generator of the grid structure for the geometry */
    private ElevationGridGenerator generator;

    /** Step information because it is not held anywhere else */
    private Point2d gridStepData;

    /**
     * Construct a new default loader with no flags set
     */
    public BTLoader()
    {
    }

    /**
     * Construct a new loader with the given flags set.
     *
     * @param flags The list of flags to be set
     */
    public BTLoader(int flags)
    {
        super(flags);
    }

    /**
     * Load the scene from the given reader. Always throws an exception as the
     * file format is binary only and readers don't handle this.
     *
     * @param reader The source of input characters
     * @return A description of the scene
     * @throws IncorrectFormatException The file is binary
     */
    public Scene load(java.io.Reader reader)
        throws IncorrectFormatException
    {
        throw new IncorrectFormatException("Loader only handles binary data");
    }

    /**
     * Load a scene from the given filename. The scene instance returned by
     * this loader will have textures already loaded.
     *
     * @param filename The name of the file to load
     * @return A description of the scene
     * @throws FileNotFoundException The reader can't find the file
     * @throws IncorrectFormatException The file is not one our loader
     *    understands
     * @throws ParsingErrorException An error parsing the file
     */
    public Scene load(String filename)
        throws FileNotFoundException,
               IncorrectFormatException,
               ParsingErrorException
    {
        File file = new File(filename);

        if(!file.exists())
            throw new FileNotFoundException("File does not exist");

        if(file.isDirectory())
            throw new FileNotFoundException("File is a directory");

        FileInputStream fis = new FileInputStream(file);
        input = new BufferedInputStream(fis);

        return load();
    }

    /**
     * Load a scene from the named URL. The scene instance returned by
     * this loader will have textures already loaded.
     *
     * @param url The URL instance to load data from
     * @return A description of the scene
     * @throws FileNotFoundException The reader can't find the file
     * @throws IncorrectFormatException The file is not one our loader
     *    understands
     * @throws ParsingErrorException An error parsing the file
     */
    public Scene load(URL url)
        throws FileNotFoundException,
               IncorrectFormatException,
               ParsingErrorException
    {

        try
        {
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();

            if(is instanceof BufferedInputStream)
                input = (BufferedInputStream)is;
            else
                input = new BufferedInputStream(is);
        }
        catch(IOException ioe)
        {
            throw new FileNotFoundException(ioe.getMessage());
        }

        return load();
    }

    /**
     * Do all the parsing work. Convenience method for all to call internally
     *
     * @return The scene description
     * @throws IncorrectFormatException The file is not one our loader
     *    understands
     * @throws ParsingErrorException An error parsing the file
     */
    @SuppressWarnings("deprecation")
		private Scene load()
        throws IncorrectFormatException,
               ParsingErrorException
    {
        float[][] heights = null;

        try
        {
            if(parser == null)
                parser = new BTParser(input);
            else
                parser.reset(input);

            heights = parser.parse();

            input = null;
        }
        catch(IOException ioe)
        {
            throw new ParsingErrorException("Error parsing stream: " + ioe);
        }

        BTHeader header = parser.getHeader();

        float width = (float)(header.rightExtent - header.leftExtent);
        float depth = (float)(header.topExtent - header.bottomExtent);

        gridStepData = new Point2d(width / header.rows,
                                   depth / header.columns);

        if(generator == null)
        {
            generator = new ElevationGridGenerator(width,
                                                   depth,
                                                   header.rows,
                                                   header.columns,
                                                   heights,
                                                   0);
        }
        else
        {
            generator.setDimensions(width, depth, header.rows, header.columns);
            generator.setTerrainDetail(heights, 0);
        }

        GeometryData data = new GeometryData();
        data.geometryType = GeometryData.TRIANGLE_STRIPS;
        data.geometryComponents = GeometryData.NORMAL_DATA |
                                  GeometryData.TEXTURE_2D_DATA;

        generator.generate(data);

        // So that passed, well let's look at the building the scene now. All
        // we need to do is create a single big tri-strip array based on the
        // points.
        //
        // In a later variant, we may want to look at dividing this up into
        // collections of points dependent on the culling algorithm we are
        // going to use or to generate multi-resolution terrains.
        //
        // At some stage, we should use the HeightMapGenerator so that we
        // can throw the GeometryData into the ITSA for the collision and
        // terrain following code.
        SceneBase scene = new SceneBase();
        BranchGroup root_group = new BranchGroup();


        int format = GeometryArray.COORDINATES |
                     GeometryArray.NORMALS |
                     GeometryArray.TEXTURE_COORDINATE_2;

        TriangleStripArray geom =
            new TriangleStripArray(data.vertexCount,
                                   format,
                                   data.stripCounts);

        geom.setCoordinates(0, data.coordinates);
        geom.setNormals(0, data.normals);
        geom.setTextureCoordinates(0, data.textureCoordinates);

        Appearance app = new Appearance();

/* This was in paul's original code. Do we really need this?
        PolygonAttributes poly = new PolygonAttributes();
        poly.setPolygonMode( PolygonAttributes.POLYGON_LINE );
        poly.setCullFace( PolygonAttributes.CULL_NONE );
        app.setPolygonAttributes( poly );
*/
        Shape3D shape = new Shape3D(geom, app);

        root_group.addChild(shape);
        scene.setSceneGroup(root_group);

        return scene;
    }

    /**
     * Get the header used to describe the last stream parsed. If no stream
     * has been parsed yet, this will return null.
     *
     * @return The header for the last read stream or null
     */
    public BTHeader getHeader()
    {
        return parser.getHeader();
    }

    /**
     * Return the height map created for the last stream parsed. If no stream
     * has been parsed yet, this will return null.
     *
     * @return The array of heights in [row][column] order or null
     */
    public float[][] getHeights()
    {
        return parser.getHeights();
    }

    /**
     * Fetch information about the real-world stepping sizes that this
     * grid uses.
     *
     * @return The stepping information for width and depth
     */
    public Point2d getGridStep()
    {
        return gridStepData;
    }

}
