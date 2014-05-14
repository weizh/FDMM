package edu.cmu.lti.weizh.models;

import gnu.trove.map.TObjectIntMap;

public class Global {

  public static String[] etypes = { "PERSON", "NORP", "FAC", "ORG", "LOC",
      "PRODUCT", "EVENT", "WORK_OF_ART", "LAW", "LANGUAGE", "DATE", "TIME", "PERCENT", "MONEY",
      "QUANTITY", "ORDINAL", "CARDINAL" };

  public static TObjectIntMap<String> E2IDMap;
  static {
    int id = 0;
    for (String s : etypes)
      E2IDMap.put(s, id++);
  }
  
}
