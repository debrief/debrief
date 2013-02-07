package com.planetmayo.debrief.satc.model.generator;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.vividsolutions.jts.algorithm.BoundaryNodeRule;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.IntersectionMatrix;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.operation.relate.RelateOp;

public class tesselate {
    
    
    public static ArrayList<Geometry> ST_Tile(Geometry p_geom,
                                                          double p_Tile_X,
                                                          double p_Tile_Y,   
                                                          String p_grid_type,
                                                          String p_option,
                                                          int    p_precision)
    throws Exception
    {
        String c_s_empty_geom   = "Geometry must not be null or empty";
        String c_s_unsupported  = "Unsupported geometry type (*GTYPE*)";
        String c_s_point_value  = "p_grid_type parameter value (*VALUE*) must be TILE or POINT";
        String c_s_option_value = "p_option value (*VALUE*) must be MBR, TOUCH, CLIP, HALFCLIP or HALFTOUCH.";
        String v_grid_type      = (p_grid_type==null || p_grid_type.length()==0 ? "TILE"  : p_grid_type.toUpperCase().trim());
        String v_option_value   = (p_option==null    || p_option.length()==0    ? "TOUCH" : p_option.toUpperCase().trim());
        double v_half_x = p_Tile_X / 2.0,
               v_half_y = p_Tile_Y / 2.0;
        int v_loCol,
            v_hiCol,
            v_loRow,
            v_hiRow;
        Geometry v_mbr = null;
        Geometry v_geometry = null;
        Geometry v_clip_geom = null;
        Coordinate[] v_vertices = null;
        ArrayList<Geometry> grid = new ArrayList<Geometry>();

          if ( p_geom == null ||  p_geom.isEmpty() || ! p_geom.isValid() ) {
             throw new Exception(c_s_empty_geom);
          } 
          if ( ! ( p_geom.getGeometryType().equalsIgnoreCase("Point") ||
                   p_geom.getGeometryType().equalsIgnoreCase("LineString") ||
                   p_geom.getGeometryType().equalsIgnoreCase("Polygon") ||
                   p_geom.getGeometryType().equalsIgnoreCase("MultiPoint") ||
                   p_geom.getGeometryType().equalsIgnoreCase("MultiLineString") ||
                   p_geom.getGeometryType().equalsIgnoreCase("MultiPolygon") ) ) {
              throw new Exception(c_s_unsupported.replace("GTYPE",p_geom.getGeometryType()));
          }
          if ( ! ( v_option_value.equals("MBR") ||
                   v_option_value.equalsIgnoreCase("TOUCH") ||
                   v_option_value.equalsIgnoreCase("CLIP") || 
                   v_option_value.equalsIgnoreCase("HALFCLIP") ||
                   v_option_value.equalsIgnoreCase("HALFTOUCH") ) ) { 
            throw new Exception (c_s_option_value.replace("*VALUE*",v_option_value));
          }
          if ( ! ( v_grid_type.equalsIgnoreCase("TILE") ||
                   v_grid_type.equalsIgnoreCase("POINT") ) ) {
             throw new Exception(c_s_point_value.replace("*VALUE*",v_grid_type));
          }
          PrecisionModel      pm = new PrecisionModel(getPrecisionScale(p_precision));
          GeometryFactory     gf = new GeometryFactory(pm,p_geom.getSRID());
          
          if ( p_geom.getGeometryType().equalsIgnoreCase("Point") ||
               p_geom.getGeometryType().equalsIgnoreCase("MultiPoint") ) {
              v_vertices = p_geom.getCoordinates();
              for (int i=0;i<v_vertices.length;i++) {
                  v_loCol = (int)Math.floor(v_vertices[i].x / p_Tile_X );
                  v_loRow = (int)Math.floor(v_vertices[i].y / p_Tile_Y );
                  if (v_grid_type.equalsIgnoreCase("POINT")) {
                      grid.add(gf.createPoint(new Coordinate((v_loCol * p_Tile_X) + v_half_x,(v_loRow * p_Tile_Y) + v_half_y)));
                  } else if ( v_grid_type.equalsIgnoreCase("TILE") ) {
                      grid.add(gf.createPolygon(
                                  gf.createLinearRing(
                                    new Coordinate[] { 
                                        new Coordinate((v_loCol * p_Tile_X),           (v_loRow * p_Tile_Y)),
                                        new Coordinate((v_loCol * p_Tile_X) + p_Tile_X,(v_loRow * p_Tile_Y)),
                                        new Coordinate((v_loCol * p_Tile_X) + p_Tile_X,(v_loRow * p_Tile_Y) + p_Tile_Y),
                                        new Coordinate((v_loCol * p_Tile_X),           (v_loRow * p_Tile_Y) + p_Tile_Y),
                                        new Coordinate((v_loCol * p_Tile_X),           (v_loRow * p_Tile_Y)) } ),
                                  (LinearRing[])null));
                                                                                                     
                  }
              }
          }
          v_mbr = p_geom.getEnvelope();
          // Check for horizontal/vertical
          v_vertices = v_mbr.getCoordinates();
          // LL and UR are coord 0 and 2
          if ( v_vertices[2].x - v_vertices[0].x < p_Tile_X ) {
              v_vertices[0].x = v_vertices[0].x - v_half_x;
              v_vertices[2].x = v_vertices[2].x + v_half_x;
          }
          if ( v_vertices[2].y - v_vertices[0].y < p_Tile_Y ) { 
              v_vertices[0].y = v_vertices[0].y - v_half_y;
              v_vertices[2].y = v_vertices[2].y + v_half_y;
          };
          v_loCol = (int)Math.floor(v_vertices[0].x / p_Tile_X );
          v_loRow = (int)Math.floor(v_vertices[0].y / p_Tile_Y );
          v_hiCol = (int)Math.ceil(v_vertices[2].x / p_Tile_X ) - 1;
          v_hiRow = (int)Math.ceil(v_vertices[2].y/p_Tile_Y) - 1;
          for (int v_col=v_loCol; v_col<=v_hiCol;v_col++) {
             for (int v_row=v_loRow; v_row<=v_hiRow;v_row++) {
                if (v_grid_type.equalsIgnoreCase("POINT")) {
                    v_geometry = gf.createPoint(new Coordinate((v_col * p_Tile_X) + v_half_x,(v_row * p_Tile_Y) + v_half_y));
                } else if ( v_grid_type.equalsIgnoreCase("TILE") ) {
                    v_geometry =  gf.createPolygon(
                                    gf.createLinearRing(
                                      new Coordinate[] { 
                                          new Coordinate(v_col * p_Tile_X,v_row * p_Tile_Y),
                                          new Coordinate((v_col * p_Tile_X) + p_Tile_X,v_row * p_Tile_Y),
                                          new Coordinate((v_col * p_Tile_X) + p_Tile_X,(v_row * p_Tile_Y) + p_Tile_Y),
                                          new Coordinate(v_col * p_Tile_X,(v_row * p_Tile_Y) + p_Tile_Y),
                                          new Coordinate(v_col * p_Tile_X,v_row * p_Tile_Y) } ),
                                    (LinearRing[])null);                                                                                                   
                }
                v_clip_geom = v_geometry;
                 if ( v_option_value.equalsIgnoreCase("MBR") ) {
                     grid.add(v_clip_geom);
                 } else {
                   if ( ! ST_Relate(v_clip_geom,"DETERMINE",p_geom,p_precision).equalsIgnoreCase("DISJOINT") ) {
                       if ( v_option_value.equalsIgnoreCase("CLIP") ||
                            v_option_value.equalsIgnoreCase("HALFCLIP") ||
                            v_option_value.equalsIgnoreCase("HALFTOUCH") &&
                            ( p_geom.getGeometryType().equalsIgnoreCase("ST_Polygon") ||
                              p_geom.getGeometryType().equalsIgnoreCase("ST_MultiPolygon") ) ) {
                           v_clip_geom = v_geometry.intersection(p_geom);
                       }
                       if ( ( v_option_value.equalsIgnoreCase("HALFCLIP") ||
                              v_option_value.equalsIgnoreCase("HALFTOUCH") ) && 
                              v_clip_geom != null ) {
                           if ( v_clip_geom.getArea() < v_geometry.getArea()/2.0) {
                              v_clip_geom = null;
                           } else if ( v_option_value.equalsIgnoreCase("HALFTOUCH") ) {
                              v_clip_geom = v_geometry;
                           }
                       }
                       if ( v_clip_geom != null ) {
                          grid.add(v_clip_geom);
                       }
                   }
                 }
            } // row_iterator;
        } // col_iterator;
          return grid;
    }
    
    public static double getPrecisionScale(int _numDecPlaces)
    {
        return _numDecPlaces < 0 
               ? (double)(1.0/Math.pow(10, Math.abs(_numDecPlaces))) 
               : (double)Math.pow(10, _numDecPlaces);
    }

    public static String ST_Relate(Geometry _geom1,
                                   String _mask,
                                   Geometry _geom2,
                                   int    _precision)
    throws SQLException 
    {
        String returnString = "";
      try
      {
          // Check parameters
          //
          if ( _geom1 == null || _geom2 == null )
              throw new Exception("One or other of supplied Geometries is NULL.");
                
          // Convert Geometries
          //
          
          // Now do the relationship processing
          //
          BoundaryNodeRule bnr = BoundaryNodeRule.MOD2_BOUNDARY_RULE;  // default (OGC SFS) Boundary Node Rule. 
          RelateOp ro = new RelateOp(_geom1, _geom2, bnr);
          IntersectionMatrix im = ro.getIntersectionMatrix();
          if ( im == null ) {
              returnString = "UNKNOWN";
          } else {
              // Process IM
              //
              int dimGeo1 = _geom1.getDimension();
              int dimGeo2 = _geom2.getDimension();
              ArrayList<String> al = new ArrayList<String>();
              if ( im.isContains())                al.add("CONTAINS");
              if ( im.isCoveredBy())               al.add("COVEREDBY");
              if ( im.isCovers())                  al.add("COVERS");
              if ( im.isCrosses( dimGeo1,dimGeo2)) al.add("CROSSES"); 
              if ( im.isDisjoint())                al.add("DISJOINT"); 
              if ( im.isEquals(  dimGeo1,dimGeo2)) al.add("EQUALS");
              if ( im.isIntersects())              al.add("INTERSECTS");
              if ( im.isOverlaps(dimGeo1,dimGeo2)) al.add("OVERLAPS");
              if ( im.isTouches( dimGeo1,dimGeo2)) al.add("TOUCHES");
              if ( im.isWithin())                  al.add("WITHIN");
              // Now compare to user mask
              //
              if ( _mask == null || _mask.length()==0) {
                // make same as determine
                // 
                _mask = "DETERMINE"; 
              }
              
              if ( _mask.equalsIgnoreCase("ANYINTERACT") ) {
                  // If the ANYINTERACT keyword is passed in mask, the function returns TRUE if the two geometries are not disjoint.
                  //
                  return al.size()==0?"UNKNOWN":(al.contains("DISJOINT")?"FALSE":"TRUE");
              } else if ( _mask.equalsIgnoreCase("DETERMINE") ) {
                  // If the DETERMINE keyword is passed in mask, the function returns the one relationship keyword that best matches the geometries.
                  // 
                  Iterator<String> iter = al.iterator();
                  returnString = "";
                  while (iter.hasNext()) {
                      returnString += (String)iter.next() +",";
                  }
                  // remove unwanted end ","
                  returnString = returnString.substring(0, returnString.length()-1);
              } else {
                  // If a mask listing one or more relationships is passed in, the function returns the name of the relationship if it
                  // is true for the pair of geometries. If all relationships are false, the procedure returns FALSE.
                  //
                  StringTokenizer st = new StringTokenizer(_mask.toUpperCase(),",");
                  String token = "";
                  returnString = "";
                  while ( st.hasMoreTokens() ) {
                      token = st.nextToken();
                      if ( al.contains(token) )
                          returnString += token + ",";
                  }
                  if ( returnString.length()==0 )
                      returnString = "FALSE";  // Passed in relationships do not exist 
                  else {
                      // remove unwanted end ","
                      returnString = returnString.substring(0, returnString.length()-1);
                  }
              }
          }
        } catch (Exception e) {
            returnString = "UNKNOWN";
        }
      return returnString;
    }

}
