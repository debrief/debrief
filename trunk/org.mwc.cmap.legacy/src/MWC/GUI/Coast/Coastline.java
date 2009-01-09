package MWC.GUI.Coast;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Plottable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Represents a single section of coastline.
 */
public class Coastline implements Plottable, Serializable
{

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Vector<CoastSegment> _data;
  protected WorldArea _myArea;

  public Coastline(InputStream str)
  {
    CoastSegment cs = null;
    String thisLine = null;
    _data = new Vector<CoastSegment>(0, 1);
    int count = 0;

    try
    {
      //DataInputStream dis = new DataInputStream(str);
      BufferedReader dis = new BufferedReader(new InputStreamReader(str));

      boolean segment_saved = false;

      while (((thisLine = dis.readLine()) != null) & (count < 10000))
      {

        // count ++;

        // so we've got a line.
        // see if it represents a new section
        if (thisLine.startsWith("# -b"))
        {
          // this is a new section of coast, store the previous one,
          // if there was one
          if (cs != null)
          {
            _data.addElement(cs);
            // indicate we've saved it
            segment_saved = true;
          }

          // create the new segment
          cs = new CoastSegment();
        }
        else
        {
          // tokenize it
          StringTokenizer st = new StringTokenizer(thisLine);
          // note that we read in the long before the lat from the file
          String longStr = st.nextToken();
          String latStr = st.nextToken();
          double _lat = Double.valueOf(latStr).doubleValue();
          double _long = Double.valueOf(longStr).doubleValue();
          WorldLocation pt = new WorldLocation(_lat, _long, 0);
          cs.addPoint(pt);

          // indicate the new segment has been changed
          segment_saved = false;
        }


      }

      // we need to tidily handle the last segment (which may not have had a #-b id)
      // see if there is a 'trailing' segment
      if (segment_saved == false)
      {
        _data.addElement(cs);
      }


    }
    catch (Exception e)
    {
      MWC.Utilities.Errors.Trace.trace(e);
    }

    // and reset the area covererd
    resetArea();
  }


  protected void resetArea()
  {
    Enumeration<CoastSegment> enumer = _data.elements();
    WorldArea res = null;
    while (enumer.hasMoreElements())
    {
      CoastSegment seg = (CoastSegment) enumer.nextElement();
      if (res == null)
      {
        res = seg.getBounds();
      }
      else
        res.extend(seg.getBounds());

    }
    _myArea = res;
  }


  public WorldArea getBounds()
  {
    if (_myArea == null)
      resetArea();
		else
		{
		}

    return _myArea;

  }


  /*  public void mergeCoasts(){
      Enumeration segs = this.elements();
      int count=0;

      System.out.println("there are " + this.size() + " segments");

      // go through all coastal segments
      while(segs.hasMoreElements()){

        // get this seg
        CoastSegment sg = (CoastSegment)segs.nextElement();

        // go through the other segs
        Enumeration others = this.elements();
        while(others.hasMoreElements()){
          CoastSegment other = (CoastSegment)others.nextElement();

          if(other != sg){
            if(other != null){
              if((other.size()>0) && (sg.size()>0)){
                if(other.getFirst().equals(sg.getLast())){
                  // this is a match, add that one to this one
                  sg.append(other);

                  this.removeElement(other);
                }else if(other.getLast().equals(sg.getFirst())){
                  other.append(sg);

                  this.removeElement(sg);
                }else if(other.getLast().equals(sg.getLast())){
                  sg.appendBackwards(other);

                  this.removeElement(other);
                }else if(other.getFirst().equals(sg.getFirst())){
                 // sg.prependBackwards(other);
                 // this.removeElement(other);
                }

              }
            }
          }
        }
      }
    }*/


	public int compareTo(Plottable arg0)
	{
		Plottable other = (Plottable) arg0;
		return this.getName().compareTo(other.getName());
	}
	
  public int size()
  {
    return _data.size();
  }

  public Object elementAt(int i)
  {
    return _data.elementAt(i);
  }

  public void paint(CanvasType dest)
  {
  }

  /**
   * set the visibility of this item (dummy implementation)
   */
  public void setVisible(boolean val)
  {
  }


  public boolean getVisible()
  {

    return false;
  }

  public double rangeFrom(WorldLocation other)
  {
    return INVALID_RANGE;
  }

  /**
   * return this item as a string
   */
  public String toString()
  {
    return getName();
  }

  public String getName()
  {
    return "Coastline";
  }

  public boolean hasEditor()
  {
    return false;
  }

  public Editable.EditorType getInfo()
  {
    return null;
  }

}


/*
private void writeObject(java.io.ObjectOutputStream out)
  throws IOException
{
  // write the number of elements
  out.writeInt(_data.size());
  Enumeration enumer = _data.elements();
  while(enumer.hasMoreElements())
  {
    CoastSegment cs = (CoastSegment)enumer.nextElement();
    writeSegment(cs, out);
  }
}

private void writeSegment(CoastSegment cs, java.io.ObjectOutputStream out)
  throws IOException
{
  // write the number of elements
  out.writeInt(cs.size());
  out.writeObject(cs.getBounds());
  Enumeration enumer = cs.elements();
  while(enumer.hasMoreElements())
  {
    WorldLocation wl = (WorldLocation)enumer.nextElement();
    out.writeDouble(wl.getLat());
    out.writeDouble(wl.getLong());
    out.writeDouble(wl.getDepth());
  }
}

private void readObject(java.io.ObjectInputStream in)
  throws IOException, ClassNotFoundException
{
  _data=new Vector(0,1);
  long sz = in.readInt();
  for(int i=0;i<sz; i++)
  {
    _data.addElement(readSegment(in));
  }
}

private CoastSegment readSegment(java.io.ObjectInputStream in)
  throws IOException, ClassNotFoundException
{
  CoastSegment cs = new CoastSegment();
  int num = in.readInt();
  cs.myArea = (WorldArea)in.readObject();
  for(int i=0;i<num;i++)
  {
    WorldLocation wl = new WorldLocation(in.readDouble(),
                                         in.readDouble(),
                                         in.readDouble());
    cs.addElement(wl);
  }
  return cs;
}*/