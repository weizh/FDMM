package edu.cmu.lti.weizh.fda;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FilterData {

  static String path = "/Users/indri/Documents/research/LDC/annotations";

  static BufferedReader br;
  static BufferedWriter bw;

  public static void main(String argv[]) {
    try {
      filterTags(new File(path));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void filterTags(File node) throws IOException {

    System.out.println(node.getAbsoluteFile());

    if (node.isDirectory()) {
      String[] subNote = node.list();
      for (String filename : subNote) {
        filterTags(new File(node, filename));
      }
    } else {
      if (node.isFile()) {
        br = new BufferedReader(new FileReader(node));
        bw = new BufferedWriter(new FileWriter(new File(node.getAbsolutePath()+".nonf")));
        String line = "";
        while ((line=br.readLine())!=null){
          if (line.startsWith("           name:  PERSON"))
            continue;
          if (line.startsWith("           name:  PRODUCT"))
            continue;
          if (line.startsWith("           name:  EVENT"))
            continue;
          if (line.startsWith("           name:  WORKOFART"))
            continue;
          if (line.startsWith("           name:  LAW"))
            continue;
          if (line.startsWith("           name:  LANGUAGE"))
            continue;
          if (line.startsWith("           name:  DATE"))
            continue;
          if (line.startsWith("           name:  TIME"))
            continue;
          if (line.startsWith("           name:  PERCENT"))
            continue;
          if (line.startsWith("           name:  MONEY"))
            continue;
          if (line.startsWith("           name:  QUANTITY"))
            continue;
          if (line.startsWith("           name:  ORDINAL"))
            continue;
          if (line.startsWith("           name:  CARDINAL"))
            continue;

          bw.write(line+"\n");
        }
      }
    }

  }
}
