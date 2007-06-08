package ASSET.Util;

/**
 * Title: IdNumber
 * Description: Generates unique id numbers
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Ian Mayo
 * @version 1.0
 */

public class IdNumber {

  /** the randomizer we use for generation
   *
   */
  private static java.util.Random _randomizer = new java.util.Random();

  /** largest integer we create
   *
   */
  private static int _limit = 1000000;

  public static int generateInt()
  {
    return _randomizer.nextInt(_limit);
  }
}