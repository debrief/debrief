/*****************************************************************************
 *  J3D.org Copyright (c) 2000
 *   Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom;

// Standard imports
import javax.media.j3d.Appearance;
import javax.media.j3d.Shape3D;
import javax.media.j3d.QuadArray;

import javax.vecmath.Vector3f;

// Application specific imports

/**
 * A torus where you specify inner radius, outer radius, inner-inner radius,
 * spacing of spirals, arclength of an average quad, coordinates, and
 * appearance.
 * <p>
 * This algorithm is not totally correct: small breaks appear between each
 * spiral.
 *
 * @author Unknown
 * @version $Revision: 1.1.1.1 $
 */
public class SpiralTorus
{
    private static final float INNER_RADIUS = 0.2f;
    private static final float OUTER_RADIUS = 0.3f;
    private static final float INNER_INNER_RADIUS = 1.0f;
    private static final float SPACING = 0.05f;
    private static final float ARCLENGTH = 0.03f;
    private static final float X_POSITION = 0.0f;
    private static final float Y_POSITION = 0.0f;
    private static final float Z_POSITION = 0.0f;

    private Shape3D storus;
    private float[] qverts;
    private float theta, t,
        calct, rlower, rupper, num, mag, x1, x2, x3, x4, y1, y2, y3, y4,
        z1, z2, z3, z4, xn, yn, zn, r;
    private int half, numcirc, upcount = 1, nspirals;
    private int vertCount = 0;
    private int normalcount = 0;
    private Vector3f[] normals;

    // * Spacing determines how many spirals to make
    // * inner-inner radius is distance from middle of torus to outermost edge
    // * inner radius is inside radius of each spiral
    // * outer radius is outside radius of each spiral

    // This class does not construct a percect SpiralTorus.  There are small
    // breaks between each spiral which need to be fixed.

    /**
    *     Constructs a Torus of inner radius 0.2, outer radius 0.3,
    *     inner-inner radius 1.0, arclength of a quad 0.1,
    *     spacing 0.05, at coordinates 0, 0, 0,
    *     with null Appearance
    **/
    public  SpiralTorus()
    {
        this(INNER_RADIUS, OUTER_RADIUS, INNER_INNER_RADIUS, SPACING, ARCLENGTH,
            X_POSITION, Y_POSITION, Z_POSITION, null);
    }

    /**
    *     Constructs a Torus of inner radius 'ir', outer radius 'or',
    *     inner-inner radius 1.0, arclength of a quad 0.1,
    *     spacing 0.05, at coordinates 0, 0, 0,
    *     with null Appearance
    **/
    public  SpiralTorus(float ir, float or)
    {
        this(ir, or, INNER_INNER_RADIUS, SPACING, ARCLENGTH,
            X_POSITION, Y_POSITION, Z_POSITION, null);
    }

    /**
    *     Constructs a Torus of inner radius 0.2, outer radius 0.3,
    *     inner-inner radius 1.0, arclength of a quad 0.1,
    *     spacing 0.05, at coordinates 0, 0, 0,
    *     with Appearance torusAppearance
    **/
    public  SpiralTorus(Appearance torusAppearance)
    {
        this(INNER_RADIUS, OUTER_RADIUS, INNER_INNER_RADIUS, SPACING, ARCLENGTH,
            X_POSITION, Y_POSITION, Z_POSITION, torusAppearance);
    }

    /**
    *     Constructs a Torus of inner radius 'ir', outer radius 'or',
    *     inner-inner radius 1.0, arclength of a quad 'arclength',
    *     spacing 0.05, at coordinates 0, 0, 0,
    *     with null Appearance
    **/
    public  SpiralTorus(float ir, float or, float arclength)
    {
        this(ir, or, INNER_INNER_RADIUS, SPACING, arclength,
            X_POSITION, Y_POSITION, Z_POSITION, null);
    }

    /**
    *     Constructs a Torus of inner radius 'ir', outer radius 'or',
    *     inner-inner radius 1.0, arclength of a quad 0.1,
    *     spacing 0.05, at coordinates 0, 0, 0,
    *     with Appearance torusAppearance
    **/
    public  SpiralTorus(float ir, float or, Appearance torusAppearance)
    {
        this(ir, or, INNER_INNER_RADIUS, SPACING, ARCLENGTH,
            X_POSITION, Y_POSITION, Z_POSITION, torusAppearance);
    }

    /**
    *     Constructs a Torus of inner radius 'ir', outer radius 'or',
    *     inner-inner radius 1.0, arclength of a quad 'arclength',
    *     spacing 0.05, at coordinates 0, 0, 0,
    *     with Appearance torusAppearance
    **/
    public  SpiralTorus(float ir, float or, float arclength,
        Appearance torusAppearance)
    {
        this(ir, or, INNER_INNER_RADIUS, SPACING, arclength,
            X_POSITION, Y_POSITION, Z_POSITION, torusAppearance);
    }

    /**
    *     Constructs a Torus centered at 'xpos', 'ypos', 'zpos',
    *     with inner radius 'ir', outer radius 'or',
    *     arclength of a quad 'arclength',
    *     and Appearance 'torusAppearance'
    **/
    public  SpiralTorus(float ir, float or, float iir, float spacing,
        float arclength, float xpos, float ypos, float zpos,
        Appearance torusAppearance)
    {
        if (spacing <= 0.0)
        {
            spacing = 0.01f;
            System.out.println("Spacing was set too low.. defaulting to 0.01");
        }
        if (arclength <= 0.0)
        {
            arclength = 0.5f;
            System.out.println("Arclength was set too low.. defaulting to 0.5");
        }
        t = arclength;
        r = (or - ir) / 2;
        num = ((float) (2*Math.PI*r)/t);
        numcirc = (int) num;

        // Change the arclength to the closest value that fits.
        calct = ((float) (2*Math.PI*r)/numcirc);
        t = calct;
        theta = t/r;
        half = (int) ((r*Math.PI)/t)+1;

        float calcor = (iir+or) / (spacing);
        nspirals = (int) calcor;
        if (nspirals == 0)
        {
            nspirals = 1;
            System.out.println("InnerInnerRadius is too small.. generating just one section");
        }
        if (calcor - nspirals != 0)
        {
            spacing = spacing + (spacing / (calcor - nspirals));
        }

        // In case theres too many quads...
        if ( 2*(half*(6*(2*numcirc))) > 600000)
        {
            System.out.println("Too detailed! Choose a bigger" +
                " arclength or smaller radiuses.");
            System.exit(0);
        }
        qverts = new float[nspirals*2*(half*(6*(2*(numcirc))))];
        float inarc = ((float) (2*Math.PI*(iir+or))/nspirals);
        // 6.2831855
        // This number is a brutal hack that should be removed once this algorithm is corrected.
        spacing = inarc/6.2831855f;
        float sigma = inarc/(iir+or);

        float tempy;
        float tempinc = spacing/numcirc;

        float sinc = sigma/numcirc;
        float sadder = -sigma;

        int spiralVertCount = 0;
        for (int sp=0; sp < nspirals; sp++)
        {
            if (sp == 0)
            {
                rlower = or;// radius of first loop
                rupper = or - (r - (r*((float) Math.cos(theta))));// radius of second loop
                for (int k=0; k < 2*half; k++)
                {
                    tempy = 0;
                    sadder = sigma*sp;
                    for (int i=0 ; i < numcirc; i++)
                    {
                        x1 =  rlower*((float) Math.cos(theta*i));
                        qverts[vertCount] = x1 + xpos;
                        vertCount++;

                        z1 =  rlower*((float) Math.sin(theta*i));
                        qverts[vertCount] = -z1 + zpos;
                        vertCount++;

                        y1 = r*((float) Math.sin(theta*(k))) + tempy;
                        qverts[vertCount] = y1 + ypos;
                        vertCount++;

                        double xr = (x1*Math.cos(sigma*sp) - y1*Math.sin(sigma*sp));
                        double  yr = (x1*Math.sin(sigma*sp) + y1*Math.cos(sigma*sp));
                        qverts[vertCount-3] = (float) xr + iir*((float) Math.cos(sadder));
                        qverts[vertCount-1] = (float) yr + iir*((float) Math.sin(sadder));

                        sadder = sadder + sinc;
                        tempy = tempy + tempinc;

                        x2 =  rlower*((float) Math.cos(theta*(i+1)));
                        qverts[vertCount] = x2 + xpos;
                        vertCount++;

                        z2 =  rlower*((float) Math.sin(theta*(i+1)));
                        qverts[vertCount] = -z2 + zpos;
                        vertCount++;

                        y2 = r*((float) Math.sin(theta*(k))) + tempy;
                        qverts[vertCount] = y2 + ypos;
                        vertCount++;

                        xr = (x2*Math.cos(sigma*sp) - y2*Math.sin(sigma*sp));
                        yr = (x2*Math.sin(sigma*sp) + y2*Math.cos(sigma*sp));
                        qverts[vertCount-3] = (float) xr + iir*((float) Math.cos(sadder));
                        qverts[vertCount-1] = (float) yr + iir*((float) Math.sin(sadder));

                        x3 =  rupper*((float) Math.cos(theta*(i+1)));
                        qverts[vertCount] = x3 + xpos;
                        vertCount++;

                        z3 =  rupper*((float) Math.sin(theta*(i+1)));
                        qverts[vertCount] = -z3 + zpos;
                        vertCount++;

                        y3 =  r*((float) Math.sin(theta*(k+1))) + tempy;
                        qverts[vertCount] = y3 + ypos;
                        vertCount++;

                        xr = (x3*Math.cos(sigma*sp) - y3*Math.sin(sigma*sp));
                        yr = (x3*Math.sin(sigma*sp) + y3*Math.cos(sigma*sp));
                        qverts[vertCount-3] = (float) xr + iir*((float) Math.cos(sadder));
                        qverts[vertCount-1] = (float) yr + iir*((float) Math.sin(sadder));

                        tempy = tempy - tempinc;
                        sadder = sadder - sinc;

                        x4 =  rupper*((float) Math.cos(theta*i));
                        qverts[vertCount] = x4 + xpos;
                        vertCount++;

                        z4 =  rupper*((float) Math.sin(theta*i));
                        qverts[vertCount] = -z4 + zpos;
                        vertCount++;

                        y4 =  r*((float) Math.sin(theta*(k+1))) + tempy;
                        qverts[vertCount] = y4 + ypos;
                        vertCount++;

                        xr = (x4*Math.cos(sigma*sp) - y4*Math.sin(sigma*sp));
                        yr = (x4*Math.sin(sigma*sp) + y4*Math.cos(sigma*sp));
                        qverts[vertCount-3] = (float) xr + iir*((float) Math.cos(sadder));
                        qverts[vertCount-1] = (float) yr + iir*((float) Math.sin(sadder));

                        tempy = tempy + tempinc;
                        sadder = sadder + sinc;
                    }
                    rlower = rupper;
                    upcount++;
                    rupper = or - (r - (r*((float) Math.cos(theta*upcount))));

                    spiralVertCount = vertCount;
                }
            }
            else
            {
                for (int k=0; k < spiralVertCount; k = k+3)
                {
                    x1 =  qverts[k];
                    vertCount++;
                    z1 =  qverts[k+1];
                    vertCount++;
                    y1 =  qverts[k+2];
                    vertCount++;
                    double xr = (x1*Math.cos(sigma*sp) - y1*Math.sin(sigma*sp));
                    double  yr = (x1*Math.sin(sigma*sp) + y1*Math.cos(sigma*sp));
                    qverts[vertCount-3] = (float) xr;
                    qverts[vertCount-2] = (float) z1;
                    qverts[vertCount-1] = (float) yr;
                }
            }
        }

        QuadArray torusGeometry = new QuadArray( vertCount/3,
            QuadArray.COORDINATES | QuadArray.NORMALS);

        torusGeometry.setCapability( QuadArray.ALLOW_COLOR_WRITE );
        torusGeometry.setCapability( QuadArray.ALLOW_COORDINATE_WRITE );
        torusGeometry.setCoordinates( 0, qverts );

        // Calculate normals of all points.
        normals = new Vector3f[vertCount/3];
        for (int w = 0; w < vertCount; w = w + 3)
        {
            Vector3f norm = new Vector3f(0.0f, 0.0f, 0.0f);
            mag = qverts[w] * qverts[w] + qverts[w+1] *
                qverts[w+1] + qverts[w+2] * qverts[w+2];
            if (mag != 0.0)
            {
                mag = 1.0f / ((float) Math.sqrt(mag));
                xn = qverts[w]*mag;
                yn = qverts[w+1]*mag;
                zn = qverts[w+2]*mag;
                norm = new Vector3f(xn, yn, zn);
            }
            normals[normalcount] = norm;
            torusGeometry.setNormal(normalcount, norm);
            normalcount++;
        }

        storus = new Shape3D(torusGeometry, torusAppearance);
    }

    // Scaling works because QuadArray.ALLOW_COORDINATE_WRITE was set in
    // constructor.
    public void Scale(float xs, float ys, float zs)
    {
        QuadArray qa = (QuadArray) storus.getGeometry();
        for (int i=0; i < qa.getVertexCount(); i++)
        {
            float[] q = new float[3];
            qa.getCoordinate(i, q);
            q[0] = xs * q[0];
            q[1] = ys * q[1];
            q[2] = ys * q[2];

            qa.setCoordinate(i, q);
        }
        storus.setGeometry(qa);
    }

    public Shape3D getChild()
    {
        return storus;
    }
}