package edu.cmu.lti.weizh.fda;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FilterData {

  static String path = "/Users/indri/Documents/research/LDC/annotations";

  static BufferedReader br;

  static PrintWriter bw;

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
      if (node.isFile()&& node.getAbsolutePath().endsWith(".onf")) {
        br = new BufferedReader(new FileReader(node));
        bw = new PrintWriter("Oshio/"+node.getName()+".nonf", "UTF-8");       
        String line = "";
        while ((line=br.readLine())!=null){
          if (line.contains("name:  PERSON"))
            continue;
          if (line.contains("name:  GPE"))
            continue;
          if (line.contains("name:  LOC"))
            continue;
          if (line.contains("name:  FAC"))
            continue;
          if (line.contains("name:  ORG"))
            continue;
          if (line.contains("name:  PRODUCT"))
            continue;
          if (line.contains("name:  EVENT"))
            continue;
          if (line.contains("name:  WORK_OF_ART"))
            continue;
          if (line.contains("name:  LAW"))
            continue;
          if (line.contains("name:  LANGUAGE"))
            continue;
          if (line.contains("name:  PERCENT"))
            continue;
          if (line.contains("name:  MONEY"))
            continue;
          if (line.contains("name:  QUANTITY"))
            continue;
          if (line.contains("name:  ORDINAL"))
            continue;
          if (line.contains("name:  CARDINAL"))
            continue;

          bw.println(line);
        }
        bw.close();
      }
    }

  }
}
