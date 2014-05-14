package edu.cmu.lti.weizh.Interface;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


public abstract class MLModel{

  public void store(String file) throws IOException {
    FileOutputStream fos = new FileOutputStream(file);
    ObjectOutputStream out = new ObjectOutputStream(fos);
    out.writeObject(this);
    out.close();
  };  

}
