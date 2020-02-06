
/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
	private static final long serialVersionUID = 1L;
	//////////////////////////////////////
  // member variables
  /**
 * /////////////////////////////////////
 */
  transient LibrarySelectionTable _myLST = null;
  /**
   * DebriefFeatureWarehouse _myWarehouse
   */
  DebriefFeatureWarehouse _myWarehouse = null;
  /**
   * CoverageAttributeTable _myCat
   */
  CoverageAttributeTable _myCat = null;

  //////////////////////////////////////
  // constructor
  /**
 * /////////////////////////////////////
 *
 */

  public LibraryLayer(final LibrarySelectionTable lst, final String name, final DebriefFeatureWarehouse warehouse, final boolean autoPopulate)
  {

    _myWarehouse = warehouse;
    _myLST = lst;
    super.setName(name);
    setVisible(false);

    try
    {

      // create the tree of coverages
      CoverageAttributeTable _myCat1 = _myLST.getCAT(name);

      // did we get a cat?
      if(_myCat1 == null)
      {
        // oh, well, let's find the first non-reference library
        final String[] libs = _myLST.getLibraryNames();
        for(int i=0; i<libs.length;i++)
        {
          final String thisName = libs[i];
          if(thisName.toLowerCase().equals("rference"))
          {
            // just ignore it, man
          }
          else
          {
            _myCat1 = _myLST.getCAT(thisName);
            break;
          }
        }

      }

      // do we want to populate the library with all of the available data?
      if(autoPopulate)
      {
        // did we get a cat?
        if(_myCat1 == null)
        	return;

      	
        // get the list of coverages in this library
        final String[] coverages = _myCat1.getCoverageNames();

        // step through the coverages
        for(int i=0;i<coverages.length;i++)
        {
          final String thisCov = (String)coverages[i];
          final CoverageLayer cl = new CoverageLayer(_myLST, _myWarehouse, thisCov, _myCat1);
          this.add(cl);
        }
      }
    }
    catch(final com.bbn.openmap.io.FormatException fe)
    {
      fe.printStackTrace();
    }
  }

  //////////////////////////////////////
  // member methods
  /////////////////////////////////////

  /**
   * set the name of this library - this tells us which library to read from disk
   *
   */
  public void setName(final String val)
  {
    // pass the name to the parent
    super.setName(val);

    // and initialise our data accordingly
  }

	/** whether this type of BaseLayer is able to have shapes added to it
	 * 
	 * @return
	 */
	@Override
	public boolean canTakeShapes()
	{
		return false;
	}
  
  /**
   * static method which returns an single painter which paints the libref feature set
   * @param path the location of the VMap data providing the reference coastline
   */
  public static CoverageLayer.ReferenceCoverageLayer createReferenceLayer(final String path)
  {
    // return value
    CoverageLayer.ReferenceCoverageLayer rcl = null;
    try
    {
      final LibrarySelectionTable LST = new LibrarySelectionTable(path);
      final DebriefFeatureWarehouse myWarehouse = new DebriefFeatureWarehouse();
      final FeaturePainter fp = new FeaturePainter("libref","Coastline");
      fp.setVisible(true);
      rcl = new CoverageLayer.ReferenceCoverageLayer(LST, myWarehouse, "libref", "libref", "Coastline", fp);
      rcl.setVisible(true);
    }
    catch(final com.bbn.openmap.io.FormatException ex)
    {
      ex.printStackTrace();
    }
    return rcl;
  }

  /**
   * getWarehouse
   *
   * @return the returned DebriefFeatureWarehouse
   */
  public DebriefFeatureWarehouse getWarehouse()
  {
    return _myWarehouse;
  }

  /**
   * getLST
   *
   * @return the returned LibrarySelectionTable
   */
  public LibrarySelectionTable getLST()
  {
    return _myLST;
  }

  /**
   * getCAT
   *
   * @return the returned CoverageAttributeTable
   */
  public CoverageAttributeTable getCAT()
  {
    return _myCat;
  }

  /**
   * paint
   *
   * @param g parameter for paint
   */
  public void paint(final CanvasType g)
  {
    if(!getVisible())
      return;
    
    final float oldWid = g.getLineWidth();

    g.setLineWidth(this.getLineThickness());
    
    // store this canvas in the warehouse, so that it knows where it's plotting to
    _myWarehouse.setCanvas(g);

    DebriefFeatureWarehouse.counter = 0;

 //   long l = System.currentTimeMillis();

    // let the Plottables handle the plotting
    super.paint(g);

    // work out how long it took
 //   System.out.println("time:" + (System.currentTimeMillis() - l) + ", ct:" + DebriefFeatureWarehouse.counter);

    g.setLineWidth(oldWid);
    
  }

}



