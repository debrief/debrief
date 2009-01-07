package ASSET.Util.XML.Vessels.Util;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */


import ASSET.Participants.Category;

abstract public class CategoryHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
  {

  private final static String catType = "Category";

  String env;
  String force;
  String type;


  public CategoryHandler(String catName)
  {
    super(catName);

    super.addAttributeHandler(new HandleAttribute("Environment")
    {
      public void setValue(String name, final String val)
      {
        env = ASSET.Participants.Category.checkEnv(val);
      }
    });
    super.addAttributeHandler(new HandleAttribute("Force")
    {
      public void setValue(String name, final String val)
      {
        force = ASSET.Participants.Category.checkForce(val);
      }
    });
    super.addAttributeHandler(new HandleAttribute("Type")
    {
      public void setValue(String name, final String val)
      {
        type = ASSET.Participants.Category.checkType(val);
      }
    });

  }

  public CategoryHandler()
  {
    this(catType);
  }

  public void elementClosed()
  {
    // just check that all of the parameters are st
    if ((force == null) || (env == null) || (type == null))
      throw new RuntimeException("Not all parameters set for category");

    // create the category
    final Category cat = new Category(force, env, type);

    setCategory(cat);
  }

  abstract public void setCategory(ASSET.Participants.Category cat);

  static public void exportThis(final ASSET.Participants.Category toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    exportThis(catType, toExport, parent, doc);
  }

  static public void exportThis(String catName, final ASSET.Participants.Category toExport,
                                final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
  {
    // create the element
    final org.w3c.dom.Element stat = doc.createElement(catName);

    // set the attributes
    stat.setAttribute("Environment", toExport.getEnvironment());
    stat.setAttribute("Force", toExport.getForce());
    stat.setAttribute("Type", toExport.getType());

    // add to parent
    parent.appendChild(stat);
  }

}