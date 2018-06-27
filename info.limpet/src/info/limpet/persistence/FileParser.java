package info.limpet.persistence;

import info.limpet.IStoreItem;

import java.io.IOException;
import java.util.List;

abstract public class FileParser
{
  abstract public List<IStoreItem> parse(String filePath) throws IOException;

  public static String filePrefix(final String fullPath)
  {
    // gets filename without extension
    return fullPath.split("\\.(?=[^\\.]+$)")[0];
  }

}
