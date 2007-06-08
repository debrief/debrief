package ASSET.Util.XML.Control.Observers;


/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import ASSET.Scenario.Observers.Summary.BatchCollator;
import ASSET.Scenario.Observers.Summary.BatchCollatorHelper;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;


public class BatchCollatorHandler extends MWCXMLReader
{

  private static String type = "BatchCollator";

  private static final String PER_CASE = "PerCase";
  private static final String FILE_NAME = "file_name";
  private static final String COLLATION_METHOD = "CollationMethod";
  private static final String ACTIVE = "Active";
  private static final String ONLY_BATCH = "OnlyBatchReporting";


  public boolean _perCase;
  public boolean _isActive = false;
  public boolean _onlyBatch = false;
  public String _fileName;
  public String _collation;


  public BatchCollatorHandler()
  {
    super(type);

    addAttributeHandler(new HandleAttribute(FILE_NAME)
    {
      public void setValue(String name, String val)
      {
        _fileName = val;
      }
    });
    addAttributeHandler(new HandleAttribute(COLLATION_METHOD)
    {
      public void setValue(String name, String val)
      {
        _collation = val;
      }
    });
    addAttributeHandler(new HandleBooleanAttribute(ACTIVE)
    {
      public void setValue(String name, boolean value)
      {
        _isActive = value;
      }
    });
    addAttributeHandler(new HandleBooleanAttribute(ONLY_BATCH)
    {
      public void setValue(String name, boolean value)
      {
        _onlyBatch = value;
      }
    });
    addAttributeHandler(new HandleBooleanAttribute(PER_CASE)
    {
      public void setValue(String name, boolean value)
      {
        _perCase = value;
      }
    });

  }

  /**
   * store our data in the collator observer we received
   *
   * @param collator
   */
  public void setData(BatchCollator collator)
  {

    // did we find any data?
    if (_collation != null)
    {
      // we have a collation method, must be allright then.

      collator.setBatchCollationProcessing(_fileName, _collation, _perCase, _isActive);
      collator.setBatchOnly(_onlyBatch);


      // and clear our data
      _perCase = false;
      _isActive = false;
      _fileName = null;
      _collation = null;
      _onlyBatch = false;
    }
  }

  public void elementClosed()
  {

  }

  public void reset()
  {
  }

  //  /** store how this batch collator works
  //   *
  //   * @param perCase
  //   * @param isActive
  //   * @param directory
  //   * @param fileName
  //   * @param collation
  //   */
  //  abstract public void setBatchCollation(boolean perCase,
  //                                         boolean isActive,
  //                                         String directory,
  //                                         String fileName,
  //                                         String collation);


  public static void exportCollator(boolean perCase,
                                    boolean isActive,
                                    boolean onlyBatch, String fileName,
                                    String collation,
                                    org.w3c.dom.Element parent, org.w3c.dom.Document doc)
  {
    org.w3c.dom.Element batch = doc.createElement(type);

    // set the attributes
    batch.setAttribute(ACTIVE, writeThis(isActive));
    batch.setAttribute(PER_CASE, writeThis(perCase));
    batch.setAttribute(FILE_NAME, fileName);
    batch.setAttribute(COLLATION_METHOD, collation);
    batch.setAttribute(ONLY_BATCH, writeThis(onlyBatch));

    parent.appendChild(batch);
  }

  public static void exportCollator(BatchCollatorHelper collator,
                                    boolean onlyBatchProcesing, org.w3c.dom.Element parent, org.w3c.dom.Document doc)
  {
    exportCollator(collator.getPerCase(), collator.getActive(),
                   onlyBatchProcesing, collator.getFilename(),
                   collator.getCollationStrategy(), parent, doc);
  }

}